package aoc09

import java.io.File


fun main() {
    val input = File("inputs/aoc09/diskmap.txt").readText().trim()

    val diskMap = expandDiskMap(input)
    println("diskMap=${diskMap.contentToString()}")

    val optimizedDiskMap = optimizeSpaceUsage(diskMap)
    println("optimizedDiskMap=${optimizedDiskMap.contentToString()}")

    val checksum = checksum(optimizedDiskMap)
    println("checksum=$checksum")
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
