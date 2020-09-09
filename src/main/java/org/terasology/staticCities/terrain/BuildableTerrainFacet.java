// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.terrain;

import org.terasology.engine.world.generation.WorldFacet;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;

/**
 *
 */
public interface BuildableTerrainFacet extends WorldFacet {

    default boolean isBuildable(BaseVector2i worldPos) {
        return isBuildable(worldPos.getX(), worldPos.getY());
    }

    boolean isBuildable(int worldX, int worldY);

    default boolean isBuildable(Rect2i rect) {
        // approximate by checking corners only
        // TODO: find a better solution
        return isBuildable(rect.minX(), rect.minY())
                && isBuildable(rect.minX(), rect.maxY())
                && isBuildable(rect.maxX(), rect.minY())
                && isBuildable(rect.maxX(), rect.maxY());
    }

    default boolean isPassable(BaseVector2i worldPos) {
        return isPassable(worldPos.getX(), worldPos.getY());
    }

    boolean isPassable(int worldX, int worldY);

    default boolean isPassable(Rect2i rect) {
        // approximate by checking corners only
        // TODO: find a better solution
        return isPassable(rect.minX(), rect.minY())
                && isPassable(rect.minX(), rect.maxY())
                && isPassable(rect.maxX(), rect.minY())
                && isPassable(rect.maxX(), rect.maxY());
    }
}
