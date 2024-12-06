package aoc06

data class GuardGameState(
    val obstacles: Set<Pair<Int, Int>>,
    var curX: Int,
    var curY: Int,
    var direction: Direction
)

fun parseInitialGameState(lines: List<String>): GuardGameState {
    val obstacles = mutableSetOf<Pair<Int, Int>>()
    var curX = -1
    var curY = -1
    lateinit var direction: Direction
    for ((y, line) in lines.withIndex()) {
        for ((x, char) in line.withIndex()) {
            when (char) {
                '#' -> obstacles += Pair(x, y)
                '^' -> {
                    curX = x
                    curY = y
                    direction = Direction.Up
                }

                '>' -> {
                    curX = x
                    curY = y
                    direction = Direction.Right
                }

                'v' -> {
                    curX = x
                    curY = y
                    direction = Direction.Down
                }

                '<' -> {
                    curX = x
                    curY = y
                    direction = Direction.Left
                }
            }
        }
    }
    return GuardGameState(obstacles, curX, curY, direction)
}

enum class Direction(val vector: Pair<Int, Int>, val str: String) {
    Left(-1 to 0, "<"),
    Up(0 to -1, "^"), // -1 as our top is index 0
    Right(1 to 0, ">"),
    Down(0 to 1, "v")
}

fun Direction.turnRight() = when (this) {
    Direction.Left -> Direction.Up
    Direction.Up -> Direction.Right
    Direction.Right -> Direction.Down
    Direction.Down -> Direction.Left
}
