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

package org.terasology.staticCities.fences;

import org.terasology.commonworld.Orientation;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;

/**
 * A rectangular fence
 */
public class SimpleFence implements Fence {

    private final Rect2i rect;
    private final Vector2i gate;
    private final Orientation gateOrient;

    /**
     * @param rect the fence outline area
     * @param gateOrient the gate's orientation
     * @param gate the gate position
     */
    public SimpleFence(Rect2i rect, Orientation gateOrient, Vector2i gate) {

        this.rect = rect;
        this.gateOrient = gateOrient;
        this.gate = gate;
    }

    /**
     * @return the rect
     */
    public Rect2i getRect() {
        return this.rect;
    }

    /**
     * @return the gate orientation
     */
    public Orientation getGateOrientation() {
        return this.gateOrient;
    }

    /**
     * @return the gate
     */
    public Vector2i getGate() {
        return this.gate;
    }
}
