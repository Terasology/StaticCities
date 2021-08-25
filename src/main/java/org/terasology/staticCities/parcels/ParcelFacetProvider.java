// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.staticCities.parcels;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.joml.Vector2f;
import org.joml.Vector2ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.commonworld.Orientation;
import org.terasology.commonworld.geom.CircleUtility;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.engine.world.generation.ConfigurableFacetProvider;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.joml.geom.Circlef;
import org.terasology.joml.geom.Rectanglef;
import org.terasology.math.TeraMath;
import org.terasology.nui.properties.Range;
import org.terasology.staticCities.blocked.BlockedAreaFacet;
import org.terasology.staticCities.roads.RoadFacet;
import org.terasology.staticCities.settlements.Settlement;
import org.terasology.staticCities.settlements.SettlementFacet;
import org.terasology.staticCities.sites.Site;
import org.terasology.staticCities.terrain.BuildableTerrainFacet;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Produces(ParcelFacet.class)
@Requires({
    @Facet(BlockedAreaFacet.class),
    @Facet(RoadFacet.class),                  // not really required, but roads update the blocked area facet
    @Facet(BuildableTerrainFacet.class),
    @Facet(SettlementFacet.class)
})
public class ParcelFacetProvider implements ConfigurableFacetProvider {

    private static final Logger logger = LoggerFactory.getLogger(ParcelFacetProvider.class);

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);

    private long seed;

    private ParcelConfiguration config = new ParcelConfiguration();

    private Cache<Site, Set<RectStaticParcel>> cache = CacheBuilder.newBuilder().build();

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public void process(GeneratingRegion region) {
        ParcelFacet facet = new ParcelFacet();
        SettlementFacet settlementFacet = region.getRegionFacet(SettlementFacet.class);
        BuildableTerrainFacet terrainFacet = region.getRegionFacet(BuildableTerrainFacet.class);
        BlockedAreaFacet blockedAreaFacet = region.getRegionFacet(BlockedAreaFacet.class);

        BlockAreac worldRect = blockedAreaFacet.getWorldArea();
        BlockArea targetRect = new BlockArea(worldRect.minX(), worldRect.minY(), worldRect.maxX(), worldRect.maxY());
        try {
            lock.readLock().lock();
            for (Settlement settlement : settlementFacet.getSettlements()) {
                Site site = settlement.getSite();
                if (CircleUtility.intersect(new Circlef(site.getPos().x(), site.getPos().y(), site.getRadius()), targetRect.getBounds(new Rectanglef()))) {
                    Set<RectStaticParcel> parcels = cache.get(site, () -> generateParcels(settlement, blockedAreaFacet, terrainFacet));
                    for (RectStaticParcel parcel : parcels) {
                        if (parcel.getShape().intersectsBlockArea(targetRect)) {
                            facet.addParcel(site, parcel);
                        }
                    }
                }
            }
        } catch (ExecutionException e) {
            logger.warn("Could not compute parcels for '{}'", region.getRegion(), e.getCause());
        } finally {
            lock.readLock().unlock();
        }

        region.setRegionFacet(ParcelFacet.class, facet);
    }

    private Set<RectStaticParcel> generateParcels(Settlement settlement, BlockedAreaFacet blockedAreaFacet, BuildableTerrainFacet terrainFacet) {
        Random rng = new FastRandom(seed ^ settlement.getSite().getPos().hashCode());

        Set<RectStaticParcel> result = new LinkedHashSet<>();
        result.addAll(generateParcels(settlement, rng, 25, 40, 1, Zone.CLERICAL, blockedAreaFacet, terrainFacet));
        result.addAll(generateParcels(settlement, rng, 25, 40, 1, Zone.GOVERNMENTAL, blockedAreaFacet, terrainFacet));
        result.addAll(generateParcels(settlement, rng, 20, 30, 1, Zone.COMMERCIAL, blockedAreaFacet, terrainFacet));
        result.addAll(generateParcels(settlement, rng, config.minSize, config.maxSize, config.maxLots,
                Zone.RESIDENTIAL, blockedAreaFacet, terrainFacet));
        return result;
    }

    private Set<RectStaticParcel> generateParcels(Settlement settlement, Random rng, float minSize, float maxSize, int count, Zone zoneType,
                                                  BlockedAreaFacet blockedAreaFacet, BuildableTerrainFacet terrainFacet) {
        Vector2ic center = settlement.getSite().getPos();
        Set<RectStaticParcel> lots = new LinkedHashSet<>();  // the order is important for deterministic generation
        float maxLotRad = maxSize * (float) Math.sqrt(2) * 0.5f;
        float minRad = 5 + maxSize * 0.5f;
        float maxRad = settlement.getSettlementRadius() - maxLotRad;

        if (minRad >= maxRad) {
            return lots;        // which is empty
        }

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < config.maxTries; j++) {
                float ang = rng.nextFloat(0, (float) Math.PI * 2.0f);
                float rad = rng.nextFloat(minRad, maxRad);
                float desSizeX = rng.nextFloat(minSize, maxSize);
                float desSizeZ = rng.nextFloat(minSize, maxSize);

                float x = center.x() + rad * (float) Math.cos(ang);
                float z = center.y() + rad * (float) Math.sin(ang);

                Vector2f pos = new Vector2f(x, z);
                Vector2f maxSpace = getMaxSpace(pos, lots);

                int sizeX = (int) Math.min(desSizeX, maxSpace.x);
                int sizeZ = (int) Math.min(desSizeZ, maxSpace.y);

                // check if enough space is available
                if (sizeX < minSize || sizeZ < minSize) {
                    continue;
                }

                int minX = TeraMath.floorToInt(pos.x() - sizeX * 0.5f);
                int minY = TeraMath.floorToInt(pos.y() - sizeZ * 0.5f);
                BlockArea shape = new BlockArea(minX, minY).setSize(sizeX, sizeZ);

                if (terrainFacet.isBuildable(shape) && !blockedAreaFacet.isBlocked(shape)) {
                    Orientation orientation = Orientation.NORTH.getRotated(90 * rng.nextInt(4));
                    RectStaticParcel lot = new RectStaticParcel(shape, zoneType, orientation);
                    blockedAreaFacet.addRect(shape);
                    lots.add(lot);
                    break;
                }
            }
        }

        logger.debug("Generated {} parcels for settlement {}", lots.size(), settlement);

        return lots;
    }

    private static Vector2f getMaxSpace(Vector2f pos, Set<RectStaticParcel> lots) {
        float maxX = Float.POSITIVE_INFINITY;
        float maxZ = Float.POSITIVE_INFINITY;

        //      xxxxxxxxxxxxxxxxxxx
        //      x                 x             (p)
        //      x        o------- x--------------|
        //      x                 x
        //      xxxxxxxxxxxxxxxxxxx       dx
        //                         <------------->

        for (RectStaticParcel lot : lots) {
            BlockAreac bounds = lot.getShape();
            float centerX = (bounds.maxX() + bounds.minX()) / 2f;
            float centerY = (bounds.maxY() + bounds.minY()) / 2f;
            float dx = Math.abs(pos.x() - centerX) - bounds.getSizeX() * 0.5f;
            float dz = Math.abs(pos.y() - centerY) - bounds.getSizeY() * 0.5f;

            // the point is inside -> abort
            if (dx <= 0 && dz <= 0) {
                return new Vector2f(0, 0);
            }

            // the point is diagonally outside -> restrict one of the two only
            if (dx > 0 && dz > 0) {
                // make the larger of the two smaller --> larger shape area
                if (dx > dz) {
                    maxX = Math.min(maxX, dx);
                } else {
                    maxZ = Math.min(maxZ, dz);
                }
            }

            // the z-axis is overlapping -> restrict x
            if (dx > 0 && dz <= 0) {
                maxX = Math.min(maxX, dx);
            }

            // the x-axis is overlapping -> restrict z
            if (dx <= 0 && dz > 0) {
                maxZ = Math.min(maxZ, dz);
            }
        }

        return new Vector2f(2 * maxX, 2 * maxZ);
    }

    @Override
    public String getConfigurationName() {
        return "Settlement Parcels";
    }

    @Override
    public Component getConfiguration() {
        return config;
    }

    @Override
    public void setConfiguration(Component configuration) {
        try {
            lock.writeLock().lock();
            this.config = (ParcelConfiguration) configuration;
            cache.invalidateAll();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static class ParcelConfiguration implements Component<ParcelConfiguration> {

        @Range(min = 5f, max = 50f, increment = 1f, precision = 0, description = "The min. parcel length")
        private float minSize = 10;

        @Range(min = 5f, max = 50f, increment = 1f, precision = 0, description = "The max. parcel length")
        private float maxSize = 18;

        @Range(min = 1, max = 5, increment = 1, precision = 0, description = "The max. number of placement attempts per parcel")
        private int maxTries = 1;

        @Range(min = 5, max = 250, increment = 1, precision = 0, description = "The max. number of parcels")
        private int maxLots = 100;

        @Override
        public void copyFrom(ParcelConfiguration other) {
            this.minSize = other.minSize;
            this.maxSize = other.maxSize;
            this.maxTries = other.maxTries;
            this.maxLots = other.maxLots;
        }
    }
}
