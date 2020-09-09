// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.fences;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFacet2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A registry for fences elements.
 */
public class FenceFacet extends BaseFacet2D {

    private final Collection<SimpleFence> fences = new ArrayList<>();

    public FenceFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public void addFence(SimpleFence area) {
        fences.add(area);
    }

    public Collection<SimpleFence> getFences() {
        return Collections.unmodifiableCollection(fences);
    }
}
