package pl.ziabski.imageproc.filter

import java.awt.image.BufferedImage

@FunctionalInterface
interface Filter {
    fun filter(image: BufferedImage): BufferedImage
}