/*
 * Copyright 2014 MovingBlocks
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
package org.terasology.staticCities.surface;

import org.joml.Vector2ic;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.facets.ElevationFacet;

/**
 * Uses the infinite surface to store the "core" region of the facet in a 2D array.
 */
@Produces(ElevationFacet.class)
@Requires(@Facet(InfiniteSurfaceHeightFacet.class))
public class ElevationFacetProvider implements FacetProvider {

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(ElevationFacet.class);
        ElevationFacet facet = new ElevationFacet(region.getRegion(), border);
        InfiniteSurfaceHeightFacet infiniteFacet = region.getRegionFacet(InfiniteSurfaceHeightFacet.class);

        for (Vector2ic pos : facet.getWorldArea()) {
            facet.setWorld(pos, infiniteFacet.getWorld(pos.x(), pos.y()));
        }

        region.setRegionFacet(ElevationFacet.class, facet);
    }
}
