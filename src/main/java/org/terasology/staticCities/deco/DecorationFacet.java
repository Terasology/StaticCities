// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.deco;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFacet2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A registry for decorations.
 */
public class DecorationFacet extends BaseFacet2D {

    private final Collection<Decoration> decorations = new ArrayList<>();

    public DecorationFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public void addDecoration(Decoration decoration) {
        decorations.add(decoration);
    }

    public Collection<Decoration> getDecorations() {
        return Collections.unmodifiableCollection(decorations);
    }
}
