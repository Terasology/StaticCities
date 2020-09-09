// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.roads;

import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.viewer.layers.AbstractFacetLayer;
import org.terasology.engine.world.viewer.layers.Renders;
import org.terasology.engine.world.viewer.layers.ZOrder;
import org.terasology.math.geom.BaseVector2i;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

/**
 * Draws the generated graph on a AWT graphics instance
 */
@Renders(value = RoadFacet.class, order = ZOrder.BIOME + 1)
public class RoadFacetLayer extends AbstractFacetLayer {

    private final Color fillColor = new Color(128, 128, 16, 232);
    private final Color dotColor = new Color(64, 64, 0, 192);

    public RoadFacetLayer() {
        // use default settings
    }

    @Override
    public void render(BufferedImage img, org.terasology.engine.world.generation.Region region) {
        RoadFacet roadFacet = region.getFacet(RoadFacet.class);

        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int dx = region.getRegion().minX();
        int dy = region.getRegion().minZ();
        g.translate(-dx, -dy);


        for (Road road : roadFacet.getRoads()) {
            BaseVector2i p0 = road.getEnd0();
            Path2D path = new Path2D.Float();
            path.moveTo(p0.getX(), p0.getY());
            for (int i = 1; i < road.getPoints().size(); i++) {
                BaseVector2i p1 = road.getPoints().get(i);
                path.lineTo(p1.getX(), p1.getY());
            }

            float width = road.getWidth();
            BasicStroke stroke = new BasicStroke(width);

            g.setColor(fillColor);
            g.setStroke(stroke);
            g.draw(path);

            BasicStroke dots = new BasicStroke(2.5f);
            g.setColor(dotColor);
            g.setStroke(dots);
            for (int i = 0; i < road.getPoints().size(); i++) {
                BaseVector2i p1 = road.getPoints().get(i);
                g.drawLine(p1.getX(), p1.getY(), p1.getX(), p1.getY());
            }
        }

        g.dispose();
    }

    @Override
    public String getWorldText(Region region, int wx, int wy) {
        return null;
    }
}
