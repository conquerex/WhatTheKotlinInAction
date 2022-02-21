package ch6

fun main() {

    println("\n\n===== 5.1.2 =====")
    val people = listOf(Person("Alice", 29), Person("Bob", 31))
    findTheOldest(people)

    println("\n\n===== 5.1.3 =====")

}

// ===== 5.1.2 =====
data class Person(val name: String, val age: Int)

fun findTheOldest(people: List<Person>) {
    var maxAge = 0  // 가장 많은 나이를 저장한다.
    var theOldest: Person? = null   // 가장 연장자인 사람을 저장한다.
    for (person in people) {
        // 현재까지 발견한 최연장자보다 더 나이가 많은 사람을 찾으면 최댓값을 바꾼다.
        if (person.age > maxAge) {
            maxAge = person.age
            theOldest = person
        }
    }
    println(theOldest)
}