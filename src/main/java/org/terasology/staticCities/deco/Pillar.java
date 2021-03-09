// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.deco;

import com.google.common.collect.ImmutableList;
import org.joml.Vector3ic;
import org.terasology.engine.math.Side;
import org.terasology.staticCities.BlockType;
import org.terasology.staticCities.DefaultBlockType;

import java.util.Collections;
import java.util.List;

public class Pillar extends ColumnDecoration {

    /**
     * @param basePos the position of the base block
     * @param height the total height of the pillar
     */
    public Pillar(Vector3ic basePos, int height) {
        super(
            createList(height),
            Collections.nCopies(height, (Side) null),
            basePos);
    }

    private static List<BlockType> createList(int height) {
        return ImmutableList.<BlockType>builder()
            .add(DefaultBlockType.PILLAR_BASE)
            .addAll(Collections.nCopies(height - 2, DefaultBlockType.PILLAR_MIDDLE))
            .add(DefaultBlockType.PILLAR_TOP)
            .build();
    }

}
