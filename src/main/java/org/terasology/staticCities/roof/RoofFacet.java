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

package org.terasology.staticCities.roof;

import org.terasology.engine.world.block.BlockRegionc;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFacet2D;
import org.terasology.staticCities.model.roof.Roof;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A registry for roofs.
 */
public class RoofFacet extends BaseFacet2D {

    private final Collection<Roof> roofs = new ArrayList<>();

    public RoofFacet(BlockRegionc targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public void addRoof(Roof bldg) {
        roofs.add(bldg);
    }

    public Collection<Roof> getRoofs() {
        return Collections.unmodifiableCollection(roofs);
    }
}
