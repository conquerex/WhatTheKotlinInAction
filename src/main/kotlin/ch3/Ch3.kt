package ch3

import java.lang.StringBuilder

fun main() {
    println("\n\n===== 3.1 =====")
    // 집합 (Set)
    val numberSet = hashSetOf(1, 2, 3)

    // 리스트 (List)
    val numberArrayList = arrayListOf(4, 5, 6, 3)

    // 맵 (Map)
    val numberMap = hashMapOf(
        1 to "one",
        2 to "two",
        3 to "three"
    )

    // [1, 2, 3]
    println(numberSet)
    println(numberSet.javaClass) // javaClass는 자바 getClass에 해당하는 코틀린 코드

    // [4, 5, 6]
    println(numberArrayList)
    println(numberArrayList.javaClass)
    println(numberArrayList.maxOrNull())

    // {1=one, 2=two, 3=three}
    println(numberMap)
    println(numberMap.javaClass)

    println("\n\n===== 3.2 =====")

    val numberList = listOf(1, 2, 3, 5)

    println(numberList)

    val joinToString = joinToString(numberList, " or ", "<", ">")
    println(joinToString)
}

fun <T> joinToString(
    collection: Collection<T>,
    separator: String,
    prefix: String,
    postfix: String
) : String {
    val result = StringBuilder(prefix)
    for ((index, element) in collection.withIndex()) {
        if (index > 0) result.append(separator) // 첫 원소 앞에는 구분자를 붙이면 안된다.
        result.append(element)
    }
    result.append(postfix)
    return result.toString()
}

class Ch3 {

}