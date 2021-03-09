// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.roof;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.DefaultBlockType;
import org.terasology.staticCities.model.roof.SaddleRoof;
import org.terasology.staticCities.raster.Pen;
import org.terasology.staticCities.raster.Pens;
import org.terasology.staticCities.raster.RasterTarget;
import org.terasology.staticCities.raster.RasterUtil;

import static org.terasology.commonworld.Orientation.EAST;
import static org.terasology.commonworld.Orientation.WEST;

/**
 * Converts a {@link SaddleRoof} into blocks
 */
public class SaddleRoofRasterizer extends RoofRasterizer<SaddleRoof> {

    /**
     * @param theme the block theme to use
     */
    public SaddleRoofRasterizer(BlockTheme theme) {
        super(theme, SaddleRoof.class);
    }

    @Override
    public void raster(RasterTarget target, SaddleRoof roof, HeightMap hm) {
        BlockAreac area = roof.getShape();

        if (!area.intersectsBlockArea(target.getAffectedArea())) {
            return;
        }

        final boolean alongX = roof.getOrientation() == EAST || roof.getOrientation() == WEST;

        HeightMap hmTop = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                int rx = x - area.minX();
                int rz = z - area.minY();

                int y = roof.getBaseHeight();

                // distance to border of the roof
                int borderDistX = Math.min(rx, area.getSizeX() - 1 - rx);
                int borderDistZ = Math.min(rz, area.getSizeY() - 1 - rz);

                if (alongX) {
                    y += borderDistZ / roof.getPitch();
                } else {
                    y += borderDistX / roof.getPitch();
                }

                return y;
            }
        };

        HeightMap hmBottom = HeightMaps.offset(hmTop, -1);
        Pen pen = Pens.fill(target, hmBottom, hmTop, DefaultBlockType.ROOF_SADDLE);
        RasterUtil.fillRect(pen, area);

        BlockAreac wallRect = roof.getBaseArea();

        HeightMap hmGableBottom = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                int h0 = roof.getBaseHeight();
                if (alongX) {
                    int left = wallRect.minX();
                    int right = wallRect.maxX();

                    if (x == left || x == right) {
                        return h0;
                    }
                } else {
                    int top = wallRect.minY();
                    int bottom = wallRect.maxY();
                    if (z == top || z == bottom) {
                        return h0;
                    }
                }

                return hmBottom.apply(x, z);        // return top-height to get a no-op
            }
        };

        pen = Pens.fill(target, hmGableBottom, hmBottom, DefaultBlockType.ROOF_GABLE);
        RasterUtil.fillRect(pen, area);
    }
}
