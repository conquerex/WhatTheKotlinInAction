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
