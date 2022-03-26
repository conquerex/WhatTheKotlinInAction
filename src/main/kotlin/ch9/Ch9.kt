package ch9

fun main() {

    println("\n\n===== 9.1.1 =====")

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