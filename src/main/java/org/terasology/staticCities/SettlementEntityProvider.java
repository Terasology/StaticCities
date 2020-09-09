// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities;

import org.terasology.engine.entitySystem.entity.EntityStore;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.logic.nameTags.NameTagComponent;
import org.terasology.engine.network.NetworkComponent;
import org.terasology.engine.world.generation.EntityBuffer;
import org.terasology.engine.world.generation.EntityProvider;
import org.terasology.engine.world.generation.Region;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.math.geom.Vector3f;
import org.terasology.nui.Color;
import org.terasology.staticCities.settlements.Settlement;
import org.terasology.staticCities.settlements.SettlementFacet;
import org.terasology.staticCities.surface.InfiniteSurfaceHeightFacet;

/**
 * Adds name tags for settlements.
 */
public class SettlementEntityProvider implements EntityProvider {

    @Override
    public void process(Region region, EntityBuffer buffer) {
        SettlementFacet settlementFacet = region.getFacet(SettlementFacet.class);
        InfiniteSurfaceHeightFacet heightFacet = region.getFacet(InfiniteSurfaceHeightFacet.class);

        for (Settlement settlement : settlementFacet.getSettlements()) {
            ImmutableVector2i pos2d = settlement.getSite().getPos();
            int x = pos2d.getX();
            int z = pos2d.getY();
            int y = TeraMath.floorToInt(heightFacet.getWorld(pos2d));
            if (region.getRegion().encompasses(x, y, z)) {

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
