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

import org.joml.RoundingMode;
import org.joml.Vector2i;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.joml.geom.Circlef;
import org.terasology.joml.geom.Rectanglef;
import org.terasology.math.TeraMath;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.DefaultBlockType;
import org.terasology.staticCities.bldg.BuildingPartRasterizer;
import org.terasology.staticCities.bldg.RoundBuildingPart;
import org.terasology.staticCities.common.CircleUtility;
import org.terasology.staticCities.raster.BuildingPens;
import org.terasology.staticCities.raster.CheckedPen;
import org.terasology.staticCities.raster.Pen;
import org.terasology.staticCities.raster.Pens;
import org.terasology.staticCities.raster.RasterTarget;
import org.terasology.staticCities.raster.RasterUtil;


/**
 * Converts a {@link RoundBuildingPart} into blocks
 */
public class RoundPartRasterizer extends BuildingPartRasterizer<RoundBuildingPart> {

    /**
     * @param theme the theme to use
     */
    public RoundPartRasterizer(BlockTheme theme) {
        super(theme, RoundBuildingPart.class);
    }

    @Override
    protected void raster(RasterTarget brush, RoundBuildingPart element, HeightMap heightMap) {

        Circlef area = element.getShape();
        if (!CircleUtility.intersect(area, brush.getAffectedArea().getBounds(new Rectanglef()))) {
            return;
        }

        Vector2i center = new Vector2i(area.x, area.y, RoundingMode.HALF_UP);
        int radius = TeraMath.floorToInt(area.r);

        int baseHeight = element.getBaseHeight();
        int wallHeight = element.getWallHeight();

        Pen floorPen = BuildingPens.floorPen(brush, heightMap, baseHeight, DefaultBlockType.BUILDING_FLOOR);
        RasterUtil.fillCircle(new CheckedPen(floorPen), center.x(), center.y(), radius);

        // create walls
        Pen wallPen = Pens.fill(brush, baseHeight, baseHeight + wallHeight, DefaultBlockType.BUILDING_WALL);
        RasterUtil.drawCircle(new CheckedPen(wallPen), center.x(), center.y(), radius);
    }
}
