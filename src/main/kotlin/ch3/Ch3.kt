package ch3

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

    println("\n\n===== 3.2.2 =====")
    println(joinToString2(numberList, ", ", "", ""))
    println(joinToString2(numberList)) // separator, prefix, postfix 생략
    println(joinToString2(numberList, "; ")) // separator를 "; "로 지정, prefix, postfix 생략


    println("\n\n===== 3.3 =====")
    println("Kotlin".lastChar())

    println("\n\n===== 3.3.3 =====")
    val list = listOf(11, 22, 33)
    println(list.joinToString3(separator = "; ", prefix = "(", postfix = ")"))
    println(list.joinToString3(" "))
    println(listOf("one", "two", "eight").join(" "))


    println("\n\n===== 3.3.4 =====")
    val view: View = Button()
    view.click() // "view"에 저장된 값의 실제 타입에 따라 호출할 메서드가 결정된다.
    view.showOff() // 확장 함수는 정적으로 결정된다.

    println("\n\n===== 3.4.1 =====")
    println(list.last())
    val numbers/*: Collection<Int>*/ = setOf(1, 14, 2)
    println(numbers.maxOrNull())


    println("\n\n===== 3.4.3 =====")
    val (number, name) = 1 to "one"

    println("\n\n===== 3.5.1 =====")
    println("12.345-6.A".split("//.|-".toRegex())) // "-"로 나누기. 정규식을 명시적으로 만든다.
    println("12.345-6.A".split(".", "-")) // 여러 구분 문자열을 지정한다.

    println("\n\n===== 3.5.2 =====")
    parsePath("/Users/yole/kotlin-book/chapter.adoc")
    parsePath2("/Users/test/kotlin-book/ch3.adoc")
}

// 첫 번째 구현 : String을 확장한 함수를 사용
fun parsePath(path: String) {
    val directory = path.substringBeforeLast("/")
    val fullName = path.substringAfterLast("/")

    val fileName = fullName.substringBeforeLast(".")
    val extension = fullName.substringAfterLast(".")

    println("Dir: $directory, name: $fileName, ext: $extension")
}

// 두 번째 구현 : 정규식을 사용
fun parsePath2(path: String) {
    val regex = """(.+)/(.+)\.(.+)""".toRegex()
    val matchResult = regex.matchEntire(path)
    if (matchResult != null) {
        val (directory, filename, extension) = matchResult.destructured
        println("Dir: $directory, name: $filename, ext: $extension")
    }
}

fun View.showOff() = println("I'm a view!")
fun Button.showOff() = println("I'm a button")

open class View {
    open fun click() = println("View clicked")
}

class Button : View() { // Button은 View를 확장한다
    override fun click() = println("Button clicked")
}

fun Collection<String>.join(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
) = joinToString(separator, prefix, postfix)

fun <T> Collection<T>.joinToString3( // Collection<T>에 대한 확장 함수를 선언한다.
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
): String {
    val result = StringBuilder(prefix)

    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }
    result.append(postfix)
    return result.toString()
}

// 본문 코드 : this.get(this.length - 1)
fun String.lastChar(): Char = this[this.length - 1]

fun String.lastChar2(): Char = get(length - 1)


fun <T> joinToString2(
    collection: Collection<T>,
    separator: String = ", ",   // 디폴트 값이 지정된 파라미터
    prefix: String = "",
    postfix: String = "",
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in collection.withIndex()) {
        if (index > 0) result.append(separator) // 첫 원소 앞에는 구분자를 붙이면 안된다.
        result.append(element)
    }
    result.append(postfix)
    return result.toString()
}

fun <T> joinToString(
    collection: Collection<T>,
    separator: String,
    prefix: String,
    postfix: String
): String {
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