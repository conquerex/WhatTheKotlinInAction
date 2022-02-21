package ch6

import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JList

fun main() {

    println("\n\n===== 6.1.1 =====")
    strLen("abcde")
//    strLen(null)
//    val x: String? = null
//    var y: String = x
//    strLen(x)

    val x: String? = null
    println(strLenSafe(x))
    println(strLenSafe("abc"))


    println("\n\n===== 6.1.3 =====")
    printAllCaps("abc")
    printAllCaps(null)

    val ceo = Employee("Da Boss", null)
    val developer = Employee("Bob Smith", ceo)
    println(managerName(developer))
    println(managerName(ceo))

    val person = Person("Dmitry", null)
    println(person.countryName())


    println("\n\n===== 6.1.4 =====")
    printShippingLabel(person)
    printShippingLabel(Person("Alexey", null))

    println("\n\n===== 6.1.5 =====")
    val p1 = Person615("Dmitry", "Jemerov")
    val p2 = Person615("Dmitry", "Jemerov")
    println(p1 == p2)   // == 연산자는 "equals" 메소드를 호출한다.
    println(p1.equals(42))


    println("\n\n===== 6.1.6 =====")
    ignoreNulls(null)
    person.company!!.address!!.country


    println("\n\n===== 6.1.7 =====")
    var email: String? = "yole@example.com"
    email?.let { sendEmailTo(it) }
    email = null
    email?.let { sendEmailTo(it) }


    println("\n\n===== 6.1.9 =====")
    verifyUserInput(" ")
    verifyUserInput(null) // isNullOrBlank에 "null"을 수신객체로 전달해도 아무런 예외가 발생하지 않는다.


    println("\n\n===== 6.1.10 =====")
    printHashCode(null)
    printHashCode(42)


    println("\n\n===== 6.1.11 =====")
//    yellAt(Person(null))
//    yellAtSafe(Person(null))
//    val i: Int = person.name
    val s: String? = person.name    // 자바 프로퍼티를 널이 될 수 있는 타입으로 볼 수 있다.
    val s1: String = person.name    // 자바 프로퍼티를 널이 될 수 없는 타입으로 볼 수 있다.
}

// ===== 6.1.1 =====
fun strLen(s: String) = s.length
//fun strLenSafe(s: String?) = s.length

// null 검사를 추가하면 코드가 컴파일된다.
fun strLenSafe(s: String?): Int = if (s != null) s.length else 0


// ===== 6.1.3 =====
fun printAllCaps(s: String?) {
    val allCaps: String? = s?.toUpperCase() // allCaps는 널일 수도 있다.
    println(allCaps)
}

class Employee(val name: String, val manager: Employee?)

fun managerName(employee: Employee): String? = employee.manager?.name

class Address(val streetAddress: String, val zipCode: Int, val city: String, val country: String)
class Company(val name: String, val address: Address?)
class Person(val name: String, val company: Company?)

fun Person.countryName(): String {
    // 여러 안전한 호출 연산자를 연쇄해 사용한다.
    val country = this.company?.address?.country
    return if (country != null) country else "Unknown"
}


// ===== 6.1.4 =====
fun foo(s: String?) {
    // "s"가 null이면 결과는 빈 문자열("")이다.
    val t: String = s ?: ""
}

fun Person.countryName614() = company?.address?.country ?: "Unknown"

fun printShippingLabel(person: Person) {
    val address = person.company?.address
        ?: throw IllegalArgumentException("No address") // 주소가 없으면 예외를 발생시킨다.
    with(address) {
        println(streetAddress)
        println("$zipCode $city, $country")
    }
}

// ===== 6.1.5 =====
class Person615(val firstName: String, val lastName: String) {
    override fun equals(o: Any?): Boolean {
        // 타입이 서로 일치하지 않으면 false를 반환
        val otherPerson = o as? Person615 ?: return false
        // 안전한 캐스트를 하고나면 otherPerson이 Person 타입으로 스마트 캐스트된다.
        return otherPerson.firstName == firstName && otherPerson.lastName == lastName
    }

    override fun hashCode(): Int = firstName.hashCode() * 37 + lastName.hashCode()
}

// ===== 6.1.6 =====
fun ignoreNulls(s: String?) {
    val sNotNull: String = s!!  // 예외는 이 지점을 가리킨다.
    println(sNotNull.length)
}

class CopyRowAction(val list: JList<String>) : AbstractAction() {
    override fun isEnabled(): Boolean = list.selectedValue != null

    // actionPerformed는 isEnabled가 "true"인 경우에만 호출된다.
    override fun actionPerformed(e: ActionEvent) {
        val value = list.selectedValue!!
        // value를 클립보드로 복사
    }
}


// ===== 6.1.7 =====
//fun sendEmailTo(email: String) { /*...*/ }
fun sendEmailTo(email: String) {
    println("Sending email to $email")
}

fun getTheBestPersonInTheWorld(): Person? = null


// ===== 6.1.9 =====
fun verifyUserInput(input: String?) {
    // 안전한 호출을 하지 않아도 된다.
    if (input.isNullOrBlank()) {
        println("Please fill in the required fields")
    }
}


// ===== 6.1.10 =====
fun <T> printHashCode(t: T) {
    println(t?.hashCode())  // "t"가 널이 될 수 있으므로 안전한 호출을 써야만 한다.
}

//fun <T: Any> printHashCode(t: T) {  // 이제 "T"는 널이 될 수 없는 타입이다.
//    println(t.hashCode())
//}


// ===== 6.1.11 =====
fun yellAt(person: Person) {
    // toUpperCase()의 수신 객체 person.name이 널이어서 예외 발생
    println(person.name.toUpperCase() + "!!!")
}

fun yellAtSafe(person: Person) {
    println((person.name ?: "Anyone").toUpperCase() + "!!!")
}

class StringPrinter : StringProcessor {
    override fun process(value: String) {
        println(value)
    }
}

class NullableStringPrinter : StringProcessor {
    override fun process(value: String?) {
        if (value != null) {
            println(value)
        }
    }
}