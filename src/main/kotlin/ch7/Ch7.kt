package ch7

fun main() {

    println("\n\n===== 7.1.1 =====")
    val p1 = Point(10, 20)
    val p2 = Point(30, 40)
    println(p1 + p2)


    println(p1 * 1.5)

}

// ===== 7.1.1 =====
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}

operator fun Point.times(scale: Double) =
    Point((x * scale).toInt(), (y * scale).toInt())
