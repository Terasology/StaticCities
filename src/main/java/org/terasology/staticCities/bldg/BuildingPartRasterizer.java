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

package org.terasology.staticCities.bldg;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.raster.RasterTarget;

/**
 * @param <T> the target class
 */
public abstract class BuildingPartRasterizer<T extends BuildingPart> extends AbstractBuildingRasterizer<T> {

    /**
     * @param theme the block theme that is used to map type to blocks
     * @param targetClass the target class that is rasterized
     */
    protected BuildingPartRasterizer(BlockTheme theme, Class<T> targetClass) {
        super(theme, targetClass);
    }

    @Override
    public void raster(RasterTarget brush, Building bldg, HeightMap hm) {
        for (BuildingPart part : bldg.getParts()) {
            if (targetClass.isInstance(part)) {
                raster(brush, targetClass.cast(part), hm);
            }
        }
    }

    protected abstract void raster(RasterTarget brush, T part, HeightMap heightMap);
}

