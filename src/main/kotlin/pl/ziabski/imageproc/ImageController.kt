package pl.ziabski.imageproc

import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.canvas.Canvas
import javafx.scene.control.Tab
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import pl.ziabski.imageproc.HistogramDrawer
import pl.ziabski.imageproc.MonochromaticImageHistogramDrawer
import pl.ziabski.imageproc.MultiChannelImageHistogramDrawer
import java.awt.image.BufferedImage
import java.io.FileInputStream
import javax.imageio.ImageIO
import kotlin.random.Random


class ImageController {

    @FXML
    private lateinit var imageView: ImageView

    @FXML
    private lateinit var root: Parent

    @FXML
    private lateinit var histogram: Tab

    @FXML
    private fun onImageLoad() {

        val fileChooser = FileChooser()
        val extFilter = FileChooser.ExtensionFilter("Image files (*.png, *.bmp)", "*.png", "*.bmp")
        fileChooser.extensionFilters.add(extFilter)
        val file = fileChooser.showOpenDialog(root.scene.window)
        val image = ImageIO.read(file)
        var hisogramDrawer: HistogramDrawer

        when (image.type) {
            BufferedImage.TYPE_BYTE_GRAY -> {
                hisogramDrawer = MonochromaticImageHistogramDrawer()
            }
//            BufferedImage.TYPE_INT_ARGB -> {
//
//            }
            else -> {
                hisogramDrawer = MultiChannelImageHistogramDrawer()
            }
        }


        hisogramDrawer.draw(histogram, image)

        imageView.image = Image(FileInputStream(file))
        imageView.isVisible = true
    }


}