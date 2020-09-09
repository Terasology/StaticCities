// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.deco;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.engine.math.Side;
import org.terasology.math.geom.ImmutableVector3i;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.BlockType;
import org.terasology.staticCities.raster.RasterTarget;

import java.util.EnumSet;
import java.util.Set;

/**
 * Converts {@link ColumnDecoration} into blocks
 */
public class ColumnRasterizer extends DecorationRasterizer<ColumnDecoration> {

    /**
     * @param theme the block theme to use
     */
    public ColumnRasterizer(BlockTheme theme) {
        super(theme, ColumnDecoration.class);
    }

    @Override
    public void raster(RasterTarget target, ColumnDecoration deco, HeightMap hm) {
        ImmutableVector3i pos = deco.getBasePos();
        int y = pos.getY();
        if (target.getAffectedArea().contains(pos.getX(), pos.getZ())) {
            if (y + deco.getBlockTypes().size() - 1 >= target.getMinHeight() && y <= target.getMaxHeight()) {
                for (int i = 0; i < deco.getHeight(); i++) {
                    BlockType type = deco.getBlockTypes().get(i);
                    Side side = deco.getSides().get(i);
                    Set<Side> sides = (side == null) ? EnumSet.noneOf(Side.class) : EnumSet.of(side);
                    target.setBlock(pos.getX(), y, pos.getZ(), type, sides);
                    y++;
                }
            }
        }
    }
}
