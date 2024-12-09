package aoc09

import java.io.File

fun main() {
    val input = File("inputs/aoc09/diskmap.txt").readText().trim()
//    val input = "2333133121414131402"

    val diskMap = expandDiskMap(input)
    println("diskMap=${diskMap.contentToString()}")

    val optimizedDiskMapV2 = optimizeSpaceUsageV2(diskMap)
    println("optimizedDiskMapV2=${optimizedDiskMapV2.contentToString()}")

    val checksum = checksum(optimizedDiskMapV2)
    println("checksumV2=$checksum")
}

data class FileEntry(val id: Int, val start: Int, val end: Int) {
    val size get() = end - start
}

fun optimizeSpaceUsageV2(originalDiskMap: DiskMap): DiskMap {
    val files = findFiles(originalDiskMap)

    val diskMap = originalDiskMap.copyOf()
    for (file in files.reversed()) {
        val freeSlotIdx = diskMap.indexOfRepeatedEntry(-1, repetitions = file.size)
        if (freeSlotIdx != -1 && freeSlotIdx < file.start) {
            diskMap.swap(freeSlotIdx, file.start, file.size)
//            println(diskMap.contentToString())
        }
    }

    return diskMap
}

private fun findFiles(originalDiskMap: DiskMap): MutableList<FileEntry> {
    val files = mutableListOf<FileEntry>()
    var lastId: Int? = null
    var start = -1
    for ((i, fileId) in originalDiskMap.withIndex()) {
        if (fileId == -1) {
            if (lastId != null) {
                // This is the end of a file, ended by free space
                files += FileEntry(lastId, start, end = i)
                lastId = null
                start = -1
            } else {
                // We haven't seen any file yet - still scanning through free space
            }
        } else {
            if (lastId == null) {
                // We're seeing the first file, or just finished reading free space
                start = i
                lastId = fileId
            } else if (lastId != fileId) {
                // We last read another file and are now seeing a new one
                files += FileEntry(lastId, start, end = i)
                start = i
                lastId = fileId
            }
        }
    }
    if (lastId != null) {
        // This is the end of a file, ended by end of disk
        files += FileEntry(lastId, start, end = originalDiskMap.size)
    }
    return files
}

fun IntArray.indexOfRepeatedEntry(entry: Int, repetitions: Int): Int {
    var curStart = -1
    for (i in indices) {
        val cur = this[i]
        if (cur == entry) {
            if (curStart == -1) {
                // Found an instance of the entry
                curStart = i
            } // else: continuing the current entry
        } else if (curStart != -1) {
            // current entry ended
            if (i - curStart >= repetitions) {
                // if entry is long enough, return it
                return curStart
            } else {
                curStart = -1
            }
        } // else current element is not our target and we also haven't seen it recently
    }
    return -1
}

fun IntArray.swap(sectionAStart: Int, sectionBStart: Int, length: Int) {
    // Theoretically we only need to copy one section to a temp var, but this is easier to read
    val sectionA = copyOfRange(sectionAStart, sectionAStart + length)
    val sectionB = copyOfRange(sectionBStart, sectionBStart + length)

    sectionA.copyInto(this, sectionBStart)
    sectionB.copyInto(this, sectionAStart)
}