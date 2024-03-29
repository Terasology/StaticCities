// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.roads;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.joml.RoundingMode;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.commonworld.Orientation;
import org.terasology.commonworld.UnorderedPair;
import org.terasology.core.world.generator.facets.BiomeFacet;
import org.terasology.engine.utilities.procedural.PerlinNoise;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetBorder;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.math.TeraMath;
import org.terasology.staticCities.blocked.BlockedAreaFacet;
import org.terasology.staticCities.roads.GeneralPathFinder.DefaultEdge;
import org.terasology.staticCities.roads.GeneralPathFinder.Edge;
import org.terasology.staticCities.roads.GeneralPathFinder.Path;
import org.terasology.staticCities.sites.Site;
import org.terasology.staticCities.sites.SiteFacet;
import org.terasology.staticCities.terrain.BuildableTerrainFacet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * Provides {@link Road} instances through {@link RoadFacet}.
 */
@Produces(RoadFacet.class)
@Requires({
    @Facet(value = SiteFacet.class, border = @FacetBorder(sides = 500)),
    @Facet(BuildableTerrainFacet.class),
    @Facet(BlockedAreaFacet.class)})
public class RoadFacetProvider implements FacetProvider {

    private static final Logger logger = LoggerFactory.getLogger(RoadFacetProvider.class);

    private final Cache<UnorderedPair<Site>, Optional<Road>> roadCache = CacheBuilder.newBuilder().build();

    private PerlinNoise noiseX;
    private PerlinNoise noiseY;

    /**
     * The amplitude of the noise
     */
    private final int noiseAmp = 48;

    /**
     * segments should be about N blocks long
     */
    private final int segLength = 48;

    /**
     * The smoothness of the noise. Lower values mean smoother curvature.
     */
    private final float smooth = 0.005f;


    @Override
    public void setSeed(long seed) {
        this.noiseX = new PerlinNoise(seed ^ 533231280);
        this.noiseY = new PerlinNoise(seed ^ 198218712);
    }

    @Override
    public void process(GeneratingRegion region) {
        BuildableTerrainFacet terrainFacet = region.getRegionFacet(BuildableTerrainFacet.class);
        SiteFacet siteFacet = region.getRegionFacet(SiteFacet.class);
        BlockedAreaFacet blockedAreaFacet = region.getRegionFacet(BlockedAreaFacet.class);

        Border3D border = region.getBorderForFacet(BiomeFacet.class);
        RoadFacet roadFacet = new RoadFacet(region.getRegion(), border);

        int thres = siteFacet.getCertainWorldRegion().getSizeX() / 2;

        List<Road> candidates = new ArrayList<>();

        List<Site> siteList = new ArrayList<>(siteFacet.getSettlements());
        for (int i = 0; i < siteList.size(); i++) {
            Site siteA = siteList.get(i);
            Vector2ic posA = siteA.getPos();
            for (int j = i + 1; j < siteList.size(); j++) {
                Site siteB = siteList.get(j);
                Vector2ic posB = siteB.getPos();

                int distX = Math.abs(posB.x() - posA.x());
                int distY = Math.abs(posB.y() - posA.y());

                if (distX < thres && distY < thres) {
                    try {
                        Optional<Road> opt = roadCache.get(new UnorderedPair<Site>(siteA, siteB),
                                () -> tryBuild(posA, posB, 8f, terrainFacet));
                        if (opt.isPresent()) {
                            candidates.add(opt.get());
                        }
                    } catch (ExecutionException e) {
                        logger.warn("Could not compute road between '{}' and '{}'", siteA, siteB);
                    }
                }
            }
        }

        candidates.sort((a, b) -> Float.compare(a.getLength(), b.getLength()));

        Map<Vector2i, Collection<Edge<Vector2i>>> sourceMap = new HashMap<>();
        GeneralPathFinder<Vector2i> pathFinder = new GeneralPathFinder<>(e -> sourceMap.getOrDefault(e, Collections.emptySet()));

        for (Road road : candidates) {

            // TODO: compute if road is even relevant for this world region first

            Optional<Path<Vector2i>> optPath = pathFinder.computePath(new Vector2i(road.getEnd0()), new Vector2i(road.getEnd1()));

            // existing connections must be at least 25% longer than the direct connection to be added
            if (!optPath.isPresent() || optPath.get().getLength() > 1.25 * road.getLength()) {
                Edge<Vector2i> e = new DefaultEdge<Vector2i>(new Vector2i(road.getEnd0()), new Vector2i(road.getEnd1()), road.getLength());
                sourceMap.computeIfAbsent(new Vector2i(road.getEnd0()), a -> new ArrayList<>()).add(e);
                sourceMap.computeIfAbsent(new Vector2i(road.getEnd1()), a -> new ArrayList<>()).add(e);
                roadFacet.addRoad(road);

                for (RoadSegment seg : road.getSegments()) {
                    blockedAreaFacet.addLine(seg.getStart(), seg.getEnd(), road.getWidth());
                }
            }
        }
        region.setRegionFacet(RoadFacet.class, roadFacet);
    }

    private Optional<Road> tryBuild(Vector2ic posA, Vector2ic posB, float width, BuildableTerrainFacet terrainFacet) {

        Optional<Road> opt;
        opt = tryDirect(posA, posB, width, terrainFacet);
        if (opt.isPresent()) {
            return opt;
        }
        opt = tryPathfinder(posA, posB, width, terrainFacet);
        if (opt.isPresent()) {
            return opt;
        }

        // TODO: consider creating a bridge instead if (water-passable)

        return opt;
    }

    private Optional<Road> tryDirect(Vector2ic posA, Vector2ic posB, float width, BuildableTerrainFacet terrainFacet) {
        double length = posA.distance(posB);
        int segCount = TeraMath.ceilToInt(length / segLength);  // ceil avoids division by zero for short distances

        List<Vector2i> segPoints = new ArrayList<>();

        segPoints.add(new Vector2i(posA));
        for (int i = 1; i < segCount; i++) {
            Vector2i pos = new Vector2i(new Vector2f(posA).lerp(new Vector2f(posB),  (float) i / segCount), RoundingMode.HALF_UP);

            // first and last point receive only half the noise distortion to smoothen the end points
            float applyFactor = (i == 1 || i == segCount - 1) ? 0.5f : 1f;
            pos.x += noiseX.noise(pos.x * smooth, 0, pos.y * smooth) * noiseAmp * applyFactor;
            pos.y += noiseY.noise(pos.x * smooth, 0, pos.y * smooth) * noiseAmp * applyFactor;

            if (!terrainFacet.isPassable(pos)) {
                return Optional.empty();
            }

            segPoints.add(pos);
        }
        segPoints.add(new Vector2i(posB));

        return Optional.of(new Road(segPoints, width));
    }

    private Optional<Road> tryPathfinder(Vector2ic posA, Vector2ic posB, float width, BuildableTerrainFacet terrainFacet) {

        Function<Vector2i, Collection<Edge<Vector2i>>> edgeFunc = new Function<Vector2i, Collection<Edge<Vector2i>>>() {

            @Override
            public Collection<Edge<Vector2i>> apply(Vector2i v) {
                if (v.distanceSquared(posB) < segLength * segLength) {
                    return Collections.singletonList(new DefaultEdge<>(v, new Vector2i(posB), v.distance(posB)));
                }
                Collection<Edge<Vector2i>> neighs = new ArrayList<>();
                for (Orientation or : Orientation.values()) {
                    Vector2i pos = new Vector2i(or.direction()).mul(segLength).add(v);
                    Vector2i noisePos = new Vector2i(pos);
                    noisePos.x += noiseX.noise(pos.x * smooth, 0, pos.y * smooth) * noiseAmp;
                    noisePos.y += noiseY.noise(pos.x * smooth, 0, pos.y * smooth) * noiseAmp;
                    if (terrainFacet.isPassable(noisePos)) {
                        neighs.add(new DefaultEdge<Vector2i>(v, pos, v.distance(noisePos)));
                    }
                }

                return neighs;
            }
        };

        GeneralPathFinder<Vector2i> pathFinder = new GeneralPathFinder<>(edgeFunc);

        Optional<Path<Vector2i>> optPath = pathFinder.computePath(new Vector2i(posA), new Vector2i(posB));
        if (optPath.isPresent()) {
            List<Vector2i> sequence = optPath.get().getSequence();
            for (int i = 1; i < sequence.size() - 1; i++) {
                Vector2i v = sequence.get(i);
                v.x += noiseX.noise(v.x * smooth, 0, v.y * smooth) * noiseAmp;
                v.y += noiseY.noise(v.x * smooth, 0, v.y * smooth) * noiseAmp;
            }
            return Optional.of(new Road(sequence, width));
        }

        return Optional.empty();
    }

}
