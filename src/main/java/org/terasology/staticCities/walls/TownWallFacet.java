// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.walls;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFacet2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A registry for town walls.
 */
public class TownWallFacet extends BaseFacet2D {

    private final Collection<TownWall> walls = new ArrayList<>();

    public TownWallFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public void addTownWall(TownWall area) {
        walls.add(area);
    }

    public Collection<TownWall> getTownWalls() {
        return Collections.unmodifiableCollection(walls);
    }
}
