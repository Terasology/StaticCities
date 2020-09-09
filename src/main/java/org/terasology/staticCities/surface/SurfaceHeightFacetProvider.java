// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.staticCities.surface;

import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.math.geom.BaseVector2i;

/**
 * Uses the infinite surface to store the "core" region of the facet in a 2D array.
 */
@Produces(SurfaceHeightFacet.class)
@Requires(@Facet(InfiniteSurfaceHeightFacet.class))
public class SurfaceHeightFacetProvider implements FacetProvider {

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(SurfaceHeightFacet.class);
        SurfaceHeightFacet facet = new SurfaceHeightFacet(region.getRegion(), border);
        InfiniteSurfaceHeightFacet infiniteFacet = region.getRegionFacet(InfiniteSurfaceHeightFacet.class);

        for (BaseVector2i pos : facet.getWorldRegion().contents()) {
            int x = pos.getX();
            int y = pos.getY();
            facet.setWorld(pos, infiniteFacet.getWorld(x, y));
        }

        region.setRegionFacet(SurfaceHeightFacet.class, facet);
    }
}
