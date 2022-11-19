package pl.ziabski.imageproc

import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.canvas.Canvas
import javafx.scene.control.Tab
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.roundToInt


class ImageController {

    @FXML
    private lateinit var canvas: Canvas

    @FXML
    private lateinit var root: Parent

    @FXML
    private lateinit var histogram: Tab

    @FXML
    private lateinit var thresholdTF: TextField
    @FXML
    private lateinit var minHist: TextField
    @FXML
    private lateinit var maxHist: TextField

    @FXML
    private lateinit var gammaStretchingTF: TextField

    private lateinit var image: BufferedImage

    private var resizeScale = 1.0

    private var lut = IntArray(256) { 0 }

    private var negate = false

    @FXML
    private fun onImageLoad() {
        resizeScale = 1.0
        val fileChooser = FileChooser()
        val extFilter = FileChooser.ExtensionFilter("Image files (*.png, *.bmp)", "*.png", "*.bmp")
        fileChooser.extensionFilters.add(extFilter)
        val file = fileChooser.showOpenDialog(root.scene.window)
        image = ImageIO.read(file)
        val pixelarr = IntArray(image.width * image.height) { 0 }
        image.raster.getPixels(0, 0, image.width, image.height, pixelarr)
        for (value in pixelarr) {
            lut[value]++
        }

        val hisogramDrawer: HistogramDrawer = getHistogram()

        canvas.width = image.width.toDouble()
        canvas.height = image.height.toDouble()
        hisogramDrawer.draw(histogram, image)
        drawImage(image.width.toDouble(), image.height.toDouble())
    }

    private fun getHistogram(): HistogramDrawer {
        val hisogramDrawer: HistogramDrawer

        when (image.type) {
            BufferedImage.TYPE_BYTE_GRAY -> {
                hisogramDrawer = MonochromaticImageHistogramDrawer()
            }

            else -> {
                hisogramDrawer = MultiChannelImageHistogramDrawer()
            }
        }
        return hisogramDrawer
    }

    fun smallerImage() {
        resizeScale -= 0.1
        canvas.width = canvas.width * resizeScale
        canvas.height = canvas.height * resizeScale
        drawImage(canvas.width, canvas.height, (canvas.width / image.width), (canvas.height / image.height))
    }

    private fun drawImage(width: Double, height: Double) {
        drawImage(width, height, 1.0, 1.0)
    }


    fun wyrownanieHistogramu() {
        val newLut = IntArray(256) { 0 }
        val D = DoubleArray(256) { 0.0 }
        val Dp = DoubleArray(256) { 0.0 }

        D[0] = (lut.firstOrNull { p -> p > 0 })?.toDouble() ?: 0.0
        Dp[0] = D[0] / (image.width * image.height)

        for (i in 1..255) {
            D[i] = D[i - 1] + lut[i]
            Dp[i] = D[i] / (image.width * image.height)
        }

        for ((index, _) in newLut.withIndex()) {
            newLut[index] = (((Dp[index] - Dp[0]) / (1 - Dp[0])) * (newLut.size - 1)).roundToInt()
        }

        imageFromLut(newLut)
    }

    private fun imageFromLut(newLut: IntArray) {
        val gc = canvas.graphicsContext2D

        val pw = gc.pixelWriter
        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                try {
                    pw.setColor(
                        (i),
                        (j),
                        Color.grayRgb(newLut[image.raster.getSampleDouble((i), j, 0).toInt()])
                    )
                } catch (e: Exception) {
                }
            }
        }
    }

    fun rozciagnijHist() {
        val minHistI = minHist.text.toInt()
        val maxHistI = maxHist.text.toInt()


        val newLut = rozciagniecieHist(minHistI, maxHistI)
        val hisogramDrawer: HistogramDrawer = getHistogram()
        hisogramDrawer.draw(histogram, newLut)
    }

    fun rozciagniecieHist(from: Int, to: Int): IntArray {
        val newLut = IntArray(256) { 0 }

        for ((index, _) in lut.withIndex()) {
            newLut[index] = rozciagnijHist(newLut[index], from, to)
        }
        imageFromLut(newLut)
        return newLut
    }

    fun gammaStretching() {
        val gammaCoeficient = gammaStretchingTF.text.toDouble()
        val newLut = IntArray(256) {0}
        //lut = i ^ 1/y

        for (i in 0..255) {
            newLut[i] = i.toDouble().pow(1/gammaCoeficient).roundToInt()
        }
        imageFromLut(newLut)
        getHistogram().draw(histogram, newLut)
    }


    private fun rozciagnijHist(i: Int, min: Int, max: Int): Int {
        val Lmin = lut.indices.minByOrNull { lut[it] } ?: 0
        val Lmax = lut.indices.maxByOrNull { lut[it] } ?: 255
        if (i < min) {
            return Lmin
        }
        if( i > max) {
            return Lmax
        }

        return ((i - min) * Lmax)/(max-min)
    }

    fun drawImage(width: Double, height: Double, scaleX: Double, scaleY: Double) {
        val gc = canvas.graphicsContext2D

        val pw = gc.pixelWriter
        for (i in 0 until width.toInt()) {
            for (j in 0 until height.toInt()) {
                try {
                    pw.setColor(
                        (i * scaleX).roundToInt(),
                        (j * scaleY).roundToInt(),
                        Color.grayRgb(
                            image.raster.getSampleDouble((i * scaleX).roundToInt(), (j * scaleY).toInt(), 0).toInt()
                        )
                    )
                } catch (e: Exception) {
                    pw.setColor((i * scaleX).roundToInt(), (j * scaleY).roundToInt(), Color.grayRgb(255))
                }
            }
        }
    }


    fun changeThreshold() {
        try {
            val threshold = thresholdTF.text.toInt()
            if (threshold == 0) {
                drawImage(image.width.toDouble(), image.height.toDouble())
            } else {
                threshold(threshold)
            }
        } catch (e: NumberFormatException) {
            drawImage(image.width.toDouble(), image.height.toDouble())
        }
    }

    private fun threshold(threshold: Int) {
        val gc = canvas.graphicsContext2D
        val pw = gc.pixelWriter
        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                val greyR = image.raster.getSampleDouble(i, j, 0).toInt()
                val newVal = if (greyR >= threshold) Color.grayRgb(255) else Color.grayRgb(0)
                pw.setColor(i, j, newVal)
            }
        }
    }

    fun negation() {
        negate = !negate
        val gc = canvas.graphicsContext2D
        val pw = gc.pixelWriter
        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                if (negate) {
                    pw.setColor(i, j, Color.grayRgb(255 - image.raster.getSampleDouble(i, j, 0).toInt()))
                } else {
                    pw.setColor(i, j, Color.grayRgb(image.raster.getSampleDouble(i, j, 0).toInt()))
                }
            }
        }
    }

    fun enlargeImage() {
        resizeScale += 0.1
        canvas.width = canvas.width * resizeScale
        canvas.height = canvas.height * resizeScale
        drawImage(canvas.width, canvas.height, (canvas.width / image.width), (canvas.height / image.height))
    }

}