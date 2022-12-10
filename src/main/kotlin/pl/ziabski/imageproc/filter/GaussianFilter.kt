package pl.ziabski.imageproc.filter

import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import pl.ziabski.imageproc.image.ImgUtil
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

class GaussianFilter: Filter{
    override fun filter(image: BufferedImage): BufferedImage {
        val img = ImgUtil.imageToMat(image)
        val newIm = Mat(image.height, image.width, CvType.CV_8UC1)
        Imgproc.GaussianBlur(img, newIm, Size(3.0, 3.0), 2.0)
        val mob = MatOfByte()

        Imgcodecs.imencode(".bmp", newIm, mob)
        return ImageIO.read(ByteArrayInputStream(mob.toArray()))
    }
}
