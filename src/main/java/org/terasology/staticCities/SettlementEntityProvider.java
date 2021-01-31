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

package org.terasology.staticCities;

import org.joml.Vector2ic;
import org.joml.Vector3f;
import org.terasology.entitySystem.entity.EntityStore;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.nameTags.NameTagComponent;
import org.terasology.math.TeraMath;
import org.terasology.network.NetworkComponent;
import org.terasology.nui.Color;
import org.terasology.staticCities.settlements.Settlement;
import org.terasology.staticCities.settlements.SettlementFacet;
import org.terasology.staticCities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.world.generation.EntityBuffer;
import org.terasology.world.generation.EntityProvider;
import org.terasology.world.generation.Region;

/**
 * Adds name tags for settlements.
 */
public class SettlementEntityProvider implements EntityProvider {

    @Override
    public void process(Region region, EntityBuffer buffer) {
        SettlementFacet settlementFacet = region.getFacet(SettlementFacet.class);
        InfiniteSurfaceHeightFacet heightFacet = region.getFacet(InfiniteSurfaceHeightFacet.class);

        for (Settlement settlement : settlementFacet.getSettlements()) {
            Vector2ic pos2d = settlement.getSite().getPos();
            int x = pos2d.x();
            int z = pos2d.y();
            int y = TeraMath.floorToInt(heightFacet.getWorld(pos2d));
            if (region.getRegion().contains(x, y, z)) {

                EntityStore entityStore = new EntityStore();

                NameTagComponent nameTagComponent = new NameTagComponent();
                nameTagComponent.text = settlement.getName();
                nameTagComponent.textColor = Color.WHITE;
                nameTagComponent.yOffset = 10;
                nameTagComponent.scale = 20;
                entityStore.addComponent(nameTagComponent);

                Vector3f pos3d = new Vector3f(x, y, z);
                LocationComponent locationComponent = new LocationComponent(pos3d);
                entityStore.addComponent(locationComponent);

                entityStore.addComponent(new NetworkComponent());
                entityStore.addComponent(new SettlementComponent(settlement));

                buffer.enqueue(entityStore);
            }
        }
   }

}
