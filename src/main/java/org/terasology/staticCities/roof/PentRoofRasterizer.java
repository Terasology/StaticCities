// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities.roof;

import com.google.common.math.DoubleMath;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.staticCities.BlockTheme;
import org.terasology.staticCities.DefaultBlockType;
import org.terasology.staticCities.model.roof.PentRoof;
import org.terasology.staticCities.raster.Pen;
import org.terasology.staticCities.raster.Pens;
import org.terasology.staticCities.raster.RasterTarget;
import org.terasology.staticCities.raster.RasterUtil;

import java.math.RoundingMode;

/**
 * Converts a {@link PentRoof} into blocks
 */
public class PentRoofRasterizer extends RoofRasterizer<PentRoof> {

    /**
     * @param theme the block theme to use
     */
    public PentRoofRasterizer(BlockTheme theme) {
        super(theme, PentRoof.class);
    }

    @Override
    public void raster(RasterTarget target, PentRoof roof, HeightMap hm) {
        Rect2i area = roof.getArea();

        if (!area.overlaps(target.getAffectedArea())) {
            return;
        }

        final HeightMap hmBottom = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                int rx = x - area.minX();
                int rz = z - area.minY();

                BaseVector2i dir = roof.getOrientation().getDir();

                if (dir.getX() < 0) {
                    rx -= area.width() - 1;  // maxX
                }

                if (dir.getY() < 0) {
                    rz -= area.height() - 1; // maxY
                }

                int hx = rx * dir.getX();
                int hz = rz * dir.getY();

                int h = DoubleMath.roundToInt(Math.max(hx, hz) * roof.getPitch(), RoundingMode.HALF_UP);

                return roof.getBaseHeight() + h;
            }
        };

        int thickness = TeraMath.ceilToInt(roof.getPitch());
        HeightMap hmTop = HeightMaps.offset(hmBottom, thickness);
        Pen pen = Pens.fill(target, hmBottom, hmTop, DefaultBlockType.ROOF_HIP);
        RasterUtil.fillRect(pen, area);

        final Rect2i wallRect = roof.getBaseArea();

        HeightMap hmGableBottom = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                int h0 = roof.getBaseHeight();

                boolean onZ = (x == wallRect.minX() || x == wallRect.maxX());
                boolean zOk = (z >= wallRect.minY() && z <= wallRect.maxY());

                if (onZ && zOk) {
                    return h0;
                }

                boolean onX = (z == wallRect.minY() || z == wallRect.maxY());
                boolean xOk = (x >= wallRect.minX() && x <= wallRect.maxX());

                if (onX && xOk) {
                    return h0;
                }

                return hmBottom.apply(x, z); // return top-height to get a no-op
            }
        };

        pen = Pens.fill(target, hmGableBottom, hmBottom, DefaultBlockType.ROOF_GABLE);
        RasterUtil.fillRect(pen, area);
    }
}
