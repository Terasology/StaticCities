// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.roof;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.math.TeraMath;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.DefaultBlockType;
import org.terasology.staticCities.common.Edges;
import org.terasology.staticCities.model.roof.HipRoof;
import org.terasology.staticCities.raster.Pen;
import org.terasology.staticCities.raster.Pens;
import org.terasology.staticCities.raster.RasterTarget;
import org.terasology.staticCities.raster.RasterUtil;

/**
 * Converts a {@link HipRoof} into blocks
 */
public class HipRoofRasterizer extends RoofRasterizer<HipRoof> {

    /**
     * @param theme the block theme to use
     */
    public HipRoofRasterizer(BlockTheme theme) {
        super(theme, HipRoof.class);
    }

    @Override
    public void raster(RasterTarget target, HipRoof roof, HeightMap hm) {
        BlockAreac area = roof.getShape();

        if (!area.intersectsBlockArea(target.getAffectedArea())) {
            return;
        }

        // this is the ground truth
        // maxHeight = baseHeight + Math.min(cur.width, cur.height) * pitch / 2;

        HeightMap hmBottom = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                int dist = Edges.getDistanceToBorder(area, x, z);
                int y = TeraMath.floorToInt(roof.getBaseHeight() + dist * roof.getPitch());
                return Math.min(y, roof.getMaxHeight());
            }
        };

        HeightMap hmTop = HeightMaps.offset(hmBottom, TeraMath.ceilToInt(roof.getPitch()));
        Pen pen = Pens.fill(target, hmBottom, hmTop, DefaultBlockType.ROOF_HIP);
        RasterUtil.fillRect(pen, area);
    }

}
