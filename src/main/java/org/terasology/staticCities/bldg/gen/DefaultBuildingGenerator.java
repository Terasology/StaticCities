// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.bldg.gen;

import com.google.common.math.IntMath;
import org.terasology.commonworld.Orientation;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.staticCities.bldg.Building;
import org.terasology.staticCities.bldg.SimpleRoundHouse;
import org.terasology.staticCities.door.SimpleDoor;
import org.terasology.staticCities.parcels.StaticParcel;
import org.terasology.staticCities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.staticCities.window.SimpleWindow;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.Set;

/**
 *
 */
public class DefaultBuildingGenerator implements BuildingGenerator {

    private final long seed;
    private final RectHouseGenerator gen = new RectHouseGenerator();

    public DefaultBuildingGenerator(long seed) {
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
        Rect2i lotRc = staticParcel.getShape().expand(new Vector2i(-1, -1));

        int centerX = lotRc.minX() + IntMath.divide(lotRc.width(), 2, RoundingMode.HALF_DOWN); // width() is 1 too much
        int centerY = lotRc.minY() + IntMath.divide(lotRc.height(), 2, RoundingMode.HALF_DOWN);

        int towerSize = Math.min(lotRc.width(), lotRc.height());
        int towerRad = towerSize / 2 - 1;

        int entranceHeight = 2;
        ImmutableVector2i doorPos = new ImmutableVector2i(centerX + towerRad, centerY);
        Orientation orient = Orientation.EAST;

        ImmutableVector2i doorDir = orient.getDir();
        Vector2i probePos = new Vector2i(doorPos.x() + doorDir.getX(), doorPos.y() + doorDir.getY());

        int baseHeight = TeraMath.floorToInt(heightFacet.getWorld(probePos)) + 1;
        int sideHeight = 4;

        SimpleRoundHouse house = new SimpleRoundHouse(orient, new ImmutableVector2i(centerX, centerY), towerRad,
                baseHeight, sideHeight);

        SimpleDoor entrance = new SimpleDoor(orient, doorPos, baseHeight, baseHeight + entranceHeight);
        house.getRoom().addDoor(entrance);

        int wndOff = 1;
        ImmutableVector2i wndPos1 = new ImmutableVector2i(centerX - towerRad, centerY);
        ImmutableVector2i wndPos2 = new ImmutableVector2i(centerX, centerY - towerRad);
        ImmutableVector2i wndPos3 = new ImmutableVector2i(centerX, centerY + towerRad);
        SimpleWindow wnd1 = new SimpleWindow(Orientation.WEST, wndPos1, baseHeight + wndOff);
        SimpleWindow wnd2 = new SimpleWindow(Orientation.NORTH, wndPos2, baseHeight + wndOff);
        SimpleWindow wnd3 = new SimpleWindow(Orientation.SOUTH, wndPos3, baseHeight + wndOff);

        house.getRoom().addWindow(wnd1);
        house.getRoom().addWindow(wnd2);
        house.getRoom().addWindow(wnd3);

        return house;
    }

}
