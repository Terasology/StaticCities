// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities;

import org.joml.Vector2ic;
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

                LocationComponent locationComponent = new LocationComponent(new org.joml.Vector3f(x, y, z));
                entityStore.addComponent(locationComponent);

                entityStore.addComponent(new NetworkComponent());
                entityStore.addComponent(new SettlementComponent(settlement));

                buffer.enqueue(entityStore);
            }
        }
   }

}
