package pl.ziabski.imageproc.image

import org.opencv.core.CvType
import org.opencv.core.Mat
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

class ImgUtil {

    companion object {
        fun imageToMat(image: BufferedImage): Mat {
            val mat = Mat(image.height, image.width, CvType.CV_8UC1)
            val data: ByteArray = (image.raster.dataBuffer as DataBufferByte).data
            mat.put(0, 0, data)
            return mat
        }

        fun cloneImage(image: BufferedImage): BufferedImage {
            val newImage = BufferedImage(image.width, image.height, image.type)

            for (i in 0 until newImage.width) {
                for (j in 0 until newImage.height) {
                    try {
                        val rgb = image.raster.getSample(i,j, 0)
                        newImage.raster.setPixel(i, j, IntArray(1) { rgb })
                    } catch (e: Exception) {
                    }
                }
            }
            return newImage
        }
    }

}