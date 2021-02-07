// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.raster.standard;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.DefaultBlockType;
import org.terasology.staticCities.bldg.BuildingPartRasterizer;
import org.terasology.staticCities.bldg.RectBuildingPart;
import org.terasology.staticCities.raster.BuildingPens;
import org.terasology.staticCities.raster.Pen;
import org.terasology.staticCities.raster.Pens;
import org.terasology.staticCities.raster.RasterTarget;
import org.terasology.staticCities.raster.RasterUtil;
import org.terasology.world.block.BlockAreac;

/**
 * Converts a {@link RectBuildingPart} into blocks
 */
public class RectPartRasterizer extends BuildingPartRasterizer<RectBuildingPart> {

    public RectPartRasterizer(BlockTheme theme) {
        super(theme, RectBuildingPart.class);
    }

    @Override
    protected void raster(RasterTarget brush, RectBuildingPart part, HeightMap heightMap) {
        BlockAreac rc = part.getShape();

        if (!rc.intersectsBlockArea(brush.getAffectedArea())) {
            return;
        }

//        int topHeight = part.getBaseHeight() + part.getWallHeight() + part.getRoof().getHeight;
//        BlockRegion bbox = new BlockRegion(rc.minX(), part.getBaseHeight(), rc.minY(), rc.maxX(), topHeight, rc.maxY());

//        if (chunk.getRegion().overlaps(bbox)) {

        int baseHeight = part.getBaseHeight();
        int wallHeight = part.getWallHeight();

        Pen floorPen = BuildingPens.floorPen(brush, heightMap, baseHeight, DefaultBlockType.BUILDING_FLOOR);
        RasterUtil.fillRect(floorPen, rc);

        // create walls
        Pen wallPen = Pens.fill(brush, baseHeight, baseHeight + wallHeight, DefaultBlockType.BUILDING_WALL);
        RasterUtil.drawRect(wallPen, rc);
    }
}

