// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.parcels;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import org.terasology.engine.world.generation.WorldFacet;
import org.terasology.staticCities.sites.Site;

import java.util.Collection;
import java.util.Collections;

/**
 *
 */
public class ParcelFacet implements WorldFacet {

    private final SetMultimap<Site, StaticParcel> parcels = LinkedHashMultimap.create();

    public void addParcel(Site settlement, StaticParcel staticParcel) {
        parcels.put(settlement, staticParcel);
    }

    public Collection<StaticParcel> getParcels() {
        return Collections.unmodifiableCollection(parcels.values());
    }

    public Collection<StaticParcel> getParcels(Site settlement) {
        return Collections.unmodifiableCollection(parcels.get(settlement));
    }
}
