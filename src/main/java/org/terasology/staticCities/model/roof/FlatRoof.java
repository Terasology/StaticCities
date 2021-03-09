// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.model.roof;

import org.terasology.engine.world.block.BlockAreac;

/**
 * A flat roof with an extruded border (terrace roof)
 */
public class FlatRoof extends RectangularRoof {

    private final int borderHeight;

    /**
     * @param baseRect the building rectangle (must be fully inside <code>withEaves</code>).
     * @param withEaves the roof area including eaves (=overhang)
     * @param baseHeight the base height of the roof
     * @param borderHeight the height of the border
     */
    public FlatRoof(BlockAreac baseRect, BlockAreac withEaves, int baseHeight, int borderHeight) {
        super(baseRect, withEaves, baseHeight);

        this.borderHeight = borderHeight;
    }

    /**
     * @param lx x in local (roof area) coordinates
     * @param lz z in local (roof area) coordinates
     * @return the borderHeight
     */
    public int getBorderHeight(int lx, int lz) {
        return borderHeight;
    }
}
