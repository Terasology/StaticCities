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

import java.math.RoundingMode;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Circle;
import org.terasology.math.geom.Vector2i;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.DefaultBlockType;
import org.terasology.staticCities.bldg.BuildingPartRasterizer;
import org.terasology.staticCities.bldg.RoundBuildingPart;
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

        Circle area = element.getShape();

        if (!area.intersects(brush.getAffectedArea())) {
            return;
        }

        Vector2i center = new Vector2i(area.getCenter(), RoundingMode.HALF_UP);
        int radius = TeraMath.floorToInt(area.getRadius());

        int baseHeight = element.getBaseHeight();
        int wallHeight = element.getWallHeight();

        Pen floorPen = BuildingPens.floorPen(brush, heightMap, baseHeight, DefaultBlockType.BUILDING_FLOOR);
        RasterUtil.fillCircle(new CheckedPen(floorPen), center.x(), center.y(), radius);

        // create walls
        Pen wallPen = Pens.fill(brush, baseHeight, baseHeight + wallHeight, DefaultBlockType.BUILDING_WALL);
        RasterUtil.drawCircle(new CheckedPen(wallPen), center.x(), center.y(), radius);
    }
}
