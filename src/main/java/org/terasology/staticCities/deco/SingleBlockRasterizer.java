/*
 * Copyright 2015 MovingBlocks
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

package org.terasology.staticCities.deco;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.math.JomlUtil;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.raster.RasterTarget;

import java.util.Collections;

/**
 * Converts {@link SingleBlockDecoration} into blocks
 */
public class SingleBlockRasterizer extends DecorationRasterizer<SingleBlockDecoration> {

    /**
     * @param theme the block theme to use
     */
    public SingleBlockRasterizer(BlockTheme theme) {
        super(theme, SingleBlockDecoration.class);
    }

    @Override
    public void raster(RasterTarget target, SingleBlockDecoration deco, HeightMap hm) {
        if (target.getAffectedRegion().contains(JomlUtil.from(deco.getPos()))) {
            target.setBlock(deco.getPos(), deco.getType(), Collections.singleton(deco.getSide()));
        }
    }

}
