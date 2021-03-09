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

package org.terasology.staticCities.fences;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.joml.Vector2i;
import org.terasology.commonworld.Orientation;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
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
            Orientation gateOrient = staticParcel.getOrientation();
            Vector2i gatePos = Edges.getCorner(staticParcel.getShape(), staticParcel.getOrientation());
            return Optional.of(new SimpleFence(staticParcel.getShape(), gateOrient, gatePos));
        } else {
            return Optional.empty();
        }
    }

}
