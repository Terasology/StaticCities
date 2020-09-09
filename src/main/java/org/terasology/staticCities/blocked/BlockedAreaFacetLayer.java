// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.blocked;

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
@Renders(value = BlockedAreaFacet.class, order = ZOrder.BIOME + 1)
public class BlockedAreaFacetLayer extends AbstractFacetLayer {

    public BlockedAreaFacetLayer() {
        setVisible(false);
        // use default settings
    }

    @Override
    public void render(BufferedImage img, org.terasology.engine.world.generation.Region region) {
        BlockedAreaFacet facet = region.getFacet(BlockedAreaFacet.class);

        Graphics2D g = img.createGraphics();
        g.setColor(Color.GRAY);
        for (BlockedArea area : facet.getAreas()) {
            Rect2i areaRc = area.getWorldRegion();
            int dx = areaRc.minX() - facet.getWorldRegion().minX();
            int dy = areaRc.minY() - facet.getWorldRegion().minY();

            g.drawRect(dx, dy, areaRc.width(), areaRc.height());
            g.drawImage(area.getImage(), dx, dy, null);
        }
        g.dispose();
    }

    @Override
    public String getWorldText(org.terasology.engine.world.generation.Region region, int wx, int wy) {
        BlockedAreaFacet facet = region.getFacet(BlockedAreaFacet.class);

        if (facet.isBlocked(wx, wy)) {
            return "Blocked";
        } else {
            return "Open";
        }
    }
}
