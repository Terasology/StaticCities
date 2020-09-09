// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities;

import org.terasology.engine.entitySystem.entity.EntityStore;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.utilities.Assets;
import org.terasology.engine.world.generation.EntityBuffer;
import org.terasology.engine.world.generation.EntityProvider;
import org.terasology.engine.world.generation.Region;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Vector3f;
import org.terasology.staticCities.roads.Road;
import org.terasology.staticCities.roads.RoadFacet;
import org.terasology.staticCities.surface.InfiniteSurfaceHeightFacet;

import java.util.Optional;

/**
 * Adds a quest, annotated with a spinning question mark icon.
 */
public class QuestEntityProvider implements EntityProvider {

    @Override
    public void process(Region region, EntityBuffer buffer) {
        RoadFacet roadFacet = region.getFacet(RoadFacet.class);
        InfiniteSurfaceHeightFacet heightFacet = region.getFacet(InfiniteSurfaceHeightFacet.class);

        Optional<Prefab> optPrefab = Assets.getPrefab("QuestionMark");
        if (!optPrefab.isPresent()) {
            return;
        }

        Prefab prefab = optPrefab.get();
        if (!prefab.hasComponent(LocationComponent.class)) {
            return;
        }

        for (Road road : roadFacet.getRoads()) {
            for (BaseVector2i pt : road.getPoints()) {
                float y = heightFacet.getWorld(pt) + 5;
                if (region.getRegion().encompasses(pt.getX(), (int) y, pt.getY())) {
                    Vector3f position = new Vector3f(pt.getX(), y, pt.getY());
                    EntityStore builder = new EntityStore(prefab);
                    builder.addComponent(new LocationComponent(position));
                    buffer.enqueue(builder);
                }
            }
        }
    }

}
