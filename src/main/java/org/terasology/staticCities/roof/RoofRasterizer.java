/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.staticCities.roof;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.engine.world.chunks.CoreChunk;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizer;
import org.terasology.engine.world.generation.facets.ElevationFacet;
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
        ElevationFacet elevationFacet = chunkRegion.getFacet(ElevationFacet.class);
        HeightMap hm = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                return TeraMath.floorToInt(elevationFacet.getWorld(x, z));
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

