// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.fences;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.terasology.commonworld.Orientation;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.staticCities.common.Edges;
import org.terasology.staticCities.parcels.ParcelFacet;
import org.terasology.staticCities.parcels.StaticParcel;
import org.terasology.staticCities.parcels.Zone;

import java.util.Optional;

/**
 * Produces an empty {@link FenceFacet}.
 */
@Produces(FenceFacet.class)
@Requires(@Facet(ParcelFacet.class))
public class FenceFacetProvider implements FacetProvider {

    private final LoadingCache<StaticParcel, Optional<SimpleFence>> cache = CacheBuilder.newBuilder().build(
            new CacheLoader<StaticParcel, Optional<SimpleFence>>() {

                @Override
                public Optional<SimpleFence> load(StaticParcel staticParcel) {
                    return generateFence(staticParcel);
                }
            });


    private long seed;

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(FenceFacet.class);
        FenceFacet facet = new FenceFacet(region.getRegion(), border);

        ParcelFacet parcelFacet = region.getRegionFacet(ParcelFacet.class);

        for (StaticParcel staticParcel : parcelFacet.getParcels()) {
            Optional<SimpleFence> optFence = cache.getUnchecked(staticParcel);
            if (optFence.isPresent()) {
                facet.addFence(optFence.get());
            }
        }

        region.setRegionFacet(FenceFacet.class, facet);
    }

    private Optional<SimpleFence> generateFence(StaticParcel staticParcel) {

        if (staticParcel.getZone() == Zone.RESIDENTIAL) {
            Rect2i fenceRc = Rect2i.createFromMinAndMax(staticParcel.getShape().min(), staticParcel.getShape().max());
            Orientation gateOrient = staticParcel.getOrientation();
            Vector2i gatePos = Edges.getCorner(fenceRc, staticParcel.getOrientation());
            return Optional.of(new SimpleFence(fenceRc, gateOrient, gatePos));
        } else {
            return Optional.empty();
        }
    }

}
