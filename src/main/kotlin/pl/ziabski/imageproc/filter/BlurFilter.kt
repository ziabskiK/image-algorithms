package pl.ziabski.imageproc.filter

import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import pl.ziabski.imageproc.image.ImgUtil
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

class BlurFilter: Filter {
    override fun filter(image: BufferedImage): BufferedImage {

        val img = ImgUtil.imageToMat(image)
        val newIm = Mat(image.height, image.width, CvType.CV_8UC1)
        Imgproc.blur(img, newIm, Size(3.0,3.0), Point(-1.0,-1.0))
        val mob = MatOfByte()

        Imgcodecs.imencode(".bmp", newIm, mob)
        return ImageIO.read(ByteArrayInputStream(mob.toArray()))
    }
}
