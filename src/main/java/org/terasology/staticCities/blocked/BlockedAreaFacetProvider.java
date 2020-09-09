// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.blocked;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Rect2i;
import org.terasology.staticCities.sites.Site;
import org.terasology.staticCities.sites.SiteFacet;

/**
 * Produces an empty {@link BlockedAreaFacet}.
 */
@Produces(BlockedAreaFacet.class)
@Requires(@Facet(SiteFacet.class))
public class BlockedAreaFacetProvider implements FacetProvider {

    private final LoadingCache<Site, BlockedArea> cache = CacheBuilder.newBuilder().build(new CacheLoader<Site,
            BlockedArea>() {

        @Override
        public BlockedArea load(Site site) {
            Rect2i siteRect = getBoundingRect(site);
            return new BlockedArea(siteRect);
        }

    });

    private static Rect2i getBoundingRect(Site site) {
        int rad = TeraMath.ceilToInt(site.getRadius());
        int cx = site.getPos().getX();
        int cy = site.getPos().getY();
        return Rect2i.createFromMinAndMax(cx - rad, cy - rad, cx + rad, cy + rad);
    }

    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(BlockedAreaFacet.class);
        BlockedAreaFacet facet = new BlockedAreaFacet(region.getRegion(), border);

        SiteFacet siteFacet = region.getRegionFacet(SiteFacet.class);

        for (Site site : siteFacet.getSettlements()) {
            Rect2i siteRect = getBoundingRect(site);
            if (facet.getWorldRegion().overlaps(siteRect)) {
                BlockedArea area = cache.getUnchecked(site);
                facet.add(area);
            }
        }

        region.setRegionFacet(BlockedAreaFacet.class, facet);
    }

}
