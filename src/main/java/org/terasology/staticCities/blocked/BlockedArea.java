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

package org.terasology.staticCities.blocked;

import org.joml.Vector2ic;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockArea;
import org.terasology.world.block.BlockAreac;
import org.terasology.world.viewer.color.ColorModels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Optional;

/**
 * An image-based registry for blocked areas.
 */
public class BlockedArea {

    private static final Color MARKER = Color.MAGENTA;

    private final BufferedImage image;
    private final BlockArea worldRect = new BlockArea(BlockArea.INVALID);
    private final DataBufferInt imageBuffer;
    private final int stride; // the image scanline stride

    public BlockedArea(BlockAreac targetRegion) {

        worldRect.set(targetRegion);

        int height = targetRegion.getSizeX();
        int width = targetRegion.getSizeY();

        DirectColorModel colorModel = ColorModels.ARGB; // TODO: could be RGB
        int[] masks = colorModel.getMasks();
        imageBuffer = new DataBufferInt(width * height);
        stride = width;
        WritableRaster raster = Raster.createPackedRaster(imageBuffer, width, height, stride, masks, null);
        image = new BufferedImage(colorModel, raster, false, null);
    }

    public BlockAreac getWorldRegion() {
        return worldRect;
    }

    public void addRect(BlockAreac area) {
        if (worldRect.intersectsBlockArea(area)) {
            Graphics2D g = image.createGraphics();
            g.translate(-worldRect.minX(), -worldRect.minY());
            g.setColor(MARKER);
            g.fillRect(area.minX(), area.minY(), area.getSizeX(), area.getSizeY());
            g.dispose();
        }
    }

    public void addLine(Vector2ic start, Vector2ic end, float width) {
        // TODO: check for intersection (with border offset=width) first
        Graphics2D g = image.createGraphics();
        g.setColor(MARKER);
        g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.translate(-worldRect.minX(), -worldRect.minY());
        g.drawLine(start.x(), start.y(), end.x(), end.y());
        g.dispose();
    }

    public boolean isBlocked(int worldX, int worldY) {
        int imgX = worldX - worldRect.minX();
        int imgY = worldY - worldRect.minY();
        if (imgX < 0 || imgX >= worldRect.getSizeX()) {
            throw new IllegalArgumentException("worldX " + worldX + " is illegal");
        }
        if (imgY < 0 || imgY >= worldRect.getSizeY()) {
            throw new IllegalArgumentException("worldY " + worldY + " is illegal");
        }
        return imageBuffer.getElem(imgY * stride + imgX) != 0;
    }

    public boolean isBlocked(BlockAreac rect) {
        Optional<BlockArea> crop = rect.intersect(worldRect, new BlockArea(BlockArea.INVALID));
        if (!crop.isPresent()) {
            throw new IllegalArgumentException("Invalid region " + rect);
        }

        BlockArea area = crop.get().translate(-worldRect.minX(), -worldRect.minY());

        for (int y = area.minY(); y <= area.maxY(); y++) {
            for (int x = area.minX(); x <= area.maxX(); x++) {
                if (imageBuffer.getElem(y * stride + x) != 0) {
                    return true;
                }
            }
        }

        return false;
    }


    BufferedImage getImage() {
        return image;
    }
}
