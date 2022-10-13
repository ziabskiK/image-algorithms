package pl.ziabski.imageproc

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView

class HelloController {

    @FXML
    private lateinit var imageView: ImageView

    @FXML
    private fun onHelloButtonClick() {
//        val image = ImageIO.read(this.javaClass.getResourceAsStream("data.png"))
//        image.getRGB(image.width/2, image.height)
        imageView.image = Image(this.javaClass.getResourceAsStream("data.png"))
        imageView.isVisible = true
    }
}