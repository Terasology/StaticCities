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

package org.terasology.staticCities.roof;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.DefaultBlockType;
import org.terasology.staticCities.model.roof.FlatRoof;
import org.terasology.staticCities.raster.Pen;
import org.terasology.staticCities.raster.Pens;
import org.terasology.staticCities.raster.RasterTarget;
import org.terasology.staticCities.raster.RasterUtil;

/**
 * Converts a {@link FlatRoof} into blocks
 */
public class FlatRoofRasterizer extends RoofRasterizer<FlatRoof> {

    /**
     * @param theme the block theme to use
     */
    public FlatRoofRasterizer(BlockTheme theme) {
        super(theme, FlatRoof.class);
    }

    @Override
    public void raster(RasterTarget target, FlatRoof roof, HeightMap hm) {
        BlockAreac area = roof.getShape();

        if (!area.intersectsBlockArea(target.getAffectedArea())) {
            return;
        }

        HeightMap hmTop = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                int rx = x - area.minX();
                int rz = z - area.minY();

                int y = roof.getBaseHeight() + 1;       // at least one block thick

                // distance to border of the roof
                int borderDistX = Math.min(rx, area.getSizeX() - 1 - rx);
                int borderDistZ = Math.min(rz, area.getSizeY() - 1 - rz);

                int dist = Math.min(borderDistX, borderDistZ);

                if (dist == 0) {
                    y += roof.getBorderHeight(rx, rz);
                }

                return y;
            }
        };
        HeightMap hmBottom = HeightMaps.constant(roof.getBaseHeight());

        Pen pen = Pens.fill(target, hmBottom, hmTop, DefaultBlockType.ROOF_FLAT);
        RasterUtil.fillRect(pen, area);
    }
}
