// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.roof;

import org.joml.RoundingMode;
import org.joml.Vector2i;
import org.terasology.commonworld.geom.CircleUtility;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.joml.geom.Circlef;
import org.terasology.joml.geom.Rectanglef;
import org.terasology.math.TeraMath;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.DefaultBlockType;
import org.terasology.staticCities.model.roof.ConicRoof;
import org.terasology.staticCities.raster.CheckedPen;
import org.terasology.staticCities.raster.Pen;
import org.terasology.staticCities.raster.Pens;
import org.terasology.staticCities.raster.RasterTarget;
import org.terasology.staticCities.raster.RasterUtil;


/**
 * Converts a {@link ConicRoof} into blocks
 */
public class ConicRoofRasterizer extends RoofRasterizer<ConicRoof> {

    /**
     * @param theme the block theme to use
     */
    public ConicRoofRasterizer(BlockTheme theme) {
        super(theme, ConicRoof.class);
    }

    @Override
    public void raster(RasterTarget target, ConicRoof roof, HeightMap hm) {
        final Circlef area = roof.getShape();

        if (!CircleUtility.intersect(area, target.getAffectedArea().getBounds(new Rectanglef()))) {
            return;
        }

        Vector2i center = new Vector2i(area.x, area.y, RoundingMode.HALF_UP);
        int radius = TeraMath.floorToInt(area.r);

        HeightMap hmBottom = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                int rx = x - center.x();
                int rz = z - center.y();

                // relative distance to border of the roof
                double dist = radius - Math.sqrt(rx * rx + rz * rz);

                int y = TeraMath.floorToInt(roof.getBaseHeight() + dist * roof.getPitch());
                return y;
            }
        };

        Pen pen = Pens.fill(target, hmBottom, HeightMaps.offset(hmBottom, 1), DefaultBlockType.ROOF_HIP);
        RasterUtil.fillCircle(new CheckedPen(pen), center.x(), center.y(), radius);
    }

}
