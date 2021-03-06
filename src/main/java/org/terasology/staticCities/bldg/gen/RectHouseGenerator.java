// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.bldg.gen;

import com.google.common.collect.Sets;
import org.joml.RoundingMode;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3i;
import org.terasology.commonworld.Orientation;
import org.terasology.commonworld.geom.Line2f;
import org.terasology.engine.math.Side;
import org.terasology.engine.utilities.random.MersenneRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.math.TeraMath;
import org.terasology.staticCities.DefaultBlockType;
import org.terasology.staticCities.bldg.Building;
import org.terasology.staticCities.bldg.DefaultBuilding;
import org.terasology.staticCities.bldg.RectBuildingPart;
import org.terasology.staticCities.common.Edges;
import org.terasology.staticCities.deco.SingleBlockDecoration;
import org.terasology.staticCities.door.SimpleDoor;
import org.terasology.staticCities.model.roof.DomeRoof;
import org.terasology.staticCities.model.roof.HipRoof;
import org.terasology.staticCities.model.roof.Roof;
import org.terasology.staticCities.model.roof.SaddleRoof;
import org.terasology.staticCities.parcels.StaticParcel;
import org.terasology.staticCities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.staticCities.window.SimpleWindow;

import java.util.Set;

/**
 *
 */
public class RectHouseGenerator {

    public Building apply(StaticParcel staticParcel, InfiniteSurfaceHeightFacet hm) {

        // use the rectangle, not the lot itself, because its hashcode is the identity hashcode
        Random rng = new MersenneRandom(staticParcel.getShape().hashCode());

        Orientation o = staticParcel.getOrientation();
        DefaultBuilding bldg = new DefaultBuilding(o);
        int inset = 2;
        BlockArea layout = staticParcel.getShape().expand(-inset, -inset, new BlockArea(BlockArea.INVALID));

        Vector2i doorPos = Edges.getCorner(layout, o);

        // use door as base height for the entire building
        Vector2ic doorDir = o.direction();
        Vector2i probePos = new Vector2i(doorPos.x() + doorDir.x(), doorPos.y() + doorDir.y());

        // we add +1, because the building starts at 1 block above the terrain
        int floorHeight = TeraMath.floorToInt(hm.getWorld(probePos)) + 1;
        int wallHeight = 3;

        int roofBaseHeight = floorHeight + wallHeight;

        Roof roof = createRoof(rng, layout, roofBaseHeight);

        RectBuildingPart part = new RectBuildingPart(layout, roof, floorHeight, wallHeight);
        bldg.addPart(part);

        int doorHeight = 2;

        SimpleDoor door = new SimpleDoor(o, doorPos, floorHeight, floorHeight + doorHeight);
        part.addDoor(door);

        for (int i = 0; i < 3; i++) {
            // use the other three cardinal directions to place windows
            Orientation orient = o.getRotated(90 * (i + 1));
            Set<SimpleWindow> wnds = createWindows(layout, floorHeight, orient);

            for (SimpleWindow wnd : wnds) {
                // test if terrain outside window is lower than window base height
                Vector2ic wndDir = wnd.getOrientation().direction();
                Vector2ic wndPos = wnd.getPos();
                Vector2i probePosWnd = new Vector2i(wndPos.x() + wndDir.x(), wndPos.y() + wndDir.y());
                if (wnd.getHeight() > hm.getWorld(probePosWnd)) {
                    part.addWindow(wnd);
                }
            }
        }

        addDecorations(part, o.getOpposite(), floorHeight, rng);

        return bldg;
    }

    private void addDecorations(RectBuildingPart part, Orientation o, int baseHeight, Random rng) {
        BlockArea rc = part.getShape().expand(-1, -1, new BlockArea(BlockArea.INVALID)); // inside
        if (rng.nextBoolean()) {
            Vector2i pos = Edges.getCorner(rc, o.getRotated(-45));
            Vector3i pos3d = new Vector3i(pos.x(), baseHeight, pos.y());
            part.addDecoration(new SingleBlockDecoration(DefaultBlockType.BARREL, pos3d, Side.FRONT));
        }
        if (rng.nextBoolean()) {
            Vector2i pos = Edges.getCorner(rc, o.getRotated(45));
            Vector3i pos3d = new Vector3i(pos.x(), baseHeight, pos.y());
            part.addDecoration(new SingleBlockDecoration(DefaultBlockType.BARREL, pos3d, Side.FRONT));
        }
    }

    private Set<SimpleWindow> createWindows(BlockAreac rc, int baseHeight, Orientation o) {

        final int wndBase = baseHeight + 1;
        final int endDist = 2;
        final int interDist = 2;
        final int wndSize = 1;

        Set<SimpleWindow> result = Sets.newHashSet();

        Line2f borderSeg = Edges.getEdge(rc, o);
        BlockArea border = new BlockArea(new Vector2i(borderSeg.getStart(), RoundingMode.FLOOR)).union(new Vector2i(borderSeg.getEnd(), RoundingMode.FLOOR));
        int step = interDist + wndSize;

        int firstX = border.minX() + endDist;
        int lastX = border.minX() + border.getSizeX() - endDist * 2;

        for (int x = firstX; x <= lastX; x += step) {
            Vector2i pos = new Vector2i(x, border.minY());
            SimpleWindow w = new SimpleWindow(o, pos, wndBase);
            result.add(w);
        }

        int firstY = border.minY() + endDist;
        int lastY = border.minY() + border.getSizeY() - endDist * 2;

        for (int y = firstY; y <= lastY; y += step) {
            Vector2i pos = new Vector2i(border.minX(), y);
            SimpleWindow w = new SimpleWindow(o, pos, wndBase);
            result.add(w);
        }

        return result;
    }

    private Roof createRoof(Random r, BlockAreac layout, int roofBaseHeight) {
        // the roof area is 1 block larger all around
        BlockAreac roofArea = layout.expand(1, 1, new BlockArea(BlockArea.INVALID));

        int type = r.nextInt(100);

        if (type < 33) {
            int roofPitch = 1;
            return new HipRoof(layout, roofArea, roofBaseHeight, roofPitch, roofBaseHeight + 1);
        }

        if (type < 66) {
            return new DomeRoof(layout, roofArea, roofBaseHeight, Math.min(roofArea.getSizeX(), roofArea.getSizeY()) / 2);
        }

        boolean alongX = (roofArea.getSizeX() > roofArea.getSizeY());
        Orientation o = alongX ? Orientation.EAST : Orientation.NORTH;

        return new SaddleRoof(layout, roofArea, roofBaseHeight, o, 1);
    }

}
