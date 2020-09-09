// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.flora;

import com.google.common.base.Predicate;
import org.terasology.coreworlds.generator.facetProviders.DefaultFloraProvider;
import org.terasology.coreworlds.generator.facets.BiomeFacet;
import org.terasology.coreworlds.generator.facets.FloraFacet;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.LineSegment;
import org.terasology.math.geom.Vector3i;
import org.terasology.staticCities.blocked.BlockedAreaFacet;
import org.terasology.staticCities.roads.Road;
import org.terasology.staticCities.roads.RoadFacet;
import org.terasology.staticCities.roads.RoadSegment;

import java.util.List;
import java.util.Set;

@Produces(FloraFacet.class)
@Requires({
        @Facet(SeaLevelFacet.class),
        @Facet(SurfaceHeightFacet.class),
        @Facet(BiomeFacet.class),
        @Facet(BlockedAreaFacet.class),
        @Facet(RoadFacet.class)
})
public class FloraFacetProvider extends DefaultFloraProvider {

    private static boolean outsideRoads(Vector3i v, Set<Road> roads) {
        int vx = v.getX();
        int vz = v.getZ();
        float minDist = 0f;   // block distance to road border
        for (Road road : roads) {
            for (RoadSegment seg : road.getSegments()) {
                BaseVector2i a = seg.getStart();
                BaseVector2i b = seg.getEnd();
                float rad = seg.getWidth() * 0.5f + minDist;
                if (LineSegment.distanceToPoint(a.getX(), a.getY(), b.getX(), b.getY(), vx, vz) < rad) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<Predicate<Vector3i>> getFilters(GeneratingRegion region) {
        List<Predicate<Vector3i>> filters = super.getFilters(region);

        BlockedAreaFacet blockedAreaFacet = region.getRegionFacet(BlockedAreaFacet.class);
        filters.add(v -> !blockedAreaFacet.isBlocked(v.getX(), v.getZ()));

        RoadFacet roadFacet = region.getRegionFacet(RoadFacet.class);
        filters.add(v -> outsideRoads(v, roadFacet.getRoads()));

        return filters;
    }
}

