// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.settlements;

import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.viewer.layers.AbstractFacetLayer;
import org.terasology.engine.world.viewer.layers.Renders;
import org.terasology.engine.world.viewer.layers.ZOrder;
import org.terasology.math.geom.ImmutableVector2i;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Draws the generated graph on a AWT graphics instance
 */
@Renders(value = SettlementFacet.class, order = ZOrder.BIOME + 3)
public class SettlementFacetLayer extends AbstractFacetLayer {

    private final Font font = new Font("SansSerif", Font.BOLD, 16);

    @Override
    public void render(BufferedImage img, org.terasology.engine.world.generation.Region region) {
        SettlementFacet settlementFacet = region.getFacet(SettlementFacet.class);

        Graphics2D g = img.createGraphics();
        int dx = region.getRegion().minX();
        int dy = region.getRegion().minZ();
        g.translate(-dx, -dy);

        g.setFont(font);
        g.setColor(Color.BLACK);

        FontMetrics fm = g.getFontMetrics(font);

        for (Settlement settlement : settlementFacet.getSettlements()) {
            String text = settlement.getName();
            int width = fm.stringWidth(text);
            ImmutableVector2i center = settlement.getSite().getPos();
            g.drawString(text, center.getX() - width / 2, center.getY() - 5);
        }

        g.dispose();
    }

    @Override
    public String getWorldText(Region region, int wx, int wy) {
        return null;
    }
}
