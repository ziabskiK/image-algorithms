package pl.ziabski.imageproc

import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.chart.BarChart
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Tab
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import java.io.FileInputStream
import javax.imageio.ImageIO


class HelloController {

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
        val pixelarr = IntArray( image.width * image.height) { 0 }
        image.raster.getPixels(0,0, image.width, image.height, pixelarr)

        val histogramData = IntArray(256) { 0 }

        for (value in pixelarr) {
            histogramData[value]++
        }

        createContent(histogramData)
        imageView.image = Image(FileInputStream(file))
        imageView.isVisible = true
    }
    private fun createContent(histogramData: IntArray): Parent {
        val xAxis = CategoryAxis()
        val yAxis = NumberAxis()
        val bc = BarChart(xAxis, yAxis)
        bc.isLegendVisible = false
        bc.animated = false
        bc.barGap = 0.0
        bc.categoryGap = 1.0
        bc.verticalGridLinesVisible = false

        // setup chart
        bc.title = "Histogram"
        xAxis.label = "Grayscale color"
        yAxis.label = "Count"
        yAxis.tickLabelFormatter = NumberAxis.DefaultFormatter(yAxis)

        // add starting data
        val series1 = Series<String, Number>()
        for ((index, value) in histogramData.withIndex()) {
            series1.data.add(XYChart.Data(index.toString(), value))
        }
        bc.data.add(series1)
        histogram.content = bc
        return bc
    }

}