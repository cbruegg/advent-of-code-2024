package aoc09

// -1 represents free space
typealias DiskMap = IntArray

fun checksum(diskMap: DiskMap): Long {
    var sum = 0L
    for ((idx, fileId) in diskMap.withIndex()) {
        if (fileId != -1) {
            sum += fileId * idx
        }
    }
    return sum
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
