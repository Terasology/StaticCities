// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.bldg;

import org.terasology.math.geom.Shape;
import org.terasology.staticCities.deco.Decoration;
import org.terasology.staticCities.door.Door;
import org.terasology.staticCities.model.roof.Roof;
import org.terasology.staticCities.window.Window;

import java.util.Set;

/**
 * Defines a part of a building. This is similar to an entire building.
 */
public interface BuildingPart {

    /**
     * @return the building layout
     */
    Shape getShape();

    Roof getRoof();

    int getWallHeight();

    int getBaseHeight();

    /**
     * @return baseHeight + wallHeight;
     */
    default int getTopHeight() {
        return getBaseHeight() + getWallHeight();
    }

    Set<Window> getWindows();

    Set<Door> getDoors();

    Set<Decoration> getDecorations();
}
