// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.events.OnEnterBlockEvent;
import org.terasology.engine.logic.console.Console;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.network.Client;
import org.terasology.engine.network.NetworkSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldComponent;
import org.terasology.engine.world.chunks.event.PurgeWorldEvent;
import org.terasology.math.geom.Circle;
import org.terasology.math.geom.Vector2f;
import org.terasology.math.geom.Vector3f;
import org.terasology.nui.FontColor;
import org.terasology.staticCities.SettlementComponent;
import org.terasology.staticCities.settlements.Settlement;
import org.terasology.staticCities.sites.Site;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Tracks player movements with respect to {@link Settlement} entities.
 */
@RegisterSystem
public class PlayerTracker extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(PlayerTracker.class);
    private final Set<Settlement> knownSettlements = new LinkedHashSet<>();
    private final Map<String, Settlement> prevLoc = new HashMap<>();
    @In
    private NetworkSystem networkSystem;
    @In
    private Console console;

    @ReceiveEvent(components = {SettlementComponent.class})
    public void onActivated(OnActivatedComponent event, EntityRef entity, SettlementComponent comp) {
        knownSettlements.add(comp.settlement);
    }

    /**
     * Called whenever a block is entered
     *
     * @param event the event
     * @param entity the character entity reference "player:engine"
     */
    @ReceiveEvent
    public void onEnterBlock(OnEnterBlockEvent event, EntityRef entity) {
        LocationComponent loc = entity.getComponent(LocationComponent.class);
        Vector3f worldPos3d = loc.getWorldPosition();
        Vector2f worldPos = new Vector2f(worldPos3d.x, worldPos3d.z);

        Client client = networkSystem.getOwner(entity);

        // TODO: entity can be AI-controlled, too. These don't have an owner
        if (client == null) {
            return;
        }

        String id = client.getId();
        String name = client.getName();

        Settlement newArea = null;
        for (Settlement area : knownSettlements) {
            Site site = area.getSite();
            Circle circle = new Circle(site.getPos().x(), site.getPos().y(), site.getRadius());
            if (circle.contains(worldPos)) {
                if (newArea != null) {
                    logger.warn("{} appears to be in {} and {} at the same time!", name, newArea.getName(),
                            area.getName());
                }

                newArea = area;
            }
        }

        if (!Objects.equals(newArea, prevLoc.get(id))) {       // both can be null
            if (newArea != null) {
                entity.send(new OnEnterSettlementEvent(newArea));
            }
            Settlement prevArea = prevLoc.put(id, newArea);
            if (prevArea != null) {
                entity.send(new OnLeaveSettlementEvent(prevArea));
            }
        }
    }

    /**
     * Called whenever a named area is entered
     *
     * @param event the event
     * @param entity the character entity reference "player:engine"
     */
    @ReceiveEvent
    public void onEnterArea(OnEnterSettlementEvent event, EntityRef entity) {

        Client client = networkSystem.getOwner(entity);
        String playerName = String.format("%s (%s)", client.getName(), client.getId());
        String areaName = event.getSettlement().getName();

        playerName = FontColor.getColored(playerName, CitiesColors.PLAYER);
        areaName = FontColor.getColored(areaName, CitiesColors.AREA);

        console.addMessage(playerName + " entered " + areaName);
    }

    /**
     * Called whenever a named area is entered
     *
     * @param event the event
     * @param entity the character entity reference "player:engine"
     */
    @ReceiveEvent
    public void onLeaveArea(OnLeaveSettlementEvent event, EntityRef entity) {

        Client client = networkSystem.getOwner(entity);
        String playerName = String.format("%s (%s)", client.getName(), client.getId());
        String areaName = event.getSettlement().getName();

        playerName = FontColor.getColored(playerName, CitiesColors.PLAYER);
        areaName = FontColor.getColored(areaName, CitiesColors.AREA);

        console.addMessage(playerName + " left " + areaName);
    }

    @ReceiveEvent(components = {WorldComponent.class})
    public void onPurgeWorld(PurgeWorldEvent event, EntityRef worldEntity) {
        knownSettlements.clear();
        prevLoc.clear();
    }
}
