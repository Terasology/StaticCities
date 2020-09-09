// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.walls;

import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.viewer.layers.AbstractFacetLayer;
import org.terasology.engine.world.viewer.layers.Renders;
import org.terasology.engine.world.viewer.layers.ZOrder;
import org.terasology.math.geom.BaseVector2i;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Renders a {@link TownWall}.
 */
@Renders(value = TownWallFacet.class, order = ZOrder.BIOME + 3)
public class TownWallFacetLayer extends AbstractFacetLayer {

    private static final Color GATE_FILL_COLOR = new Color(255, 255, 224);
    private static final Color WALL_FILL_COLOR = new Color(192, 192, 192);
    private static final Color WALL_COLOR = new Color(96, 96, 96);

    @Override
    public void render(BufferedImage img, Region region) {
        TownWallFacet facet = region.getFacet(TownWallFacet.class);

        Graphics2D g = img.createGraphics();
        g.translate(-facet.getWorldRegion().minX(), -facet.getWorldRegion().minY());
        for (TownWall wall : facet.getTownWalls()) {
            render(g, wall);
        }
    }

    @Override
    public String getWorldText(Region region, int wx, int wy) {
        return null;
    }

    private void render(Graphics2D g, TownWall tw) {

        for (WallSegment ws : tw.getWalls()) {
            BaseVector2i start = ws.getStart();
            BaseVector2i end = ws.getEnd();

            g.setColor(WALL_COLOR);
            g.setStroke(new BasicStroke(ws.getWallThickness(), BasicStroke.CAP_ROUND, BasicStroke.CAP_BUTT));
            g.drawLine(start.getX(), start.getY(), end.getX(), end.getY());

            if (ws.isGate()) {
                g.setColor(GATE_FILL_COLOR);
            } else {
                g.setColor(WALL_FILL_COLOR);
            }
            g.setStroke(new BasicStroke(ws.getWallThickness() - 2, BasicStroke.CAP_ROUND, BasicStroke.CAP_BUTT));
            g.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
        }
    }
}
