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

package org.terasology.staticCities.walls;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.ImmutableVector2i;

/**
 * A straight wall segment
  */
public abstract class WallSegment {

    private final Vector2i start = new Vector2i();
    private final Vector2i end = new Vector2i();

    private int wallThickness;

    /**
     * @param start one end of the wall
     * @param end the other end of the wall
     * @param wallThickness the wall thickness in block
     */
    public WallSegment(Vector2ic start, Vector2ic end, int wallThickness) {
        this.start.set(start);
        this.end.set(end);
        this.wallThickness = wallThickness;
    }

    /**
     * @return one end of the wall
     */
    public Vector2ic getStart() {
        return this.start;
    }

    /**
     * @return the other end of the wall
     */
    public Vector2ic getEnd() {
        return this.end;
    }

    /**
     * @return the wallThickness
     */
    public int getWallThickness() {
        return this.wallThickness;
    }

    /**
     * @return true if the segment is passable
     */
    public abstract boolean isGate();
}
