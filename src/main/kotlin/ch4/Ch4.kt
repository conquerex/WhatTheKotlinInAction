package ch4

import java.io.Serializable

fun main() {
    println("\n\n===== 4.1.1 =====")
    val button = Button()
    button.showOff()
    button.setFocus(true)
    button.click()

    println("\n\n===== 4.1.4 =====")

    val button2 = SampleButton()
    button2.getCurrentState()

    println("\n\n===== 4.2.3 =====")
    println(PrivateUser("test@kotlinlang.org").nickname)
    println(SubscribingUser("test@kotlinlang.org").nickname)

    println("\n\n===== 4. =====")
    println("\n\n===== 4. =====")

}

// ===== 4.3.3 =====
class CountingSet<T>(
    val innerSet: MutableCollection<T> = HashSet<T>()
) : MutableCollection<T> by innerSet { // MutableCollection의 구현을 innerSet에게 위임한다.
    var objectsAdded = 0

    // 위임하지 않고 새로운 구현을 제공한다.
    override fun add(element: T): Boolean {
        objectsAdded++
        return innerSet.add(element)
    }

    // 위임하지 않고 새로운 구현을 제공한다.
    override fun addAll(c: Collection<T>): Boolean {
        objectsAdded += c.size
        return innerSet.addAll(c)
    }
}

// ===== 4.3.1 =====
class Client(val name: String, val postalCode: Int) {
    // "Any"는 java.lang.Object에 대응하는 클래스로,
    // 코틀린의 모든 클래스의 최상위 클래스다.
    // "Any?"는 널이 될 수 있는 타입이므로 "other"는 null일 수 있다.
    override fun equals(other: Any?): Boolean {
        // "other"가 Client인지 검사한다.
        if (other == null || other !is Client)
            return false
        // 두 객체의 프로퍼티 값이 서로 같은지 검사한다.
        return name == other.name && postalCode == other.postalCode
    }

    override fun toString() = "Client(name=$name, postalCode=$postalCode)"
}

// ===== 4.2.3 =====

interface User2 {
    val email: String
    val nickname: String
        get() = email.substringBefore('@')  // 프로퍼티에 뒷받침하는 필드가 없다. 대신 매번 결과를 계산해 돌려준다.
}

class PrivateUser(override val nickname: String) : User // 주 생성자에 있는 프로퍼티

class SubscribingUser(val email: String) : User {
    override val nickname: String
        get() = email.substringBefore('@')  // 커스텀 게터
}

class FacebookUser(val accountId: Int) : User {
    override val nickname = getFacebookName(accountId)  // 프로퍼티 초기화 식

    private fun getFacebookName(accountId: Int): String {
        return when (accountId % 2 == 0) {
            true -> "Apple"
            else -> "Banana"
        }
    }
}

interface User {
    val nickname: String
}

// ===== 4.1.5 =====

sealed class Expr {
    class Num(val value: Int) : Expr()
    class Sum(val left: Expr, val right: Expr) : Expr()
//    class Num2(val value: Int) : Expr()
}

fun eval(e: Expr): Int =
    when (e) {
        is Expr.Num -> e.value
        is Expr.Sum -> eval(e.right) + eval(e.left)
    }


// ===== 4.1.4 =====

class Button414 : View {
    override fun getCurrentState(): State = ButtonState()

    override fun restoreState(state: State) {
        //
    }

    class ButtonState : State { /*...*/ }  // 이 클래스는 자바의 정적 중첩 클래스와 대응한다.
}

interface State : Serializable

interface View {
    fun getCurrentState(): State
    fun restoreState(state: State) {}
}

// ===== 4.1.3 =====

internal open class TalkativeButton : Focusable {
    private fun yell() = println("Hey!")
    protected fun whisper() = println("Let's talk!")
}

/*
// 오류: public 멤버가 자신의 internal 수신 타입인 TalkativeButton을 노출함.
// 'public' member exposes its 'internal' receiver type TalkativeButton
fun TalkativeButton.giveSpeech() {
    // 오류: yell에 접근할 수 없음. yell은 TalktiveButton의 private 멤버임.
    // Cannot access 'yell': it is private in 'TalkativeButton'
    yell()

    // 오류: whisper에 접근할 수 없음. whisper는 TalktiveButton의 protected 멤버임.
    // Cannot access 'whisper': it is protected in 'TalkativeButton'
    whisper()
}
*/


// ===== 4.1.2 =====

open class RichButton : Clickable {  // 이 클래스는 열려있다. 다른 클래스가 이 클래스를 상속할 수 있다.
    fun disable() {}  // 이 함수는 final이다. 하위 클래스가 이 메서드를 override할 수 없다.

    open fun animate() {}  // 이 함수는 열려있다. 하위 클래스에서 이 메서드를 override해도 된다.

    override fun click() {}  // 이 함수는 (상위 클래스에서 선언된) 열려있는 메서드를 override한다. override한 메서드는 기본적으로 열려있다.
}

open class RichButton2 : Clickable {

    // 여기 있는 final은 쓸데 없이 붙은 중복이 아니다.
    // final이 없는 override 메서드나 프로퍼티는 기본적으로 열려있다.
    final override fun click() {}

}

//class Ch412Sample : RichButton2() {
//    override fun click() {
//        super.click()
//    }
//}

// 이 클래스는 추상클래스이므로 이 클래스의 인스턴스를 만들 수 없다.
abstract class Animated {
    // 이 함수는 추상 함수이므로 구현이 없다. 하위 클래스에서는 이 함수를 반드시 오버라이드해야 한다.
    abstract fun animate()

    // 추상 클래스에 속했더라도 비추상 함수는 기본적으로 final이지만 원한다면 open으로 오버라이드를 허용할 수 있다.
    open fun stopAnimating() {}

    fun animateTwice() {}
}

// ===== 4.1.1 =====

class Button : Clickable, Focusable {
    override fun click() = println("I was clicked")

    // 이름과 시그니처가 같은 멤버 메소드에 대해 둘 이상의 디폴트 구현이 있는 경우
    // 인터페이스를 구현하는 하위 클래스에서 명시적으로 새로운 구현을 제공해야 한다.
    override fun showOff() {
        // 상위 타입의 이름을 (제네릭) 꺾쇠 괄호 사이에 넣어서 super를 지정하면
        // 어떤 상위 타입의 멤버 메소드를 호출할지 지정할 수 있다.
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }
}

interface Clickable {
    fun click() // 일반 메소드 선언
    fun showOff() = println("I'm clickable!") // 디폴트 구현이 있는 메소드
}

interface Focusable {
    fun setFocus(b: Boolean) =
        println("I ${if (b) "got" else "lost"} focus.")

    fun showOff() = println("I'm focusable!")
}

class Ch4 {
    //
}
