// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.lakes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.terasology.commonworld.contour.Contour;

import java.util.Collections;
import java.util.Set;

/**
 * Defines a lake
 */
public class Lake {

    private final Contour contour;
    private final Set<Contour> islands = Sets.newHashSet();
    private final String name;

    /**
     * @param contour the contour of the lake (never <code>null</code>)
     * @param name the name of the lake
     */
    public Lake(Contour contour, String name) {
        Preconditions.checkArgument(contour != null, "Contour must not be null");

        this.contour = contour;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * @return the contour
     */
    public Contour getContour() {
        return this.contour;
    }

    /**
     * @param cont the island contour
     */
    public void addIsland(Contour cont) {
        islands.add(cont);
    }

    /**
     * @return the set of island contours
     */
    public Set<Contour> getIslandContours() {
        return Collections.unmodifiableSet(islands);
    }

    @Override
    public String toString() {
        return "Lake " + name + " [" + islands.size() + " islands]";
    }
}


