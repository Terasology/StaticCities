// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.sites;

import org.terasology.math.geom.ImmutableVector2i;

import java.util.Objects;

/**
 * Provides information on a settlement site.
 */
public class Site {

    private final ImmutableVector2i coords;
    private final float radius;

    /**
     * @param radius the city radius in blocks
     * @param bx the x world coord (in blocks)
     * @param bz the z world coord (in blocks)
     */
    public Site(int bx, int bz, float radius) {
        this.radius = radius;
        this.coords = new ImmutableVector2i(bx, bz);
    }

    /**
     * @return the city center in block world coordinates
     */
    public ImmutableVector2i getPos() {
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
