// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.door;

import org.terasology.commonworld.Orientation;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;

/**
 * A simple, rectangular door with fixed height and orientation
 */
public class WingDoor implements Door {

    private final Orientation orientation;
    private final int baseHeight;
    private final int topHeight;
    private final BlockArea area = new BlockArea(BlockArea.INVALID);

    /**
     * @param orientation the orientation
     * @param area the door area (must be 2x1 or 1x2 sized)
     * @param baseHeight the height at the bottom
     * @param topHeight the height at the top
     */
    public WingDoor(Orientation orientation, BlockAreac area, int baseHeight, int topHeight) {
        this.orientation = orientation;
        this.area.set(area);
        this.baseHeight = baseHeight;
        this.topHeight = topHeight;
    }

    /**
     * @return the orientation
     */
    public Orientation getOrientation() {
        return this.orientation;
    }

    /**
     * @return the door area
     */
    public BlockAreac getArea() {
        return this.area;
    }

    /**
     * @return the baseHeight
     */
    public int getBaseHeight() {
        return this.baseHeight;
    }

    /**
     * @return the topHeight
     */
    public int getTopHeight() {
        return this.topHeight;
    }
}
