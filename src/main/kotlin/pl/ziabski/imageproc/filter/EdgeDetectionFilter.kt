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

class EdgeDetectionFilter :Filter {
    override fun filter(image: BufferedImage): BufferedImage {
        val img = ImgUtil.imageToMat(image)
        val newIm = Mat(image.height, image.width, CvType.CV_8UC1)
        val kernel = getKernal_2()
        println(kernel.dump())
        Imgproc.filter2D(img, newIm,-1, kernel )
        val mob = MatOfByte()

        Imgcodecs.imencode(".bmp", newIm, mob)
        return ImageIO.read(ByteArrayInputStream(mob.toArray()))
    }
    private fun getKernal_2(): Mat {
        // define and return 3x3 kernel, filled manually with numbers:
        val kernel = Mat(3, 3, CvType.CV_16SC1)
        kernel.put(0, 0,
            -1.0, -1.0, -1.0,
            -1.0, 8.0, -1.0,
            -1.0, -1.0, -1.0)
        return kernel
    }

}