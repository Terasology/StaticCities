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

/**
 * An abstract base class for different roof types
 */
public abstract class AbstractRoof implements Roof {

    private int baseHeight;

    /**
     * @param shape the roof area
     * @param baseHeight the base height of the roof
     */
    public AbstractRoof(int baseHeight) {
        this.baseHeight = baseHeight;
    }

    /**
     * @return the base height of the roof
     */
    public int getBaseHeight() {
        return baseHeight;
    }

}
