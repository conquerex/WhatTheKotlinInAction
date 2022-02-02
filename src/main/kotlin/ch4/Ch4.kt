package ch4

fun main() {
    println("\n\n===== 4.1.1 =====")
    val button = Button()
    button.showOff()
    button.setFocus(true)
    button.click()

    println("\n\n===== 4. =====")
    println("\n\n===== 4. =====")
    println("\n\n===== 4. =====")
    println("\n\n===== 4. =====")

}

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
