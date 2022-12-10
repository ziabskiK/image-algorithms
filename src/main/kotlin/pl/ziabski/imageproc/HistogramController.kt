package pl.ziabski.imageproc

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.awt.image.BufferedImage

class HistogramController {

    @FXML
    private lateinit var histogramCanvas: Canvas

    @FXML
    private lateinit var leftLabel: Label

    @FXML
    private lateinit var rightLabel: Label

    @FXML
    private lateinit var histogramMin: TextField

    @FXML
    private lateinit var histogramMax: TextField


    private var lut = IntArray(256) { 0 }

    private var image: BufferedImage
    private var pixelArr: IntArray
    constructor(image: BufferedImage, pixelArr: IntArray) {
        this.image = image
        var x = IntArray( image.width * image.height) { 0 }  ;
        image.raster.getPixels(0,0, image.width, image.height,x )
        this.pixelArr = x;
        calcLut()
    }

    fun draw() {
        drawHistogramUsingCanvas(Color.BLACK)
    }

    fun calcLut() {
        for (value in pixelArr) {
            lut[value]++
        }
    }

    private fun drawHistogramUsingCanvas(color: Color): Canvas {
        val histogramW = 800.0
        val histogramH = 400.0
        val canvas = histogramCanvas
        canvas.width = histogramW
        canvas.height = histogramH
        val gc = canvas.graphicsContext2D
        val max = lut.maxOrNull() ?: 0
        val scale = histogramH / max
        gc.stroke = color
        gc.lineWidth = (histogramW / 256.0)
        for ((i, value) in lut.withIndex()) {
            gc.strokeLine(
                (i * gc.lineWidth), histogramH,
                (i * gc.lineWidth), histogramH - (value * scale)
            )
        }
        leftLabel.minWidth = histogramW / 2
        rightLabel.minWidth = histogramW / 2
        return canvas
    }

    fun setHistogramStretching() {
        rozciagniecieHist(histogramMin.text.toInt(), histogramMax.text.toInt())
        drawHistogramUsingCanvas(Color.BLACK)

        val imageController = ImageController(image, lut)
        val fxmlLoader = FXMLLoader(ImageApplication::class.java.getResource("hello-view.fxml"))
        fxmlLoader.setController(imageController)
        val scene = Scene(fxmlLoader.load(), 1920.0, 1080.0)
        val stage = Stage()
        stage.title = "Image processing WIT 2022"
        stage.scene = scene
        imageController.initWithImage()
        imageController.imageFromLut(lut)
        stage.show()


    }

    fun rozciagniecieHist(from: Int, to: Int) {
        val x = IntArray(256) { x -> x + 1 }
        for ((index, value) in x.withIndex()) {
            x[index] = rozciagnijHist(value, from, to)
        }
        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                try {
                    image.raster.setPixel(i,j, IntArray(1) {x[image.raster.getSampleDouble((i), j, 0).toInt()]})
                } catch (e: Exception) {
                }
            }
        }

        image.raster.getPixels(0,0, image.width, image.height, pixelArr)
        calcLut()
    }

    private fun rozciagnijHist(i: Int, min: Int, max: Int): Int {
        if (i < min) return 0
        if (i > max) return 255
        return ((i - min)) / (255 / (max-min))
    }

}