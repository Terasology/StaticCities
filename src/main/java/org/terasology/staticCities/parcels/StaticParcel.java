// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.parcels;

import com.google.common.collect.Sets;
import org.terasology.cities.parcels.Parcel;
import org.terasology.commonworld.Orientation;
import org.terasology.math.geom.Rect2i;
import org.terasology.staticCities.bldg.Building;

import java.util.Collections;
import java.util.Set;

/**
 * A parcel where buildings can be placed on.
 */
public class StaticParcel implements Parcel {

    private final Rect2i shape;

    private final Set<Building> buildings = Sets.newHashSet();

    private final Orientation orientation;
    private final Zone zone;

    /**
     * @param shape the shape of the lot
     * @param zone the zone type
     * @param orientation the orientation of the parcel (e.g. towards the closest street)
     */
    protected StaticParcel(Rect2i shape, Zone zone, Orientation orientation) {
        this.shape = shape;
        this.zone = zone;
        this.orientation = orientation;
    }

    /**
     * @return the layout shape
     */
    public Rect2i getShape() {
        return this.shape;
    }

    /**
     * @return the orientation of the parcel
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * @return the zone type that was assigned to this parcel
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * @param bldg the building to add
     */
    public void addBuilding(Building bldg) {
        buildings.add(bldg);
    }

    /**
     * @return an unmodifiable view on all buildings in this lot
     */
    public Set<Building> getBuildings() {
        return Collections.unmodifiableSet(buildings);
    }

}
