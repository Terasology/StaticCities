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

package org.terasology.staticCities.sites;

import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Objects;

/**
 * Provides information on a settlement site.
  */
public class Site {

    private final Vector2i coords = new Vector2i();
    private float radius;

    /**
     * @param radius the city radius in blocks
     * @param bx the x world coord (in blocks)
     * @param bz the z world coord (in blocks)
     */
    public Site(int bx, int bz, float radius) {
        this.radius = radius;
        this.coords.set(bx, bz);
    }

    /**
     * @return the city center in block world coordinates
     */
    public Vector2ic getPos() {
        return coords;
    }

    /**
     * @return the radius of the settlements in blocks
     */
    public float getRadius() {
        return radius;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coords, radius);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && this.getClass() == obj.getClass()) {
            Site other = (Site) obj;
            return Objects.equals(coords, other.coords)
                && Objects.equals(radius, other.radius);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Site [" + coords + " (" + radius + ")]";
    }
}
