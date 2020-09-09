// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.settlements;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFacet2D;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class SettlementFacet extends BaseFacet2D {

    private final Set<Settlement> settlements = new HashSet<>();

    public SettlementFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public void addSettlement(Settlement settlement) {
        settlements.add(settlement);
    }

    public Set<Settlement> getSettlements() {
        return Collections.unmodifiableSet(settlements);
    }

}
