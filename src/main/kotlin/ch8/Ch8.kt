package ch8

fun main() {

    println("\n\n===== 8.1.1 =====")
    val p1 = Point(10, 20)


}

// ===== 8.1.1 =====
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}
