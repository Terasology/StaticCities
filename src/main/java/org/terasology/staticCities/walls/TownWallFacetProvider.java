// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.walls;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.commonworld.Orientation;
import org.terasology.commonworld.geom.CircleUtility;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.Updates;
import org.terasology.joml.geom.Circlef;
import org.terasology.joml.geom.Rectanglef;
import org.terasology.math.TeraMath;
import org.terasology.staticCities.bldg.BuildingFacet;
import org.terasology.staticCities.bldg.BuildingPart;
import org.terasology.staticCities.bldg.RectBuildingPart;
import org.terasology.staticCities.bldg.SimpleTower;
import org.terasology.staticCities.bldg.Tower;
import org.terasology.staticCities.blocked.BlockedAreaFacet;
import org.terasology.staticCities.common.Edges;
import org.terasology.staticCities.deco.Decoration;
import org.terasology.staticCities.deco.DecorationFacet;
import org.terasology.staticCities.door.Door;
import org.terasology.staticCities.door.DoorFacet;
import org.terasology.staticCities.door.WingDoor;
import org.terasology.staticCities.roof.RoofFacet;
import org.terasology.staticCities.settlements.Settlement;
import org.terasology.staticCities.settlements.SettlementFacet;
import org.terasology.staticCities.sites.Site;
import org.terasology.staticCities.sites.SiteFacet;
import org.terasology.staticCities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.staticCities.terrain.BuildableTerrainFacet;
import org.terasology.staticCities.window.SimpleWindow;
import org.terasology.staticCities.window.Window;
import org.terasology.staticCities.window.WindowFacet;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Generates a {@link TownWall} around a given settlement
 * while respecting blocked areas
 */
@Produces(TownWallFacet.class)
@Updates(@Facet(BuildingFacet.class)) // TODO: find out why specifying door/window/roof does not work
@Requires({
    @Facet(SiteFacet.class),
    @Facet(BlockedAreaFacet.class),
    @Facet(BuildableTerrainFacet.class),
    @Facet(InfiniteSurfaceHeightFacet.class)})
public class TownWallFacetProvider implements FacetProvider {

    private static final Logger logger = LoggerFactory.getLogger(TownWallFacetProvider.class);

    private final Cache<Site, TownWall> cache = CacheBuilder.newBuilder().build();

    private long seed;

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public void process(GeneratingRegion region) {
        InfiniteSurfaceHeightFacet heightFacet = region.getRegionFacet(InfiniteSurfaceHeightFacet.class);
        TownWallFacet wallFacet = new TownWallFacet(region.getRegion(), region.getBorderForFacet(TownWallFacet.class));
        SettlementFacet settlementFacet = region.getRegionFacet(SettlementFacet.class);
        BlockedAreaFacet blockedAreaFacet = region.getRegionFacet(BlockedAreaFacet.class);
        BuildableTerrainFacet buildableAreaFacet = region.getRegionFacet(BuildableTerrainFacet.class);
        BuildingFacet buildingFacet = region.getRegionFacet(BuildingFacet.class);

        WindowFacet windowFacet = region.getRegionFacet(WindowFacet.class);
        RoofFacet roofFacet = region.getRegionFacet(RoofFacet.class);
        DoorFacet doorFacet = region.getRegionFacet(DoorFacet.class);
        DecorationFacet decoFacet = region.getRegionFacet(DecorationFacet.class);

        for (Settlement settlement : settlementFacet.getSettlements()) {
            if (settlement.hasTownwall()) {
                Site site = settlement.getSite();
                BlockAreac area = wallFacet.getWorldArea();
                if (CircleUtility.intersect(new Circlef(site.getPos().x(), site.getPos().y(), site.getRadius()), new BlockArea(area.minX(), area.minY(), area.maxX(), area.maxY()).getBounds(new Rectanglef()))) {
                    try {
                        TownWall tw = cache.get(site, () -> generate(site, heightFacet, buildableAreaFacet, blockedAreaFacet));
                        wallFacet.addTownWall(tw);
                        for (Tower tower : tw.getTowers()) {
                            buildingFacet.addBuilding(tower);

                            // TODO: add bounds check
                            for (BuildingPart part : tower.getParts()) {
                                for (Window wnd : part.getWindows()) {
                                    windowFacet.addWindow(wnd);
                                }
                                for (Door door : part.getDoors()) {
                                    doorFacet.addDoor(door);
                                }
                                for (Decoration deco : part.getDecorations()) {
                                    decoFacet.addDecoration(deco);
                                }
                                roofFacet.addRoof(part.getRoof());
                            }
                        }
                    } catch (ExecutionException e) {
                        logger.error("Could not compute buildings for {}", region.getRegion(), e);
                    }
                }
            }
        }

        region.setRegionFacet(TownWallFacet.class, wallFacet);
    }

    private TownWall generate(Site city, InfiniteSurfaceHeightFacet heightFacet, BuildableTerrainFacet buildableAreaFacet, BlockedAreaFacet blockedAreaFacet) {

        Random rand = new FastRandom(seed ^ city.getPos().hashCode());

        int cx = city.getPos().x();
        int cz = city.getPos().y();

        Vector2i center = new Vector2i(cx, cz);

        int maxWallThick = 4;
        float maxRad = city.getRadius() - maxWallThick * 0.5f;


        TownWall tw = new TownWall();

        float sampleDist = 15; // average distance between two consecutive potential tower locations
        float minDist = 50;  // minimum distance between two consecutive towers in blocks

        // generate towers first
        List<Vector2i> tp = getTowerPosList(rand, center, maxRad, sampleDist);
        List<Boolean> blocked = getBlockedPos(tp, buildableAreaFacet, blockedAreaFacet);
        SimpleTower lastTower = null;
        SimpleTower firstTower = null;
        int lastHit = Short.MIN_VALUE;
        int firstHit = Short.MIN_VALUE;
        int count = tp.size();

        for (int i = 0; i < count; i++) {

            int nextIndex = (i + 1) % count;
            int prevIndex = (i + count - 1) % count;

            boolean thisOk = blocked.get(i);
            boolean nextOk = blocked.get(nextIndex);
            boolean prevOk = blocked.get(prevIndex);

            Vector2i towerPos = tp.get(i);

            if (thisOk) {
                Vector2i dirFromCenter = new Vector2i(center).sub(towerPos);
                Orientation orient = Orientation.cardinalFromDirection(dirFromCenter);

                if (!prevOk) {            // the previous location was blocked -> this is the second part of a gate
                    SimpleTower tower = createGateTower(orient, heightFacet, towerPos);
                    tw.addTower(tower);

                    if (firstHit < 0) {
                        firstHit = i;
                        firstTower = tower;
                    }

                    if (lastHit >= 0) {
                        Vector2i start = getAnchor(lastTower, -90);
                        Vector2i end = getAnchor(tower, 90);
                        tw.addWall(createGateWall(start, end));
                    }
                    lastHit = i;
                    lastTower = tower;
                } else

                if (!nextOk) {            // the next location is blocked -> this is the first part of a gate
                    SimpleTower tower = createGateTower(orient, heightFacet, towerPos);
                    tw.addTower(tower);

                    if (firstHit < 0) {
                        firstHit = i;
                        firstTower = tower;
                    }
                    if (lastHit >= 0) {
                        Vector2i start = getAnchor(lastTower, -90);
                        Vector2i end = getAnchor(tower, 90);

                        tw.addWall(createSolidWall(start, end));
                    }
                    lastHit = i;
                    lastTower = tower;
                } else

                if (distToTower(towerPos, firstTower) > minDist && distToTower(towerPos, lastTower) > minDist) {
                    // the last/next tower is too far away -> place another one
                    SimpleTower tower = createTower(orient, heightFacet, towerPos);
                    tw.addTower(tower);

                    if (firstHit < 0) {
                        firstHit = i;
                        firstTower = tower;
                    }

                    if (lastHit >= 0) {
                        Vector2i start = getAnchor(lastTower, -90);
                        Vector2i end = getAnchor(tower, 90);
                        tw.addWall(createSolidWall(start, end));
                    }
                    lastHit = i;
                    lastTower = tower;
                }
            }
        }

        // connect first and last tower to close the circle
        // --> this could be a gate segment
        // --> TODO: find out
        if (firstHit >= 0 && lastHit >= 0) {
            Vector2i start = getAnchor(lastTower, -90);
            Vector2i end = getAnchor(firstTower, 90);
            tw.addWall(createSolidWall(start, end));
        }

        return tw;
    }

    private float distToTower(Vector2i pos, SimpleTower tower) {
        if (tower == null) {
            return Float.POSITIVE_INFINITY; // larger than any finite number
        }
        BlockAreac rc = tower.getShape();
        float cx = (rc.minX() + rc.maxX()) * 0.5f;
        float cy = (rc.minY() + rc.maxY()) * 0.5f;
        float dx = pos.x() - cx;
        float dy = pos.y() - cy;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private Vector2i getAnchor(SimpleTower tower, int degrees) {
        Orientation orientation = tower.getOrientation();
        BlockAreac lastRect = tower.getStaircase().getShape();
        return Edges.getCorner(lastRect, orientation.getRotated(degrees));
    }

    private WallSegment createSolidWall(Vector2i start, Vector2i end) {
        int wallThick = 4;
        int wallHeight = 8;

        WallSegment wall = new SolidWallSegment(start, end, wallThick, wallHeight);
        return wall;
    }

    private WallSegment createGateWall(Vector2ic start, Vector2ic end) {
        int wallHeight = 8;
        int wallThick = 4;

        WallSegment wall = new GateWallSegment(start, end, wallThick, wallHeight);
        return wall;
    }

    private List<Boolean> getBlockedPos(List<Vector2i> tp, BuildableTerrainFacet buildable, BlockedAreaFacet blocked) {
        List<Boolean> list = Lists.newArrayList();

        for (Vector2i pos : tp) {
            BlockAreac layout = getTowerRect(pos);
            boolean ok = buildable.isPassable(layout) && !blocked.isBlocked(layout);
            list.add(Boolean.valueOf(ok));
        }

        return list;
    }

    private SimpleTower createTower(int towerHeight, Orientation orient, InfiniteSurfaceHeightFacet hm, Vector2i pos) {
        int baseHeight = TeraMath.floorToInt(hm.getWorld(pos)) + 1;

        BlockAreac layout = getTowerRect(pos);
        int wndHeight = 4 + 1;
        SimpleTower tower = new SimpleTower(orient, layout, baseHeight, towerHeight);
        Vector2i windowPos = new Vector2i(Edges.getCorner(layout, orient));
        tower.getStaircase().addWindow(new SimpleWindow(orient.getOpposite(), windowPos, baseHeight + wndHeight));
        return tower;
    }

    private SimpleTower createTower(Orientation orient, InfiniteSurfaceHeightFacet hm, Vector2i towerPos) {
        int towerHeight = 9;
        SimpleTower tower = createTower(towerHeight, orient, hm, towerPos);
        RectBuildingPart staircase = tower.getStaircase();
        BlockAreac staircaseRect = staircase.getShape().expand(-1, -1, new BlockArea(BlockArea.INVALID)); // inner staircase
        Vector2i c1 = Edges.getCorner(staircaseRect, orient.getRotated(270 - 45));
        Vector2i c2 = Edges.getCorner(staircaseRect, orient.getRotated(270 + 45));
        BlockArea doorRect = new BlockArea(c1).union(c2);
        int roofLevel = staircase.getTopHeight();
        staircase.addDoor(new WingDoor(orient.getOpposite(), doorRect, roofLevel, roofLevel + 1));
        return tower;
    }

    private SimpleTower createGateTower(Orientation orient, InfiniteSurfaceHeightFacet hm, Vector2i towerPos) {
        int towerHeight = 10;
        SimpleTower tower = createTower(towerHeight, orient, hm, towerPos);
        return tower;
    }

    private List<Vector2i> getTowerPosList(Random rand, Vector2i center, double maxRad, double sampleDist) {

        double step = sampleDist / maxRad;

        double maxRadiusDiv = maxRad * 0.1;
        double maxAngularDiv = step * 0.1;

        List<Vector2i> list = Lists.newArrayList();

        double ang = rand.nextDouble(0, Math.PI);   // actually, the range doesn't matter
        double endAng = ang + Math.PI * 2.0 - step; // subtract one step to avoid duplicate first/last position

        while (ang < endAng) {
            double rad = maxRad - rand.nextDouble() * maxRadiusDiv;

            int tx = center.x + (int) (rad * Math.cos(ang));
            int ty = center.y + (int) (rad * Math.sin(ang));

            list.add(new Vector2i(tx, ty));

            double angDiv = (rand.nextDouble() - 0.5) * maxAngularDiv;
            ang += step + angDiv;
       }

        return list;
    }

    private BlockArea getTowerRect(Vector2i tp) {
        int towerRad = 3;

        // make sure the width/height are odd to make the BattlementRoof look pretty
        return new BlockArea(tp.x - towerRad, tp.y - towerRad).setSize(towerRad * 2 - 1, towerRad * 2 - 1);
    }
}
