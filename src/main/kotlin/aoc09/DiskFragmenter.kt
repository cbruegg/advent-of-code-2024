package aoc09

fun main() {
    val input = "2333133121414131402"

    val diskMap = expandDiskMap(input)
    println("diskMap=$diskMap")

    val optimizedDiskMap = optimizeSpaceUsage(diskMap)
    println("optimizedDiskMap=$optimizedDiskMap")

    val checksum = checksum(optimizedDiskMap)
    println("checksum=$checksum")
}

fun checksum(diskMap: String): Long {
    var sum = 0L
    for ((idx, fileIdStr) in diskMap.withIndex()) {
        if (fileIdStr in '0'..'9') {
            val fileId = fileIdStr.digitToInt()
            sum += fileId * idx
        }
    }
    return sum
}

fun optimizeSpaceUsage(originalDiskMap: String): String {
    val diskMap = originalDiskMap.toCharArray()
    for (i in diskMap.indices.reversed()) {
        val block = diskMap[i]
        if (block != '.') {
            val freeBlockIdx = diskMap.indexOf('.') // we could optimize this by going from left to right, but whatever
            if (freeBlockIdx == -1 || freeBlockIdx > i) {
                // No more free space
                break
            }
            diskMap[freeBlockIdx] = block
            diskMap[i] = '.'
        }
    }
    return diskMap.concatToString()
}

fun expandDiskMap(input: String): String =
    buildString {
        for ((idx, blockLengthStr) in input.withIndex()) {
            val blockLength = blockLengthStr.digitToInt()
            if (idx % 2 == 0) {
                // File
                val fileId = idx / 2
                repeat(fileId.digitToChar().code, blockLength)
            } else {
                // Free space
                repeat('.'.code, blockLength)
            }
        }
    }
