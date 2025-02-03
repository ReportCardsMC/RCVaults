package xyz.reportcards.vaults.utils

import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream
import java.io.ByteArrayInputStream
import kotlin.random.Random
import kotlin.system.measureTimeMillis

object CompressionUtils {
    private const val BUFFER_SIZE = 8192

    private val WORDS = listOf("AMillion", "ABillion", "ATrillion", "FartBubble", "CraftyFartTime", "EwYouFarted", "Fabious", "Trendy", "Crafty", "BallTime", "Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Hotel", "India", "Juliet", "Kilo", "Lima", "Mike", "November", "Oscar", "Papa", "Quebec", "Romeo", "Sierra", "Tango", "Uniform", "Victor", "Whiskey", "X-ray", "Yankee", "Zulu").map { it.toByteArray() }
    private fun createTestData(size: Int): ByteArray {
        val output = ByteArrayOutputStream(size)

        // Create a repeating pattern that should be highly compressible
        while (output.size() < size) {
            val word = WORDS[Random.nextInt(WORDS.size)]
            output.write(word)
        }

        return output.toByteArray()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val data: ByteArray
        val msForData = measureTimeMillis {
            data = createTestData(1500 * 1024 * 1024)
        }
        //println("Data creation took $msForData ms")

        var compressed: ByteArray
        val compressMs = measureTimeMillis {
            compressed = compress(data)
        }
        //println("Compression took $compressMs ms")

        // Show compression ratio
        val mbCompressed = compressed.size.toDouble() / (1024 * 1024)
        val mbOriginal = data.size.toDouble() / (1024 * 1024)
        //println("Compressed %.2f MB to %.2f MB".format(mbOriginal, mbCompressed))
        val ratio = mbCompressed / mbOriginal
        //println("Compression ratio: %.2f".format(ratio))

        val decompressed: ByteArray
        val decompressMs = measureTimeMillis {
            decompressed = decompress(compressed)
        }
        //println("Decompression took $decompressMs ms")

        // Verify the decompressed data matches the original
        if (data.contentEquals(decompressed)) {
            //println("Decompression successful - data matches original")
        } else {
            //println("ERROR: Decompressed data does not match original!")
        }
    }

    /**
     * Compresses data using DEFLATE algorithm with best compression level
     * @param data ByteArray of data to compress
     * @return ByteArray of compressed data
     */
    fun compress(data: ByteArray): ByteArray {
        ByteArrayOutputStream().use { outputStream ->
            val deflater = Deflater(Deflater.BEST_COMPRESSION, true)
            DeflaterOutputStream(outputStream, deflater).use { deflaterStream ->
                deflaterStream.write(data)
            }
            return outputStream.toByteArray()
        }
    }

    /**
     * Decompresses data that was compressed using the compress function
     * @param compressedData ByteArray of compressed data
     * @return ByteArray of decompressed data
     */
    fun decompress(compressedData: ByteArray): ByteArray {
        ByteArrayInputStream(compressedData).use { inputStream ->
            ByteArrayOutputStream().use { outputStream ->
                val inflater = Inflater(true)
                InflaterInputStream(inputStream, inflater).use { inflaterStream ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var length: Int
                    while (inflaterStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }
                return outputStream.toByteArray()
            }
        }
    }
}