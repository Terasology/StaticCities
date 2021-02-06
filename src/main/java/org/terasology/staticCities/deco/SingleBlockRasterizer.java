// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.deco;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.raster.RasterTarget;

import java.util.Collections;

/**
 * Converts {@link SingleBlockDecoration} into blocks
 */
public class SingleBlockRasterizer extends DecorationRasterizer<SingleBlockDecoration> {

    /**
     * @param theme the block theme to use
     */
    public SingleBlockRasterizer(BlockTheme theme) {
        super(theme, SingleBlockDecoration.class);
    }

    @Override
    public void raster(RasterTarget target, SingleBlockDecoration deco, HeightMap hm) {
        if (target.getAffectedRegion().contains(deco.getPos())) {
            target.setBlock(deco.getPos(), deco.getType(), Collections.singleton(deco.getSide()));
        }
    }

}
