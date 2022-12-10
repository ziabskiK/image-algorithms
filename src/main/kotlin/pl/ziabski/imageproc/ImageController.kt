package pl.ziabski.imageproc

import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.opencv.core.Core
import pl.ziabski.imageproc.filter.*
import pl.ziabski.imageproc.image.ImageService
import pl.ziabski.imageproc.image.ImgUtil
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.roundToInt


class ImageController {

    @FXML
    private lateinit var canvas: Canvas

    @FXML
    private lateinit var root: Parent

    @FXML
    private lateinit var thresholdTF: TextField

    @FXML
    private lateinit var minHist: TextField

    @FXML
    private lateinit var maxHist: TextField

    @FXML
    private lateinit var gammaStretchingTF: TextField


    private  var histogramMin = 0
    private  var histogramMax = 255

    private lateinit var image: BufferedImage
    private lateinit var pixelArr: IntArray

    private var resizeScale = 1.0

    private var lut = IntArray(256) { 0 }

    private var negate = false


    constructor(image: BufferedImage) {
        this.image = image
    }


    fun initEmpty() {
        root.scene.window.width = image.width.toDouble() * 1.2
        root.scene.window.height = image.height.toDouble() * 1.2
        canvas.width = image.width.toDouble()
        canvas.height = image.height.toDouble()
        pixelArr = IntArray(image.width * image.height * image.raster.numBands) { 0 }
        image.raster.getPixels(0, 0, image.width, image.height, pixelArr)

        drawImage()
    }

    fun initWithImage() {
        root.scene.window.width = image.width.toDouble() * 1.2
        root.scene.window.height = image.height.toDouble() * 1.2
        canvas.width = image.width.toDouble()
        canvas.height = image.height.toDouble()

        drawImage()
    }

    constructor()
    constructor(image: BufferedImage, lut: IntArray) {
        this.lut = lut
        this.image = image
    }


    @FXML
    private fun onImageLoad() {
        resizeScale = 1.0
        val fileChooser = FileChooser()
        val extFilter = FileChooser.ExtensionFilter("Image files (*.png, *.bmp)", "*.png", "*.bmp")
        fileChooser.extensionFilters.add(extFilter)
        val file = fileChooser.showOpenDialog(root.scene.window)
        image = ImageIO.read(file)
        pixelArr = IntArray(image.width * image.height * image.raster.numBands) { 0 }
        image.raster.getPixels(0, 0, image.width, image.height, pixelArr)
        for (value in pixelArr) {
            lut[value]++
        }
        root.scene.window.width = image.width.toDouble() * 1.2
        root.scene.window.height = image.height.toDouble() * 1.2

        canvas.width = image.width.toDouble()
        canvas.height = image.height.toDouble()
        drawImage(image.width.toDouble(), image.height.toDouble())

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    fun show8BitHistogram() {
        if (image.type == BufferedImage.TYPE_BYTE_GRAY) {
            val fxmlLoader = FXMLLoader(ImageApplication::class.java.getResource("histogram.fxml"))
            val histogramController = HistogramController(image, pixelArr)
            fxmlLoader.setController(histogramController)
            val scene = Scene(fxmlLoader.load(), 850.0, 500.0)
            val stage = Stage()
            stage.title = "8-bit Histogram"
            stage.scene = scene
            histogramController.draw()
            stage.show()
        }
    }

    fun show24BitHistogram() {
        val fxmlLoader = FXMLLoader(ImageApplication::class.java.getResource("histogram24bit.fxml"))
        val histogramController = MultiChannelHistogramController()
        fxmlLoader.setController(histogramController)
        val scene = Scene(fxmlLoader.load(), 850.0, 500.0)
        val stage = Stage()
        stage.title = "24-bit Histogram"
        stage.scene = scene
        histogramController.draw(image)
        stage.show()
    }

    fun smallerImage() {
        resizeScale -= 0.1
        canvas.width = canvas.width * resizeScale
        canvas.height = canvas.height * resizeScale

        drawImage(canvas.width, canvas.height, image, resizeScale, resizeScale)
    }

    private fun drawImage(width: Double, height: Double) {
        drawImage(width, height, image,1.0, 1.0)
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

        if (image.type == BufferedImage.TYPE_BYTE_GRAY) {
            val fxmlLoader = FXMLLoader(ImageApplication::class.java.getResource("histogram.fxml"))
            val histogramController = HistogramController(image, newLut)
            fxmlLoader.setController(histogramController)
            val scene = Scene(fxmlLoader.load(), 850.0, 500.0)
            val stage = Stage()
            stage.title = "Histogram normalization"
            stage.scene = scene
            histogramController.draw()
            stage.show()
        }

        imageFromLut(newLut)
    }

    fun addConstant() {
        try {
            val root = AnchorPane()

            val textField = TextField("Wartość stałej")
            textField.onAction = javafx.event.EventHandler {
                addConstant(textField.text.toInt())
            }
            thresholdTF = textField
            root.children.add(textField)
            val stage = Stage()
            stage.title = "Dodaj stałą"
            stage.scene = Scene(root, 200.0, 200.0)

            stage.show()
            // Hide this current window (if this is what you want)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun addConstant(toInt: Int) {
        lut = IntArray(256) { i -> if (i + toInt < 0) 0 else if (i + toInt > 255) 255 else i + toInt }
        imageFromLut(lut)
    }


    fun subtractConstant() {
        try {
            val root = AnchorPane()

            val textField = TextField("Wartość stałej")
            textField.onAction = javafx.event.EventHandler {
                addConstant(-textField.text.toInt())
            }
            root.children.add(textField)
            val stage = Stage()
            stage.title = "Odejmij stałą"
            stage.scene = Scene(root, 200.0, 200.0)

            stage.show()
            // Hide this current window (if this is what you want)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun rozciagnijhistogramGUI() {
        try {
            val root = VBox()

            val textField = TextField("Wartosc progu min")
            textField.onAction = javafx.event.EventHandler {
                histogramMin = textField.text.toInt() ?: 0
            }
            val textFieldMax = TextField("Wartosc progu max")
            textFieldMax.onAction = javafx.event.EventHandler {
                histogramMax = textFieldMax.text.toInt()


                image = rozciagnijHist(histogramMin, histogramMax)
                drawImage()

            }

            root.children.add(textField)
            root.children.add(textFieldMax)
            val stage = Stage()
            stage.title = "Rozciagnij histogram"
            stage.scene = Scene(root, 200.0, 200.0)

            stage.show()
            // Hide this current window (if this is what you want)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun multiplyByConstant() {
        try {
            val root = AnchorPane()

            val textField = TextField("Wartość stałej")
            textField.onAction = javafx.event.EventHandler {
                multiplyConstant(textField.text.toInt())
            }
            root.children.add(textField)
            val stage = Stage()
            stage.title = "Pomnóz przez stałą"
            stage.scene = Scene(root, 200.0, 200.0)

            stage.show()
            // Hide this current window (if this is what you want)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun multiplyConstant(toInt: Int) {
        for ((index, vlaue) in lut.withIndex())
            lut = IntArray(256) { i -> if (i * toInt < 0) 0 else if (i * toInt > 255) 255 else i * toInt }
        imageFromLut(lut)
    }

    public fun imageFromLut(newLut: IntArray) {
        val gc = canvas.graphicsContext2D

        val pw = gc.pixelWriter
        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                try {
                    image.raster.setPixel(i, j, IntArray(1) { newLut[image.raster.getSampleDouble((i), j, 0).toInt()] })
                    pw.setColor(
                        (i),
                        (j),
                        Color.grayRgb(newLut[image.raster.getSampleDouble((i), j, 0).toInt()])
                    )
                    pixelArr[i + j] = newLut[image.raster.getSampleDouble((i), j, 0).toInt()]
                } catch (e: Exception) {
                }
            }
        }
    }

    public fun imageFromArr(width: Int, height: Int, pixelArr: IntArray) {
        val gc = canvas.graphicsContext2D

        val pw = gc.pixelWriter
        for (i in 0 until width) {
            for (j in 0 until height) {
                try {
                    image.raster.setPixel(i, j, IntArray(1) { pixelArr[i + j] })
                } catch (e: Exception) {
                }
            }
        }
        drawImage()
    }

    fun rozciagnijHist() {
        val minHistI = minHist.text.toInt()
        val maxHistI = maxHist.text.toInt()

        image = rozciagnijHist(minHistI, maxHistI)
        drawImage()
    }

    fun showGammaStretchingThresholdTextField() {
        try {
            val root = AnchorPane()

            val textField = TextField()
            textField.onAction = javafx.event.EventHandler {
                gammaStretching()
            }
            gammaStretchingTF = textField
            root.children.add(textField)
            root.children.add(Label("Współczynnik gamma", textField))
            val stage = Stage()
            stage.title = "Korekcja gamma - opcje"
            stage.scene = Scene(root, 300.0, 200.0)
            stage.show()
            // Hide this current window (if this is what you want)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun gammaStretching() {
        val gammaCoeficient = gammaStretchingTF.text.toDouble()
        val newLut = IntArray(256) { 0 }
        val newArr = IntArray(image.width * image.height) { 0 }
        val max = image.raster.getPixels(0, 0, image.width, image.height, newArr).maxOf { x -> x }

        for (i in 0..255) {
            newLut[i] =
                ((i.toDouble().pow(1 / gammaCoeficient)) / (max.toDouble()
                    .pow(((1 - gammaCoeficient) / gammaCoeficient)))).roundToInt()
        }
        imageFromLut(newLut)
        //getHistogram().draw(histogram, newLut)
    }


    private fun rozciagnijHist(min: Int, max: Int): BufferedImage {
        println("min = %s, max = %s".format(min, max))
        val newImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_BYTE_GRAY)

        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                var rgb = image.raster.getSample(i,j,0)
                rgb = if (rgb < min){
                    0
                } else if (rgb > max )  {
                    255
                } else {
                    ((rgb - min) * 255)/(max-min)
                }
                newImage.raster.setPixel(i, j, IntArray(1) { rgb })
            }
        }

        return newImage
    }

    fun drawImage(width: Double, height: Double,image: BufferedImage ,scaleX: Double, scaleY: Double) {
        val gc = canvas.graphicsContext2D

        val pw = gc.pixelWriter
        for (i in 0 until width.toInt()) {
            for (j in 0 until height.toInt()) {
                try {
                    val color = if (image.type == BufferedImage.TYPE_BYTE_GRAY) Color.grayRgb(
                        image.raster.getSampleDouble((i * scaleX).roundToInt(), (j * scaleY).toInt(), 0).toInt()
                    ) else
                        Color.rgb(
                            image.raster.getSampleDouble((i * scaleX).roundToInt(), (j * scaleY).toInt(), 0).toInt(),
                            image.raster.getSampleDouble((i * scaleX).roundToInt(), (j * scaleY).toInt(), 1).toInt(),
                            image.raster.getSampleDouble((i * scaleX).roundToInt(), (j * scaleY).toInt(), 2).toInt()
                        )
                    pw.setColor(
                        i,
                        j,
                        color
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

    fun showThresholdOptions() {
        try {
            val root = AnchorPane()

            val textField = TextField("Wartość progu")
            textField.onAction = javafx.event.EventHandler {
                changeThreshold()
            }
            thresholdTF = textField
            root.children.add(textField)
            val stage = Stage()
            stage.title = "Progowanie"
            stage.scene = Scene(root, 200.0, 200.0)

            stage.show()
            // Hide this current window (if this is what you want)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun changeThresholdWithLevels() {
        try {
            val threshold = thresholdTF.text.toInt()
            if (threshold == 0) {
                drawImage(image.width.toDouble(), image.height.toDouble())
            } else {
                thresholdWithLevels(threshold)
            }
        } catch (e: NumberFormatException) {
            drawImage(image.width.toDouble(), image.height.toDouble())
        }
    }

    fun showThresholdOptionsWithLevels() {
        try {
            val root = AnchorPane()

            val textField = TextField("Wartość progu")
            textField.onAction = javafx.event.EventHandler {
                changeThresholdWithLevels()
            }
            thresholdTF = textField
            root.children.add(textField)
            val stage = Stage()
            stage.title = "Progowanie"
            stage.scene = Scene(root, 200.0, 200.0)

            stage.show()
            // Hide this current window (if this is what you want)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun threshold(threshold: Int) {
        val gc = canvas.graphicsContext2D
        val pw = gc.pixelWriter
        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                val greyR = image.raster.getSampleDouble(i, j, 0).toInt()
                val rgb = if (greyR >= threshold) 255 else 0
                image.raster.setPixel(i, j, IntArray(1) { rgb })
            }
        }
        drawImage()
    }

    private fun thresholdWithLevels(threshold: Int) {
        val gc = canvas.graphicsContext2D
        val pw = gc.pixelWriter
        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                val greyR = image.raster.getSampleDouble(i, j, 0).toInt()
                val rgb = if (greyR >= threshold) greyR else 0
                image.raster.setPixel(i, j, IntArray(1) { rgb })
            }
        }
        drawImage()
    }

    private fun drawImage() {
        drawImage(image.width.toDouble(), image.height.toDouble())
    }

    private fun drawImage(image: BufferedImage) {
        drawImage(image.width.toDouble(), image.height.toDouble(), image, 1.0,1.0)
    }


    fun negation() {
        negate = !negate
        val gc = canvas.graphicsContext2D
        val pw = gc.pixelWriter
        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                val rgb =
                    if (negate) 255 - image.raster.getSampleDouble(i, j, 0).toInt() else image.raster.getSampleDouble(
                        i,
                        j,
                        0
                    ).toInt()
                pw.setColor(i, j, Color.grayRgb(rgb))
                image.raster.setPixel(i, j, IntArray(1) { rgb })
            }
        }
    }
    fun addImage() {
        val fileChooser = FileChooser()
        val extFilter = FileChooser.ExtensionFilter("Image files (*.png, *.bmp)", "*.png", "*.bmp")
        fileChooser.extensionFilters.add(extFilter)
        val file = fileChooser.showOpenDialog(root.scene.window) ?: return
        val image2 = ImageIO.read(file)
        image = ImageService().addImageWithoutOverflow(image, image2)
        drawImage()
    }

    fun addImageWysycenie() {
        val fileChooser = FileChooser()
        val extFilter = FileChooser.ExtensionFilter("Image files (*.png, *.bmp)", "*.png", "*.bmp")
        fileChooser.extensionFilters.add(extFilter)
        val file = fileChooser.showOpenDialog(root.scene.window) ?: return
        val image2 = ImageIO.read(file)
        image = ImageService().addImageWithOverflow(image, image2)
        drawImage()
    }
    fun subtractImage() {
        val fileChooser = FileChooser()
        val extFilter = FileChooser.ExtensionFilter("Image files (*.png, *.bmp)", "*.png", "*.bmp")
        fileChooser.extensionFilters.add(extFilter)
        val file = fileChooser.showOpenDialog(root.scene.window) ?: return
        val image2 = ImageIO.read(file)
        image = ImageService().subtractImage(image, image2)
        drawImage()
    }

    fun multiplyImages() {
        val fileChooser = FileChooser()
        val extFilter = FileChooser.ExtensionFilter("Image files (*.png, *.bmp)", "*.png", "*.bmp")
        fileChooser.extensionFilters.add(extFilter)
        val file = fileChooser.showOpenDialog(root.scene.window) ?: return
        val image2 = ImageIO.read(file)
        image = ImageService().multiply(image, image2)
        drawImage()
    }

    fun enlargeImage() {
        resizeScale += 0.1
        canvas.width = canvas.width * resizeScale
        canvas.height = canvas.height * resizeScale
        drawImage(canvas.width, canvas.height, image,(canvas.width / image.width), (canvas.height / image.height))
    }

    fun duplicate(actionEvent: ActionEvent) {
        val fxmlLoader = FXMLLoader(ImageApplication::class.java.getResource("hello-view.fxml"))
        val imageController = ImageController(ImgUtil.cloneImage(image))
        fxmlLoader.setController(imageController)
        val scene = Scene(fxmlLoader.load(), 1920.0, 1080.0)
        val stage = Stage()
        stage.title = "Image processing WIT 2022"
        stage.scene = scene
        imageController.initEmpty()
        stage.show()
    }

    fun blur() {
        val filter = BlurFilter()
        image =  filter.filter(image)
        drawImage()
    }
    fun median() {
        val root = AnchorPane()
        val filter = MedianFilter()

        val sizeMapping = mapOf("3x3" to 3, "5x5" to 5, "7x7" to 7, "9x9" to 9)
        val choiceBox = ChoiceBox(FXCollections.observableArrayList("3x3", "5x5", "7x7", "9x9"))
        choiceBox.onAction = javafx.event.EventHandler {
            val newimage =  filter.filter(image, sizeMapping[choiceBox.value] ?: 3)
            drawImage(newimage)
        }
        root.children.add(choiceBox)
        val stage = Stage()
        stage.title = "Filtr medianowy"
        stage.scene = Scene(root, 200.0, 200.0)

        stage.show()
    }

    fun gauss() {
        val filter = GaussianFilter()
        image =  filter.filter(image)
        drawImage()
    }

    fun sharpening() {
        val filter = SharpeningFilter()
        image =  filter.filter(image)
        drawImage()
    }

    fun edgeDetection() {
        val filter = EdgeDetectionFilter()
        image =  filter.filter(image)
        drawImage()
    }

    fun andBitOperation() {
        val fileChooser = FileChooser()
        val extFilter = FileChooser.ExtensionFilter("Image files (*.png, *.bmp)", "*.png", "*.bmp")
        fileChooser.extensionFilters.add(extFilter)
        val file = fileChooser.showOpenDialog(root.scene.window) ?: return
        val image2 = ImageIO.read(file)
        image = ImageService().andBitOperation(image, image2)
        drawImage()
    }
    fun orBitOperation() {
        val fileChooser = FileChooser()
        val extFilter = FileChooser.ExtensionFilter("Image files (*.png, *.bmp)", "*.png", "*.bmp")
        fileChooser.extensionFilters.add(extFilter)
        val file = fileChooser.showOpenDialog(root.scene.window) ?: return
        val image2 = ImageIO.read(file)
        image = ImageService().orBitOperation(image, image2)
        drawImage()
    }
    fun xorBitOperation() {
        val fileChooser = FileChooser()
        val extFilter = FileChooser.ExtensionFilter("Image files (*.png, *.bmp)", "*.png", "*.bmp")
        fileChooser.extensionFilters.add(extFilter)
        val file = fileChooser.showOpenDialog(root.scene.window) ?: return
        val image2 = ImageIO.read(file)
        image = ImageService().xorBitOperation(image, image2)
        drawImage()
    }
}