// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.parcels;

import org.terasology.engine.world.viewer.layers.AbstractFacetLayer;
import org.terasology.engine.world.viewer.layers.Renders;
import org.terasology.engine.world.viewer.layers.ZOrder;
import org.terasology.math.geom.Rect2i;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Draws the generated graph on a AWT graphics instance
 */
@Renders(value = ParcelFacet.class, order = ZOrder.BIOME + 1)
public class ParcelFacetLayer extends AbstractFacetLayer {

    public ParcelFacetLayer() {
        setVisible(false);
        // use default settings
    }

    @Override
    public void render(BufferedImage img, org.terasology.engine.world.generation.Region region) {
        ParcelFacet facet = region.getFacet(ParcelFacet.class);

        Color fillColor = new Color(64, 64, 64, 128);
        Color frameColor = new Color(64, 64, 64, 224);

        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int dx = region.getRegion().minX();
        int dy = region.getRegion().minZ();
        g.translate(-dx, -dy);

        for (StaticParcel staticParcel : facet.getParcels()) {
            g.setColor(fillColor);
            Rect2i shape = staticParcel.getShape();
            Rectangle rc = new Rectangle(shape.minX(), shape.minY(), shape.width() - 1, shape.height() - 1);
            g.fill(rc);
            g.setColor(frameColor);
            g.draw(rc);
        }

        g.dispose();
    }

    @Override
    public String getWorldText(org.terasology.engine.world.generation.Region region, int wx, int wy) {
        ParcelFacet facet = region.getFacet(ParcelFacet.class);

        for (StaticParcel staticParcel : facet.getParcels()) {
            if (staticParcel.getShape().contains(wx, wy)) {
                return "StaticParcel [" + staticParcel.getZone() + "]";
            }
        }
        return null;
    }
}
