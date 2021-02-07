/*
 * Copyright 2014 MovingBlocks
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
package org.terasology.staticCities.roads;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.terasology.commonworld.geom.BoundingBox;
import org.terasology.commonworld.geom.Line2f;
import org.terasology.commonworld.geom.Ramp;
import org.terasology.math.TeraMath;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.DefaultBlockType;
import org.terasology.staticCities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.world.block.BlockArea;
import org.terasology.world.block.BlockRegion;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RoadRasterizer implements WorldRasterizer {

    private BlockTheme blockTheme;

    /**
     * @param blockTheme the block these to use
     */
    public RoadRasterizer(BlockTheme blockTheme) {
        this.blockTheme = blockTheme;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        RoadFacet roadFacet = chunkRegion.getFacet(RoadFacet.class);
        InfiniteSurfaceHeightFacet heightFacet = chunkRegion.getFacet(InfiniteSurfaceHeightFacet.class);

        BlockRegion reg = chunkRegion.getRegion();
        BlockArea rc = new BlockArea(reg.minX(), reg.minZ(), reg.maxX(), reg.maxZ());

        // first compute the collection of road segments that could be relevant
        // TODO: use Line/Rectangle for each segment instead (include road width!)
        Collection<RoadSegment> segs = new ArrayList<>();
        for (Road road : roadFacet.getRoads()) {
            // TODO: check y component as well
            BoundingBox bbox = new BoundingBox();
            for (Vector2ic pt : road.getPoints()) {
                bbox.add(pt);
            }
            int intRad = TeraMath.ceilToInt(road.getWidth() * 0.5f);
            BlockArea roadBox = bbox.toRect2i().expand(intRad, intRad, new BlockArea(BlockArea.INVALID));

            if (roadBox.intersectsBlockArea(rc)) {
                for (RoadSegment seg : road.getSegments()) {
                    segs.add(seg);
                }
            }
        }

        // compute Ramp geometry for a RoadSegment on demand
        Map<RoadSegment, Ramp> ramps = new HashMap<>();
        Function<RoadSegment, Ramp> createRamp = s -> {
            int heightA = TeraMath.floorToInt(heightFacet.getWorld(s.getStart()));
            int heightB = TeraMath.floorToInt(heightFacet.getWorld(s.getEnd()));
            return new Ramp(
                    s.getStart().x(), heightA, s.getStart().y(),
                    s.getEnd().x(), heightB, s.getEnd().y());
        };

        Vector2i pos = new Vector2i();
        for (int z = rc.minY(); z <= rc.maxY(); z++) {
            for (int x = rc.minX(); x <= rc.maxX(); x++) {
                int heightP = TeraMath.floorToInt(heightFacet.getWorld(x, z));
                pos.set(x, z);

                for (RoadSegment seg : segs) {
                    Vector2ic pointA = seg.getStart();
                    Vector2ic pointB = seg.getEnd();
                    float rad = seg.getWidth() * 0.5f;
                    int y = Integer.MIN_VALUE;

                    // first check if close to start/end point
                    // if so, create a flat circle around it
                    // this ensures that junctions of multiple roads result in a nice common area
                    if (pointA.distanceSquared(pos) < rad * rad) {
                        y = TeraMath.floorToInt(heightFacet.getWorld(pointA));
                    } else if (pointB.distanceSquared(pos) < rad * rad) {
                        y = TeraMath.floorToInt(heightFacet.getWorld(pointB));
                    } else if (Line2f.distanceToPoint(pointA.x(), pointA.y(),
                            pointB.x(), pointB.y(), x, z) < rad) {

                        Ramp ramp = ramps.computeIfAbsent(seg, createRamp);
                        y = TeraMath.floorToInt(ramp.getClampedY(x, z));
                    }

                    // a simple trick to check if pos is on a road
                    if (y > Integer.MIN_VALUE) {
                        int cx = x - chunk.getChunkWorldOffsetX();
                        int cz = z - chunk.getChunkWorldOffsetZ();

                        // fill up with air until default surface height is reached
                        for (int i = Math.max(reg.minY(), y + 1); i <= Math.min(reg.maxY(), heightP); i++) {
                            int cy = i - chunk.getChunkWorldOffsetY();
                            chunk.setBlock(cx, cy, cz, blockTheme.apply(DefaultBlockType.AIR));
                        }

                        // fill up with dirt (top soil layer inclusive) until road height is reached
                        for (int i = Math.max(reg.minY(), heightP); i <= Math.min(reg.maxY(), y - 1); i++) {
                            int cy = i - chunk.getChunkWorldOffsetY();
                            chunk.setBlock(cx, cy, cz, blockTheme.apply(DefaultBlockType.ROAD_FILL));
                        }

                        // put actual road layer
                        if (y >= reg.minY() && y <= reg.maxY()) {
                            int cy = y - chunk.getChunkWorldOffsetY();
                            chunk.setBlock(cx, cy, cz, blockTheme.apply(DefaultBlockType.ROAD_SURFACE));
                        }
                    }
                }
            }
        }
    }
}
