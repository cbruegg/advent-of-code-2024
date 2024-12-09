package aoc09

import java.io.File

// -1 represents free space
typealias DiskMap = IntArray

fun main() {
    val input = File("inputs/aoc09/diskmap.txt").readText().trim()

    val diskMap = expandDiskMap(input)
    println("diskMap=${diskMap.contentToString()}")

    val optimizedDiskMap = optimizeSpaceUsage(diskMap)
    println("optimizedDiskMap=${optimizedDiskMap.contentToString()}")

    val checksum = checksum(optimizedDiskMap)
    println("checksum=$checksum")
}

fun checksum(diskMap: DiskMap): Long {
    var sum = 0L
    for ((idx, fileId) in diskMap.withIndex()) {
        if (fileId != -1) {
            sum += fileId * idx
        }
    }
    return sum
}

fun optimizeSpaceUsage(originalDiskMap: DiskMap): DiskMap {
    val diskMap = originalDiskMap.copyOf()
    for (i in diskMap.indices.reversed()) {
        val block = diskMap[i]
        if (block != -1) {
            val freeBlockIdx = diskMap.indexOf(-1) // we could optimize this by going from left to right, but whatever
            if (freeBlockIdx == -1 || freeBlockIdx > i) {
                // No more free space
                break
            }
            diskMap[freeBlockIdx] = block
            diskMap[i] = -1
        }
    }
    return diskMap
}

fun expandDiskMap(input: String): DiskMap =
    buildList {
        for ((idx, blockLengthStr) in input.withIndex()) {
            val blockLength = blockLengthStr.digitToInt()
            if (idx % 2 == 0) {
                // File
                val fileId = idx / 2
                repeat(blockLength) {
                    add(fileId)
                }
            } else {
                // Free space
                repeat(blockLength) {
                    add(-1)
                }
            }
        }
    }.toIntArray()
