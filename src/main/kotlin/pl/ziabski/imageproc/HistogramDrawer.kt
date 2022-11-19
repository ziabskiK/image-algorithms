package pl.ziabski.imageproc

import javafx.scene.control.Tab
import java.awt.image.BufferedImage

interface HistogramDrawer {

    fun draw(tab: Tab, image: BufferedImage)
    fun draw(tab: Tab, lut: IntArray)
}