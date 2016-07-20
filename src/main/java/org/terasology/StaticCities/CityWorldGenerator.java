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

package org.terasology.StaticCities;

import org.terasology.StaticCities.bldg.BuildingFacetProvider;
import org.terasology.StaticCities.blocked.BlockedAreaFacetProvider;
import org.terasology.StaticCities.deco.ColumnRasterizer;
import org.terasology.StaticCities.deco.DecorationFacetProvider;
import org.terasology.StaticCities.deco.SingleBlockRasterizer;
import org.terasology.StaticCities.door.DoorFacetProvider;
import org.terasology.StaticCities.door.SimpleDoorRasterizer;
import org.terasology.StaticCities.door.WingDoorRasterizer;
import org.terasology.StaticCities.fences.FenceFacetProvider;
import org.terasology.StaticCities.fences.SimpleFenceRasterizer;
import org.terasology.StaticCities.flora.FloraFacetProvider;
import org.terasology.StaticCities.flora.TreeFacetProvider;
import org.terasology.StaticCities.lakes.LakeFacetProvider;
import org.terasology.StaticCities.parcels.ParcelFacetProvider;
import org.terasology.StaticCities.raster.standard.HollowBuildingPartRasterizer;
import org.terasology.StaticCities.raster.standard.RectPartRasterizer;
import org.terasology.StaticCities.raster.standard.RoundPartRasterizer;
import org.terasology.StaticCities.raster.standard.StaircaseRasterizer;
import org.terasology.StaticCities.roads.RoadFacetProvider;
import org.terasology.StaticCities.roads.RoadRasterizer;
import org.terasology.StaticCities.roof.*;
import org.terasology.StaticCities.settlements.SettlementFacetProvider;
import org.terasology.StaticCities.sites.SiteFacetProvider;
import org.terasology.StaticCities.surface.InfiniteSurfaceHeightFacetProvider;
import org.terasology.StaticCities.surface.SurfaceHeightFacetProvider;
import org.terasology.StaticCities.terrain.BuildableTerrainFacetProvider;
import org.terasology.StaticCities.walls.TownWallFacetProvider;
import org.terasology.StaticCities.walls.TownWallRasterizer;
import org.terasology.StaticCities.window.RectWindowRasterizer;
import org.terasology.StaticCities.window.SimpleWindowRasterizer;
import org.terasology.StaticCities.window.WindowFacetProvider;
import org.terasology.core.world.generator.facetProviders.PerlinHumidityProvider;
import org.terasology.core.world.generator.facetProviders.SeaLevelProvider;
import org.terasology.core.world.generator.facetProviders.SurfaceToDensityProvider;
import org.terasology.core.world.generator.rasterizers.FloraRasterizer;
import org.terasology.core.world.generator.rasterizers.SolidRasterizer;
import org.terasology.core.world.generator.rasterizers.TreeRasterizer;
import org.terasology.engine.SimpleUri;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.spawner.Spawner;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.world.block.BlockManager;
import org.terasology.world.generation.BaseFacetedWorldGenerator;
import org.terasology.world.generation.WorldBuilder;
import org.terasology.world.generator.RegisterWorldGenerator;
import org.terasology.world.generator.plugin.WorldGeneratorPluginLibrary;

@RegisterWorldGenerator(id = "city", displayName = "City World")
public class CityWorldGenerator extends BaseFacetedWorldGenerator {

    private final Spawner spawner = new CitySpawner();

    @In
    private BlockManager blockManager;

    private BlockTheme theme;

    /**
     * @param uri the uri
     */
    public CityWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    public Vector3f getSpawnPosition(EntityRef entity) {
        return spawner.getSpawnPosition(getWorld(), entity);
    }

    @Override
    protected WorldBuilder createWorld() {
        int seaLevel = 2;

        theme = BlockTheme.builder(blockManager)
            .register(DefaultBlockType.ROAD_FILL, "core:dirt")
            .register(DefaultBlockType.ROAD_SURFACE, "core:Gravel")
            .register(DefaultBlockType.LOT_EMPTY, "core:dirt")
            .register(DefaultBlockType.BUILDING_WALL, "Cities:stonawall1")
            .register(DefaultBlockType.BUILDING_FLOOR, "Cities:stonawall1dark")
            .register(DefaultBlockType.BUILDING_FOUNDATION, "core:gravel")
            .register(DefaultBlockType.TOWER_STAIRS, "core:CobbleStone")
            .register(DefaultBlockType.ROOF_FLAT, "Cities:rooftiles2")
            .register(DefaultBlockType.ROOF_HIP, "Cities:wood3")
            .register(DefaultBlockType.ROOF_SADDLE, "Cities:wood3")
            .register(DefaultBlockType.ROOF_DOME, "core:plank")
            .register(DefaultBlockType.ROOF_GABLE, "core:plank")
            .register(DefaultBlockType.SIMPLE_DOOR, BlockManager.AIR_ID)
            .register(DefaultBlockType.WING_DOOR, BlockManager.AIR_ID)
            .register(DefaultBlockType.WINDOW_GLASS, BlockManager.AIR_ID)
            .register(DefaultBlockType.TOWER_WALL, "Cities:stonawall1")

             // -- requires Fences module
            .registerFamily(DefaultBlockType.FENCE, "Fences:Fence")
            .registerFamily(DefaultBlockType.FENCE_GATE, BlockManager.AIR_ID)  // there is no fence gate :-(
            .registerFamily(DefaultBlockType.TOWER_STAIRS, "core:CobbleStone:engine:stair")
            .registerFamily(DefaultBlockType.BARREL, "StructuralResources:Barrel")
            .registerFamily(DefaultBlockType.LADDER, "Core:Ladder")
            .registerFamily(DefaultBlockType.PILLAR_BASE, "core:CobbleStone:StructuralResources:pillarBase")
            .registerFamily(DefaultBlockType.PILLAR_MIDDLE, "core:CobbleStone:StructuralResources:pillar")
            .registerFamily(DefaultBlockType.PILLAR_TOP, "core:CobbleStone:StructuralResources:pillarTop")
            .registerFamily(DefaultBlockType.TORCH, "Core:Torch")

            .build();

        PerlinHumidityProvider.Configuration humidityConfig = new PerlinHumidityProvider.Configuration();
        humidityConfig.octaves = 4;
        humidityConfig.scale = 0.5f;

        WorldBuilder worldBuilder = new WorldBuilder(CoreRegistry.get(WorldGeneratorPluginLibrary.class))
                .setSeaLevel(seaLevel)
                .addProvider(new SeaLevelProvider(seaLevel))
                .addProvider(new InfiniteSurfaceHeightFacetProvider())
                .addProvider(new SurfaceHeightFacetProvider())
                .addProvider(new SurfaceToDensityProvider())
                .addProvider(new BuildableTerrainFacetProvider())
                .addProvider(new BlockedAreaFacetProvider())
                .addProvider(new LakeFacetProvider())
                .addProvider(new PerlinHumidityProvider(humidityConfig))
                .addProvider(new SimpleBiomeProvider())
                .addProvider(new SiteFacetProvider())
                .addProvider(new TownWallFacetProvider())
                .addProvider(new RoadFacetProvider())
                .addProvider(new ParcelFacetProvider())
                .addProvider(new FenceFacetProvider())
                .addProvider(new WindowFacetProvider())
                .addProvider(new DecorationFacetProvider())
                .addProvider(new DoorFacetProvider())
                .addProvider(new RoofFacetProvider())
                .addProvider(new BuildingFacetProvider())
                .addProvider(new SettlementFacetProvider())
                .addProvider(new FloraFacetProvider())
                .addProvider(new TreeFacetProvider())
                .addRasterizer(new SolidRasterizer())
                .addPlugins()
                .addEntities(new SettlementEntityProvider())
                .addRasterizer(new RoadRasterizer(theme))
                .addRasterizer(new TownWallRasterizer(theme))
                .addRasterizer(new SimpleFenceRasterizer(theme))
                .addRasterizer(new RectPartRasterizer(theme))
                .addRasterizer(new HollowBuildingPartRasterizer(theme))
                .addRasterizer(new RoundPartRasterizer(theme))
                .addRasterizer(new StaircaseRasterizer(theme))
                .addRasterizer(new FlatRoofRasterizer(theme))
                .addRasterizer(new SaddleRoofRasterizer(theme))
                .addRasterizer(new PentRoofRasterizer(theme))
                .addRasterizer(new HipRoofRasterizer(theme))
                .addRasterizer(new ConicRoofRasterizer(theme))
                .addRasterizer(new DomeRoofRasterizer(theme))
                .addRasterizer(new SimpleWindowRasterizer(theme))
                .addRasterizer(new RectWindowRasterizer(theme))
                .addRasterizer(new SimpleDoorRasterizer(theme))
                .addRasterizer(new WingDoorRasterizer(theme))
                .addRasterizer(new SingleBlockRasterizer(theme))
                .addRasterizer(new ColumnRasterizer(theme))
                .addRasterizer(new FloraRasterizer())
                .addRasterizer(new TreeRasterizer());
        return worldBuilder;
    }

    public void expungeCaches() {
        // TODO: clear all caches
    }

}
