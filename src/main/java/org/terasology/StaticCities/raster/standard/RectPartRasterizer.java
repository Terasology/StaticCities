/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.staticCities.raster.standard;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.math.geom.Rect2i;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.DefaultBlockType;
import org.terasology.staticCities.bldg.BuildingPartRasterizer;
import org.terasology.staticCities.bldg.RectBuildingPart;
import org.terasology.staticCities.raster.BuildingPens;
import org.terasology.staticCities.raster.Pen;
import org.terasology.staticCities.raster.Pens;
import org.terasology.staticCities.raster.RasterTarget;
import org.terasology.staticCities.raster.RasterUtil;

/**
 * Converts a {@link RectBuildingPart} into blocks
 */
public class RectPartRasterizer extends BuildingPartRasterizer<RectBuildingPart> {

    public RectPartRasterizer(BlockTheme theme) {
        super(theme, RectBuildingPart.class);
    }

    @Override
    protected void raster(RasterTarget brush, RectBuildingPart part, HeightMap heightMap) {
        Rect2i rc = part.getShape();

        if (!rc.overlaps(brush.getAffectedArea())) {
            return;
        }

//        int topHeight = part.getBaseHeight() + part.getWallHeight() + part.getRoof().getHeight;
//        Region3i bbox = Region3i(rc.minX(), part.getBaseHeight(), rc.minY(), rc.maxX(), topHeight, rc.maxY());

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

