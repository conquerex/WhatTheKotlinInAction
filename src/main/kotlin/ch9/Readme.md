# 9장. 제네릭스 

## 입구
- 제네릭 함수와 클래스를 정의하는 방법
- 타입 소거와 실체화한 타입 파라미터(reified type parameter)
- 선언 지점과 사용 지점 변성(declaration-site variance)

<span style="color:orange">실체화된 타입 파라미터</span>를 사용하면 
인라인 함수 호출에서 타입 인자로 쓰인 구체적인 타입을 실행 시점에 알 수 있다.
(일반 클래스나 함수의 경우 타입 인자 정보가 실행 시점에 사라지기 때문에 이런 일이 불가능하다.)

<span style="color:orange">선언 지점 변성</span>을 사용하면 기저 타입은 같지만 
타입 인자가 다른 두 제네릭 타입 Type< A >와 Type< B >가 있을 때
타입 인자 A와 B의 상위/하위 타입 관계에 따라 
두 제네릭 타입의 상위/하위 타입 관계가 어떻게 되는지 지정할 수 있다.

- 예를 들어 List<Any>를 인자로 받는 함수가 있다고 했을 때 
  - List<Int>타입의 값을 전달할 수 있는지 여부를 선언 지점 변성을 통해 지정할 수 있다

<span style="color:orange">사용 지점 변성</span>은 
같은 목표(제네릭 타입 값 사이의 상위/하위 타입 관계 지정)를 
제네릭 타입 값을 사용하는 위치에서 파라미터 타입에 대한 제약을 표시하는 방식으로 달성한다.



<br/>
<br/>


## 9.1. 제네릭 타입 파라미터

제네릭스를 사용하면 타입 파라미터를 받는 타입을 정의할 수 있다. 
제네릭 타입의 인스턴스를 만들려면 타입 파라미터를 구체적인 타입 인자로 치환해야 한다.

- List 타입이 있다고 했을 때, 명확하게 문자열을 담는 리스트 = List<String>
- Map 타입은 제네릭 타입변수로 Map<K, V>형태로 선언이 되어 있고, 
  이를 인스턴스화 할때 Map<String, Person>처럼 구체적인 타입 인자를 넘겨 인스턴스화 할수 있다.


코틀린 컴파일러는 보통 타입과 마찬가지로 타입 인자도 추론할 수 있다.

```kotlin
val authors = listOf("Dmitry", "Svetlana")
```

listOf에 전달된 두 값이 문자열이기 때문에 컴파일러는 이 리스트가 List<String>이라 추론한다.
반면, 빈 리스트를 만들 때는 타입 인자를 추론할 근거가 없어 직접 타입을 명시해야 한다.

```kotlin
// 두 선언은 동등
val readers: MutableList<String> = mutableListOf()
val readers = mutableListOf<String>()
```

> 자바와 달리 코틀린에서는 제네릭 타입의 타입 인자를 프로그래머가 명시하거나 컴파일러가 추론할 수 있어야 한다. 
> 자바는 맨 처음에 제네릭 지원이 없었고 자바 1.5에 뒤늦게 제네릭을 도입했기 때문에 
> 이전 버전과 호환성을 유지하기 위해 타입 인자가 없는 제네릭 타입(raw 타입)을 허용한다.
> 
> 예를 들어 자바에서는 리스트 원소 타입을 지정하지 않고 List 타입의 변수를 선언할 수도 있다. 
> 코틀린은 처음부터 제네릭을 도입했기 때문에 raw 타입을 지원하지 않고 
> 제네릭 타입의 타입 인자를 (프로그래머가 직접 정의하든 타입 추론에 의해 자동으로 정의되든) 항상 정의해야 한다.

<br/>


## 9.1.1. 제네릭 함수와 프로퍼티

리스트를 다루는 함수를 작성한다면 어떤 특정 타입을 저장하는 리스트뿐 아니라 
모든 리스트를 다룰 수 있는 함수를 원할 것이다. 이럴 때 제네릭 함수를 작성해야 한다.
제네릭 함수를 호출할 때 반드시 구체적 타입으로 타입 인자를 넘겨야 한다. 

컬렉션을 다루는 라이브러리 함수는 대부분 제네릭 함수다.

```
     👇 타입 파라미터 선언
    ----
fun <T> List<T>.slice(indices: IntRange): List<T>
            ----                              ----
            👆타입 파라미터가 수신 객체와 반환 타입에 쓰인다👆
```

함수의 타입 파라미터 T가 수신 객체와 반환 타입에 쓰인다. 
수신 객체와 반환 타입 모두 List<T>다. 
이런 함수를 구체적인 리스트에 대해 호출할 때 타입 인자를 명시적으로 지정할 수 있다. 
하지만 실제로는 대부분 컴파일러가 타입 인자를 추론할 수 있으므로 그럴 필요가 없다.

```kotlin
>>> val letters = ('a'..'z').toList()
>>> println(letters.slice<Char>(0..2)) // 타입 인자를 명시적으로 지정
[a, b, c]
>>> println(letters.slice(10..13)) // 컴파일러는 여기서 T가 Char라는 사실을 추론한다
[k, l, m, n]
```

두 호출의 결과 타입은 모두 List<Char>이다.
컴파일러는 반환 타입 List<T>의 T를 자신이 추론한 Char로 치환한다.

filter 함수의 정의에서 (T) -> Boolean 타입의 함수를 파라미터로 받는다.
이 함수를 앞 예제에서 본 변수에 적용하는 부분을 살펴보자.

```kotlin
val authors = listOf("Dmitry", "Svetlana")
val readers = mutableListOf<String>(/* ... */)
fun <T> List<T>.filter(predicate: (T) -> Boolean): List<T>
>>> readers.filter{ it !in authors }
```

람다 파라미터에 대해 자동으로 만들어진 변수 it의 타입은 T라는 제네릭 타입이다.
컴파일러는 filter가 List<T>타입의 리스트에 대해 호출될 수 있다는 사실과 
filter의 수신 객체인 reader의 타입이 List<String>이라는 것을 알고, 
이를 토대로 T가 String이라고 추론한다.

클래스나 인터페이스 안에 정의된 메소드, 확장 함수 또는 최상위 함수에서 타입 파라미터를 선언할 수 있고,
확장 함수에선 수신 객체나 파라미터 타입에 따라 파라미터를 사용할 수 있다.

제네릭 함수를 정의할 때와 같이 제네릭 확장 프로퍼티를 선언할 수 있다.

```kotlin
val <T> List<T>.penultimate: T // 모든 리스트 타입에 이 제네릭 확장 프로퍼티를 사용할 수 있다
    get() = this[size - 2]
>>> println(listOf(1, 2, 3, 4).penultimate) // 이 호출에서 타입 파라미터 T는 Int로 추론된다
3
```

> ### ✅확장 프로퍼티만 제네릭하게 만들 수 있다. 
> 일반(확장이 아닌) 프로퍼티는 타입 파라미터를 가질 수 없다. 
> 클래스 프로퍼티에 여러 타입의 값을 저장할 수는 없으므로 
> 제네릭한 일반 프로퍼티는 말이 되지 않는다. 
> 일반 프로퍼티를 제네릭하게 정의하면 컴파일러가 다음과 같은 오류를 표시한다.
> 
> ```kotlin
> val <T> x: T = TODO() 
> Error: type parameter of a property must be used in its receiver type
> ```


<br/>


## 9.1.2. 제네릭 클래스 선언

자바와 마찬가지로 코틀린에서도 타입 파라미터를 넣은 꺽쇠 기호 (< >)를 
클래스(인터페이스) 이름 뒤에 붙이면 클래스(인터페이스)를 제네릭하게 만들 수 있다. 
타입 파라미터를 이름 뒤에 붙이고 나면 
클래스 본문 안에서 타입 파라미터를 다른 일반타입처럼 사용할 수 있다.

```kotlin
interface List<T> { // List 인터페이스에 T라는 타입 파라미터를 정의한다.
    operator fun get(index: Int): T // 인터페이스 안에서 T를 일반 타입처럼 사용할 수 있다.
}
```

제네릭 클래스를 확장하는 클래스 또는 제네릭 인터페이스를 구현하는 클래스를 정의하려면 
기반 타입의 제네릭 파라미터에 대해 타입 인자를 지정해야 한다.
이때 구체적인 타입을 넘길 수도 있고 
(하위 클래스도 제네릭 클래스라면) 타입 파라미터로 받은 타입을 넘길 수도 있다.

```kotlin
// 이 클래스는 구체적인 타입 인자로 String을 지정해 List를 구현한다
class StringList: List<String> {
    override fun get(index: Int): String = ... // String을 어떻게 사용하는지 살펴보라
}

// ArrayList의 제네릭 타입 파라미터 T를 List의 타입 인자로 넘긴다
class ArrayList<T>: List<T> {
    override fun get(index: Int): T = ...
}
```

StringList 클래스는 String 타입의 원소만을 포함. 따라서 String을 기반 타입의 타입 인자로 지정.
하위 클래스에서 상위 클래스에 정의된 함수를 오버라이드하거나 사용하려면 
타입 인자 T를 구체적 타입 String으로 치환해야 한다.
따라서 StringList에서 `fun get(Int): T`가 아니라 
`fun get(Int): String`이라는 시그니처를 사용한다.

ArrayList 클래스는 자신만의 타입 파라미터 T를 정의하면서 그 T를 기반 클래스의 타입 인자로 사용한다.
ArrayList<T>의 T와 앞에서 본 List<T>의 T는 같지 않고, 전혀 다른 타입 파라미터이다.
실제로는 T가 아닌 다른 이름을 사용해도 의미에는 아무 차이가 없다.

클래스는 자기 자신을 타입 인자로 참조할 수 있다.
Comparable 인터페이스를 구현하는 클래스가 예이다.

```kotlin
interface Comparable<T> {
    fun compareTo(other: T): Int
}

class String: Comparable<String> {
    override fun compareTo(other: String): Int = /* ... */
}
```

String 클래스는 제네릭 Comparable 인터페이스를 구현하면서 
그 인터페이스의 타입 파라미터 T로 String 자신을 지정한다.


<br/>


## 9.1.3. 타입 파라미터 제약

<span style="color:orange">타입 파라미터 제약</span>은 
클래스나 함수에 사용할 수 있는 타입 인자를 제한하는 기능이다.
어떤 타입을 제네릭 타입의 타입 파라미터에 대한 상한(upper bound)으로 지정하면 
그 제네릭 타입을 인스턴스화할 때 사용하는 타입 인자는 반드시 그 상한 타입이거나 
그 상한 타입의 하위 타입이어야 한다.
제약을 가하려면 타입 파라미터 이름 뒤에 콜론(:)을 표시하고 그 뒤에 상한 타입을 적으면 된다.

```
     👇 타입 파라미터
     --
fun <T : Number> List<T>.sum() : T
        -------
          👆 상한
```

아래는 실제 타입 인자(Int)가 Number를 확장하므로 합법적인 예시

```kotlin
>>> println(listOf(1, 2, 3).sum())
6
```

타입 파라미터 T에 대한 상한을 정하고나면 T 타입의 값을 그 상한 타입의 값으로 취급할 수 있다.

```kotlin
// Number를 타입 파라미터 상한으로 정한다
fun <T: Number> oneHalf(value: T): Double {
    return value.toDouble() / 2.0   // Number 클래스에 정의된 메소드를 호출할 수 있다.
}
>>> println(oneHalf(3))
1.5
```

두 파라미터 사이에서 더 큰 값을 찾는 제네릭 함수. 
비교할 수 있어야 최댓값을 찾을 수 있으므로 
함수 시그니처에도 두 인자를 서로 비교할 수있어야 한다는 사실을 지정해야 한다.

```kotlin
// 이 함수들의 인자들은 비교 가능해야 한다.
fun <T: Comparable<T>> max(first: T, second: T): T {
    return if (first > second) first else second
}
>>> println(max("kotlin", "java")) // 문자열은 알파벳순으로 비교된다
kotlin
```

max를 비교할 수 없는 값 사이에 호출하면 컴파일 오류가 난다.

```kotlin
>>> println(max("kotlin", 42))
Error: Type parameter bound for T is not satisfied
inferred type Any is not a subtype of Comparable<Any>
```

T의 상한 타입은 Comparable<T>다.
String이 Comparable<String>을 확장하므로 String은 max 함수에 적합한 타입 인자다.

`first > second`라는 식은 코틀린 연산자 관례에 따라 
`first.compareTo(second) > 0`이라고 컴파일 된다.
max 함수에서 first의 타입 T는 Comparable<T>를 확장하므로 
first를 다른 T 타입 값인 second와 비교할 수 있다.

드물지만 타입 파라미터에 대해 둘 이상의 제약을 가해야 하는 경우도 있다.

- CharSequence의 맨 끝에 마침표(.)가 있는지 검사하는 제네릭 함수
  - 표준 StringBuilder나 java.nio.CharBuffer 클래스 등에 대해 이 함수를 사용할 수 있다.


```kotlin
fun <T> ensureTrailingPeriod(seq: T) 
    where T: CharSequence, T: Appendable { // 타입 파라미터 제약 목록
    if (!seq.endsWith('.')) { // CharSequence 인터페이스의 확장함수를 호출
        seq.append('.') // Appendable 인터페이스의 메소드를 호출
    }
}

>>> val helloWorld = StringBuilder("Hello World")
>>> ensureTrailingPeriod(helloWorld)
>>> println(helloWorld)
Hello World.
```

예제는 타입 인자가 CharSequence와 Appendable 인터페이스를 반드시 구현해야 한다는 사실을 표현한다.
이는 데이터에 접근하는 연산(endsWith)과 
테이터를 변환하는 연산(append)을 T 타입의 값에게 수행할 수 있다는 뜻이다.


<br/>


## 9.1.4. 타입 파라미터를 널이 될 수 없는 타입으로 한정



<br/>
<br/>



## 9.2.


<br/>



## 9.2.


<br/>



## 9.2.


<br/>



## 9.2.


<br/>



## 9.2.


<br/>



## 9.2.


<br/>



> ### ✅


<span style="color:orange"></span>
<span style="color:orange">xxxx</span>


```mermaid
graph TD
      A-->B
      A-->C
      B-->D
      C-->D
```

<br/>
<br/>
<br/>
<br/>