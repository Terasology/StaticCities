// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.roof;

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
import org.terasology.staticCities.model.roof.Roof;
import org.terasology.staticCities.raster.ImageRasterTarget;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Draws roofs in a given image
 */
@Renders(value = RoofFacet.class, order = ZOrder.BIOME + 3)
public class RoofFacetLayer extends AbstractFacetLayer {

    private final BufferedImage bufferImage = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);

    private final Map<BlockType, Color> blockColors = ImmutableMap.<BlockType, Color>builder()
            .put(DefaultBlockType.AIR, new Color(0, 0, 0, 0))
            .put(DefaultBlockType.ROOF_FLAT, new Color(255, 60, 60))
            .put(DefaultBlockType.ROOF_HIP, new Color(255, 60, 60))
            .put(DefaultBlockType.ROOF_SADDLE, new Color(224, 120, 100))
            .put(DefaultBlockType.ROOF_DOME, new Color(160, 190, 190))
            .put(DefaultBlockType.ROOF_GABLE, new Color(180, 120, 100))
            .build();

    private final Set<RoofRasterizer<?>> rasterizers = new HashSet<>();

    public RoofFacetLayer() {
        setVisible(true);

        BlockTheme theme = null;
        rasterizers.add(new FlatRoofRasterizer(theme));
        rasterizers.add(new SaddleRoofRasterizer(theme));
        rasterizers.add(new PentRoofRasterizer(theme));
        rasterizers.add(new HipRoofRasterizer(theme));
        rasterizers.add(new ConicRoofRasterizer(theme));
        rasterizers.add(new DomeRoofRasterizer(theme));
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

        RoofFacet roofFacet = chunkRegion.getFacet(RoofFacet.class);
        for (Roof roof : roofFacet.getRoofs()) {
            for (RoofRasterizer<?> rasterizer : rasterizers) {
                rasterizer.tryRaster(brush, roof, hm);
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
