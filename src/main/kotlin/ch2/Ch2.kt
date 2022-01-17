package ch2

import ch2.Color.*

fun main() {
    println("\n\n===== 2.3.1 =====")
    println(Color.BLUE.rgb())
    println(Color.GREEN.rgb())
    println(Color.VIOLET.rgb())

    println("\n\n===== 2.3.3 =====")
    println(mix(BLUE, YELLOW))

    println("\n\n===== 2.3.5 =====")
    println(eval((Sum(Sum(Num(1), Num(4)), Num(5)))))

    println("\n\n===== 2.3.7 =====")
    println(evalWithLogging((Sum(Sum(Num(1), Num(4)), Num(5)))))

    println("\n\n===== 2.4.2 =====")
    for (i in 1..20) { // 해당 범위의 정수에 대해 이터레이션한다
        println(fizzBuzz(i))
    }

    println("\n\n===== ooo =====")


    println("\n\n===== ooo =====")


}


// ========================== 2.3.7 ==========================
fun fizzBuzz(i: Int) = when {
    i % 15 == 0 -> "FizzBuzz"
    i % 3 == 0 -> "Fizz"
    i % 5 == 0 -> "Buzz"
    else -> "$i"
}


// ========================== 2.3.7 ==========================
fun evalWithLogging(e: Expr): Int =
    when (e) {
        is Num -> {
            println("num: ${e.value}")
            e.value  // 이 식이 블록의 마지막 식. e의 타입이 Num이면 e.value 반환
        }
        is Sum -> {
            val left = evalWithLogging(e.left)
            val right = evalWithLogging(e.right)
            println("sum: $left + $right")
            left + right // e의 타입이 Sum이면 이 식의 값이 반환
        }
        else -> throw   IllegalArgumentException("Unknown expression")
    }


// ========================== 2.3.5 ==========================
interface Expr

// value라는 프로퍼티만 존재하는 단순한 클래스. Expr 인터페이스를 구현
class Num(val value: Int) : Expr

// Expr 타입의 객체라면 어떤 것이나 Sum 연산의 인자가 될 수 있다
// 따라서 Num이나 다른 Sum이 인자로 올 수 있다
class Sum(val left: Expr, val right: Expr) : Expr

fun eval(e: Expr): Int {
    if (e is Num) {
        val n = e as Num // 여기서 Num으로 타입을 변환하는데 이는 불필요한 중복
        return n.value
    }
    if (e is Sum) {
        return eval(e.right) + eval(e.left) // 변수 e에 대해 스마트 캐스트를 사용
    }
    throw IllegalArgumentException("Unknown expression")
}


// ========================== 2.3.3 ==========================

fun mix(c1: Color, c2: Color) =
    when (setOf(c1, c2)) {
        // when 식의 인자로 아무 객체나 사용할 수 있다
        // when은 이렇게 인자로 받은 객체가 각 분기 조건에 있는 객체와 같은지 테스트한다.
        setOf(RED, YELLOW) -> ORANGE // 두 색을 혼합해서 다른 색을 만들수 있는 경우를 열거
        setOf(YELLOW, BLUE) -> GREEN
        setOf(BLUE, VIOLET) -> INDIGO
        else -> throw Exception("Dirty color") // 매치되는 분기 조건이 없으면 이 문장을 실행
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
            Color.RED -> "Riard"
            Color.ORANGE -> "Of"
            Color.YELLOW -> "York"
            Color.GREEN -> "Gave"
            Color.BLUE -> "Battle"
            Color.INDIGO -> "In"
            Color.VIOLET -> "Vain"
        }
    }
}