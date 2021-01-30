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
import org.joml.Vector3ic;
import org.terasology.commonworld.Orientation;
import org.terasology.math.TeraMath;
import org.terasology.staticCities.bldg.Building;
import org.terasology.staticCities.bldg.DefaultBuilding;
import org.terasology.staticCities.bldg.RectBuildingPart;
import org.terasology.staticCities.common.Edges;
import org.terasology.staticCities.door.SimpleDoor;
import org.terasology.staticCities.model.roof.HipRoof;
import org.terasology.staticCities.model.roof.Roof;
import org.terasology.staticCities.parcels.StaticParcel;
import org.terasology.staticCities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.staticCities.window.SimpleWindow;
import org.terasology.world.block.BlockArea;
import org.terasology.world.block.BlockAreac;

/**
 *
 */
public class TownHallGenerator {

    public Building generate(StaticParcel staticParcel, InfiniteSurfaceHeightFacet hm) {

        Orientation o = staticParcel.getOrientation();
        DefaultBuilding bldg = new DefaultBuilding(o);

        BlockArea rc = staticParcel.getShape().expand(-2, -2, new BlockArea(BlockArea.INVALID));

        Vector2ic doorDir = o.getOpposite().direction();
        Vector2i doorPos = Edges.getCorner(rc, o.getOpposite());
        Vector2i probePos = new Vector2i(doorPos.x() + doorDir.x(), doorPos.y() + doorDir.y());

        Turtle turtle = new Turtle(doorPos, o);
        turtle.move(0, 2);
        int width = turtle.width(rc);
        int length = turtle.length(rc);

        width -= width % 6;
        length -= length % 6;

        // we add +1, because the building starts at 1 block above the terrain
        int floorHeight = TeraMath.floorToInt(hm.getWorld(probePos)) + 1;
        int wallHeight = 6;

        // Create entry hall
        BlockArea hallRect = turtle.rectCentered(0, width, length / 3);
        RectBuildingPart hall = createHall(hallRect, o, floorHeight, wallHeight);

        bldg.addPart(hall);

        BlockArea leftRect = turtle.rect(-width / 2, length / 3 - 1, width / 3, length / 3); // -1 to overlap
        BlockArea rightRect = turtle.rect(width * 1 / 6, length / 3 - 1, width / 3, length / 3); // -1 to overlap

        RectBuildingPart left = createHallway(leftRect, o, floorHeight, wallHeight - 2);
        RectBuildingPart right = createHallway(rightRect, o, floorHeight, wallHeight - 2);

        bldg.addPart(left);
        bldg.addPart(right);

        hallRect = turtle.rectCentered(length * 2 / 3 - 2, width, length / 3);
        hall = createHall(hallRect, o, floorHeight, wallHeight);

        bldg.addPart(hall);

        return bldg;
    }

    private RectBuildingPart createHall(BlockAreac rc, Orientation o, int floorHeight, int wallHeight) {
        Roof roof = new HipRoof(rc, rc.expand(1, 1, new BlockArea(BlockArea.INVALID)), floorHeight + wallHeight, 1f);
        RectBuildingPart hall = new RectBuildingPart(rc, roof, floorHeight, wallHeight);
        Vector2i doorPos = Edges.getCorner(rc, o.getOpposite());
        hall.addDoor(new SimpleDoor(o, doorPos, floorHeight, floorHeight + 2));
        hall.addWindow(new SimpleWindow(o.getRotated(90), Edges.getCorner(rc, o.getRotated(90)), floorHeight + 1));
        hall.addWindow(new SimpleWindow(o.getRotated(270), Edges.getCorner(rc, o.getRotated(270)), floorHeight + 1));
        hall.addWindow(new SimpleWindow(o, Edges.getCorner(rc, o), floorHeight + 1));
        return hall;
    }

    private RectBuildingPart createHallway(BlockAreac rc, Orientation o, int floorHeight, int wallHeight) {
        HipRoof roof = new HipRoof(rc, rc, floorHeight + wallHeight, 1f);
        RectBuildingPart hallway = new RectBuildingPart(rc, roof, floorHeight, wallHeight);
        hallway.addDoor(new SimpleDoor(o, Edges.getCorner(rc, o.getOpposite()), floorHeight, floorHeight + 2));
        hallway.addDoor(new SimpleDoor(o, Edges.getCorner(rc, o), floorHeight, floorHeight + 2));
        hallway.addWindow(new SimpleWindow(o.getRotated(90), Edges.getCorner(rc, o.getRotated(90)), floorHeight + 1));
        hallway.addWindow(new SimpleWindow(o.getRotated(270), Edges.getCorner(rc, o.getRotated(270)), floorHeight + 1));
        return hallway;
    }

}
