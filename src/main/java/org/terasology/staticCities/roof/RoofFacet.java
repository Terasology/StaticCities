// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.roof;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFacet2D;
import org.terasology.staticCities.model.roof.Roof;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A registry for roofs.
 */
public class RoofFacet extends BaseFacet2D {

    private final Collection<Roof> roofs = new ArrayList<>();

    public RoofFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public void addRoof(Roof bldg) {
        roofs.add(bldg);
    }

    public Collection<Roof> getRoofs() {
        return Collections.unmodifiableCollection(roofs);
    }
}
