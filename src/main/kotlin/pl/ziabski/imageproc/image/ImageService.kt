package pl.ziabski.imageproc.image

import java.awt.image.BufferedImage

class ImageService {

    fun addImageWithOverflow(image: BufferedImage, image2: BufferedImage): BufferedImage {
        val width = Integer.min(image.width, image2.height)
        val height = Integer.min(image.height, image2.height)
        val newImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

        for (i in 0 until width) {
            for (j in 0 until height) {
                val rgb = Integer.min(255, image.raster.getSample(i, j, 0) + image2.raster.getSample(i, j, 0))
                newImage.raster.setPixel(i, j, IntArray(1) { rgb })
            }
        }
        return newImage;
    }

    fun addImageWithoutOverflow(image: BufferedImage, image2: BufferedImage): BufferedImage {
        val width = Integer.min(image.width, image2.height)
        val height = Integer.min(image.height, image2.height)
        val newImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

        for (i in 0 until width) {
            for (j in 0 until height) {
                val rgb = (image.raster.getSample(i,j, 0) / 2)  + (image2.raster.getSample(i,j, 0) /2)
                newImage.raster.setPixel(i, j, IntArray(1) { rgb })
            }
        }
        return newImage;
    }


    fun multiply(image: BufferedImage, image2: BufferedImage): BufferedImage {
        val width = Integer.min(image.width, image2.height)
        val height = Integer.min(image.height, image2.height)
        val newImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

        for (i in 0 until width) {
            for (j in 0 until height) {
                val rgb = Integer.min(255, image.raster.getSample(i, j, 0) * image2.raster.getSample(i, j, 0))
                newImage.raster.setPixel(i, j, IntArray(1) { rgb })
            }
        }
        return newImage;
    }

    fun subtractImage(image: BufferedImage, image2: BufferedImage): BufferedImage {
        val width = Integer.min(image.width, image2.height)
        val height = Integer.min(image.height, image2.height)
        val newImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

        for (i in 0 until width) {
            for (j in 0 until height) {
                val rgb = Integer.max(0, image.raster.getSample(i, j, 0) - image2.raster.getSample(i, j, 0))
                newImage.raster.setPixel(i, j, IntArray(1) { rgb })
            }
        }
        return newImage;
    }

    fun andBitOperation(image: BufferedImage, image2: BufferedImage): BufferedImage {
        val width = Integer.min(image.width, image2.height)
        val height = Integer.min(image.height, image2.height)
        val newImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

        for (i in 0 until width) {
            for (j in 0 until height) {
                val rgb = image.getRGB(i,j) and image2.getRGB(i,j)
                newImage.raster.setPixel(i, j, IntArray(1) { rgb })
            }
        }
        return newImage;
    }

    fun orBitOperation(image: BufferedImage, image2: BufferedImage): BufferedImage {
        val width = Integer.min(image.width, image2.height)
        val height = Integer.min(image.height, image2.height)
        val newImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

        for (i in 0 until width) {
            for (j in 0 until height) {
                val rgb = image.getRGB(i,j) or image2.getRGB(i,j)
                newImage.raster.setPixel(i, j, IntArray(1) { rgb })
            }
        }
        return newImage;
    }

    fun xorBitOperation(image: BufferedImage, image2: BufferedImage): BufferedImage {
        val width = Integer.min(image.width, image2.height)
        val height = Integer.min(image.height, image2.height)
        val newImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

        for (i in 0 until width) {
            for (j in 0 until height) {
                val rgb = image.getRGB(i,j) xor image2.getRGB(i,j)
                newImage.raster.setPixel(i, j, IntArray(1) { rgb })
            }
        }
        return newImage;
    }

}