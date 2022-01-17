package ch2

fun main() {
    println("===== ch2.3.1 =====")
    println(Color.BLUE.rgb())
    println(Color.GREEN.rgb())
    println(Color.VIOLET.rgb())
}

enum class Color(
    val r: Int, val g: Int, val b: Int //상수의 프로퍼티 정의
) {
    //각 상수를 생성할 때 그에 대한 프로퍼티 값을 지정
    RED(255, 0, 0), ORANGE(255, 165, 0),
    YELLOW(255, 255, 0), GREEN(0, 255, 0), BLUE(0, 0, 255),
    INDIGO(75, 0, 130), VIOLET(238, 130, 238); //세미콜론 반드시 사용

    fun rgb() = (r * 256 + g) * 256 + b //enum 클래스 안에서 메소드를 정의

    fun getMnemonic(color: Color) {
        when (color) {
            Color.RED -> "Richard"
            Color.ORANGE -> "Of"
            Color.YELLOW -> "York"
            Color.GREEN -> "Gave"
            Color.BLUE -> "Battle"
            Color.INDIGO -> "In"
            Color.VIOLET -> "Vain"
        }
    }
}