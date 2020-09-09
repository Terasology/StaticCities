// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.window;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFacet2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A registry for windows.
 */
public class WindowFacet extends BaseFacet2D {

    private final Collection<Window> windows = new ArrayList<>();

    public WindowFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public void addWindow(Window bldg) {
        windows.add(bldg);
    }

    public Collection<Window> getWindows() {
        return Collections.unmodifiableCollection(windows);
    }
}
