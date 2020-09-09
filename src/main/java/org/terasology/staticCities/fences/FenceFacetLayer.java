// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.fences;

import org.terasology.engine.world.viewer.layers.AbstractFacetLayer;
import org.terasology.engine.world.viewer.layers.Renders;
import org.terasology.engine.world.viewer.layers.ZOrder;
import org.terasology.math.geom.Rect2i;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Draws the blocked area in a given image
 */
@Renders(value = FenceFacet.class, order = ZOrder.BIOME + 3)
public class FenceFacetLayer extends AbstractFacetLayer {

    private static final Color FENCE_COLOR = Color.YELLOW.darker();
    private static final Color GATE_COLOR = FENCE_COLOR.darker().darker();

    public FenceFacetLayer() {
        setVisible(true);
    }

    @Override
    public void render(BufferedImage img, org.terasology.engine.world.generation.Region region) {
        FenceFacet facet = region.getFacet(FenceFacet.class);

        Graphics2D g = img.createGraphics();
        g.translate(-facet.getWorldRegion().minX(), -facet.getWorldRegion().minY());
        for (SimpleFence fence : facet.getFences()) {
            Rect2i areaRc = fence.getRect();

            g.setColor(FENCE_COLOR);
            g.drawRect(areaRc.minX(), areaRc.minY(), areaRc.width() - 1, areaRc.height() - 1);
            g.setColor(GATE_COLOR);
            g.drawRect(fence.getGate().getX(), fence.getGate().getY(), 0, 0);
        }
        g.dispose();
    }

    @Override
    public String getWorldText(org.terasology.engine.world.generation.Region region, int wx, int wy) {
        return null;
    }
}
