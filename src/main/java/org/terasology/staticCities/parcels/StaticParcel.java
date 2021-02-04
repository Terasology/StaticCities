/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.staticCities.parcels;

import com.google.common.collect.Sets;
import org.terasology.cities.parcels.Parcel;
import org.terasology.commonworld.Orientation;
import org.terasology.staticCities.bldg.Building;
import org.terasology.world.block.BlockArea;
import org.terasology.world.block.BlockAreac;

import java.util.Collections;
import java.util.Set;

/**
 * A parcel where buildings can be placed on.
 */
public class StaticParcel implements Parcel {

    private final BlockArea shape = new BlockArea(BlockArea.INVALID);

    private final Set<Building> buildings = Sets.newHashSet();

    private final Orientation orientation;
    private final Zone zone;

    /**
     * @param shape the shape of the lot
     * @param zone the zone type
     * @param orientation the orientation of the parcel (e.g. towards the closest street)
     */
    protected StaticParcel(BlockAreac shape, Zone zone, Orientation orientation) {
        this.shape.set(shape);
        this.zone = zone;
        this.orientation = orientation;
    }

    /**
     * @return the layout shape
     */
    public BlockAreac getShape() {
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
