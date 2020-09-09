// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.roof;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.engine.world.chunks.CoreChunk;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizer;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.math.TeraMath;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.model.roof.Roof;
import org.terasology.staticCities.raster.ChunkRasterTarget;
import org.terasology.staticCities.raster.RasterTarget;

/**
 * @param <T> the target class
 */
public abstract class RoofRasterizer<T extends Roof> implements WorldRasterizer {

    private final BlockTheme theme;
    private final Class<T> targetClass;

    /**
     * @param theme the block theme that is used to map type to blocks
     * @param targetClass the target class that is rasterized
     */
    protected RoofRasterizer(BlockTheme theme, Class<T> targetClass) {
        this.theme = theme;
        this.targetClass = targetClass;
    }

    @Override
    public void initialize() {
        // nothing to do
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        SurfaceHeightFacet heightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        HeightMap hm = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                return TeraMath.floorToInt(heightFacet.getWorld(x, z));
            }
        };
        RasterTarget brush = new ChunkRasterTarget(chunk, theme);
        RoofFacet buildingFacet = chunkRegion.getFacet(RoofFacet.class);
        for (Roof roof : buildingFacet.getRoofs()) {
            if (targetClass.isInstance(roof)) {
                raster(brush, targetClass.cast(roof), hm);
            }
        }
    }

    public void tryRaster(RasterTarget brush, Roof roof, HeightMap heightMap) {
        if (targetClass.isInstance(roof)) {
            raster(brush, targetClass.cast(roof), heightMap);
        }
    }

    protected abstract void raster(RasterTarget brush, T part, HeightMap heightMap);
}

