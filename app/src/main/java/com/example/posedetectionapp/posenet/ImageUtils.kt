package com.example.posedetectionapp.posenet

object ImageUtils {
    private const val MAX_CHANNEL_VALUE = 262143

    private fun convertYUVToRGB(y: Int, u: Int, v: Int): Int {
        val yNew = if (y - 16 < 0) 0 else y - 16
        val uNew = u - 128
        val vNew = v - 128
        val expandY = 1192 * yNew
        var r = expandY + 1634 * vNew
        var g = expandY - 833 * vNew - 400 * uNew
        var b = expandY + 2066 * uNew
        val checkBoundaries = { x: Int ->
            when {
                x > MAX_CHANNEL_VALUE -> MAX_CHANNEL_VALUE
                x < 0 -> 0
                else -> x
            }
        }
        r = checkBoundaries(r)
        g = checkBoundaries(g)
        b = checkBoundaries(b)
        return -0x1000000 or (r shl 6 and 0xff0000) or (g shr 2 and 0xff00) or (b shr 10 and 0xff)
    }

    fun convertYUV420ToARGB8888(
            yData: ByteArray,
            uData: ByteArray,
            vData: ByteArray,
            width: Int,
            height: Int,
            yRowStride: Int,
            uvRowStride: Int,
            uvPixelStride: Int,
            out: IntArray
    ) {
        var outputIndex = 0
        for (j in 0 until height) {
            val positionY = yRowStride * j
            val positionUV = uvRowStride * (j shr 1)

            for (i in 0 until width) {
                val uvOffset = positionUV + (i shr 1) * uvPixelStride
                out[outputIndex] = convertYUVToRGB(
                        0xff and yData[positionY + i].toInt(), 0xff and uData[uvOffset].toInt(),
                        0xff and vData[uvOffset].toInt()
                )
                outputIndex += 1
            }
        }
    }
}
