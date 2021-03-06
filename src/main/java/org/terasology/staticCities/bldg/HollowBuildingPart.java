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

package org.terasology.staticCities.bldg;

import org.terasology.cities.bldg.shape.RectangularBase;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.staticCities.model.roof.Roof;

/**
 *
 */
public class HollowBuildingPart extends AbstractBuildingPart implements RectangularBase {

    private int arcRadius;
    private BlockArea layout = new BlockArea(BlockArea.INVALID);

    public HollowBuildingPart(BlockAreac layout, Roof roof, int baseHeight, int wallHeight, int arcRadius) {
        super(roof, baseHeight, wallHeight);
        this.layout.set(layout);
        this.arcRadius = arcRadius;
    }

    @Override
    public BlockAreac getShape() {
        return layout;
    }

    public int getArcRadius() {
        return arcRadius;
    }
}
