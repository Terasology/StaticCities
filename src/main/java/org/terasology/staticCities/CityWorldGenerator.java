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

package org.terasology.staticCities;

import org.joml.Vector3fc;
import org.terasology.core.world.generator.facetProviders.SeaLevelProvider;
import org.terasology.core.world.generator.facetProviders.SimplexHumidityProvider;
import org.terasology.core.world.generator.facetProviders.SurfaceToDensityProvider;
import org.terasology.core.world.generator.rasterizers.FloraRasterizer;
import org.terasology.core.world.generator.rasterizers.SolidRasterizer;
import org.terasology.core.world.generator.rasterizers.TreeRasterizer;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.spawner.Spawner;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.generation.BaseFacetedWorldGenerator;
import org.terasology.engine.world.generation.WorldBuilder;
import org.terasology.engine.world.generator.RegisterWorldGenerator;
import org.terasology.engine.world.generator.plugin.WorldGeneratorPluginLibrary;
import org.terasology.staticCities.bldg.BuildingFacetProvider;
import org.terasology.staticCities.blocked.BlockedAreaFacetProvider;
import org.terasology.staticCities.deco.ColumnRasterizer;
import org.terasology.staticCities.deco.DecorationFacetProvider;
import org.terasology.staticCities.deco.SingleBlockRasterizer;
import org.terasology.staticCities.door.DoorFacetProvider;
import org.terasology.staticCities.door.SimpleDoorRasterizer;
import org.terasology.staticCities.door.WingDoorRasterizer;
import org.terasology.staticCities.fences.FenceFacetProvider;
import org.terasology.staticCities.fences.SimpleFenceRasterizer;
import org.terasology.staticCities.flora.FloraFacetProvider;
import org.terasology.staticCities.flora.TreeFacetProvider;
import org.terasology.staticCities.lakes.LakeFacetProvider;
import org.terasology.staticCities.parcels.ParcelFacetProvider;
import org.terasology.staticCities.raster.standard.HollowBuildingPartRasterizer;
import org.terasology.staticCities.raster.standard.RectPartRasterizer;
import org.terasology.staticCities.raster.standard.RoundPartRasterizer;
import org.terasology.staticCities.raster.standard.StaircaseRasterizer;
import org.terasology.staticCities.roads.RoadFacetProvider;
import org.terasology.staticCities.roads.RoadRasterizer;
import org.terasology.staticCities.roof.ConicRoofRasterizer;
import org.terasology.staticCities.roof.DomeRoofRasterizer;
import org.terasology.staticCities.roof.FlatRoofRasterizer;
import org.terasology.staticCities.roof.HipRoofRasterizer;
import org.terasology.staticCities.roof.PentRoofRasterizer;
import org.terasology.staticCities.roof.RoofFacetProvider;
import org.terasology.staticCities.roof.SaddleRoofRasterizer;
import org.terasology.staticCities.settlements.SettlementFacetProvider;
import org.terasology.staticCities.sites.SiteFacetProvider;
import org.terasology.staticCities.surface.ElevationFacetProvider;
import org.terasology.staticCities.surface.InfiniteSurfaceHeightFacetProvider;
import org.terasology.staticCities.terrain.BuildableTerrainFacetProvider;
import org.terasology.staticCities.walls.TownWallFacetProvider;
import org.terasology.staticCities.walls.TownWallRasterizer;
import org.terasology.staticCities.window.RectWindowRasterizer;
import org.terasology.staticCities.window.SimpleWindowRasterizer;
import org.terasology.staticCities.window.WindowFacetProvider;

@RegisterWorldGenerator(id = "staticCities", displayName = "Static City World")
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
    public Vector3fc getSpawnPosition(EntityRef entity) {
        return spawner.getSpawnPosition(getWorld(), entity);
    }

    @Override
    protected WorldBuilder createWorld() {
        int seaLevel = 2;

        theme = BlockTheme.builder(blockManager)
            .register(DefaultBlockType.ROAD_FILL, "CoreAssets:Dirt")
            .register(DefaultBlockType.ROAD_SURFACE, "CoreAssets:Gravel")
            .register(DefaultBlockType.LOT_EMPTY, "CoreAssets:Dirt")
            .register(DefaultBlockType.BUILDING_WALL, "StructuralResources:StoneBlocks")
            .register(DefaultBlockType.BUILDING_FLOOR, "StructuralResources:StoneBlocksDark")
            .register(DefaultBlockType.BUILDING_FOUNDATION, "CoreAssets:Gravel")
            .register(DefaultBlockType.TOWER_STAIRS, "CoreAssets:CobbleStone")
            .register(DefaultBlockType.ROOF_FLAT, "StructuralResources:RoofTilesLarge")
            .register(DefaultBlockType.ROOF_HIP, "StructuralResources:PlanksEvenDark")
            .register(DefaultBlockType.ROOF_SADDLE, "StructuralResources:PlanksEvenDark")
            .register(DefaultBlockType.ROOF_DOME, "CoreAssets:Plank")
            .register(DefaultBlockType.ROOF_GABLE, "CoreAssets:Plank")
            .register(DefaultBlockType.SIMPLE_DOOR, BlockManager.AIR_ID)
            .register(DefaultBlockType.WING_DOOR, BlockManager.AIR_ID)
            .register(DefaultBlockType.WINDOW_GLASS, BlockManager.AIR_ID)
            .register(DefaultBlockType.TOWER_WALL, "StructuralResources:StoneBlocks")

             // -- requires Fences module
            .registerFamily(DefaultBlockType.FENCE, "Fences:Fence")
            .registerFamily(DefaultBlockType.FENCE_GATE, BlockManager.AIR_ID)  // there is no fence gate :-(
            .registerFamily(DefaultBlockType.TOWER_STAIRS, "CoreAssets:CobbleStone:engine:stair")
            .registerFamily(DefaultBlockType.BARREL, "StructuralResources:Barrel")
            .registerFamily(DefaultBlockType.LADDER, "CoreAssets:Ladder")
            .registerFamily(DefaultBlockType.PILLAR_BASE, "CoreAssets:CobbleStone:StructuralResources:pillarBase")
            .registerFamily(DefaultBlockType.PILLAR_MIDDLE, "CoreAssets:CobbleStone:StructuralResources:pillar")
            .registerFamily(DefaultBlockType.PILLAR_TOP, "CoreAssets:CobbleStone:StructuralResources:pillarTop")
            .registerFamily(DefaultBlockType.TORCH, "CoreAssets:Torch")

            .build();

        SimplexHumidityProvider.Configuration humidityConfig = new SimplexHumidityProvider.Configuration();
        humidityConfig.octaves = 4;
        humidityConfig.scale = 0.5f;

        WorldBuilder worldBuilder = new WorldBuilder(CoreRegistry.get(WorldGeneratorPluginLibrary.class))
                .setSeaLevel(seaLevel)
                .addProvider(new SeaLevelProvider(seaLevel))
                .addProvider(new InfiniteSurfaceHeightFacetProvider())
                .addProvider(new ElevationFacetProvider())
                .addProvider(new SurfaceToDensityProvider())
                .addProvider(new BuildableTerrainFacetProvider())
                .addProvider(new BlockedAreaFacetProvider())
                .addProvider(new LakeFacetProvider())
                .addProvider(new SimplexHumidityProvider(humidityConfig))
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
