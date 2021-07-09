// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.staticCities.surface;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.commonworld.heightmap.NoiseHeightMap;
import org.terasology.commonworld.symmetry.Symmetries;
import org.terasology.commonworld.symmetry.Symmetry;
import org.terasology.engine.world.generation.ConfigurableFacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.nui.properties.OneOf.Enum;

@Produces(InfiniteSurfaceHeightFacet.class)
public class InfiniteSurfaceHeightFacetProvider implements ConfigurableFacetProvider {

    private HeightMap heightMap;
    private NoiseHeightMap noiseMap;
    private InfiniteSurfaceConfiguration configuration = new InfiniteSurfaceConfiguration();

    public InfiniteSurfaceHeightFacetProvider() {
        noiseMap = new NoiseHeightMap(0);
        setConfiguration(new InfiniteSurfaceConfiguration());
    }

    @Override
    public void setSeed(long seed) {
        noiseMap.setSeed(seed);
    }

    @Override
    public void process(GeneratingRegion region) {
        InfiniteSurfaceHeightFacet facet = new InfiniteSurfaceHeightFacet() {

            @Override
            public float getWorld(int worldX, int worldY) {
                return heightMap.apply(worldX, worldY);
            }

        };

        region.setRegionFacet(InfiniteSurfaceHeightFacet.class, facet);
    }

    @Override
    public String getConfigurationName() {
        return "Height Map";
    }

    @Override
    public Component getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Component configuration) {
        this.configuration = (InfiniteSurfaceConfiguration) configuration;
        Symmetry sym = this.configuration.symmetry.getInstance();
        if (sym != null) {
            heightMap = HeightMaps.symmetric(noiseMap, sym);
        } else {
            heightMap = noiseMap;
        }
    }

    private enum SymmetryType {
        NONE("None", null),
        X_AXIS("X-Axis", Symmetries.alongX()),
        Z_AXIS("Z-Axis", Symmetries.alongZ()),
        POS_DIAGONAL("Positive Diagonal", Symmetries.alongPositiveDiagonal()),
        NEG_DIAGONAL("Negative Diagonal", Symmetries.alongNegativeDiagonal());

        private final String name;
        private final Symmetry instance;

        SymmetryType(String name, Symmetry instance) {
            this.name = name;
            this.instance = instance;
        }

        @Override
        public String toString() {
            return name;
        }

        public Symmetry getInstance() {
            return instance;
        }
    }

    private static class InfiniteSurfaceConfiguration implements Component<InfiniteSurfaceConfiguration> {
        @Enum(label = "Symmetric World", description = "Check to create an axis-symmetric world")
        private SymmetryType symmetry = SymmetryType.NONE;
    }
}
