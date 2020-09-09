// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.roads;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFacet2D;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class RoadFacet extends BaseFacet2D {

    private final Set<Road> roads = new HashSet<>();

    public RoadFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public void addRoad(Road road) {
        roads.add(road);
    }

    public Set<Road> getRoads() {
        return Collections.unmodifiableSet(roads);
    }

}
