// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.deco;

import com.google.common.collect.ImmutableMap;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.engine.world.viewer.layers.AbstractFacetLayer;
import org.terasology.engine.world.viewer.layers.Renders;
import org.terasology.engine.world.viewer.layers.ZOrder;
import org.terasology.math.TeraMath;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.BlockType;
import org.terasology.staticCities.DefaultBlockType;
import org.terasology.staticCities.raster.ImageRasterTarget;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Draws decorations in a given image
 */
@Renders(value = DecorationFacet.class, order = ZOrder.BIOME + 5)
public class DecorationFacetLayer extends AbstractFacetLayer {

    private final BufferedImage bufferImage = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);

    private final Map<BlockType, Color> blockColors = ImmutableMap.<BlockType, Color>builder()
            .put(DefaultBlockType.BARREL, new Color(110, 110, 10))
            .put(DefaultBlockType.TORCH, new Color(240, 240, 10))
            .put(DefaultBlockType.LADDER, new Color(180, 80, 80))
            .put(DefaultBlockType.PILLAR_BASE, new Color(210, 210, 210))
            .put(DefaultBlockType.PILLAR_MIDDLE, new Color(180, 180, 180))
            .put(DefaultBlockType.PILLAR_TOP, new Color(150, 150, 150))
            .build();

    private final Set<DecorationRasterizer<?>> rasterizers = new HashSet<>();

    public DecorationFacetLayer() {
        setVisible(true);

        BlockTheme theme = null;
        rasterizers.add(new SingleBlockRasterizer(theme));
        rasterizers.add(new ColumnRasterizer(theme));
    }

    @Override
    public void render(BufferedImage img, Region region) {

        int wx = region.getRegion().minX();
        int wz = region.getRegion().minZ();
        ImageRasterTarget brush = new ImageRasterTarget(wx, wz, img, blockColors::get);
        render(brush, region);
    }

    private void render(ImageRasterTarget brush, Region chunkRegion) {
        SurfaceHeightFacet heightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        HeightMap hm = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                return TeraMath.floorToInt(heightFacet.getWorld(x, z));
            }
        };

        DecorationFacet facet = chunkRegion.getFacet(DecorationFacet.class);
        for (Decoration deco : facet.getDecorations()) {
            for (DecorationRasterizer<?> rasterizer : rasterizers) {
                rasterizer.tryRaster(brush, deco, hm);
            }
        }
    }

    @Override
    public String getWorldText(Region region, int wx, int wy) {
        int dx = bufferImage.getWidth() / 2;
        int dy = bufferImage.getHeight() / 2;
        ImageRasterTarget brush = new ImageRasterTarget(wx - dx, wy - dy, bufferImage, blockColors::get);
        render(brush, region);

        int height = brush.getHeight(wx, wy);
        BlockType type = brush.getBlockType(wx, wy);
        return type == null ? null : type.toString() + "(" + height + ")";
    }
}
