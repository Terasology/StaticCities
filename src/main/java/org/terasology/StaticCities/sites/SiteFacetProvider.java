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

package org.terasology.StaticCities.sites;

import org.terasology.StaticCities.terrain.BuildableTerrainFacet;
import org.terasology.entitySystem.Component;
import org.terasology.math.Region3i;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.properties.Range;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.generation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 */
@Produces(SiteFacet.class)
@Requires(@Facet(BuildableTerrainFacet.class))
public class SiteFacetProvider implements ConfigurableFacetProvider {

    private SiteConfiguration config = new SiteConfiguration();

    private Noise seedNoiseGen;
    private Noise sizeNoiseGen;
    private Noise priorityNoiseGen;

    @Override
    public void setSeed(long seed) {
        this.seedNoiseGen = new WhiteNoise(seed);
        this.sizeNoiseGen = new WhiteNoise(seed ^ 0x2347928);
        this.priorityNoiseGen = new WhiteNoise(seed ^ 0x9B87F3D4);
    }

    @Override
    public void process(GeneratingRegion region) {

        BuildableTerrainFacet terrainFacet = region.getRegionFacet(BuildableTerrainFacet.class);

        // iterate in large steps over the world regions searching for suitable sites
        int scale = 10;

        Border3D border = region.getBorderForFacet(SiteFacet.class);
        border = border.extendBy(0, 0, TeraMath.ceilToInt(config.maxRadius * 3 + config.minDistance));
        Region3i coreReg = region.getRegion();
        int uncertainBorder = 2 * config.maxRadius + config.minDistance;
        SiteFacet settlementFacet = new SiteFacet(coreReg, border, uncertainBorder);
        Rect2i worldRect = settlementFacet.getWorldRegion();
        Rect2i worldRectScaled = Rect2i.createFromMinAndMax(worldRect.min().div(scale), worldRect.max().div(scale));

        List<Site> sites = new ArrayList<>();

        Vector2i pos = new Vector2i();
        for (BaseVector2i posScaled : worldRectScaled.contents()) {
            pos.set(posScaled.getX() * scale, posScaled.getY() * scale);
            if (seedNoiseGen.noise(pos.getX(), pos.getY()) > 0.99) {
                float size = sizeNoiseGen.noise(pos.getX(), pos.getY());
                size = config.minRadius + (size + 1) * 0.5f * (config.maxRadius - config.minRadius);

                if (terrainFacet.isBuildable(pos)) {
                    Site settlement = new Site(pos.getX(), pos.getY(), size);
                    sites.add(settlement);
                }
            }
        }

        ensureMinDistance(sites, config.minDistance);

        // remove all settlements that might not actually exist
        // this can happen if other settlements further away with higher priority intersect
        for (Site site : sites) {
            float borderDist = config.maxRadius + config.minDistance + site.getRadius();
            Rect2i certainRect = worldRect.expand(new Vector2i(-borderDist, -borderDist));
            if (certainRect.contains(site.getPos())) {
                settlementFacet.addSettlement(site);
            }
        }

        region.setRegionFacet(SiteFacet.class, settlementFacet);
    }

    @Override
    public String getConfigurationName() {
        return "Settlements";
    }

    @Override
    public Component getConfiguration() {
        return config;
    }

    @Override
    public void setConfiguration(Component configuration) {
        this.config = (SiteConfiguration) configuration;
    }

    private boolean ensureMinDistance(List<Site> sites, double minDist) {

        sites.sort((s1, s2) -> Float.compare(
                priorityNoiseGen.noise(s1.getPos().getX(), s1.getPos().getY()),
                priorityNoiseGen.noise(s2.getPos().getX(), s2.getPos().getY())
                ));

        ListIterator<Site> it = sites.listIterator();
        while (it.hasNext()) {
            Site site = it.next();
            BaseVector2i thisPos = site.getPos();

            Iterator<Site> otherIt = sites.listIterator(it.nextIndex());
            while (otherIt.hasNext()) {
                Site other = otherIt.next();
                BaseVector2i otherPos = other.getPos();
                double distSq = thisPos.distanceSquared(otherPos);
                double thres = minDist + site.getRadius() + other.getRadius();
                if (distSq < thres * thres) {
                    it.remove();
                    break;
                }
            }
        }

        return true;
    }

    private static class SiteConfiguration implements Component {

        @Range(label = "Minimal town size", description = "Minimal town size in blocks", min = 1, max = 150, increment = 10, precision = 1)
        private int minRadius = 50;

        @Range(label = "Maximum town size", description = "Maximum town size in blocks", min = 10, max = 350, increment = 10, precision = 1)
        private int maxRadius = 256;

        @Range(label = "Minimum distance between towns", min = 10, max = 1000, increment = 10, precision = 1)
        private int minDistance = 128;
    }
}
