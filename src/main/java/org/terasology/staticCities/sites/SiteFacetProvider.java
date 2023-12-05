// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.sites;

import org.joml.RoundingMode;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.terasology.engine.utilities.procedural.Noise;
import org.terasology.engine.utilities.procedural.WhiteNoise;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.ConfigurableFacetProvider;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.math.TeraMath;
import org.terasology.nui.properties.Range;
import org.terasology.staticCities.terrain.BuildableTerrainFacet;

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
        BlockRegion coreReg = region.getRegion();
        int uncertainBorder = 2 * config.maxRadius + config.minDistance;
        SiteFacet settlementFacet = new SiteFacet(coreReg, border, uncertainBorder);
        BlockAreac area = settlementFacet.getWorldArea();
        BlockArea worldRect = new BlockArea(area.minX(), area.minY(), area.maxX(), area.maxY());
        BlockArea worldRectScaled = new BlockArea(
            worldRect.minX()/scale,
            worldRect.minY()/scale,
            worldRect.maxX()/scale,
            worldRect.maxY()/scale);

        List<Site> sites = new ArrayList<>();

        Vector2i pos = new Vector2i();
        for (Vector2ic posScaled : worldRectScaled) {
            pos.set(posScaled.x() * scale, posScaled.y() * scale);
            if (seedNoiseGen.noise(pos.x(), pos.y()) > 0.99) {
                float size = sizeNoiseGen.noise(pos.x(), pos.y());
                size = config.minRadius + (size + 1) * 0.5f * (config.maxRadius - config.minRadius);

                if (terrainFacet.isBuildable(pos)) {
                    Site settlement = new Site(pos.x(), pos.y(), size);
                    sites.add(settlement);
                }
            }
        }

        ensureMinDistance(sites, config.minDistance);

        // remove all settlements that might not actually exist
        // this can happen if other settlements further away with higher priority intersect
        for (Site site : sites) {
            float borderDist = config.maxRadius + config.minDistance + site.getRadius();
            BlockArea certainRect = worldRect.expand(new Vector2i(-borderDist, -borderDist, RoundingMode.FLOOR), new BlockArea(BlockArea.INVALID));
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
                priorityNoiseGen.noise(s1.getPos().x(), s1.getPos().y()),
                priorityNoiseGen.noise(s2.getPos().x(), s2.getPos().y())
                ));

        ListIterator<Site> it = sites.listIterator();
        while (it.hasNext()) {
            Site site = it.next();
            Vector2ic thisPos = site.getPos();

            Iterator<Site> otherIt = sites.listIterator(it.nextIndex());
            while (otherIt.hasNext()) {
                Site other = otherIt.next();
                Vector2ic otherPos = other.getPos();
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

    public static class SiteConfiguration implements Component<SiteConfiguration> {

        @Range(label = "Minimal town size", description = "Minimal town size in blocks", min = 1, max = 150, increment = 10, precision = 1)
        public int minRadius = 50;

        @Range(label = "Maximum town size", description = "Maximum town size in blocks", min = 10, max = 350, increment = 10, precision = 1)
        public int maxRadius = 256;

        @Range(label = "Minimum distance between towns", min = 10, max = 1000, increment = 10, precision = 1)
        public int minDistance = 128;

        @Override
        public void copyFrom(SiteConfiguration other) {
            this.minRadius = other.minRadius;
            this.maxRadius = other.maxRadius;
            this.minDistance = other.minDistance;
        }
    }
}
