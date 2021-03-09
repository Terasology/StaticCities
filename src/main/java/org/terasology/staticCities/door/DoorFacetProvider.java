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

package org.terasology.staticCities.door;

import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.staticCities.bldg.BuildingFacet;

@Produces(DoorFacet.class)
public class DoorFacetProvider implements FacetProvider {

    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(BuildingFacet.class);
        DoorFacet facet = new DoorFacet(region.getRegion(), border);

        region.setRegionFacet(DoorFacet.class, facet);
    }
}

