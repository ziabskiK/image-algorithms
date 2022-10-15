package pl.ziabski.imageproc

import javafx.scene.Node
import javafx.scene.control.Tab
import java.awt.image.BufferedImage

interface HistogramDrawer {

    fun draw(tab: Tab, image: BufferedImage)
}