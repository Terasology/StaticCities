// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.staticCities;

import org.terasology.coreworlds.CoreBiome;
import org.terasology.coreworlds.generator.facets.BiomeFacet;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.engine.world.generation.facets.SurfaceHumidityFacet;
import org.terasology.math.geom.BaseVector2i;

/**
 * Determines the biome based on temperature and humidity
 */
@Produces(BiomeFacet.class)
@Requires({
        @Facet(SeaLevelFacet.class),
        @Facet(SurfaceHeightFacet.class),
        @Facet(SurfaceHumidityFacet.class)
})
public class SimpleBiomeProvider implements FacetProvider {

    @Override
    public void setSeed(long seed) {
    }

    @Override
    public void process(GeneratingRegion region) {
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);
        SurfaceHeightFacet heightFacet = region.getRegionFacet(SurfaceHeightFacet.class);
        SurfaceHumidityFacet humidityFacet = region.getRegionFacet(SurfaceHumidityFacet.class);

        Border3D border = region.getBorderForFacet(BiomeFacet.class);
        BiomeFacet biomeFacet = new BiomeFacet(region.getRegion(), border);

        int seaLevel = seaLevelFacet.getSeaLevel();

        for (BaseVector2i pos : biomeFacet.getRelativeRegion().contents()) {
            float height = heightFacet.get(pos);

            CoreBiome biome;
            if (height <= seaLevel) {
                biome = CoreBiome.OCEAN;
            } else if (height <= seaLevel + 2) {
                biome = CoreBiome.BEACH;
            } else if (height <= seaLevel + 30) {
                float humidity = humidityFacet.get(pos);
                biome = (humidity <= 0.66f) ? CoreBiome.PLAINS : CoreBiome.FOREST;
            } else if (height <= seaLevel + 40) {
                biome = CoreBiome.MOUNTAINS;
            } else {
                biome = CoreBiome.SNOW;
            }

            biomeFacet.set(pos, biome);
        }
        region.setRegionFacet(BiomeFacet.class, biomeFacet);
    }
}
