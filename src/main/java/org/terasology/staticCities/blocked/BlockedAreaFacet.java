// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.blocked;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFacet2D;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.math.geom.Rect2i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * An image-based registry for blocked areas.
 */
public class BlockedAreaFacet extends BaseFacet2D {

    private final Collection<BlockedArea> areas = new ArrayList<>();

    public BlockedAreaFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public void add(BlockedArea area) {
        areas.add(area);
    }

    Collection<BlockedArea> getAreas() {
        return Collections.unmodifiableCollection(areas);
    }

    /**
     * @param wx the world x coordinate
     * @param wy the world y coordinate
     * @return true if blocked, false is not or unknown
     */
    public boolean isBlocked(int wx, int wy) {
        for (BlockedArea area : areas) {
            if (area.getWorldRegion().contains(wx, wy)) {
                return area.isBlocked(wx, wy);
            }
        }
        return false;
    }


    public boolean isBlocked(Rect2i shape) {
        for (BlockedArea area : areas) {
            if (area.getWorldRegion().overlaps(shape)) {
                if (area.isBlocked(shape)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addLine(ImmutableVector2i start, ImmutableVector2i end, float width) {
        // TODO: maybe check for line/rect intersection with this FACET first
        for (BlockedArea area : areas) {
            area.addLine(start, end, width);
        }
    }

    public void addRect(Rect2i rc) {
        for (BlockedArea area : areas) {
            area.addRect(rc);
        }
    }

}
