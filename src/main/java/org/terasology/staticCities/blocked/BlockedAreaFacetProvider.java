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

package org.terasology.staticCities.blocked;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Rect2i;
import org.terasology.staticCities.sites.Site;
import org.terasology.staticCities.sites.SiteFacet;
import org.terasology.world.block.BlockArea;
import org.terasology.world.block.BlockAreac;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;

/**
 * Produces an empty {@link BlockedAreaFacet}.
 */
@Produces(BlockedAreaFacet.class)
@Requires(@Facet(SiteFacet.class))
public class BlockedAreaFacetProvider implements FacetProvider {

    private final LoadingCache<Site, BlockedArea> cache = CacheBuilder.newBuilder().build(new CacheLoader<Site, BlockedArea>() {

        @Override
        public BlockedArea load(Site site) {
            BlockArea siteRect = getBoundingRect(site);
            return new BlockedArea(siteRect);
        }

    });

    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(BlockedAreaFacet.class);
        BlockedAreaFacet facet = new BlockedAreaFacet(region.getRegion(), border);

        SiteFacet siteFacet = region.getRegionFacet(SiteFacet.class);

        for (Site site : siteFacet.getSettlements()) {
            BlockArea siteRect = getBoundingRect(site);
            if (facet.getWorldArea().intersectsBlockArea(new BlockArea(siteRect.minX(), siteRect.minY(), siteRect.maxX(), siteRect.maxY()))) {
                BlockedArea area = cache.getUnchecked(site);
                facet.add(area);
            }
        }

        region.setRegionFacet(BlockedAreaFacet.class, facet);
    }

    private static BlockArea getBoundingRect(Site site) {
        int rad = TeraMath.ceilToInt(site.getRadius());
        int cx = site.getPos().x();
        int cy = site.getPos().y();
        return new BlockArea(cx - rad, cy - rad, cx + rad, cy + rad);
    }

}
