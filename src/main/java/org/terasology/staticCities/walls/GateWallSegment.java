// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.walls;

import org.joml.Vector2ic;

/**
 * A straight wall segment with a gate
 */
public class GateWallSegment extends WallSegment {

    private int wallHeight;

    /**
     * @param start one end of the wall
     * @param end the other end of the wall
     * @param wallThickness the wall thickness in block
     * @param wallHeight the wall height in blocks above terrain
     */
    public GateWallSegment(Vector2ic start, Vector2ic end, int wallThickness, int wallHeight) {
        super(start, end, wallThickness);
        this.wallHeight = wallHeight;
    }

    /**
     * @return the wall height in blocks above terrain
     */
    public int getWallHeight() {
        return this.wallHeight;
    }

    @Override
    public boolean isGate() {
        return true;
    }
}
