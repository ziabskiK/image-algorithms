package pl.ziabski.imageproc

import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.canvas.Canvas
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import java.awt.image.BufferedImage

class MultiChannelHistogramController {
    private val colorArar = arrayOf(Color.RED, Color.GREEN, Color.BLUE)
    private val colorArarText = arrayOf("RED", "GREEN", "BLUE")

    @FXML
    private lateinit var vBox: VBox

    fun draw(image: BufferedImage) {
        val calculatePixelsForColors = calculatePixelsForColor(image)
        for ((withIndex, value) in calculatePixelsForColors.withIndex()) {
            val canvas = drawHistogramUsingCanvas(value, colorArar[withIndex])
            vBox.children.add(Text(colorArarText[withIndex]))
            vBox.children.add(canvas)
            val text0 = Label("0")
            text0.alignment = Pos.BASELINE_LEFT
            text0.minWidth(canvas.width / 2)
            val text255 = Label("255")
            text255.alignment = Pos.BASELINE_LEFT
            text255.minWidth(canvas.width / 2)
            val hBox = HBox(text0, text255)
            hBox.minHeight = 0.0
            vBox.children.add(hBox)
        }

    }

    private fun calculatePixelsForColor(image: BufferedImage): Array<IntArray> {
        val redArr = IntArray(256)
        val greenArr = IntArray(256)
        val blueArr = IntArray(256)
        for (x in 0 until image.height) {
            for (y in 0 until image.width) {
                val clr = image.getRGB(x, y)
                val red = clr and 0x00ff0000 shr 16
                redArr[red]++
                val green = clr and 0x0000ff00 shr 8
                greenArr[green]++
                val blue = clr and 0x000000ff
                blueArr[blue]++
            }
        }

        return arrayOf(redArr, greenArr, blueArr)
    }

    private fun drawHistogramUsingCanvas(array: IntArray, color: Color): Canvas {
        val histogramW = 500.0
        val histogramH = 300.0
        val canvas = Canvas(histogramW, histogramH)
        val gc = canvas.graphicsContext2D
        val max = array.maxOrNull() ?: 0
        val scale = histogramH / max
        gc.stroke = color
        gc.lineWidth = (histogramW / 256.0)
        for ((i, value) in array.withIndex()) {
            gc.strokeLine(
                (i * gc.lineWidth), histogramH,
                (i * gc.lineWidth), histogramH - (value * scale)
            )
        }
        return canvas
    }
}