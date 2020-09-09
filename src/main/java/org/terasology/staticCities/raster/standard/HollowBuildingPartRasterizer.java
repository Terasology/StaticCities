// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.raster.standard;

import com.google.common.math.DoubleMath;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.math.geom.Rect2i;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.DefaultBlockType;
import org.terasology.staticCities.bldg.BuildingPartRasterizer;
import org.terasology.staticCities.bldg.HollowBuildingPart;
import org.terasology.staticCities.common.Edges;
import org.terasology.staticCities.raster.BuildingPens;
import org.terasology.staticCities.raster.Pen;
import org.terasology.staticCities.raster.Pens;
import org.terasology.staticCities.raster.RasterTarget;
import org.terasology.staticCities.raster.RasterUtil;

import java.math.RoundingMode;

/**
 * Converts a {@link HollowBuildingPart} into blocks
 */
public class HollowBuildingPartRasterizer extends BuildingPartRasterizer<HollowBuildingPart> {

    public HollowBuildingPartRasterizer(BlockTheme theme) {
        super(theme, HollowBuildingPart.class);
    }

    @Override
    protected void raster(RasterTarget brush, HollowBuildingPart part, HeightMap heightMap) {
        Rect2i rc = part.getShape();

        if (!rc.overlaps(brush.getAffectedArea())) {
            return;
        }

//      TODO: check y overlap

        int baseHeight = part.getBaseHeight();
        int wallHeight = part.getWallHeight();
        int arcRadius = part.getArcRadius();

        Pen floorPen = BuildingPens.floorPen(brush, heightMap, baseHeight, DefaultBlockType.BUILDING_FLOOR);
        RasterUtil.fillRect(floorPen, rc);

        HeightMap hmTop = HeightMaps.constant(baseHeight + wallHeight);
        HeightMap hmBottom = new HeightMap() {

            @Override
            public int apply(int x, int z) {

                int distToCornerSq = Edges.getDistanceToCornerSq(rc, x, z);
                if (distToCornerSq == 0) {
                    return baseHeight;
                }

                int top = hmTop.apply(x, z);
                int arcSq = arcRadius * arcRadius;
                int dxSq = Math.max(0, arcSq - distToCornerSq);
                int dy = DoubleMath.roundToInt(Math.sqrt(arcSq - dxSq), RoundingMode.HALF_UP);
                return top - arcRadius + dy;
            }

        };

        // create walls
        Pen wallPen = Pens.fill(brush, hmBottom, hmTop, DefaultBlockType.BUILDING_WALL);
        RasterUtil.drawRect(wallPen, rc);
    }
}

