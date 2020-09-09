// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.staticCities.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldComponent;
import org.terasology.engine.world.chunks.event.PurgeWorldEvent;
import org.terasology.engine.world.generator.WorldGenerator;
import org.terasology.staticCities.CityWorldGenerator;

/**
 * Receives events for {@link WorldComponent} and delegates the "purgeWorld" command to the {@link CityWorldGenerator}
 * class.
 */
@RegisterSystem
public class WorldEventReceiver extends BaseComponentSystem {

    @In
    private WorldGenerator worldGenerator;

    /**
     * @param event the event
     * @param worldEntity the world entity (empty)
     */
    @ReceiveEvent(components = {WorldComponent.class})
    public void onPurgeWorld(PurgeWorldEvent event, EntityRef worldEntity) {
        if (worldGenerator instanceof CityWorldGenerator) {
            ((CityWorldGenerator) worldGenerator).expungeCaches();
        }
    }
}
