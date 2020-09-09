// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.lakes;

import org.terasology.engine.world.viewer.layers.AbstractFacetLayer;
import org.terasology.engine.world.viewer.layers.Renders;
import org.terasology.engine.world.viewer.layers.ZOrder;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Draws the generated graph on a AWT graphics instance
 */
@Renders(value = LakeFacet.class, order = ZOrder.BIOME + 1)
public class LakeFacetLayer extends AbstractFacetLayer {

    public LakeFacetLayer() {
        setVisible(false);
        // use default settings
    }

    @Override
    public void render(BufferedImage img, org.terasology.engine.world.generation.Region region) {
        LakeFacet graphFacet = region.getFacet(LakeFacet.class);

        Color fillColor = new Color(64, 64, 255, 128);
        Color frameColor = new Color(64, 64, 255, 224);

        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int dx = region.getRegion().minX();
        int dy = region.getRegion().minZ();
        g.translate(-dx, -dy);

        for (Lake lake : graphFacet.getLakes()) {
            Polygon poly = lake.getContour().getPolygon();
            g.setColor(fillColor);
            g.fill(poly);
            g.setColor(frameColor);
            g.draw(poly);
        }

        g.dispose();
    }

    @Override
    public String getWorldText(org.terasology.engine.world.generation.Region region, int wx, int wy) {
        LakeFacet lakeFacet = region.getFacet(LakeFacet.class);

        for (Lake lake : lakeFacet.getLakes()) {
            if (lake.getContour().getPolygon().contains(wx, wy)) {
                return lake.getName();
            }
        }
        return null;
    }
}
