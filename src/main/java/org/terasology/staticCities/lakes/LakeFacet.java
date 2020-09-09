// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.lakes;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFacet2D;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 */
public class LakeFacet extends BaseFacet2D {

    private final Set<Lake> lakes = new LinkedHashSet<>();

    public LakeFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    /**
     * @param lake
     */
    public void add(Lake lake) {
        lakes.add(lake);
    }

    /**
     * @return the lakes
     */
    public Set<Lake> getLakes() {
        return Collections.unmodifiableSet(lakes);
    }
}
