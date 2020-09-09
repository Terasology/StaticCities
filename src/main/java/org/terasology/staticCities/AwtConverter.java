// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities;

import org.terasology.math.geom.Circle;
import org.terasology.math.geom.Rect2f;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Shape;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 *
 */
public final class AwtConverter {

    private AwtConverter() {
        // no instances
    }

    /**
     * Converts shapes to AWT shapes
     *
     * @param shape the shape to convert
     * @return the AWT shape instance
     * @throws IllegalArgumentException if no mapping exists
     */
    public static java.awt.Shape toAwt(Shape shape) {
        if (shape instanceof Rect2i) {
            return toAwt((Rect2i) shape);
        }

        if (shape instanceof Rect2f) {
            return toAwt((Rect2f) shape);
        }

        if (shape instanceof Circle) {
            return toAwt((Circle) shape);
        }

        throw new IllegalArgumentException("Not recognized: " + shape);
    }

    public static java.awt.Rectangle toAwt(Rect2i rc) {
        return new Rectangle(rc.minX(), rc.minY(), rc.width() - 1, rc.height() - 1);
    }

    public static java.awt.geom.Rectangle2D toAwt(Rect2f rc) {
        return new Rectangle2D.Float(rc.minX(), rc.minY(), rc.width(), rc.height());
    }

    public static java.awt.geom.Ellipse2D toAwt(Circle circle) {
        float minX = circle.getCenter().getX() - circle.getRadius();
        float minY = circle.getCenter().getY() - circle.getRadius();
        float dia = circle.getRadius() * 2f;
        return new Ellipse2D.Float(minX, minY, dia, dia);
    }

}
