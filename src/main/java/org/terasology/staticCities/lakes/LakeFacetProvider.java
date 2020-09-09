// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.lakes;

import com.google.common.base.Objects;
import org.terasology.commonworld.contour.Contour;
import org.terasology.commonworld.contour.ContourTracer;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Rect2i;
import org.terasology.namegenerator.waters.DebugWaterTheme;
import org.terasology.namegenerator.waters.WaterNameProvider;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Collection;

/**
 *
 */
@Produces(LakeFacet.class)
@Requires({
        @Facet(SurfaceHeightFacet.class),
        @Facet(SeaLevelFacet.class)
})
public class LakeFacetProvider implements FacetProvider {

    private static final int SEED_SALT = 2354234;

    private long seed;

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public void process(GeneratingRegion region) {
        SurfaceHeightFacet surfaceHeightFacet = region.getRegionFacet(SurfaceHeightFacet.class);
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);
        LakeFacet lakeFacet = new LakeFacet(region.getRegion(), region.getBorderForFacet(LakeFacet.class));
        Rect2i worldRect = lakeFacet.getWorldRegion();

        int ngseed = Objects.hashCode(seed, region.getRegion(), SEED_SALT);
        WaterNameProvider ng = new WaterNameProvider(ngseed, new DebugWaterTheme());

        int minSize = 1;

        int scale = 1;
//        int size = Sector.SIZE / scale;
        HeightMap orgHm = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                return TeraMath.floorToInt(surfaceHeightFacet.getWorld(x, z));
            }
        };

//       HeightMaps.scalingArea(heightMap, scale);
//        Vector2i coords = sector.getCoords();

//        Rectangle sectorRect = new Rectangle(coords.x * size, coords.y * size, size, size);
        Rectangle sectorRect = new Rectangle(worldRect.minX(), worldRect.minY(), worldRect.width(), worldRect.height());
        ContourTracer ct = new ContourTracer(orgHm, sectorRect, seaLevelFacet.getSeaLevel());

        for (Contour c : ct.getOuterContours()) {
            Contour scaledContour = c.scale(scale);
            Polygon polyLake = scaledContour.getPolygon();

            if (polyLake.getBounds().width > minSize
                    && polyLake.getBounds().height > minSize) {
                Lake lake = new Lake(scaledContour, ng.generateName());

                for (Contour isl : ct.getInnerContours()) {
                    Rectangle bboxIsland = isl.getPolygon().getBounds();

                    if (polyLake.getBounds().contains(bboxIsland)) {
                        if (allInside(polyLake, isl.getPoints())) {
                            lake.addIsland(isl);
                        }
                    }
                }

                lakeFacet.add(lake);
            }
        }

        region.setRegionFacet(LakeFacet.class, lakeFacet);
    }

    private boolean allInside(Polygon polygon, Collection<Point> points) {
        for (Point pt : points) {
            if (!polygon.contains(pt)) {
                return false;
            }
        }
        return true;
    }

}
