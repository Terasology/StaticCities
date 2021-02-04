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

import com.google.common.math.IntMath;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.terasology.commonworld.Orientation;
import org.terasology.math.TeraMath;
import org.terasology.staticCities.bldg.Building;
import org.terasology.staticCities.bldg.SimpleRoundHouse;
import org.terasology.staticCities.door.SimpleDoor;
import org.terasology.staticCities.parcels.StaticParcel;
import org.terasology.staticCities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.staticCities.window.SimpleWindow;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.block.BlockArea;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.Set;

/**
 *
 */
public class DefaultBuildingGenerator implements BuildingGenerator {

    private long seed;
    private final RectHouseGenerator gen = new RectHouseGenerator();

    public DefaultBuildingGenerator(long seed)  {
        this.seed = seed;
    }

    @Override
    public Set<Building> generate(StaticParcel staticParcel, InfiniteSurfaceHeightFacet heightFacet) {
        Random rng = new FastRandom(staticParcel.getShape().hashCode() ^ seed);
        Building b;
        switch (staticParcel.getZone()) {
        case RESIDENTIAL:
            if (rng.nextFloat() < 0.2f) {
                b = generateRoundHouse(staticParcel, heightFacet);
            } else {
                b = gen.apply(staticParcel, heightFacet);
            }
            break;

        case GOVERNMENTAL:
            b = new TownHallGenerator().generate(staticParcel, heightFacet);
            break;

        case COMMERCIAL:
            b = new CommercialBuildingGenerator(seed).generate(staticParcel, heightFacet);
            break;

        case CLERICAL:
            b = new SimpleChurchGenerator(seed).apply(staticParcel, heightFacet);
            break;

        default:
            return Collections.emptySet();
        }

        return Collections.singleton(b);
    }

    private Building generateRoundHouse(StaticParcel staticParcel, InfiniteSurfaceHeightFacet heightFacet) {

        // make build-able area 1 block smaller, so make the roof stay inside
        BlockArea lotRc = staticParcel.getShape().expand(-1, -1, new BlockArea(BlockArea.INVALID));

        int centerX = lotRc.minX() + IntMath.divide(lotRc.getSizeX(), 2, RoundingMode.HALF_DOWN); // width() is 1 too much
        int centerY = lotRc.minY() + IntMath.divide(lotRc.getSizeY(), 2, RoundingMode.HALF_DOWN);

        int towerSize = Math.min(lotRc.getSizeX(), lotRc.getSizeY());
        int towerRad = towerSize / 2 - 1;

        int entranceHeight = 2;
        Vector2i doorPos = new Vector2i(centerX + towerRad, centerY);
        Orientation orient = Orientation.EAST;

        Vector2ic doorDir = orient.direction();
        Vector2i probePos = new Vector2i(doorPos.x() + doorDir.x(), doorPos.y() + doorDir.y());

        int baseHeight = TeraMath.floorToInt(heightFacet.getWorld(probePos)) + 1;
        int sideHeight = 4;

        SimpleRoundHouse house = new SimpleRoundHouse(orient, new Vector2i(centerX, centerY), towerRad, baseHeight, sideHeight);

        SimpleDoor entrance = new SimpleDoor(orient, doorPos, baseHeight, baseHeight + entranceHeight);
        house.getRoom().addDoor(entrance);

        int wndOff = 1;
        Vector2i wndPos1 = new Vector2i(centerX - towerRad, centerY);
        Vector2i wndPos2 = new Vector2i(centerX, centerY - towerRad);
        Vector2i wndPos3 = new Vector2i(centerX, centerY + towerRad);
        SimpleWindow wnd1 = new SimpleWindow(Orientation.WEST, wndPos1, baseHeight + wndOff);
        SimpleWindow wnd2 = new SimpleWindow(Orientation.NORTH, wndPos2, baseHeight + wndOff);
        SimpleWindow wnd3 = new SimpleWindow(Orientation.SOUTH, wndPos3, baseHeight + wndOff);

        house.getRoom().addWindow(wnd1);
        house.getRoom().addWindow(wnd2);
        house.getRoom().addWindow(wnd3);

        return house;
    }

}
