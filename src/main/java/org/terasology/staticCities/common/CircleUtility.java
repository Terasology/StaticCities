// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.common;

import org.terasology.joml.geom.Circlef;
import org.terasology.joml.geom.Rectanglef;

public class CircleUtility {
    public static boolean intersect(Circlef cir, Rectanglef rect) {
        if (!rect.isValid()) {
            return false;
        }

        float halfWidth = rect.getSizeX() * 0.5f;
        float circleDistanceX = Math.abs(cir.x - rect.minX() - halfWidth);
        if (circleDistanceX > (halfWidth + cir.r)) {
            return false;
        }
        float halfHeight = rect.getSizeY() * 0.5f;
        float circleDistanceY = Math.abs(cir.y - rect.minY() - halfHeight);
        if (circleDistanceY > (halfHeight + cir.r)) {
            return false;
        }

        if (circleDistanceX <= halfWidth) {
            return true;
        }

        if (circleDistanceY <= halfHeight) {
            return true;
        }
        float a1 = circleDistanceX - halfWidth;
        float a2 = circleDistanceY - halfHeight;
        return a1 * a1 + a2 * a2 <= cir.r * cir.r;
    }

    public static boolean contains(Circlef center,float x, float y) {
        float dx = x - center.x;
        float dy = y - center.y;

        return dx * dx + dy * dy <= center.r * center.r;
    }
}
