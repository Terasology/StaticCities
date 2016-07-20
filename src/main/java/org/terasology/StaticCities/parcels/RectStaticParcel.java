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

package org.terasology.StaticCities.parcels;

import org.terasology.commonworld.Orientation;
import org.terasology.math.geom.Rect2i;

/**
 * A rectangular {@link StaticParcel}
 */
public class RectStaticParcel extends StaticParcel {

    /**
     * @param shape the shape of the lot
     * @param zone the zone type
     * @param orientation the orientation of the parcel (e.g. towards the closest street)
     */
    public RectStaticParcel(Rect2i shape, Zone zone, Orientation orientation) {
        super(shape, zone, orientation);
    }

    @Override
    public Rect2i getShape() {
        return super.getShape();
    }
}
