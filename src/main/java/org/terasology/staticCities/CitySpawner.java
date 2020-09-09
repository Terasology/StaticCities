// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.logic.spawner.AbstractSpawner;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.World;
import org.terasology.math.geom.Vector2i;
import org.terasology.math.geom.Vector3f;
import org.terasology.staticCities.sites.Site;
import org.terasology.staticCities.sites.SiteFacet;

import java.util.Collections;

/**
 * Spawns entities at the center of the closest settlement. Requires a {@link SiteFacet} provider.
 */
public class CitySpawner extends AbstractSpawner {

    private static final int SEARCH_RADIUS = 16;

    @Override
    public Vector3f getSpawnPosition(World world, EntityRef entity) {
        LocationComponent location = entity.getComponent(LocationComponent.class);
        Vector3f pos = location.getWorldPosition();
        Vector2i pos2d = new Vector2i(pos.x, pos.z);
        Region3i region = Region3i.createFromCenterExtents(pos, new Vector3f(32, 1, 32));
        Region data = world.getWorldData(region);
        SiteFacet settlementFacet = data.getFacet(SiteFacet.class);
        Vector2i searchPos;
        if (!settlementFacet.getSettlements().isEmpty()) {
            Site closest = Collections.min(settlementFacet.getSettlements(),
                    (a, b) -> a.getPos().distanceSquared(pos2d) - b.getPos().distanceSquared(pos2d));

            searchPos = new Vector2i(closest.getPos()); // TODO: replace as soon as findSpawnPos takes BaseVector2i
        } else {
            searchPos = pos2d;
        }
        Vector3f realPos = findSpawnPosition(world, searchPos, SEARCH_RADIUS);
        return realPos;
    }

}
