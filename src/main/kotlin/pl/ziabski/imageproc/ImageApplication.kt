package pl.ziabski.imageproc

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import kotlin.random.Random


class ImageApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(ImageApplication::class.java.getResource("hello-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 1920.0, 1080.0)
        stage.title = "Image processing WIT 2022"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(ImageApplication::class.java)
}