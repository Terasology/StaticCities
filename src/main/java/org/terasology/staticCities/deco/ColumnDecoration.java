// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.deco;

import com.google.common.base.Preconditions;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.engine.math.Side;
import org.terasology.staticCities.BlockType;

import java.util.List;

/**
 * A decoration made up by a column of blocks (e.g. ladders, pillars, etc.)
 */
public class ColumnDecoration implements Decoration {

    private final Vector3i pos = new Vector3i();
    private final List<BlockType> blocks;
    private final List<Side> sides;

    /**
     * @param blocks the decoration block types
     * @param sides the facing sides of the blocks
     * @param basePos the window position
     */
    public ColumnDecoration(List<BlockType> blocks, List<Side> sides, Vector3ic basePos) {
        Preconditions.checkArgument(blocks.size() == sides.size(), "blockCount != sideCount");
        this.blocks = blocks;
        this.sides = sides;
        this.pos.set(basePos);
    }

    /**
     * @return the position
     */
    public Vector3ic getBasePos() {
        return this.pos;
    }

    /**
     * @return the block type to raster
     */
    public List<BlockType> getBlockTypes() {
        return blocks;
    }

    /**
     * @return the facing sides of the blocks
     */
    public List<Side> getSides() {
        return sides;
    }

    /**
     * @return the height of the column
     */
    public int getHeight() {
        return blocks.size(); // the lists are equally long
    }
}
