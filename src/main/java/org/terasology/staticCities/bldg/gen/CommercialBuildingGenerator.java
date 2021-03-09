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

package org.terasology.staticCities.bldg.gen;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3i;
import org.terasology.commonworld.Orientation;
import org.terasology.engine.math.Side;
import org.terasology.engine.utilities.procedural.WhiteNoise;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.math.TeraMath;
import org.terasology.staticCities.DefaultBlockType;
import org.terasology.staticCities.bldg.Building;
import org.terasology.staticCities.bldg.DefaultBuilding;
import org.terasology.staticCities.bldg.HollowBuildingPart;
import org.terasology.staticCities.common.Edges;
import org.terasology.staticCities.deco.SingleBlockDecoration;
import org.terasology.staticCities.model.roof.HipRoof;
import org.terasology.staticCities.parcels.StaticParcel;
import org.terasology.staticCities.surface.InfiniteSurfaceHeightFacet;

/**
 *
 */
public class CommercialBuildingGenerator {

    private long seed;

    /**
     * @param seed
     */
    public CommercialBuildingGenerator(long seed) {
        this.seed = seed;
    }

    public Building generate(StaticParcel staticParcel, InfiniteSurfaceHeightFacet heightFacet) {
        Orientation o = staticParcel.getOrientation();
        DefaultBuilding bldg = new DefaultBuilding(o);

        BlockArea rc = staticParcel.getShape().expand(-4, -4, new BlockArea(BlockArea.INVALID));
        BlockArea roofRc = rc.expand(2, 2, new BlockArea(BlockArea.INVALID));

        int wallHeight = 8;
        int arcRadius = 4;

        int centerX = (rc.minX() + rc.maxX()) / 2;
        int centerY = (rc.minY() + rc.maxY()) / 2;
        int baseHeight = TeraMath.floorToInt(heightFacet.getWorld(centerX, centerY)) + 1;
        int roofBaseHeight = baseHeight + wallHeight - 1; // 1 block overlap

        HipRoof roof = new HipRoof(roofRc, roofRc, roofBaseHeight, 0.5f, roofBaseHeight + 1);

        HollowBuildingPart hall = new HollowBuildingPart(rc, roof, baseHeight, wallHeight, arcRadius);
        bldg.addPart(hall);

        WhiteNoise noiseGen = new WhiteNoise(seed);

        float fillFactor = 0.3f;
        BlockArea storeRc = rc.expand(-3, -3, new BlockArea(BlockArea.INVALID));

        for (Vector2ic v : storeRc) {
            if (noiseGen.noise(v.x(), v.y()) * 0.5f + 0.5f < fillFactor) {
                Vector3i pos = new Vector3i(v.x(), baseHeight, v.y());
                hall.addDecoration(new SingleBlockDecoration(DefaultBlockType.BARREL, pos, Side.FRONT));
            }
        }

        BlockArea inner = rc.expand(-1, -1, new BlockArea(BlockArea.INVALID));
        for (int i = 0; i < 4; i++) {
            Vector2i pos = Edges.getCorner(inner, Orientation.NORTHEAST.getRotated(i * 90));
            Vector3i pos3d = new Vector3i(pos.x(), roofBaseHeight - 2, pos.y());
            hall.addDecoration(new SingleBlockDecoration(DefaultBlockType.TORCH, pos3d, Side.FRONT));
        }

        return bldg;
    }

}
