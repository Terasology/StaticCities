/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.staticCities.flora;

import com.google.common.base.Predicate;
import org.joml.Vector2ic;
import org.joml.Vector3i;
import org.terasology.commonworld.geom.Line2f;
import org.terasology.core.world.generator.facetProviders.DefaultTreeProvider;
import org.terasology.core.world.generator.facets.BiomeFacet;
import org.terasology.core.world.generator.facets.TreeFacet;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetBorder;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;
import org.terasology.engine.world.generation.facets.SurfacesFacet;
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
    @Facet(value = SurfacesFacet.class, border = @FacetBorder(sides = 13 + 1, bottom = 1)),
    @Facet(value = BiomeFacet.class, border = @FacetBorder(sides = 13)),
    @Facet(SettlementFacet.class),
    @Facet(RoadFacet.class)
})
public class TreeFacetProvider extends DefaultTreeProvider {

    @Override
    public List<Predicate<Vector3i>> getFilters(GeneratingRegion region) {
        List<Predicate<Vector3i>> filters = super.getFilters(region);

        SettlementFacet settlementFacet = region.getRegionFacet(SettlementFacet.class);
        filters.add(v -> outsideSettlements(v, settlementFacet.getSettlements()));

        RoadFacet roadFacet = region.getRegionFacet(RoadFacet.class);
        filters.add(v -> outsideRoads(v, roadFacet.getRoads()));

        return filters;
    }

    private static boolean outsideRoads(Vector3i v, Set<Road> roads) {
        int vx = v.x();
        int vz = v.z();
        float minDist = 5f;   // block distance to road border
        for (Road road : roads) {
            for (RoadSegment seg : road.getSegments()) {
                Vector2ic a = seg.getStart();
                Vector2ic b = seg.getEnd();
                float rad = seg.getWidth() * 0.5f + minDist;
                if (Line2f.distanceToPoint(a.x(), a.y(), b.x(), b.y(), vx, vz) < rad) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean outsideSettlements(Vector3i v, Set<Settlement> settlements) {
        for (Settlement settlement : settlements) {
            Site site = settlement.getSite();
            Vector2ic center = site.getPos();
            float r = site.getRadius();
            if (distanceSquared(center, v.x(), v.z()) < r * r) {
                return false;
            }
        }
        return true;
    }

    private static float distanceSquared(Vector2ic v, int x, int y) {
        int dx = x - v.x();
        int dy = y - v.y();

        return dx * dx + dy * dy;
    }

}

