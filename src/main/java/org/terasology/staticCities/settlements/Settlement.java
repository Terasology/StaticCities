// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.settlements;

import org.terasology.staticCities.roads.Road;
import org.terasology.staticCities.sites.Site;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Provides information on a settlement.
 */
public class Settlement {

    private final Site site;
    private final Collection<Road> roads = new ArrayList<>();
    private final String name;

    /**
     * @param site the site of the settlement
     * @param name the name of the settlement
     */
    public Settlement(Site site, String name) {
        this.site = site;
        this.name = name;
    }

    /**
     * @return the site of the settlement
     */
    public Site getSite() {
        return site;
    }

    public float getSettlementRadius() {
        return (hasTownwall() ? 0.9f : 1.0f) * site.getRadius();
    }

    /**
     * @return
     */
    public boolean hasTownwall() {
        return site.getRadius() > 150;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " (" + site.getPos() + ")";
    }

    /**
     * @param road the road to add
     */
    public void addRoad(Road road) {
        roads.add(road);
    }
}
