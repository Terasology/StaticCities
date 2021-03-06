// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.raster;

import org.joml.Vector2f;
import org.terasology.commonworld.geom.Line2f;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.joml.geom.Rectanglef;
import org.terasology.math.TeraMath;

import java.util.Optional;

/**
 * Converts model elements into blocks
 */
public abstract class RasterUtil {


    /**
     * if (x2 < x1) nothing will be drawn.
     * @param pen the pen use
     * @param x1 left x coord
     * @param x2 right x coord
     * @param z the z coord
     */
    public static void drawLineX(Pen pen, int x1, int x2, int z) {
        BlockAreac rc = pen.getTargetArea();

        if (z >= rc.minY() && z <= rc.maxY()) {
            int minX = Math.max(x1, rc.minX());
            int maxX = Math.min(x2, rc.maxX());
            for (int x = minX; x <= maxX; x++) {
                pen.draw(x, z);
            }
        }
    }

    /**
     * if (z2 < z1) nothing will be drawn.
     * @param pen the pen use
     * @param z1 top z coord
     * @param z2 bottom z coord
     * @param x the x coord
     */
    public static void drawLineZ(Pen pen, int x, int z1, int z2) {
        BlockAreac rc = pen.getTargetArea();

        if (x >= rc.minX() && x <= rc.maxX()) {
            int minZ = Math.max(z1, rc.minY());
            int maxZ = Math.min(z2, rc.maxY());
            for (int z = minZ; z <= maxZ; z++) {
                pen.draw(x, z);
            }
        }
    }

    /**
     * @param rect the area to fill
     * @param pen the pen to use for the rasterization of the rectangle
     */
    public static void fillRect(Pen pen, BlockAreac rect) {
        Optional<BlockArea> rc = pen.getTargetArea().intersect(rect, new BlockArea(BlockArea.INVALID));
        if (!rc.isPresent()) {
            return;
        }

        for (int z = rc.get().minY(); z <= rc.get().maxY(); z++) {
            for (int x = rc.get().minX(); x <= rc.get().maxX(); x++) {
                pen.draw(x, z);
            }
        }
    }

    /**
     * @param pen the pen to use
     * @param rc the rectangle to draw
     */
    public static void drawRect(Pen pen, BlockAreac rc) {

        // walls along x-axis
        drawLineX(pen, rc.minX(), rc.maxX(), rc.minY());
        drawLineX(pen, rc.minX(), rc.maxX(), rc.maxY());

        // walls along z-axis
        drawLineZ(pen, rc.minX(), rc.minY() + 1, rc.maxY() - 1); // no need to draw corners again
        drawLineZ(pen, rc.maxX(), rc.minY() + 1, rc.maxY() - 1); //  -> inset by one on both ends

    }

    /**
     * Draws a line.<br>
     * See Wikipedia: Bresenham's line algorithm, chapter Simplification
     * @param pen the pen to use
     * @param line the line to draw
     */
    public static void drawLine(Pen pen, Line2f line) {

        BlockAreac outerBox = pen.getTargetArea();

        Vector2f p0 = new Vector2f();
        Vector2f p1 = new Vector2f();
        if (line.getClipped(outerBox.getBounds(new Rectanglef()), p0, p1)) {
            int cx1 = TeraMath.floorToInt(p0.x());
            int cy1 = TeraMath.floorToInt(p0.y());
            int cx2 = TeraMath.floorToInt(p1.x());
            int cy2 = TeraMath.floorToInt(p1.y());
            drawClippedLine(pen, cx1, cy1, cx2, cy2);
        }
    }


    private static void drawClippedLine(Pen pen, int x1, int z1, int x2, int z2) {

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(z2 - z1);

        int sx = (x1 < x2) ? 1 : -1;
        int sy = (z1 < z2) ? 1 : -1;

        int err = dx - dy;

        int x = x1;
        int z = z1;

        while (true) {
            pen.draw(x, z);

            if (x == x2 && z == z2) {
                break;
            }

            int e2 = 2 * err;

            if (e2 > -dy) {
                err = err - dy;
                x += sx;
            }
            // if going along diagonals is not ok use " .. } else if (e2.. " instead

            if (e2 < dx) {
                err = err + dx;
                z += sy;
            }
        }
    }

    /**
     * Draws a circle based on Horn's algorithm (see B. K. P. Horn: Circle Generators for Display Devices.
     * Computer Graphics and Image Processing 5, 2 - June 1976)
     * @param cx the center x
     * @param cy the center y
     * @param rad the radius
     * @param checkedPen the receiving instance. Must be checked, because iterator could draw outside.
     */
    public static void drawCircle(CheckedPen checkedPen, int cx, int cy, int rad) {
        int d = -rad;
        int x = rad;
        int y = 0;
        while (y <= x) {
            checkedPen.draw(cx + x, cy + y);
            checkedPen.draw(cx - x, cy + y);
            checkedPen.draw(cx - x, cy - y);
            checkedPen.draw(cx + x, cy - y);

            checkedPen.draw(cx + y, cy + x);
            checkedPen.draw(cx - y, cy + x);
            checkedPen.draw(cx - y, cy - x);
            checkedPen.draw(cx + y, cy - x);

            d = d + 2 * y + 1;
            y = y + 1;
            if (d > 0) {
                d = d - 2 * x + 2;
                x = x - 1;
            }
        }
    }

    /**
     * @param cx the center x
     * @param cy the center y
     * @param rad the radius
     * @param pen the pen to draw
     */
    public static void fillCircle(CheckedPen pen, int cx, int cy, int rad) {
        for (int y = 0; y <= rad; y++) {
            for (int x = 0; x * x + y * y <= (rad + 0.5) * (rad + 0.5); x++) {
                pen.draw(cx + x, cy + y);
                pen.draw(cx - x, cy + y);
                pen.draw(cx - x, cy - y);
                pen.draw(cx + x, cy - y);
            }
        }
    }
}
