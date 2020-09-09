// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.bldg.gen;

import org.terasology.staticCities.bldg.Building;
import org.terasology.staticCities.parcels.StaticParcel;
import org.terasology.staticCities.surface.InfiniteSurfaceHeightFacet;

import java.util.Set;

/**
 *
 */
public interface BuildingGenerator {

    Set<Building> generate(StaticParcel staticParcel, InfiniteSurfaceHeightFacet heightFacet);

}
