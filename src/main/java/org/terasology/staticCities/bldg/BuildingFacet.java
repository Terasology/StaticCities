// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.bldg;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFacet2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A registry for buildings.
 */
public class BuildingFacet extends BaseFacet2D {

    private final Collection<Building> buildings = new ArrayList<>();

    public BuildingFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public void addBuilding(Building bldg) {
        buildings.add(bldg);
    }

    public Collection<Building> getBuildings() {
        return Collections.unmodifiableCollection(buildings);
    }
}
