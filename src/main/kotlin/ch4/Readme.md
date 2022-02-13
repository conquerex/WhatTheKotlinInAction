# 4장. 클래스, 객체, 인터페이스

## 입구

- 클래스와 인터페이스
- 뻔하지 않은 생성자와 프로퍼티
- 데이터 클래스
- 클래스 위임
- object 키워드 사용

코틀린의 클래스와 인터페이스는 자바의 것과는 약간 다르다. 예를 들어, **인터페이스**에 프로퍼티 선언이 들어갈 수 있다. 자바와 달리 **코틀린의 선언**은 기본적으로 final이며 public이다. 게다가 중첩
클래스는 기본적으로는 내부 클래스가 아니다. 즉, 코틀린 중첩 클래스에는 외부 클래스에 대한 참조가 없다.

짧은 주 생성자 구문으로도 거의 모든 경우를 잘 처리할 수 있지만, 복잡한 초기화 로직을 수행하는 경우를 대비해 완전한 문법도 있다. 프로퍼티도 마찬가지로 간결한 구문으로 충분히 제 기능을 하지만, 필요하면 **
접근자를 직접 정의**할 수 있다.

코틀린 컴파일러는 번잡스러움을 피하기 위해 유용한 메서드를 자동으로 만들어준다.
**클래스를 data로** 선언하면 일부 표준 메서드를 생성해준다.

그리고 코틀린 언어가 제공하는 **위임**(delegation)을 사용하면 위임을 처리하기 위한 준비 메서드를 직접 작성할 필요가 없다.

또한 클래스와 인스턴스를 동시에 선언하면서 만들 때 쓰는 **object** 키워드에 대해 알아본다. 싱글턴 클래스, 동반 객체(companion object), 객체 식(object expression = java
익명 클래스)을 표현할 때 사용한다.


<br/>

## 4.1. 클래스 계층 정의

## 4.1.1. 코틀린 인터페이스

코틀린 인터페이스는 자바8 인터페이스와 비슷하다. 코틀린 인터페이스 안에는 추상 메서드뿐 아니라 구현이 있는 메서드(자바8의 디폴트 메서드와 비슷하다)도 정의할 수 있다. 다만 인터페이스에는 아무런 상태(필드)도
저장될 수 없다.

```kotlin
// 인터페이스 정의
interface Clickable {
    fun click()
}

// 구현
class Button : Clickable {
    override fun click() = println("I was clicked")
}
```

자바에서는 `extends`와 `implements` 키워드를 사용하지만, 코틀린에서는 클래스 이름 뒤에 콜론을 붙이는 것으로 확장과 구현을 모두 처리한다. 자바와 마찬가지로 클래스는 인터페이스를 원하는 만큼
마음대로 구현(implements)할 수 있지만, 클래스는 오직 하나만 확장(extends)할 수 있다.

자바의 @Override 애노테이션과 비슷한 `override` 변경자는 상위 클래스나 상위 인터페이스에 있는 프로퍼티나 메서드를 재정의 한다는 뜻이다. 하지만 자바와 달리 코틀린에서는 override 변경자를
반드시 사용해야 한다. 이는 실수로 상위 클래스의 메서드를 오버라이드 하는 경우를 방지해준다.

상위 클래스에 있는 메서드와 시그니처가 같은 메서드를 우연히 하위 클래스에서 선언하는 경우 컴파일이 안되기 때문에 override를 붙이거나 메서드 이름을 바꿔야만 한다.

인터페이스 메서드도 디폴트 구현을 제공할 수 있다. default를 붙여야하는 자바와 달리 그냥 메서드 본문을 추가하면 된다. 이 인터페이스를 구현하는 클래스는 click에 대한 구현을 제공해야 하는 반면,
showOff 메서드의 경우 재정의할 수도 있고, 디폴트 구현을 사용할 수도 있다.

```kotlin
interface Clickable {
    fun click() // 일반 메소드 선언
    fun showOff() = println("I'm clickable!") // 디폴트 구현이 있는 메소드
}
```

한 클래스에서 이 두 인터페이스를 함께 구현하면 (이 경우에는 showOff 메소드) 어떻게 될까? 정답은 어느 쪽도 선택되지 않는다. 클래스가 구현하는 두 상위 인터페이스에 정의된 구현을 대체할 오버라이딩 메서드를
직접 제공하지 않으면 컴파일러 오류가 발생한다.

```
Class 'Button' must override public open fun showOff(): 
Unit defined in ch4.Clickable because it inherits multiple interface methods of it
```

코틀린 컴파일러는 두 메서드를 아우르는 구현을 하위 클래스에 직접 구현하게 강제한다.

```kotlin
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
```

Button은 상속한 두 상위 타입의 showOff() 메서드를 호출하는 방식으로 showOff()를 구현한다. 
상위 타입의 구현을 호출할 때는 자바와 마찬가지로 `super`를 사용한다. 
하지만 구체적으로 타입을 지정하는 문법은 다르다. 
자바에서는 Clickable.super.showOff() 처럼 super 앞에 기반 타입을 적지만, 
코틀린에서는 super<Clickable>.showOff()처럼 꺾쇠 괄호 안에 기반 타입 이름을 지정한다.

상속한 구현 중 단 하나만 호출해도 된다면 다음과 같이 쓸 수도 있다.

```kotlin
override fun showOff() = super<Clickable>.showOff()
```

아래 코드에서 Button 클래스는 Focusable 인터페이스 안에 선언된 setFocus의 구현을 자동으로 상속한다.

```kotlin
val button = Button()
button.showOff()
button.setFocus(true)
button.click()
```

> ### ✅자바에서 코틀린의 메소드가 있는 인터페이스 구현하기
> 코틀린은 자바 6와 호환되게 설계됐다. 따라서 인터페이스의 디폴트 메서드를 지원하지 않는다. 
> 따라서 코틀린은 디폴트 메서드가 있는 인터페이스를 
> 일반 인터페이스와 디폴트 메서드 구현이 정적 메서드로 들어있는 클래스를 조합해 구현한다. 
> 
> 인터페이스에는 메서드 선언만 들어가며, 인터페이스와 함께 생성되는 클래스에는 모든 디폴트 메서드 구현이 정적 메서드로 들어간다. 
> 그러므로 디폴트 인터페이스가 포함된 코틀린 인터페이스를 자바 클래스에서 상속해 구현하고 싶다면 
> 코틀린에서 메서드 본문을 제공하는 메서드를 포함하는 모든 메서드에 대한 본문을 작성해야 한다. 
> (즉 자바에서는 코틀린 디폴트 메서드 구현에 의존할 수 없다) (필자: 따로 구현이 필요하다는 뜻인가?)

<br/>

## 4.1.2. open, final, abstract 변경자: 기본적으로 final

자바에서는 클래스를 다른 클래스가 상속할 수 있다. (final로 명시적으로 상속을 금지하는 경우는 제외) 
이렇게 기본적으로 상속이 가능하면 편리한 경우도 많지만 문제가 생기는 경우도 많다.

`취약한 기반 클래스`(fragile base class)라는 문제는 
하위 클래스가 기반 클래스에 대해 가졌던 가정이 기반 클래스를 변경함으로써 깨져버린 경우에 생긴다. 
어떤 클래스가 자신을 상속하는 방법에 대해 정확한 규칙(어떤 메서드를 어떻게 오버라이드해야 하는지 등)을 제공하지 않는다면 
그 클래스의 클라이언트는 기반 클래스를 작성한 사람의 의도와 다른 방식으로 메서드를 오버라이드할 위험이 있다.

모든 하위 클래스를 분석하는 것은 불가능하기 때문에 기반 클래스를 변경하는 경우 
하위 클래스의 동작이 예기치 않게 바뀔 수도 있다는 면에서 기반 클래스는 '취약'하다.
(필자 : 요즘 이런거는 IDE에서 잡아주지 않나?)

이 문제를 해결하기 위해 자바 프로그래밍 기법에 대한 책 중 가장 유명한 책인 '이펙티브 자바'에서는
- 상속을 위한 설계와 문서를 갖추거나, 
- 그럴 수 없다면 상속을 금지하라라는 조언을 한다. 
- 이는 특별히 하위 클래스에서 오버라이드하게 의도된 클래스와 메서드가 아니라면 **모두 final로** 만들라는 뜻이다.

코틀린도 마찬가지 철학을 따른다. 자바의 클래스와 메서드는 기본적으로 상속에 대해 열려있지만 
코틀린의 클래스와 메서드는 기본적으로 `final`이다.

어떤 클래스의 상속을 허용하려면 클래스 앞에 `open` 변경자를 붙여야 한다. 
그와 더불어 오버라이드를 허용하고 싶은 메서드나 프로퍼티의 앞에도 open 변경자를 붙여야한다.

```kotlin
// 이 클래스는 열려있다. 다른 클래스가 이 클래스를 상속할 수 있다.
open class RichButton : Clickable {
    // 이 함수는 final이다. 하위 클래스가 이 메서드를 override할 수 없다.    
    fun disable() {}  

    // 이 함수는 열려있다. 하위 클래스에서 이 메서드를 override해도 된다.
    open fun animate() {}

    // 이 함수는 (상위 클래스에서 선언된) 열려있는 메서드를 override한다.
    // override한 메서드는 기본적으로 열려있다.
    override fun click() {}  
}
```

기반 클래스나 인터페이스의 멤버를 오버라이드하는 경우 그 메서드는 기본적으로 열려있다. 
오버라이드하는 메서드의 구현을 하위 클래스에서 오버라이드하지 못하게 금지하려면 
오버라이드하는 메서드 앞에 final을 명시해야 한다.

```kotlin
class Ch412Sample : RichButton2() {
    // 컴파일 에러
    // 'click' in 'RichButton2' is final and cannot be overridden
    override fun click() {
        super.click()
    }
}
```

```kotlin
open class RichButton2 : Clickable {

    // 여기 있는 final은 쓸데 없이 붙은 중복이 아니다.
    // final이 없는 override 메서드나 프로퍼티는 기본적으로 열려있다.
    final override fun click() {}   
    
}
```

> ### ✅열린 클래스와 스마트 캐스트
> 클래스의 기본적인 상속 가능 상태를 `final`로 함으로써 얻을 수 있는 큰 이익은 
> 다양한 경우에 `스마트 캐스트`가 가능하다는 점이다. 
> 클래스 프로퍼티의 경우 이는 `val`이면서 커스텀 접근자가 없는 경우에만 스마트 캐스트를 쓸 수 있다는 것이다. 
> 이 요구 사항은 또한 프로퍼티가 final이어야만 한다는 뜻이기도 하다. 
> 프로퍼티가 final이 아니라면 그 프로퍼티를 다른 클래스가 상속하면서 커스텀 접근자를 정의함으로써 
> 스마트 캐스트의 요구 사항을 깰 수 있다. 
> 코틀린의 경우 프로퍼티가 기본적으로 final이므로 고민할 필요 없이 대부분의 프로퍼티를 스마트 캐스트에 활용할 수 있다.

자바처럼 코틀린에서도 클래스를 `abstract`로 선언할 수 있다.
abstract로 선언한 추상 클래스는 인스턴스화할 수 없다.
추상 클래스에는 구현이 없는 추상 멤버가 있기 때문에 하위 클래스에서 그 추상 멤버를 오버라이드해야만 하는게 보통이다.
추상 멤버는 **항상 열려있기 때문에** open 변경자를 명시할 필요가 없다.

```kotlin
// 이 클래스는 추상클래스이므로 이 클래스의 인스턴스를 만들 수 없다.
abstract class Animated {
    // 이 함수는 추상 함수이므로 구현이 없다. 하위 클래스에서는 이 함수를 반드시 오버라이드해야 한다.
    abstract fun animate()

    // 추상 클래스에 속했더라도 비추상 함수는 기본적으로 final이지만
    // 원한다면 open으로 오버라이드를 허용할 수 있다.
    open fun stopAnimating() {}
    
    fun animateTwice() {}
}
```

인터페이스 멤버의 경우 final, open, abstract를 사용하지 않는다.  
인터페이스 멤버는 항상 열려있으며 final로 변경할 수 없다.  
인터페이스 멤버에게 본문이 없으면 자동으로 추상 멤버가 되지만, 
그렇더라도 따로 멤버 선언 앞에 abstract 키워드를 덧붙일 필요가 없다.

> ### 🌼 클래스 내에서 상속 제어 변경자의 의미
>
> - _변경자_
>   - _이 변경자가 붙은 멤버는..._
>   - _설명_
>
> 위의 양식을 기준으로 아래와 같이 설명한다.
> 
> - final
>   - 오버라이드할 수 없음
>   - 클래스 멤버의 기본 변경자다.
> - open
>   - 오버라이드할 수 있음
>   - 반드시 open을 명시해야 오버라이드할 수 있다.
> - abstract
>   - 반드시 오버라이드해야 함
>   - 추상 클래스의 멤버에만 이 변경자를 붙일 수 있다. 추상 멤버에는 구현이 있으면 안된다.
> - override
>   - 상위 클래스나 상위 인스턴스의 멤버를 오버라이드하는 중
>   - 오버라이드하는 멤버는 기본적으로 열려있다. 하위 클래스의 오버라이드를 금지하려면 final을 명시해야 한다.

<br/>


## 4.1.3. 가시성 변경자: 기본적으로 공개

**가시성 변경자**(visibility modifier)는 코드 기반에 있는 선언에 대한 클래스 외부 접근을 제어한다. 
어떤 클래스의 구현에 대한 접근을 제한함으로써 그 클래스에 의존하는 외부 코드를 깨지 않고도 내부 구현을 변경할 수 있다.

기본적으로 코틀린 가시성 변경자는 자바와 비슷하다. 
자바와 같은 public, protected, private 변경자가 있다. 

- 코틀린 : 아무 변경자도 없는 경우 선언은 모두 공개(public)된다.
- 자바의 기본 가시성인 패키지 전용(package-private)은 코틀린에 없다. 
  - 코틀린은 패키지를 네임스페이스(namespace)를 관리하기 위한 용도로만 사용한다. 
  - 그래서 패키지를 가시성 제어에 사용하지 않는다.

패키지 전용 가시성에 대한 대안으로 `internal`
- 우리말로는 모듈 내부라고 번역 
- "모듈 내부에서만 볼 수 있음"이라는 뜻이다. 
- 모듈(module)은 한 번에 한꺼번에 컴파일되는 코틀린 파일들을 의미한다. 
- IntelliJ, Eclipse, Maven, Gradle 등의 프로젝트가 모듈이 될 수 있고, 
- 앤트 태스크(task)가 한 번 실행될 때 함께 컴파일되는 파일의 집합도 모듈이 될 수 있다.

> Ant란 Java 기반의 자동화 빌드 툴. Another Neat Tool를 줄인 말. 
> MAVEN이나 Gradle처럼 빌드 도구로 역할. 아파치 진영에서 만들어졌음.
> 전부 대문자가 아닌 이유는 실제 개미(ant)의 특징을 강조하고 싶었던 것으로 추정됨.
> (출처 : https://ant.apache.org/faq.html)

모듈 내부 가시성은 우리의 모듈의 구현에 대해 진정한 **캡슐화**를 제공한다는 장점이 있다. 
자바에서는 패키지가 같은 클래스를 선언하기만 하면 어떤 프로젝트의 외부에 있는 코드라도 
패키지 내부에 있는 패키지 전용 선언에 쉽게 접근할 수 있다. 그래서 모듈의 캡슐화가 쉽게 깨진다.

다른 차이는 코틀린에서는 최상위 선언에 대해 private 가시성(비공개 가시성)을 허용한다는 점이다. 
그런 최상위 선언에는 클래스, 함수, 프로퍼티 등이 포함된다. 
비공개 가시성인 최상위 선언은 그 선언이 들어있는 파일 내부에서만 사용할 수 있다. 
이 또한 하위 시스템의 자세한 구현 사항을 외부에 감추고 싶을 때 유용한 방법이다. 
아래의 표는 모든 가시성 변경자를 요약해 보여준다.

> ### 🌼 코틀린의 가시성 변경자 
> 
> - _변경자_
>  - _클래스 멤버_	
>  - _최상위 선언_
> 
> 위의 양식을 기준으로 아래와 같이 설명한다.
> 
> - public (기본 가시성임)
>   - 모든 곳에서 볼 수 있다.
>   - 모든 곳에서 볼 수 있다.
> - internal
>   - 같은 모듈 안에서만 볼 수 있다.
>   - 같은 모듈 안에서만 볼 수 있다.
> - protected
>   - 하위 클래스에서만 볼 수 있다.
>   - (최상위 선언에 적용할 수 없음)
> - private
>   - 같은 클래스 안에서만 볼 수 있다.
>   - 같은 파일 안에서만 볼 수 있다.

(필자 : 자바는 최상위 선언은 오직 클래스뿐)

<br/>

giveSpeech 함수 안의 각 줄은 가시성 규칙을 위반한다.

```kotlin
internal open class TalkativeButton : Focusable {
    private fun yell() = println("Hey!")
    protected fun whisper() = println("Let's talk!")
}

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
```

코틀린은 public 함수인 giveSpeech 안에서 
그보다 가시성이 더 낮은(이 경우 internal) 타입인 TalkativeButton을 참조하지 못하게 한다. 

- 이는 어떤 클래스의 기반 타입 목록에 들어있는 타입이나 제네릭 클래스의 타입 파라미터에 들어있는 타입의 가시성은 
  - 그 클래스 자신의 가시성과 같거나 더 높아야 하고, 
- 메서드의 시그니처에 사용된 모든 타입의 가시성은
  - 그 메서드의 가시성과 같거나 더 높아야 한다는 더 일반적인 규칙에 해당한다.
  
이런 규칙은 어떤 함수를 호출하거나 어떤 클래스를 확장할 때 필요한 모든 타입에 접근할 수 있게 보장해준다.

- 여기서 컴파일 오류를 없애려면 
  - giveSpeech 확장 함수의 가시성을 internal로 바꾸거나, 
  - TalkativeButton 클래스의 가시성을 public으로 바꿔야 한다.

자바에서는 같은 패키지 안에서 `protected` 멤버에 접근할 수 있지만, 
코틀린에서는 그렇지 않다는 점에서 자바와 코틀린의 protected가 다르다는 사실에 유의하자. 
코틀린의 가시성 규칙은 단순하다. protected 멤버는 오직 어떤 클래스나 그 클래스를 상속한 클래스 안에서만 보인다. 
클래스를 확장한 함수(확장 함수)는 그 클래스의 private이나 protected 멤버에 접근할 수 없다.

> ### ✅코틀린의 가시성 변경자와 자바
> 코틀린의 public, protected, private 변경자는 컴파일된 자바 바이트코드 안에서도 그대로 유지된다. 
> 그렇게 컴파일된 코틀린 선언의 가시성은 마치 자바에서 똑같은 가시성을 사용해 선언한 경우와 같다.
> 
> 유일한 예외는 `private` 클래스다. 자바에서는 클래스를 private으로 만들 수 없으므로 
> 내부적으로 코틀린은 private 클래스를 패키지-전용 클래스로 컴파일한다.
>  
> 자바에는 `internal`에 딱 맞는 가시성이 없다. 패키지-전용 가시성은 internal과는 전혀 다르다. 
> 모듈은 보통 여러 패키지로 이뤄지며 서로 다른 모듈에 같은 패키지에 속한 선언이 들어 있을 수도 있다. 
> 따라서 internal 변경자는 바이트코드상에서는 public이 된다.
> 
> 코틀린 선언과 그에 해당하는 자바 선언(또는 바이트코드 표현)에 이런 차이가 있기 때문에 
> 코틀린에서는 접근할 수 없는 대상을 자바에서 접근할 수 있는 경우가 생긴다. 
> 예를 들어 다른 모듈에 정의된 internal 클래스나 internal 최상위 선언을 모듈 외부의 자바 코드에서 접근할 수 있다. 
> 또한 코틀린에서 protected로 정의한 멤버를 코틀린 클래스와 같은 패키지에 속한 자바 코드에서는 
> 접근할 수 있다.(이는 자바에서 자바 protected 멤버에 접근하는 경우와 같다)
> 
> 하지만 코틀린 컴파일러가 internal 멤버의 이름을 보기 나쁘게 바꾼다는(mangle, 짓이기다, 심하게 훼손하다) 사실을 기억하라. 
> 그로 인해 기술적으로는 internal 멤버를 자바에서 문제없이 사용할 수 있지만, 
> 멤버 이름이 보기 불편하고 코드가 못생겨 보인다. 이렇게 이름을 바꾸는 이유는 두 가지다.
> 
> - 첫 번째는 한 모듈에 속한 어떤 클래스를 모듈 밖에서 상속한 경우, 
>   그 하위 클래스 내부의 메서드 이름이 우연히 상위 클래스의 internal 메서드와 같아져서 
>   내부 메서드를 오버라이드하는 경우를 방지하기 위함이고, 
> - 두 번째는 실수로 internal 클래스를 모듈 외부에서 사용하는 일을 막기 위함이다.
> 
> (필자 : 자바와 코틀린, 둘 모두 사용하는 일을 피하면 되지 않을까?)

코틀린과 자바 가시성 규칙의 또 다른 차이는 코틀린에서는 외부 클래스가 
내부 클래스나 중첩 클래스의 private 멤버에 접근할 수 없다는 점이다. 
다음 절에서 내부 클래스와 중첩된 클래스에 대해 설명하고 가시성과 관련된 예제도 살펴보자.


<br/>


## 4.1.4. 내부 클래스와 중첩된 클래스: 기본적으로 중첩 클래스

자바처럼 코틀린에서도 클래스 안에 다른 클래스를 선언할 수 있다. 
- 클래스 안에 다른 클래스를 선언하면 도우미 클래스를 캡슐화하거나 
- 코드 정의를 그 코드를 사용하는 곳 가까이에 두고 싶을 때 유용하다. 
- 자바와의 차이는 코틀린의 **중첩 클래스**(nested class)는 
명시적으로 요청하지 않는 한 바깥쪽 클래스 인스턴스에 대한 접근 권한이 없다는 점이다.

View의 상태를 직렬화. 
뷰를 직렬화하는 일은 쉽지 않지만 필요한 모든 데이터를 다른 도우미 클래스로 복사할 수는 있다. 
이를 위해 State 인터페이스를 선언하고 Serializable을 구현한다. 
View 인터페이스 안에는 뷰의 상태를 가져와 저장할 때 사용할 getCurrentState와 restoreState 메서드 선언이 있다.


```kotlin
interface State : Serializable

interface View {
  fun getCurrentState(): State
  fun restoreState(state: State) {}
}
```
 
Button 클래스의 상태를 저장하는 클래스는 Button 클래스 내부에 선언하면 편리할 것.


```java
public class Button implements View {
    @Override
    public State getCurrentState() {
        return new ButtonState();
    }

    @Override
    public void restoreState(final State state) { /* ... */ }
    
    public class ButtonState implements State { /* ... */ }
}
```

State 인터페이스를 구현한 ButtonState 클래스를 정의해서 Button에 대한 구체적인 정보를 저장한다. 
getCurrentState 메서드 안에서는 ButtonState의 새 인스턴스를 만든다. 
실제로는 getCurrentState 안에 필요한 모든 정보를 추가해야 한다.

이 코드의 어디가 잘못된 걸까? 왜 선언한 버튼의 상태를 직렬화하면 
java.io.NotSerializableException: Button이라는 오류가 발생할까?  
직렬화하려는 변수는 ButtonState 타입이 state 였는데 왜 Button을 직렬화할 수 없다는 예외가 발생할까?

자바에서 다른 클래스 안에 정의한 클래스는 자동으로 내부 클래스(inner class)가 된다.
이 예제의 ButtonState 클래스는 바깥쪽 Button 클래스에 대한 참조를 묵시적으로 포함한다. 
그 참조로 인해 ButtonState를 직렬화할 수 없다.
Button을 직렬화할 수 없으므로 버튼에 대한 참조가 ButtonState의 직렬화를 방해한다.

이 문제를 해결하려면 ButtonState를 static 클래스로 선언해야 한다. 
- 자바에서 중첩 클래스를 static으로 선언하면 그 클래스를 둘러싼 바깥쪽 클래스에 대한 묵시적인 참조가 사라진다. 
- 코틀린에서 중첩된 클래스가 기본적으로 동작하는 방식은 방금 설명한 것과 정반대다.

```kotlin
class Button : View {
    override fun getCurrentState(): State = ButtonState()

    override fun restoreState(state: State) { /*...*/ }
    
    class ButtonState : State { /*...*/ }  // 이 클래스는 자바의 정적 중첩 클래스와 대응한다.
}
```

코틀린 중첩 클래스에 아무런 변경자가 붙지 않으면 자바 static 중첩 클래스와 같다. 
이를 내부 클래스로 변경해서 바깥쪽 클래스에 대한 참조를 포함하게 만들고 싶다면 inner 변경자를 붙여야 한다. 

> ### 🌼 자바와 코틀린의 중첩 클래스와 내부 클래스 관계
> - _클래스 B 안에 정의된 클래스 A_
>   - _자바에서는_
>   - _코틀린에서는_
> 
> 위의 양식을 기준으로 아래와 같이 설명한다.
> 
> - 중첩 클래스(바깥쪽 클래스에 대한 참조를 저장하지 않음) 
>   - static class A 
>   - class A 
> - 내부 클래스(바깥쪽 클래스에 대한 참조를 저장함) 
>   - class A 
>   - inner class A

내부 클래스 Inner 안에서 바깥쪽 클래스 Outer의 참조에 접근하려면 this@Outer라고 써야 한다.

```kotlin
class Outer {
    inner class Inner {
        fun getOuterReference(): Outer = this@Outer
    }
}
```

<br/>


## 4.1.5. 봉인된 클래스: 클래스 계층 정의 시 계층 확장 제한

상위 클래스인 Expr에는 숫자를 표현하는 Num과 덧셈 연산을 표현하는 Sum이라는 두 하위 클래스가 있다.

```kotlin
interface Expr

class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr

fun eval(e: Expr): Int =
    when (e) {
        is Num -> e.value
        is Sum -> eval(e.right) + eval(e.left)
        else -> throw IllegalArgumentException("Unknown expression")
    }
```

코틀린 컴파일러는 when을 사용해 Expr 타입의 값을 검사할 때 꼭 디폴트 분기인 else 분기를 덧붙이게 강제한다. 
이 예제의 else 분기에서는 반환할 만한 의미 있는 값이 없으므로 예외를 던진다.

But...
- 항상 디폴트 분기를 추가하는 게 편하지는 않다. 
- 디폴트 분기가 있으면 이런 클래스 계층에 새로운 하위 클래스를 추가하더라도 
  컴파일러가 when이 모든 경우를 처리하는지 제대로 검사할 수 없다. 
- 혹 실수로 새로운 클래스 처리를 잊어버렸더라도 디폴트 분기가 선택되기 때문에 심각한 버그가 발생할 수 있다.

코틀린은 이런 문제에 대한 해법을 제공한다. `sealed` 클래스가 그 답이다. 
상위 클래스에 sealed 변경자를 붙이면 **그 상위 클래스를 상속한 하위 클래스 정의를 제한할 수** 있다. 
sealed 클래스의 하위 클래스를 정의할 때는 반드시 상위 클래스 안에 중첩시켜야 한다.

```kotlin
sealed class Expr {
  class Num(val value: Int) : Expr()
  class Sum(val left: Expr, val right: Expr) : Expr()
}

fun eval(e: Expr): Int =
  when (e) {
    is Expr.Num -> e.value
    is Expr.Sum -> eval(e.right) + eval(e.left)
  }
```

when 식에서 sealed 클래스의 모든 하위 클래스를 처리한다면 디폴트 분기(else 분기)가 필요 없다. 
sealed로 표시된 클래스는 자동으로 `open`임을 기억하라. 따라서 별도로 open 변경자를 붙일 필요가 없다. 
(필자 : 책에 있는 그림 4.2 참고)

sealed 클래스에 속한 값에 대해 디폴트 분기를 사용하지 않고 when 식을 사용하면 
나중에 sealed 클래스의 상속 계층에 새로운 하위 클래스를 추가해도 when 식이 컴파일되지 않는다. 
따라서 when 식을 고쳐야 한다는 사실을 쉽게 알 수 있다. 내부적으로 Expr 클래스는 private 생성자를 가진다. 
그 생성자는 클래스 내부에서만 호출할 수 있다. sealed 인터페이스를 정의할 수는 없다. 
왜냐하면 봉인된 인터페이스를 만들 수 있다면 
그 인터페이스를 자바 쪽에서 구현하지 못하게 막을 수 있는 수단이 코틀린 컴파일러에게 없기 때문이다.

> **코틀린 1.0에서 sealed는 너무 제약이 심하다.** 
> 예를 들어 모든 하위 클래스는 중첩 클래스여야 하고, 데이터 클래스로 sealed 클래스를 상속할 수도 없다. 
> 코틀린 1.1부터는 이 제한이 완화됐다. 
> 봉인된 클래스와 같은 파일의 아무데서나 봉인된 클래스를 상속한 하위 클래스를 만들 수 있고, 
> 데이터 클래스로 하위 클래스를 정의할 수도 있다.
> (필자 : 이해 안감. 버전이 지났기에 그냥 넘어감)

코틀린에서는 클래스를 확장할 때나 인터페이스를 구현할 때 모두 콜론(:)을 사용한다.

```class Num(val value: Int) : Expr()```

Expr()에 쓰인 괄호에 대해서는 코틀린의 클래스 초기화에 대해 다루는 다음 절에서 설명한다.

<br/>


## 4.2. 뻔하지 않은 생성자와 프로퍼티를 갖는 클래스 선언

자바에서는 생성자를 하나 이상 선언할 수 있다. 코틀린도 비슷하지만 한 가지 바뀐 부분이 있다. 
코틀린은 주 생성자(보통 주 생성자는 클래스를 초기화할 때 주로 사용하는 간략한 생성자로, 클래스 본문 밖에서 정의한다)와 
부 생성자(클래스 본문안에서 정의한다)를 구분한다. 
또한 코틀린에서는 초기화 블록(initializer block)을 통해 초기화 로직을 추가할 수 있다. 


<br/>


## 4.2.1 클래스 초기화: 주 생성자와 초기화 블록

```kotlin
class User(val nickname: String)
```

보통 클래스의 모든 선언은 중괄호({}) 사이에 들어간다. 
하지만 이 클래스 선언에는 중괄호가 없고 괄호 사이에 val 선언만 존재한다. 
이렇게 클래스 이름 뒤에 오는 괄호로 둘러싸인 코드를 주 생성자(primary constructor)라고 부른다. 
주 생성자는... 
- 생성자 파라미터를 지정하고 
- 그 생성자 파라미터에 의해 초기화되는 프로퍼티를 정의하는 두 가지 목적에 쓰인다.

```kotlin
class User constructor(_nickname: String) { // 파라미터가 하나만 있는 주 생성자
    val nickname: String

    init { // 초기화 블록
        this.nickname = _nickname
    }
}
```

- `constructor` 키워드는 주 생성자나 부 생성자 정의를 시작할 때 사용한다. 
- `init` 키워드는 초기화 블록을 시작한다. 

초기화 블록에는 클래스의 객체가 만들어질 때(인스턴스화될 때) 실행될 초기화 코드가 들어간다. 
초기화 블록은 주 생성자와 함께 사용된다. 
주 생성자는 제한적이기 때문에 별도의 코드를 포함할 수 없으므로 초기화 블록이 필요하다. 
필요하다면 클래스 안에 여러 초기화 블록을 선언할 수 있다.

생성자 파라미터 _nickname에서 맨 앞의 밑줄(_)은 프로퍼티와 생성자 파라미터를 구분해준다. 
다른 방법으로 자바에서 흔히 쓰는 방식처럼 this.nickname = nickname 같은 식으로 
생성자 파라미터와 프로퍼티의 이름을 같게 하고 프로퍼티에 this를 써서 모호성을 없애도 된다.

이 예제에서는 nickname 프로퍼티를 초기화하는 코드를 nickname 프로퍼티 선언에 포함시킬 수 있어서 
초기화 코드를 초기화 블록에 넣을 필요가 없다. 
또 주 생성자 앞에 별다른 애너테이션이나 가시성 변경자가 없다면 constructor를 생략해도 된다.

```kotlin
class User(_nickname: String) { // 파라미터가 하나만 있는 주 생성자
    // 프로퍼티를 주 생성자의 파라미터로 초기화
    val nickname: String = _nickname 
}
```

프로퍼티를 초기화하는 식이나 초기화 블록 안에서만 주 생성자의 파라미터를 참조할 수 있다는 점에 유의하자.

방금 살펴본 두 예제는 클래스 본문에서 val 키워드를 통해 프로퍼티를 정의했다. 
주 생성자 파라미터 이름앞에 val을 추가하는 방식으로 프로퍼티 정의와 초기화를 간략히 쓸 수 있다.

```kotlin
class User(val nickname: String) // 'val'은 파라미터에 상응하는 프로퍼티가 생성된다는 뜻
```

함수 파라미터와 마찬가지로 생성자 파라미터에도 디폴트 값을 정의할 수 있다.

```kotlin
// 생성자 파라미터에 대한 디폴트 값을 제공
class User(val nickname: String, val isSubscribed: Boolean = true)
```

클래스의 인스턴스를 만드려면 new 키워드 없이 생성자를 직접 호출하면 된다.

```kotlin
val hyun = User("현석")
println(hyun.isSubscribed)
```

> 모든 생성자 파라미터에 디폴트 값을 지정하면 컴파일러가 자동으로 **파라미터 없는 생성자**를 만들어준다. 
> 그렇게 자동으로 만들어진 파라미터 없는 생성자는 디폴트 값을 사용해 클래스를 초기화한다. 
> 의존관계 주입(DI, Dependency Injection) 프레임워크 등 자바 라이브러리 중에는 
> 파라미터 없는 생성자를 통해 객체를 생성해야만 라이브러리 사용이 가능한 경우가 있는데, 
> 코틀린이 제공하는 파라미터 없는 생성자는 그런 라이브러리와의 통합을 쉽게 해준다.

클래스에 기반 클래스가 있다면 주 생성자에서 기반 클래스의 생성자를 호출해야 할 필요가 있다. 
기반 클래스를 초기화하려면 기반 클래스 이름 뒤에 괄호를 치고 생성자인자를 넘긴다.

```kotlin
open class User(val nickname: String) { /**/ }
class TwitterUser(nickname: String) : User(nickname) { /**/ } // 기반 클래스 초기화
```

클래스를 정의할 때 별도로 생성자를 정의하지 않으면 
컴파일러가 자동으로 아무 일도 하지 않는 인자가 없는 디폴트 생성자를 만들어준다

```kotlin
open class Button // 인자가 없는 디폴트 생성자가 만들어진다
```

Button의 생성자는 아무 인자도 받지 않지만, 
Button 클래스를 상속한 하위 클래스는 반드시 Button 클래스의 생성자를 호출해야 한다.

```kotlin
class RadioButton: Button()
```

이 규칙으로 인해 기반 클래스의 이름 뒤에는 꼭 빈 괄호가 들어간다(물론 생성자 인자가 있다면 괄호 안에 인자가 들어간다). 
반면 인터페이스는 생성자가 없기 때문에 아무 괄호도 없다. 

어떤 클래스를 클래스 외부에서 인스턴스화하지 못하게 막고 싶다면 모든 생성자를 private으로 만들면 된다. 

```kotlin
class Secretive private constructor() {}    // 이 클래스의 주 생성자는 비공개다.
```

Secretive 클래스 안에는 주 생성자밖에 없고 그 주 생성자는 비공개이므로 외부에서는 Secretive를 인스턴스화할 수 없다. 

> ### ✅비공개 생성자에 대한 대안
> 유틸리티 함수를 담아두는 역할만을 하는 클래스는 인스턴스화할 필요가 없고, 
> 싱글턴인 클래스는 미리 정한 팩토리 메서드 등의 생성 방법을 통해서만 객체를 생성해야 한다.
> 
> 자바에서는 이런 더 일반적인 요구 사항을 명시할 방법이 없으므로 
> 어쩔 수 없이 private 생성자를 정의해서 클래스를 다른 곳에서 인스턴스화하지 못하게 막는 경우가 생긴다.
> 
> 코틀린은 그런 경우를 언어에서 기본 지원한다. 정적 유틸리티 함수 대신 최상위 함수를 사용할 수 있고, 
> 싱글턴을 사용하고 싶으면 객체를 선언하면 된다.

실제로 대부분의 경우 클래스의 생성자는 아주 단순하다. 
생성자에 아무 파라미터도 없는 클래스도 많고, 
생성자 코드 안에서 생성자가 인자로 받은 값을 프로퍼티에 설정하기만 하는 생성자도 많다. 
그래서 코틀린은 간단한 주 생성자 문법을 제공한다. 대부분 이런 간단한 주 생성자 구문만으로도 충분하다. 

<br/>


## 4.2.2. 부 생성자: 상위 클래스를 다른 방식으로 초기화

자바에서 오버로드한 생성자가 필요한 상황 중 상당 수는 
코틀린의 디폴트 파라미터 값과 이름 붙인 인자 문법을 사용해 해결할 수 있다.

> 인자에 대한 디폴트 값을 제공하기 위해 부 생성자를 여럿 만들지 말라. 
> 대신 파라미터의 디폴트 값을 생성자 시그니처에 직접 명시하라.

가장 일반적인 상황은 프레임워크 클래스를 확장해야 하는데 
여러 가지 방법으로 인스턴스를 초기화할 수 있게 다양한 생성자를 지원해야 하는 경우다. 
생성자가 2개인 View 클래스. (안드로이드 개발자라면 이 클래스를 알아볼 수 있을 것이다)

```kotlin
open class View {
    constructor(ctx: Context) { }
    constructor(ctx: Context, attr: AttributeSet) { }
}
```

이 클래스는 주 생성자를 선언하지 않고(클래스 헤더에 있는 클래스 이름 뒤에 괄호가 없다), 부 생성자만 2가지 선언한다. 
부 생성자는 constructor 키워드로 시작한다.
이 클래스를 확장하면서 똑같이 부 생성자를 정의할 수 있다.

```kotlin
class MyButton : View {
    constructor(ctx: Context) : super(ctx) { }
    constructor(ctx: Context, attr: AttributeSet) : super(ctx, attr) { }
}
```

여기서 두 부 생성자는 super() 키워드를 통해 자신에 대응하는 상위 클래스 생성자를 호출한다. 
(생성자가 상위 클래스 생성자에게 객체 생성을 위임)

자바와 마찬가지로 생성자에서 this()를 통해 클래스 자신의 다른 생성자를 호출할 수 있다.

```kotlin
class MyButton : View {
    constructor(ctx: Context): this(ctx, MY_STYLE) { // 이 클래스의 다른 생성자에게 위임
        // ...
    }
    constructor(ctx: Context, attr: AttributeSet): super(ctx, attr) {
        // ...
    }
}
```

MyButton 클래스의 생성자 중 하나가 파라미터의 디폴트 값을 넘겨서 
같은 클래스의 다른 생성자(this를 사용해 참조함)에게 생성을 위임한다. 
두 번째 생성자는 여전히 super()를 호출한다.
클래스에 주 생성자가 없다면 모든 부 생성자는 반드시 상위 클래스를 초기화하거나 다른 생성자에게 생성을 위임해야 한다.

부 생성자가 필요한 주된 이유는 자바 상호운용성이다. 하지만 부 생성자가 필요한 다른 경우도 있다. 
클래스 인스턴스를 생성할 때 파라미터 목록이 다른 생성 방법이 여럿 존재하는 경우에는 부 생성자를 여럿 둘 수밖에 없다.

<br/>


## 4.2.3. 인터페이스에 선언된 프로퍼티 구현

코틀린에서는 인터페이스에 추상 프로퍼티 선언을 넣을 수 있다.

```kotlin
interface User {
  val nickname: String
}
```

User 인터페이스를 구현하는 클래스가 nickname의 값을 얻을 수 있는 방법을 제공해야 한다. 
인터페이스에 있는 프로퍼티 선언에는 뒷받침하는 필드나 게터 등의 정보가 들어있지 않다. 
인퍼테이스는 아무 상태도 포함할 수 없으므로 
상태를 저장할 필요가 있다면 인터페이스를 구현한 하위 클래스에서 상태 저장을 위한 프로퍼티 등을 만들어야 한다.

PrivateUser는 별명을 저장하기만 하고 
SubscribingUser는 이메일을 함께 저장한다. 
FacebookUser는 페이스북 계정의 ID를 저장한다.

```kotlin
class PrivateUser(override val nickname: String) : User // 주 생성자에 있는 프로퍼티
class SubscribingUser(val email: String) : User {
    override val nickname: String
        get() = email.substringBefore('@')  // 커스텀 게터
}
class FacebookUser(val accountId: Int) : User {
    override val nickname = getFacebookName(accountId)  // 프로퍼티 초기화 식
}

println(PrivateUser("test@kotlinlang.org").nickname)
// test@kotlinlang.org
println(SubscribingUser("test@kotlinlang.org").nickname)
// test
```

- **PrivateUser**는 주 생성자 안에 프로퍼티를 직접 선언하는 간결한 구문을 사용한다. 
이 프로퍼티는 User의 추상 프로퍼티를 구현하고 있으므로 override를 표시해야 한다.
- **SubscribingUser**는 커스텀 게터로 nickname 프로퍼티를 설정한다. 
이 프로퍼티는 뒷받침하는 필드에 값을 저장하지 않고 매번 이메일 주소에서 별명을 계산해 반환한다.
- **FacebookUser**에서는 초기화 식으로 nickname 값을 초기화한다. 
이때 페이스북 사용자 ID를 받아서 그 사용자의 이름을 반환해주는 
getFacebookName 함수(이 함수는 다른 곳에 정의돼 있다고 가정한다)를 호출해서 nickname을 초기화한다. 
getFacebookName은 페이스북에 접속해서 인증을 거친 후 원하는 데이터를 가져와야 하기 때문에 비용이 많이 들 수도 있다. 
그래서 객체를 초기화하는 단계에 한 번만 getFacebookName을 호출하게 설계했다.

SubscribingUser의 nickname은 매번 호출될 때마다 
substringBefore를 호출해 계산하는 커스텀 게터를 활용하고, 
FacebookUser의 nickname은 객체 초기화 시 계산한 데이터를 뒷받침하는 필드에 저장했다가 불러오는 방식을 활용한다.
(필자 : **뒷받침하는 필드**란 프로퍼티의 값을 저장하는 field)

인터페이스에는 추상 프로퍼티뿐 아니라 게터와 세터가 있는 프로퍼티를 선언할 수도 있다. 
물론 그런 게터와 세터는 뒷받침하는 필드를 참조할 수 없다
(뒷받침하는 필드가 있다면 인터페이스에 상태를 추가하는 셈인데 인터페이스는 상태를 저장할 수 없다).

```kotlin
interface User {
    val email: String
    val nickname: String
        get() = email.substringBefore('@')  // 프로퍼티에 뒷받침하는 필드가 없다. 대신 매번 결과를 계산해 돌려준다.
}
```

이 인터페이스에는... 
- 추상 프로퍼티인 email과 
- 커스텀 게터가 있는 nickname 프로퍼티가 함께 들어있다. 
- 하위 클래스는 추상 프로퍼티인 email을 반드시 오버라이드해야 한다. 
- 반면 nickname은 오버라이드하지 않고 상속할 수 있다.


<br/>


## 4.2.4. 게터와 세터에서 뒷받침하는 필드에 접근

지금까지 프로퍼티의 두 가지 유형
- 값을 저장하는 프로퍼티 
- 커스텀 접근자에서 매번 값을 계산하는 프로퍼티

값을 저장하는 동시에 로직을 실행할 수 있게 하기 위해서는 
접근자 안에서 프로퍼티를 뒷받침하는 필드에 접근할 수 있어야 한다.

프로퍼티에 저장된 값의 변경 이력을 로그에 남기려는 경우를 생각해보자. 
그런 경우 변경 가능한 프로퍼티를 정의하되 세터에서 프로퍼티 값을 바꿀 때마다 약간의 코드를 추가로 실행해야 한다.

```kotlin
class User(val name: String) {
    var address: String = "unspecified"
        set(value: String) {
            println("""
                Address was changed for $name:
                "$field" -> "$value".""".trimIndent())  // 뒷받침하는 필드 값 읽기
            field = value   // 뒷받침하는 필드 값 변경하기
        }
}

>>> val user = User("Alice")
>>> user.address = "Elsenheimerstrasse 47, 80687 Muenchen"  // address의 세터 호출
Address was changed for Alice:
"unspecified" -> "Elsenheimerstrasse 47, 80687 Muenchen".
```
 
이 구문은 내부적으로 address의 세터를 호출한다. 
이 예제에서는 커스텀 세터를 정의해서 추가 로직을 실행한다(여기서는 단순화를 위해 화면에 값의 변화를 출력하기만 한다).

접근자의 본문에서는 field라는 특별한 식별자를 통해 `뒷받침하는 필드`에 접근할 수 있다. 
게터에서는 field 값을 읽을 수만 있고, 세터에서는 field 값을 읽거나 쓸 수 있다.

변경 가능 프로퍼티의 게터와 세터 중 한쪽만 직접 정의해도 된다는 점을 기억하라. 
위의 코드에서 address의 게터는 필드 값을 그냥 반환해주는 뻔한 게터다. 
따라서 게터를 굳이 직접 정의할 필요가 없다.

뒷받침하는 필드가 있는 프로퍼티와 그런 필드가 없는 프로퍼티의 차이 
- 클래스의 프로퍼티를 사용하는 쪽에서 프로퍼티를 읽는 방법이나 쓰는 방법은 뒷받침하는 필드의 유무와는 관계가 없다. 
- 컴파일러는 디폴트 접근자 구현을 사용하건 직접 게터나 세터를 정의하건 관계없이 
게터나 세터에 field를 사용하는 프로퍼티에 대해 뒷받침하는 필드를 생성해준다. 
- 다만 field를 사용하지 않는 커스텀 접근자 구현을 정의한다면 
뒷받침하는 필드는 존재하지 않는다(프로퍼티가 val인 경우에는 게터에 field가 없으면 되지만, 
var인 경우에는 게터나 세터 모두에 field가 없어야 한다).

```kotlin
// 커스텀 접근자 예시
val isSquare: Boolean
    get() = height == width
 
var counter: Int = 0
    private set
```


<br/>


## 4.2.5. 접근자의 가시성 변경

접근자의 가시성은 기본적으로는 프로퍼티의 가시성과 같다. 
하지만 원한다면 get이나 set 앞에 가시성 변경자를 추가해서 접근자의 가시성을 변경할 수 있다. 

```kotlin
class LengthCounter {
  var counter: Int = 0
    private set // 이 클래스 밖에서 이 프로퍼티의 값을 바꿀 수 없다.
  fun addWord(word: String) {
    counter += word.length
  }
}

>>> val lengthCounter = LengthCounter()
>>> lengthCounter.addWord("Hi!")
>>> println(lengthCounter.counter)
3
```

이 클래스는 자신에게 추가된 모든 단어의 길이를 합산한다. 
전체 길이를 저장하는 프로퍼티는 클라이언트에게 제공하는 API의 일부분이므로 public으로 외부에 공개된다. 
하지만 외부 코드에서 단어 길이의 합을 마음대로 바꾸지 못하게 이 클래스 내부에서만 길이를 변경하게 만들고 싶다. 
그래서 기본 가시성을 가진 게터를 컴파일러가 생성하게 내버려 두는 대신 세터의 가시성을 private으로 지정한다.


> ### ✅프로퍼티에 대해 나중에 다룰 내용
> 이 책의 뒷부분에서 프로퍼티에 대해 다룰 내용을 참고할 수 있게 여기 미리 밝혀둔다. 
> - `lateinit` 변경자를 널이 될 수 없는 프로퍼티에 지정하면 프로퍼티를 생성자가 호출된 다음에 초기화한다는 뜻이다. 
> 일부 프레임워크에서는 이런 특성이 꼭 필요하다. 6장에서 이 내용을 다룬다. 
> - 요청이 들어오면 비로소 초기화되는 `지연 초기화`(lazy initialized) 프로퍼티는 
> 더 일반적인 위임 프로퍼티(delegated properly)의 일종이다. 
> 위임 프로퍼티 및 지연 초기화 프로퍼티에 대해서는 7장에서 다룬다. 
> - 자바 프레임워크와의 호환성을 위해 `자바의 특징을 코틀린에서 에뮬레이션하는 애노테이션`을 활용할 수 있다. 
> 예를 들어 @JvmField 애너테이션을 프로퍼티에 붙이면 접근자가 없는 public 필드를 노출시켜준다. 
> 애너테이션에 대해서는 10장에서 다룬다. `const` 변경자를 사용하면 애너테이션을 더 편리하게 다룰 수 있고 
> 원시 타입이나 String 타입인 값을 애너테이션 인자로 활용할 수 있다. 이에 대해서는 10장에서 다룬다.


<br/>

## 4.3. 컴파일러가 생성한 메소드: 데이터 클래스와 클래스 위임

자바 플랫폼에서는 클래스가 equals, hashCode, toString 등의 메서드를 구현해야한다. 
하지만 자동으로 equals, hashCode, toString 등을 생성한다고 해도 코드베이스가 번잡해진다는 면은 동일하다. 

코틀린 컴파일러는 한걸음 더 나가서 이런 메서드를 기계적으로 생성하는 작업을 보이지 않는 곳에서 해준다. 
따라서 필수 메서드로 인한 잡음 없이 소스코드를 깔끔하게 유지할 수 있다.

이제 코틀린 컴파일러가 데이터 클래스에 유용한 메서드를 자동으로 만들어주는 예와 
클래스 위임 패턴을 아주 간단하게 쓸 수 있게 해주는 예를 살펴보자.

## 4.3.1. 모든 클래스가 정의해야 하는 메서드
자바와 마찬가지로 코틀린 클래스도 toString, equals, hashCode 등을 오버라이드할 수 있다. 
코틀린은 이런 메서드 구현을 자동으로 생성해줄 수 있다. 

```kotlin
class Client(val name: String, val postalCode: Int)
```

### 문자열 표현: toString()
자바처럼 코틀린의 모든 클래스도 인스턴스 문자열 표현을 얻을 방법을 제공한다. 
기본 제공되는 객체의 문자열 표현은 Client@5e9f23b4 같은 방식인데, 
이는 그다지 유용하지 않다. 이 기본 구현을 바꾸려면 toString 메서드를 오버라이드해야 한다.

```kotlin
class Client(val name: String, val postalCode: Int) {
    override fun toString() = "Client(name=$name, postalCode=$postalCode)"
}

>>> val client1 - Client("오현석", 4122)
>>> println(client1)
Client(name=오현석, postalCode=4122)
```


### 객체의 동등성: equals()
Client 클래스를 사용하는 모든 계산은 클래스 밖에서 이뤄진다. 
Client는 단지 데이터를 저장할 뿐이며, 그에 따라 구조도 단순하고 내부 정보를 투명하게 외부에 노출하게 설계됐다. 
그렇지만 클래스는 단순할지라도 동작에 대한 몇 가지 요구 사항이 있을 수 있다. 
예를 들어 서로 다른 두 객체가 내부에 동일한 데이터를 포함하는 경우 그 둘을 동등한 객체로 간주해야 할 수도 있다.

```kotlin
>>> val client1 = Client("오현석", 4122)
>>> val client2 = Client("오현석", 4122)
>>> println(client1 == client2) // 코틀린에서 == 연산자는 참조 동일성을 검사하지 않고 객체의 동등성을 검사한다. 따라서 == 연산은 equals를 호출하는 식으로 컴파일된다.
false
```


> ### ✅동등성 연산에 ==를 사용함
> 자바에서는 ==를 원시 타입과 참조 타입을 비교할 때 사용한다.
원시 타입의 경우 ==는 두 피연산자의 값이 같은지 비교한다(동등성(equality)).
반면 참조 타입의 경우 ==는 두 피연산자의 주소가 같은지를 비교한다(참조 비교(reference comparision)).
따라서 자바에서는 두 객체의 동등성을 알려면 equals를 호출해야 한다.
자바에서는 equals 대신 ==를 호출하면 문제가 될 수 있다는 사실도 아주 잘 알려져 있다.
> 
> 코틀린에서는 ==연산자가 두 객체를 비교하는 기본적인 방법이다.
==는 내부적으로 equals를 호출해서 객체를 비교한다.
따라서 **클래스가 equals를 오버라이드하면 ==를 통해 안전하게 그 클래스의 인스턴스를 비교**할 수 있다.
**참조 비교를 위해서는 === 연산자를** 사용할 수 있다.
=== 연산자는 자바에서 객체의 참조를 비교할 때 사용하는 == 연산자와 같다.


```kotlin
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
```

다시 말하지만 코틀린의 is 검사는 자바의 instanceof와 같다. 
is는 어떤 값의 타입을 검사한다. !is의 결과는 is 연산자의 결과를 부정한 값이다.

코틀린에서는 override 변경자가 필수여서 실수로 override fun equals(other: Any?) 대신 
override fun equals(other: Client)를 작성할 수는 없다. 
그래서 equals를 오버라이드하고 나면 프로퍼티의 값이 모두 같은 두 고객 객체는 동등하리라 예상할 수 있다. 
실제로 client1 == client2는 이제 true를 반환한다. 

하지만 Client 클래스로 더 복잡한 작업을 수행해보면 제대로 작동하지 않는 경우가 있다. 
이와 관련해 흔히 면접에서 질문하는 내용이 "Client"가 제대로 작동하지 않는 경우를 말하고 
문제가 무엇인지 설명하시오"다. hashCode 정의를 빠뜨려서 그렇다고 답하는 개발자가 많을 것이다. 
이 경우에는 실제 hashCode가 없다는 점이 원인이다.

### 해시 컨테이너: hashCode()
자바에서는 equals를 오버라이드할 때 반드시 hashCode도 함께 오버라이드해야 한다.
아래 코드를 보면 프로퍼티가 모두 일치하므로 새 인스턴스와 집합에 있는 기존 인스턴스는 동등하다. 

```kotlin
val processed = hashSetOf(Client("오현석", 3123))
println(processed.contains(Client("오현석", 3123)))
>>> false
```

이는 Client 클래스가 hashCode 메서드를 정의하지 않았기 때문이다. 
JVM 언어에서는 hashCode가 지켜야하는 "equals()가 true를 반환하는 두 객체는 
반드시 같은 hashCode()를 반환해야 한다"라는 제약이 있는데, Client는 이를 어기고 있다.

processed 집합은 HashSet이다. HashSet은 원소를 비교할 때 비용을 줄이기 위해 
먼저 **객체의 해시 코드를 비교**하고 해시 코드가 같은 경우에만 실제 값을 비교한다. 
방금 본 예제의 두 Client 인스턴스는 해시 코드가 다르기 때문에 두 번째 인스턴스가 집합 안에 들어있지 않다고 판단한다.
해시 코드가 다를 때 equals가 반환하는 값은 판단 결과에 영향을 끼치지 못한다. 
즉, 원소 객체들이 해시 코드에 대한 규칙을 지키지 않는 경우 HashSet은 제대로 작동할 수 없다.

이 문제를 고치려면 Client가 hashCode를 구현해야 한다.

```kotlin
class Client(val name: String, val postalCode: Int) {
    ...
    override fun hashCode(): Int = name.hashCode() * 31 + postalCode
}
```

<br/>


## 4.3.2. 데이터 클래스: 모든 클래스가 정의해야 하는 메소드 자동 생성

어떤 클래스가 데이터를 저장하는 역할만을 수행한다면 toStirng, equals, hashCode를 반드시 오버라이드해야 한다. 
코틀린에서 data라는 변경자는 클래스 앞에 붙이면 필요한 메서드를 컴파일러가 자동으로 만들어준다. 
data 변경자가 붙은 클래스를 데이터 클래스라고 부른다.

```kotlin
data class Client(val name: String, val postalCode: Int)
```

이제 Client 클래스는 자바에서 요구하는 모든 메서드를 포함한다.

- 인스턴스 간 비교를 위한 equals
- HashMap과 같은 해시 기반 컨테이너에서 키로 사용할 수 있는 hashCode
- 클래스의 각 필드를 선언 순서대로 표시하는 문자열 표현을 만들어주는 toString

equals와 hashCode는 주 생성자에 나열된 모든 프로퍼티를 고려해 만들어진다. 
생성된 equals 메서드는 모든 프로퍼티 값의 동등성을 확인한다. 
hashCode 메서드는 모든 프로퍼티의 해시 값을 바탕으로 계산한 해시 값을 반환한다. 
이때 **주 생성자 밖에 정의된 프로퍼티는 equals나 hashCode를 계산할 때 고려의 대상이 아니라는 사실**에 유의하라.


### 데이터 클래스와 불변성: copy() 메서드

데이터 클래스의 프로퍼티가 꼭 val일 필요는 없다. 원한다면 var프로퍼티를 써도 된다. 
하지만 데이터 클래스의 모든 프로퍼티를 읽기 전용으로 만들어서 
데이터 클래스를 불변(immutable) 클래스로 만들라고 권장한다.
HashMap 등의 컨테이너에 데이터 클래스 객체를 담는 경우엔 불변성이 필수적이다. 
데이터 클래스 객체를 키로 하는 값을 컨테이너에 담은 다음에 키로 쓰인 데이터 객체의 프로퍼티를 변경하면 
컨테이너 상태가 잘못될 수 있다. 

게다가 불변 객체를 사용하면 프로그램에 대해 훨씬 쉽게 추론할 수 있다. 
특히 다중스레드 프로그램의 경우 이런 성질은 더 중요하다. 
불변 객체를 주로 사용하는 프로그램에서는 스레드가 사용 중인 데이터를 다른 스레드가 변경할 수 없으므로 
스레드를 동기화해야 할 필요가 줄어든다.

코틀린 컴파일러는 한 가지 편의 메서드를 제공한다. 
그 메서드는 객체를 복사(copy)하면서 일부 프로퍼티를 바꿀 수 있게 해주는 copy 메서드다. 
객체를 메모리상에서 직접 바꾸는 대신 복사본을 만드는 편이 더 낫다. 복사본은 원본과 다른 생명주기를 가지며, 
복사를 하면서 일부 프로퍼티 값을 바꾸거나 복사본을 제거해도 
프로그램에서 원본을 참조하는 다른 부분에 전혀 영향을 끼치지 않는다. 
Client의 copy를 구현한다면 다음과 같을 것이다.

```kotlin
class Client(val name: String, val pastalCode: Int) {
    ...
    fun copy(name: String = this.name,
             postalCode: Int = this.postalCody) =
        Client(name, postalCode)
}

>>> val lee = Client("이계영", 4122)
>>> println(lee.copy(postalCode = 4000))
Client(name=이계영, postalCode=4000)
```

<br/>


## 4.3.3. 클래스 위임: by 키워드 사용
대규모 객체지향 시스템을 설계할 때 시스템을 취약하게 만드는 문제는 
보통 구현 상속(implementation inheritance)에 의해 발생한다. 
하위 클래스가 상위 클래스의 메서드 중 일부를 오버라이드하면 하위 클래스는 상위 클래스의 세부 구현 사항에 의존하게 된다. 
시스템이 변함에 따라 상위 클래스의 구현이 바뀌거나 상위 클래스에 새로운 메서드가 추가된다. 
그 과정에서 **하위 클래스가 상위 클래스에 대해 갖고 있던 가정이 깨져서 코드가 정상적으로 작동하지 못하는 경우**가 생길 수 있다.

코틀린을 설계하면서 우리는 이런 문제를 인식하고 기본적으로 클래스를 `final`로 취급하기로 결정했다. 
모든 클래스를 기본적으로 final로 취급하면 상속을 염두에 두고 `open` 변경자로 열어둔 클래스만 확장할 수 있다. 
열린 상위 클래스의 소스코드를 변경할 때는 open 변경자를 보고 해당 클래스를 다른 클래스가 상속하리라 예상할 수 있으므로, 
변경 시 하위 클래스를 깨지 않기 위해 좀 더 조심할 수 있다.

하지만 종종 상속을 허용하지 않는 클래스에 새로운 동작을 추가해야 할 때가 있다. 
이럴 때 사용하는 일반적인 방법이 **데코레이터 패턴**(Decorator)이다. 
이 패턴의 핵심은 상속을 허용하지 않는 클래스(기존 클래스) 대신 사용할 수 있는 새로운 클래스(데코레이터)를 만들되 
기존 클래스와 같은 인터페이스를 데코레이터가 제공하게 만들고, 
기존 클래스를 데코레이터 내부에 필드로 유지하는 것이다. 
이때 새로 정의해야 하는 기능은 데코레이터의 메서드에 새로 정의하고 
기존 기능이 그대로 필요한 부분은 데코레이터의 메서드가 기존 클래스의 메서드에게 요청을 **전달**(forwarding)한다.

이런 접근 방법의 단점은 **준비 코드가 상당히 많이 필요하다는 점**이다
(필요한 준비 코드가 너무 많기 때문에 IntelliJ 아이디어 등의 IDE는 
데코레이터의 준비 코드를 자동으로 생성해주는 기능을 제공한다). 
예를 들어 Collection 같이 비교적 단순한 인터페이스를 구현하면서 아무 동작도 변경하지 않는 데코레이터를 만들 때조차도 
다음과 같이 복잡한 코드를 작성해야 한다.

```kotlin
class DelegatingCollection<T> : Collection<T> {
    private val innerList = arrayListOf<T>()
    
    override int size: Int get() = innerList.size
    override fun isEmpty(): Boolean = innerList.isEmpty()
    override fun contains(element: T): Boolean = innerList.contains(element)
    override fun iterator(): Iterator<T> = innerList.iterator()
    override fun containsAll(elements: Collection<T>): Boolean =
        innerList.containsAll(elements)
}
```

이런 위임을 언어가 제공하는 일급 시민 기능으로 지원한다는 점이 코틀린의 장점이다. 
인터페이스를 구현할 때 `by` 키워드를 통해 그 인터페이스에 대한 구현을 다른 객체에 위임 중이라는 사실을 명시할 수 있다. 

```kotlin
class DelegatingCollection<T>(
    innerList: Collection<T> = ArrayList<T>()
) : Collection<T> by innerList {}
```

> (필자 첨언) 1급 객체의 특징
> - 함수의 실제 파라미터가 될 수 있다. 
> - 함수의 결과값으로 리턴될 수 있다. 
> - 변수 할당문의 대상이 될 수 있다. 
> - 등식(Equality)을 테스트할 수 있다.

클래스 안에 있던 모든 메서드 정의가 없어졌다. 
컴파일러가 그런 전달 메서드를 자동으로 생성하며 
자동 생성한 코드의 구현은 DelegatingCollection에 있던 구현과 비슷하다. 
그런 단순한 코드 중 관심을 가질 만한 부분은 거의 없기 때문에 
컴파일러가 자동으로 해줄 수 있는 작업을 굳이 직접 해야 할 이유가 없다.

메서드 중 일부의 동작을 변경하고 싶은 경우 메서드를 오버라이드하면 
컴파일러가 생성한 메서드 대신 오버라이드한 메서드가 쓰인다. 
기존 클래스의 메서드에 위임하는 기본 구현으로 충분한 메서드는 따로 오버라이드할 필요가 없다.

이 기법을 이용해서 원소를 추가하려고 시도한 횟수를 기록하는 컬렉션을 구현해보자. 
예를 들어 중복을 제거하는 프로세스를 설계하는 중이라면 
원소 추가 횟수를 기록하는 컬렉션을 통해 최종 컬렉션 크기와 원소 추가 시도 횟수 사이의 비율을 살펴봄으로써 
중복 제거 프로세스의 효율성을 판단할 수 있다.

```kotlin
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

>>> val cset = CountingSet<Int>()
>>> cset.addAll(listOf(1, 1, 2))
>>> println("${cset.objectAdded} objects were added, ${cset.size} remain")
3 objects were added, 2 remain // 중복이 제거 되었으므로 2개가 남음
```

예제를 보면 알 수 있지만 add와 addAll을 오버라이드해서 카운터를 증가시키고, 
MutableCollection 인터페이스의 나머지 메서드는 내부 컨테이너(innerSet)에게 위임한다.

이때 **CountingSet에 MutableCollection의 구현 방식에 대한 의존관계가 생기지 않는다는 점**이 중요하다. 
예를 들어 내부 컨테이너가 addAll을 처리할 때 루프를 돌면서 add를 호출할 수도 있지만, 
최적화를 위해 다른 방식을 택할 수도 있다. 

클라이언트 코드가 CountingSet의 코드를 호출할 때 발생하는 일은 CountingSet 안에서 마음대로 제어할 수 있지만, 
CountingSet 코드는 위임 대상 내부 클래스 MutableCollection이 문서화된 API를 변경하지 않는 한 
CountingSet 코드가 계속 잘 작동할 것임을 확신할 수 있다.

<br/>

## 4.4. Object 키워드 : 클래스 선언과 인스턴스 생성

object 키워드를 사용하는 여러 상황

- 객체 선언(object declaration)은 싱글턴을 정의하는 방법 중 하나다.
- 동반 객체(companion object)는 인스턴스 메서드는 아니지만 
  어떤 클래스와 관련 있는 메서드와 팩토리 메서드를 담을 때 쓰인다. 
  동반 객체 메서드에 접근할 때는 동반 객체가 포함된 클래스의 이름을 사용할 수 있다.
- 객체 식은 자바의 무명 내부 클래스(anonymous inner class) 대신 쓰인다.

<br/>

## 4.4.1. 객체 선언: 싱글턴을 쉽게 만들기
객체지향 시스템을 설계하다 보면 인스턴스가 하나만 필요한 클래스가 유용한 경우가 많다. 
자바에서는 보통 클래스의 생성자를 private으로 제한하고 정적인 필드에 
그 클래스의 유일한 객체를 저장하는 싱글턴 패턴(singleton pattern)을 통해 이를 구현한다.

코틀린은 객체 선언 기능을 통해 싱글턴을 언어에서 기본 지원한다. 
**객체 선언**은 클래스 선언과 그 클래스에 속한 단일 인스턴스의 선언을 합친 선언이다.

예를 들어 객체 선언을 사용해 회사 급여 대장을 만들 수 있다. 
한 회사에 여러 급여 대장이 필요하지는 않을 테니 싱글턴을 쓰는 게 정당해 보인다.

```kotlin
object Payroll {
    val allEmployees = arrayListOf<Person>()

    fun calculateSalary() {
        for (person in allEmployees) {
            ...
        }
    }
}
```

객체 선언은 `object` 키워드로 시작한다. 
객체 선언은 클래스를 정의하고 그 클래스의 인스턴스를 만들어서 변수에 저장하는 모든 작업을 단 한 문장으로 처리한다. 
클래스와 마찬가지로 객체 선언 안에도 프로퍼티, 메서드, 초기화 블록 등이 들어갈 수 있다.

하지만 생성자는(주 생성자와 부 생성자 모두) 객체 선언에 쓸 수 없다. 
일반 클래스 인스턴스와 달리 싱글턴 객체는 객체 선언문이 있는 위치에서 생성자 호출 없이 즉시 만들어진다. 
따라서 **객체 선언에는 생성자 정의가 필요 없다.**

변수와 마찬가지로 객체 선언에 상요한 이름 뒤에 마침표(.)를 붙이면 객체에 속한 메서드나 프로퍼티에 접근할 수 있다.

```kotlin
Payroll.allEmployees.add(Person(...))
Payroll.calculateSalary()
```

객체 선언도 클래스나 인터페이스를 상속할 수 있다. 프레임워크를 사용하기 위해 특정 인터페이스를 구현해야 하는데, 
그 구현 내부에 다른 상태가 필요하지 않은 경우에 이런 기능이 유용하다. 
예를 들어 java.util.Comparator 인터페이스를 살펴보자.
 
Comparator 구현은 두 객체를 인자로 받아 그중 어느 객체가 더 큰지 알려주는 정수를 반환한다. 
Comparator 안에는 데이터를 저장할 필요가 없다. 
따라서 어떤 클래스에 속한 객체를 비교할 때 사용하는 Comparator는 보통 클래스마다 단 하나씩만 있으면된다. 
따라서 Comparator 인스턴스를 만드는 방법으로는 객체 선언이 가장 좋은 방법이다.

구체적인 예제로 두 파일 경로를 대소문자 관계없이 비교해주는 Comparator를 구현해보자.
```kotlin
object CaseInsensitiveFileComparator : Comparator<File> {
    override fun compare(file1: File, file2: File): Int {
        return file1.path.compareTo(file2.path, ignoreCase = true)
    }
}

>>> println(CaseInsensitiveFileComparator.compare(
... File("/User"), File("/user")))
0
```

일반 객체(클래스 인스턴스)를 사용할 수 있는 곳에서는 항상 싱글턴 객체를 사용할 수 있다. 
예를 들어 이 객체를 Comparator를 인자로 받는 함수에게 인자로 넘길 수 있다.

이 예제는 전달받은 Comparator에 따라 리스트를 정렬하는 sortedWith 함수를 사용한다.

> ### ✅싱글턴과 의존관계 주입
> 싱글턴 패턴과 마찬가지 이유로 **대규모 소프트웨어 시스템에서는 객체 선언이 항상 적합하지는 않다.** 
의존관계가 별로 많지 않은 소규모 소프트웨어에서는 싱글턴이나 객체 선언이 유용하지만, 
시스템을 구현하는 다양한 구성 요소와 상호작용하는 대규모 컴포넌트에는 싱글턴이 적합하지 않다. 
**이유는 객체 생성을 제어할 방법이 없고 생성자 파라미터를 지정할 수 없어서다.**
> 
> 생성을 제어할 수 없고 생성자 파라미터를 지정할 수 없으므로 
> 단위 테스트를 하거나 소프트웨어 시스템의 설정이 달라질 때 객체를 대체하거나 객체의 의존관계를 바꿀 수 없다. 
> 따라서 그런 기능이 필요하다면 자바와 마찬가지로 의존관계 주입 프레임워크(예: 구글 주스(Guice))와 
> 코틀린 클래스를 함께 사용해야 한다.

클래스 안에서 객체를 선언할 수도 있다. 그런 객체도 인스턴스는 단 하나뿐이다
(바깥 클래스의 인스턴스마다 중첩 객체 선언에 해당하는 인스턴스가 하나씩 따로 생기는 것이 아니다). 
예를들어 어떤 클래스의 인스턴스를 비교하는 Comparator를 클래스 내부에 정의하는 게 더 바람직하다.

```kotlin
data class Person(val name: String) {
    object NameComparator : Comparator<Person> {
        override fun compare(p1: Person, p2: Person): Int = 
            p1.name.compareTo(p2.name)
    }
}

>>> val persons = listOf(Person("Bob"), Person("Alice"))
>>> println(persons.sortedWith(person.NameComparator))
[Person(name=Alice), Person(name=Bob)]
```

> ### ✅코틀린 객체를 자바에서 사용하기
> 코틀린 객체 선언은 유일한 인스턴스에 대한 정적인 필드가 있는 자바 클래스로 컴파일된다. 
> 이때 인스턴스 필드의 이름은 항상 INSTANCE다. 싱글턴 패턴을 자바에서 구현해도 비슷한 필드가 필요하다. 
> 자바 코드에서 싱글턴 객체를 사용하려면 정적인 INSTANCE 필드를 통하면 된다.
> ```kotlin
> /* 자바 */
> CaseInsensitiveFileComparator.INSTANCE.compare(file1, file2);
> ```

<br/>

## 4.4.2. 동반 객체: 팩토리 메소드와 정적 멤버가 들어갈 장소

코틀린 클래스 안에는 정적인 멤버가 없다. 코틀린 언어는 자바 static 키워드를 지원하지 않는다. 그 대신 코틀린에서는 패키지 수준의 최상위 함수(자바의 정적 메서드 역할을 거의 대신 할 수 있다)와 객체 선언(자바의 정적 메서드 역할 중 코틀린 최상위 함수가 대신할 수 없는 역할이나 정적 필드는 대신할 수 있다)을 활용한다. 대부분의 경우 최상위 함수는 private으로 표시된 클래스 비공개 멤버에 접근할 수 없다. 그래서 클래스의 인스턴스와 관계없이 호출해야 하지만, 클래스 내부 정보에 접근해야 하는 함수가 필요할 때는 클래스에 중첩된 객체 선언의 멤버 함수로 정의해야 한다. 그런 함수의 대표적인 예로 팩토리 메서드를 들 수 있다.

클래스 안에 정의된 객체 중 하나에 companion이라는 특별한 표시를 붙이면 그 클래스의 동반 객체로 만들 수 있다. 동반 객체의 프로퍼티나 메서드에 접근하려면 그 동반객체가 정의된 클래스 이름을 사용한다. 이때 객체의 이름을 따로 지정할 필요가 없다. 그 결과 동반 객체의 멤버를 사용하는 구문은 자바의 정적 메서드 호출이나 정적 필드 사용 구문과 같아진다.

private 생성자를 호출하기 좋은 위치를 알려준다고 했던 사실을 기억하는가? 바로 동반 객체가 private 생성자를 호출하기 좋은 위치다. 동반 객체는 자신을 둘러싼 클래스의 모든 private 멤버에 접근할 수 있다. 따라서 동반 객체는 바깥쪽 클래스의 private 생성자도 호출할 수 있다. 따라서 동반 객체는 팩토리 패턴을 구현하기 가장 적합한 위치다.

이제 예제로 부 생성자 2개 있는 클래스를 살펴보고, 다시 그 클래스를 동반 객체 안에서 팩토리 클래스를 정의하는 방식으로 변경해보자. 이 예제는 FacebookUser와 SubscribingUser 예제를 바탕으로 한다. 두 클래스 모두 User 클래스를 상속했다. 하지만 이제는 두 클래스를 한 클래스로 합치면서 사용자 객체를 생성하는 여러 방법을 제공하기로 결정했다.


이런 로직을 표현하는 더 유용한 방법으로 클래스의 인스턴스를 생성하는 팩토리 메서드가 있다. 아래의 리스트 4.26에 있는 구현에서는 생성자를 통해 User 인스턴스를 만들 수 없고 팩토리 메서드를 통해야만 한다.

클래스 이름을 사용해 그 클래스에 속한 동반 객체의 메서드를 호출할 수 있다.

팩토리 메서드는 매우 유용하다. 이 예제처럼 목적에 따라 팩토리 메서드 이름을 정할 수 있다. 게다가 팩토리 메서드는 그 팩토리 메서드가 선언된 클래스의 하위 클래스 객체를 반환할 수도 있다. 예를 들어 SubscribingUser와 FacebookUser 클래스가 따로 존재한다면 그때그때 필요에 따라 적당한 클래스의 객체를 반환할 수 있다. 또 팩토리 메서드는 생성할 필요가 없는 객체를 생성하지 않을 수도 있다. 예를 들어 이메일 주소별로 유일한 User 인스턴스를 만드는 경우 팩토리 메서드가 이미 존재하는 인스턴스에 해당하는 이메일 주소를 전달받으면 새 인스턴스를 만들지 않고 캐시에 있는 기존 인스턴스를 반환할 수 있다. 하지만 클래스를 확장해야만 하는 경우에는 동반 객체 멤버를 하위 클래스에서 오버라이드할 수 없으므로 여러 생성자를 사용하는 편이 더 나은 해법이다.


<br/>

## 4.4.3. 동반 객체를 일반 객체처럼 사용

<br/>

## 4.4.4. 객체 식: 무명 내부 클래스를 다른 방식으로 작성

<br/>


<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
