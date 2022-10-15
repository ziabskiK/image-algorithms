package pl.ziabski.imageproc

import javafx.scene.Parent
import javafx.scene.chart.BarChart
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Tab
import java.awt.image.BufferedImage

class MonochromaticImageHistogramDrawer: HistogramDrawer {
    override fun draw(tab: Tab, image: BufferedImage) {
        val pixelarr = IntArray( image.width * image.height) { 0 }
        image.raster.getPixels(0,0, image.width, image.height, pixelarr)

        val histogramData = IntArray(256) { 0 }

        for (value in pixelarr) {
            histogramData[value]++
        }
        val drawGrayscaleHistogram = drawGrayscaleHistogram(histogramData)

        tab.content = drawGrayscaleHistogram;
    }

    private fun drawGrayscaleHistogram(histogramData: IntArray): Parent {
        val xAxis = CategoryAxis()
        val yAxis = NumberAxis()
        val bc = BarChart(xAxis, yAxis)
        bc.isLegendVisible = false
        bc.animated = false
        bc.barGap = 0.0
        bc.categoryGap = 0.0
        bc.verticalGridLinesVisible = false

        // setup chart
        bc.title = "Histogram"
        xAxis.label = "Grayscale color"
        yAxis.label = "Pixel count"
        yAxis.tickLabelFormatter = NumberAxis.DefaultFormatter(yAxis)

        // add starting data
        val series1 = XYChart.Series<String, Number>()
        for ((index, value) in histogramData.withIndex()) {
            series1.data.add(XYChart.Data(index.toString(), value))
        }
        bc.data.add(series1)
        return bc
    }

}