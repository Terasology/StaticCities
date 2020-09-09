// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities;

import com.google.common.base.Function;
import org.terasology.commonworld.Sector;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.chunks.CoreChunk;
import org.terasology.engine.world.generator.ChunkGenerationPass;
import org.terasology.math.geom.Vector2i;

import java.util.Collections;
import java.util.Map;

/**
 * Draws chunk and sector borders
 */
public class BoundaryGenerator implements ChunkGenerationPass {

    private final Function<Vector2i, Integer> heightMap;

    private final BlockManager blockManager = CoreRegistry.get(BlockManager.class);
    private final Block chunkBorder = blockManager.getBlock("StructuralResources:BlackMarble");
    private final Block sectorBorder = blockManager.getBlock("StructuralResources:PinkMarble");

    /**
     * @param heightMap the height map to use
     */
    public BoundaryGenerator(Function<Vector2i, Integer> heightMap) {
        this.heightMap = heightMap;
    }

    @Override
    public void setWorldSeed(String seed) {
        // ignore
    }

    @Override
    public Map<String, String> getInitParameters() {
        return Collections.emptyMap();
    }

    @Override
    public void setInitParameters(Map<String, String> initParameters) {
        // ignore
    }

    @Override
    public void generateChunk(CoreChunk chunk) {

        int wx = chunk.chunkToWorldPositionX(0);
        int wz = chunk.chunkToWorldPositionZ(0);

        for (int z = 0; z < chunk.getChunkSizeZ(); z++) {
            for (int x = 0; x < chunk.getChunkSizeX(); x++) {
                Block block = null;
                int y = heightMap.apply(new Vector2i(wx + x, wz + z));

                if (x == 0 || z == 0) {
                    block = chunkBorder;
                }

                if (x == 0 && (wx % Sector.SIZE == 0)) {
                    block = sectorBorder;
                }

                if (z == 0 && (wz % Sector.SIZE == 0)) {
                    block = sectorBorder;
                }

                if (block != null) {
                    chunk.setBlock(x, y, z, block);
                }
            }
        }

    }

}
