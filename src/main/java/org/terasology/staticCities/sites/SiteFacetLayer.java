// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.sites;

import org.terasology.engine.world.viewer.layers.AbstractFacetLayer;
import org.terasology.engine.world.viewer.layers.Renders;
import org.terasology.engine.world.viewer.layers.ZOrder;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Vector2i;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Draws the generated settlement sites in a AWT images.
 */
@Renders(value = SiteFacet.class, order = ZOrder.BIOME + 1)
public class SiteFacetLayer extends AbstractFacetLayer {

    private final Color fillColor = new Color(255, 64, 64, 128);
    private final Color frameColor = new Color(255, 64, 64, 224);

    public SiteFacetLayer() {
        setVisible(false);
        // use default settings
    }

    @Override
    public void render(BufferedImage img, org.terasology.engine.world.generation.Region region) {
        SiteFacet settlementFacet = region.getFacet(SiteFacet.class);

        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int dx = region.getRegion().minX();
        int dy = region.getRegion().minZ();
        g.translate(-dx, -dy);

        for (Site settlement : settlementFacet.getSettlements()) {
            BaseVector2i center = settlement.getPos();
            int radius = TeraMath.floorToInt(settlement.getRadius());
            g.setColor(fillColor);
            g.fillOval(center.getX() - radius, center.getY() - radius, radius * 2, radius * 2);
            g.setColor(frameColor);
            g.drawOval(center.getX() - radius, center.getY() - radius, radius * 2, radius * 2);
        }

        g.dispose();
    }

    @Override
    public String getWorldText(org.terasology.engine.world.generation.Region region, int wx, int wy) {
        SiteFacet facet = region.getFacet(SiteFacet.class);

        Vector2i cursor = new Vector2i(wx, wy);
        for (Site settlement : facet.getSettlements()) {
            if (settlement.getPos().distance(cursor) < settlement.getRadius()) {
                return settlement.toString();
            }
        }
        return null;
    }
}
