// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.sites;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFacet2D;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class SiteFacet extends BaseFacet2D {

    private final Set<Site> settlements = new HashSet<>();
    private final int uncertainBorder;

    public SiteFacet(Region3i targetRegion, Border3D border, int uncertainBorder) {
        super(targetRegion, border);
        this.uncertainBorder = uncertainBorder;
    }

    public Rect2i getCertainWorldRegion() {
        return getWorldRegion().expand(new Vector2i(-uncertainBorder, -uncertainBorder));
    }

    public void addSettlement(Site settlement) {
        settlements.add(settlement);
    }

    public Set<Site> getSettlements() {
        return Collections.unmodifiableSet(settlements);
    }

}
