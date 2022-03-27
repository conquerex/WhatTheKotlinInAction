package ch9

fun main() {

    println("\n\n===== 9.1.1 =====")


//    println(max("kotlin", 42))

    println("\n\n===== 9.1.4 =====")
//    val nullableStringProcessor = Processor<String?>()

}

// ===== 9.1.4 =====
class Processor<T: Any> {
    fun process(value: T) {
        value.hashCode() // T 타입의 value는 null이 될 수 없다
    }
}

fun <T: Comparable<T>> max(first: T, second: T): T {
    return if (first > second) first else second
}

// ===== 9.1.1 =====
fun String.filter(predicate: (Char) -> Boolean): String {
    val sb = StringBuilder()
    for (index in 0 until length) {
        val element = get(index)
        // predicate 파라미터로 전달받은 함수를 호출
        if (predicate(element)) sb.append(element)
    }
    return sb.toString()
}