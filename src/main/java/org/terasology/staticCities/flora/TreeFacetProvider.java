// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.flora;

import com.google.common.base.Predicate;
import org.terasology.coreworlds.generator.facetProviders.DefaultTreeProvider;
import org.terasology.coreworlds.generator.facets.BiomeFacet;
import org.terasology.coreworlds.generator.facets.TreeFacet;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetBorder;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.math.geom.LineSegment;
import org.terasology.math.geom.Vector3i;
import org.terasology.staticCities.roads.Road;
import org.terasology.staticCities.roads.RoadFacet;
import org.terasology.staticCities.roads.RoadSegment;
import org.terasology.staticCities.settlements.Settlement;
import org.terasology.staticCities.settlements.SettlementFacet;
import org.terasology.staticCities.sites.Site;

import java.util.List;
import java.util.Set;

@Produces(TreeFacet.class)
@Requires({
        @Facet(value = SeaLevelFacet.class, border = @FacetBorder(sides = 13)),
        @Facet(value = SurfaceHeightFacet.class, border = @FacetBorder(sides = 13 + 1)),
        @Facet(value = BiomeFacet.class, border = @FacetBorder(sides = 13)),
        @Facet(SettlementFacet.class),
        @Facet(RoadFacet.class)
})
public class TreeFacetProvider extends DefaultTreeProvider {

    private static boolean outsideRoads(Vector3i v, Set<Road> roads) {
        int vx = v.getX();
        int vz = v.getZ();
        float minDist = 5f;   // block distance to road border
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

    private static boolean outsideSettlements(Vector3i v, Set<Settlement> settlements) {
        for (Settlement settlement : settlements) {
            Site site = settlement.getSite();
            ImmutableVector2i center = site.getPos();
            float r = site.getRadius();
            if (distanceSquared(center, v.getX(), v.getZ()) < r * r) {
                return false;
            }
        }
        return true;
    }

    private static float distanceSquared(BaseVector2i v, int x, int y) {
        int dx = x - v.getX();
        int dy = y - v.getY();

        return dx * dx + dy * dy;
    }

    @Override
    public List<Predicate<Vector3i>> getFilters(GeneratingRegion region) {
        List<Predicate<Vector3i>> filters = super.getFilters(region);

        SettlementFacet settlementFacet = region.getRegionFacet(SettlementFacet.class);
        filters.add(v -> outsideSettlements(v, settlementFacet.getSettlements()));

        RoadFacet roadFacet = region.getRegionFacet(RoadFacet.class);
        filters.add(v -> outsideRoads(v, roadFacet.getRoads()));

        return filters;
    }

}

