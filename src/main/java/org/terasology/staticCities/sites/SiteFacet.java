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

package org.terasology.staticCities.sites;

import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.world.block.BlockAreac;
import org.terasology.world.block.BlockRegion;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.facets.base.BaseFacet2D;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class SiteFacet extends BaseFacet2D {

    private Set<Site> settlements = new HashSet<>();
    private int uncertainBorder;

    public SiteFacet(BlockRegion targetRegion, Border3D border, int uncertainBorder) {
        super(targetRegion, border);
        this.uncertainBorder = uncertainBorder;
    }

    public Rect2i getCertainWorldRegion() {
        BlockAreac area = getWorldArea();
        return Rect2i.createFromMinAndMax(area.minX(), area.minY(), area.maxX(), area.maxY()).expand(new Vector2i(-uncertainBorder, -uncertainBorder));
    }

    public void addSettlement(Site settlement) {
        settlements.add(settlement);
    }

    public Set<Site> getSettlements() {
        return Collections.unmodifiableSet(settlements);
    }

}
