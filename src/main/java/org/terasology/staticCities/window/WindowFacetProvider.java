// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.window;

import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.staticCities.bldg.BuildingFacet;

@Produces(WindowFacet.class)
public class WindowFacetProvider implements FacetProvider {

    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(BuildingFacet.class);
        WindowFacet facet = new WindowFacet(region.getRegion(), border);

        region.setRegionFacet(WindowFacet.class, facet);
    }
}

