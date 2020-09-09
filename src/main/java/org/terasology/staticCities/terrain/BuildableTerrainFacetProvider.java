// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.terrain;

import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;
import org.terasology.staticCities.surface.InfiniteSurfaceHeightFacet;

/**
 *
 */
@Produces(BuildableTerrainFacet.class)
@Requires({
        @Facet(InfiniteSurfaceHeightFacet.class),
        @Facet(SeaLevelFacet.class)
})
public class BuildableTerrainFacetProvider implements FacetProvider {

    @Override
    public void process(GeneratingRegion region) {
        InfiniteSurfaceHeightFacet surfaceHeightFacet = region.getRegionFacet(InfiniteSurfaceHeightFacet.class);
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);

        BuildableTerrainFacet facet = new BuildableTerrainFacet() {

            @Override
            public boolean isBuildable(int worldX, int worldY) {
                float height = surfaceHeightFacet.getWorld(worldX, worldY);
                // at least 3 blocks above sea level
                return !(height < seaLevelFacet.getSeaLevel() + 3);
            }

            @Override
            public boolean isPassable(int worldX, int worldY) {
                float height = surfaceHeightFacet.getWorld(worldX, worldY);
                // at least above sea level
                return !(height <= seaLevelFacet.getSeaLevel());
            }

        };

        region.setRegionFacet(BuildableTerrainFacet.class, facet);
    }

}
