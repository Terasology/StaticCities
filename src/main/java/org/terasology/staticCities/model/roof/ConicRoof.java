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

package org.terasology.staticCities.model.roof;

import org.joml.Vector2ic;
import org.terasology.cities.bldg.shape.CircularBase;
import org.terasology.joml.geom.Circlef;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Circle;

/**
 * A conic (circular base area) roof
 */
public class ConicRoof extends AbstractRoof implements CircularBase {

    private int pitch;
    private Circlef shape;
    /**
     * @param center
     * @param radius
     * @param baseHeight
     * @param pitch
     */
    public ConicRoof(Vector2ic center, int radius, int baseHeight, int pitch) {
        super(baseHeight);
        this.pitch = pitch;
        this.shape = new Circlef(center.x(), center.y(), radius);
    }

    /**
     * @return the pitch
     */
    public int getPitch() {
        return pitch;
    }

    @Override
    public Circlef getShape() {
        return shape;
    }
}
