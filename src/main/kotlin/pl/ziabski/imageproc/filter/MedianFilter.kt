package pl.ziabski.imageproc.filter

import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import pl.ziabski.imageproc.image.ImgUtil
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

class MedianFilter {
    fun filter(image: BufferedImage, ksize: Int = 3): BufferedImage {

        val img = ImgUtil.imageToMat(image)
        val newIm = Mat(image.height, image.width, CvType.CV_8UC1)
        Imgproc.medianBlur(img, newIm, ksize)
        val mob = MatOfByte()

        Imgcodecs.imencode(".bmp", newIm, mob)
        return ImageIO.read(ByteArrayInputStream(mob.toArray()))
    }

}