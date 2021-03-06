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

package org.terasology.staticCities.walls;

import org.joml.Vector2ic;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.viewer.layers.AbstractFacetLayer;
import org.terasology.engine.world.viewer.layers.Renders;
import org.terasology.engine.world.viewer.layers.ZOrder;

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
        g.translate(-facet.getWorldArea().minX(),  -facet.getWorldArea().minY());
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
            Vector2ic start = ws.getStart();
            Vector2ic end = ws.getEnd();

            g.setColor(WALL_COLOR);
            g.setStroke(new BasicStroke(ws.getWallThickness(), BasicStroke.CAP_ROUND, BasicStroke.CAP_BUTT));
            g.drawLine(start.x(), start.y(), end.x(), end.y());

            if (ws.isGate()) {
                g.setColor(GATE_FILL_COLOR);
            } else {
                g.setColor(WALL_FILL_COLOR);
            }
            g.setStroke(new BasicStroke(ws.getWallThickness() - 2, BasicStroke.CAP_ROUND, BasicStroke.CAP_BUTT));
            g.drawLine(start.x(), start.y(), end.x(), end.y());
        }
    }
}
