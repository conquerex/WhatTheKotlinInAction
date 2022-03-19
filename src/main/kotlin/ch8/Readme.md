# 8장. 고차함수: 파라미터와 반환 값으로 람다 사용

## 입구
- 함수 타입
- 고차 함수와 코드를 구조화할 때 고차 함수를 사용하는 방법
- 인라인 함수
- 비로컬 return과 레이블
- 무명 함수

람다를 인자로 받거나 반환하는 함수인
<span style="color:orange">고차 함수(high order function)</span>를 만드는 방법을 다루어 보자.

고차 함수로 코드를 더 간결하게 다듬고 코드 중복을 없애고 더 나은 추상화를 구축하는 방법을 살펴본다.

또한 람다를 사용함에 따라 발생할 수 있는 성능상 부가 비용을 없애고 
람다 안에서 더 유연하게 흐름을 제어할 수 있는 코틀린 특성인
<span style="color:orange">인라인(inline) 함수</span>에 대해 배워보자.

<br/>
<br/>


## 8.1. 고차 함수 정의

<span style="color:orange">고차 함수</span>는 다른 함수를 인자로 받거나 함수를 반환하는 함수다. 

코틀린에서는 람다나 함수 참조를 사용해 함수를 값으로 표현할 수 있다. 
따라서, 고차 함수는 <span style="color:orange">람다나 함수 참조를 인자로 넘길 수 있거나 
람다나 함수 참조를 반환하는 함수</span>다.

아래 예시의 표준 라이브러리 함수 filter는 술어 함수를 인자로 받으므로 고차 함수다.

```kotlin
list.filter { x > 0 }
```



<br/>


## 8.1.1. 함수 타입

```kotlin
// 타입 추론
val sum = {x: Int, y: Int -> x + y}
val action = { println(42) }
```

이 경우 컴파일러는 sum과 action이 함수 타입임을 추론한다. 
이제는 각 변수에 구체적인 타입 선언을 추가하면 어떻게 되는지 살펴보자.

```kotlin
// 함수 타입 명시
// Int 파라미터 두개를 받아서 Int 값 반환하는 함수
val sum: (Int, Int) -> Int = {x, y -> x + y}
// 아무 인자도 받지 않고 리턴값이 없는(Unit) 함수
val action: () -> Unit = { println(42) }
```

![코틀린 함수 타입 문법](/Users/barley.son/dev/WhatTheKotlinInAction/src/main/kotlin/ch8/img8-1.png)

- `->` 좌측: 함수의 파라미터을 괄호 안에 명시
- `->` 우측: 함수의 반환 타입을 명시

함수 타입을 정의하려면 함수 파라미터의 타입을 괄호 안에 넣고, 그 뒤에 화살표(->)를 추가한 다음, 
함수의 반환 타입을 지정하면 된다. 
그냥 함수를 정의한다면 함수의 파라미터 목록 뒤에 오는 Unit 반환 타입 지정을 생략해도 되지만, 
**함수 타입을 선언할 때는 반환 타입을 반드시 명시해야 하므로 Unit을 빼먹어서는 안 된다.**

이렇게 변수 타입을 함수 타입으로 지정하면 
함수 타입에 있는 파라미터로부터 람다의 파라미터 타입을 유추할 수 있다. 
따라서 람다 식 안에서 굳이 파라미터 타입을 적을 필요가 없다.

다른 함수와 마찬가지로 함수 타입에서도 반환 타입을 널이 될 수 있는 타입으로 지정할 수 있다.

```kotlin
// 반환값이 널이 될 수 있는 경우
var canReturnNull: (Int, Int) -> Int? = {x, y => null}
// 함수 자체가 널이 될 수 있는 경우
var funOrNull: ((Int, Int) -> Int)? = null
```

canReturnNull의 타입과 funOrNull의 타입 사이에는 큰 차이가 있다. 
funOrNull의 타입을 지정하면서 괄호를 빼먹으면 널이 될 수 있는 타입이 아니라 
널이 될 수 있는 반환 타입을 갖는 함수 타입을 선언하게 된다.


> ### ✅ 파라미터 이름과 함수 타입
> ```kotlin
> fun performRequest(
>     url: String,
>     callback: (code: Int, conetnt: String) -> Unit // 함수 타입의 각 파라미터에 이름을 붙인다
> ) {
> /* ... */
> }
>
> val url = "http://kotl.in"
> performRequest(url) { code, content -> /* ... */ } // API에서 제공하는 이름을 람다에서 사용
> performRequest(url) { code, page -> /* ... */ } // 원하는 이름으로 붙여서 사용
> ```
> 파라미터 이름은 타입 검사 시 무시된다. 
> 이 함수 타입의 람다를 정의할 때 파라미터 이름이 꼭 함수 타입 선언의 파라미터 이름과 일치하지 않아도 되지만, 
> 함수 타입에 인자 이름을 추가하면 코드 가독성이 좋아지고, IDE는 그 이름을 코드 완성에 사용할 수 있다.



<br/>


## 8.1.2. 인자로 받은 함수 호출

```kotlin
// 함수 타입인 파라미터를 선언한다
fun twoAndThree(operation: (Int, Int)-> Int){
    // 함수타입인 파라미터를 호출한다
    val result = oepration(2, 3)
    println("The result is $result")
}

>>> twoAndThree(a, b -> a + b)
The result is 5
>>> twoAndThree(a, b -> a * b)
The result is 6
```

인자로 받은 함수를 호출하는 구문은 일반 함수를 호출하는 구문과 같다.
예제를 보기 위해 filter 함수를 구현해보자.
예제를 단순히 하기 위해 String에 대한 filter를 구현한다.
하지만 제네릭을 사용해 모든 타입의 원소를 지원하게 구현해도 많이 달라지지는 안는다.

![술어 함수를 파라미터로 받는 filter 함수 정의](/Users/barley.son/dev/WhatTheKotlinInAction/src/main/kotlin/ch8/img8-2.png)

`filter` 함수는 술어를 파라미터로 받는다. 
`predicate` 파라미터는 문자(Char)를 파라미터로 받고 Boolean 결과 값을 반환한다. 
술어는 인자로 받은 문자가 filter 함수가 돌려주는 결과 문자열에 남아있기를 바라면 true, 아니면 false를 반환한다. 
아래는 filter 함수를 구현한 방법이다.

```kotlin
fun String.filter(predicate: (Char) -> Boolean): String{
    val sb = StringBuilder()
    for(index in 0 until length){
        val element = get(index)
        // predicate 파라미터로 전달받은 함수를 호출
        if(predicate(element)) sb.append(element)
    }
    return sb.toString()
}

// 람다를 predicate 파라미터로 전달한다
>>> println("ab1c".filter {it in 'a'..'z'})
abc
```


<br/>


## 8.1.3. 자바에서 코틀린 함수 타입 사용

컴파일된 코드 안에서 함수 타입은 일반 인터페이스로 바뀐다. 
즉 함수 타입의 변수는 `FunctionN` 인터페이스를 구현하는 객체를 저장한다. 

코틀린 표준 라이브러리는 함수 인자의 개수에 따라 Function0<R> (인자가 없는 함수), 
Function1<P1, R> (인자가 1개인 함수) 등의 인터페이스를 제공한다.

각 인터페이스에는 <span style="color:orange">invoke 메소드</span> 정의가 하나 들어있다. 
`invoke`를 호출하면 함수를 실행할 수 있다. 
함수 타입인 변수는 인자 개수에 따라 적당한 FunctionN 인터페이스를 구현하는 클래스의 인스턴스를 저장하며, 
그 클래스의 invoke 메소드 본문에는 람다의 본문이 들어간다.

```kotlin
fun processTheAnswer(f: (Int) -> Int){
    println(f(42))
}
/* 자바 */
>>> processTheAnswer(number -> number+1)
43
```

자바 8 이전의 자바에서는 필요한 FunctionN 인터페이스의 invoke 메소드를 구현하는 무명 클래스를 넘기면 된다.

```
>>> processTheAnswer {
       // 자바 코드에서 코틀린 함수 타입을 사용한다 (자바 8 이전)
...    new Function1<Integer, Integer>() {
...        @override
...        public Integer invoke(Integer number) {
...            System.out.println(number);
...            return number + 1;
...        }
...});
43
```

자바에서 코틀린 표준 라이브러리가 제공하는 람다를 인자로 받는 확장 함수를 쉽게 호출할 수 있다. 
하지만 수신 객체를 확장 함수의 첫 번재 인자로 명시적으로 넘겨야 하므로 
코틀린에서 확장 함수를 호출할 때처럼 코드가 깔끔하지는 않다.

```
>>> List<String< strings = new ArrayList();
>>> strings.add("42")
    // 코틀린 표준 라이브러리에서 가져온 함수를 자바 코드에서 호출할 수 있다
>>> CollectionsKt.forEach(strings, s -> { // strings는 확장 함수의 수신 객체
...    System.out.println(s);
       // Unit 타입의 값을 명시적으로 반환해야만 한다.
...    return Unit.INSTANCE;
...});
```

코틀린 Unit 타입에는 값이 존재하므로 자바에서는 그 값을 명시적으로 반환해줘야 한다. 
`(String) -> Unit`처럼 반환 타입이 Unit인 함수 타입의 파라미터 위치에 
void를 반환하는 자바 람다를 넘길 수는 없다.


<br/>


## 8.1.4. 디폴트 값을 지정한 함수 타입 파라미터나 널이 될 수 있는 함수 타입 파라미터

파라미터를 함수 타입으로 선언할 때도 디폴트 값을 정할 수 있다.
3장에서 살펴본 joinToString 함수를 함수 타입의 파라미터에 대한 디폴트 값을 지정하여 가공해보자.

```kotlin
fun<T> Collection<T>.joinToString(
    separator: String= ",",
    prefix: String= "",
    postfix: String= ""
): String {
    val result = StringBuilder(prefix)
    for((index, element) in this.withIndex()){
        if(index > 0) result.append(separator)
        result.append(element)
    }
    result.append(postfix)
    // 기본 toString 메소드를 사용해 객체를 문자열로 변환한다
    return result.toString()
}
```

이 구현은 유연하지만 핵심 요소를 제어할 수 없다는 단점있다. 
그 핵심 요소는 컬렉션의 각 원소를 문자열로 변환하는 방법이다. 
코드는 `StringBuilder.append(o: Any?)`를 사용하는데, 
이 함수는 항상 객체를 toString 메소드를 통해 문자열로 바꾼다. 
toString으로 충분한 경우도 많지만 그렇지 않을 때도 있다. 

이럴 때 함수 타입의 파라미터에 대한 디폴트 값을 지정하면 이런 문제를 해결할 수 있다. 
**디폴트 값으로 람다 식을 넣으면 된다.**

```kotlin
fun <T> Collection<T>.joinToString(
        separator: String = ", ",
        prefix: String = "",
        postfix: String = "",
        // 함수 타입 파라미터를 선언하면서 람다를 디폴트 값으로 지정한다.
        transform: (T) -> String = { it.toString() }  
): String {
    val result = StringBuilder(prefix)

    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(transform(element)) // "transform" 파라미터로 받은 함수를 호출한다. 
    }

    result.append(postfix)
    return result.toString()
}

// ---- Result ----
val letters = listOf("Alpha", "Beta")

// 디폴트 변환 함수를 사용한다.
println(letters.joinToString())
// 결과: Alpha, Beta

// 람다를 인자로 전달한다
println(letters.joinToString { it.toLowerCase() })
// 결과: alpha, beta

// 이름 붙은 인자 구문을 사용해 람다를 포함하는 여러 인자를 전달한다
println(letters.joinToString(separator = "! ", postfix = "! ", transform = { it.toUpperCase() }))
//결과: ALPHA! BETA!

```

이 함수는 제네릭 함수다. 따라서 컬렉션의 원소 타입을 표현하는 `T`를 타입 파라미터로 받는다. 
transform 람다는 그 T 타입의 값을 인자로 받는다.

다른 디폴트 파라미터 값과 마찬가지로 함수 타입에 대한 디폴트 값 선언도 `=` 뒤에 람다를 넣으면 된다.
- 람다를 아예 생략하거나
  - 람다를 생략하면 디폴트 람다에 있는 대로 toString을 써서 원소를 변환
- 인자 목록 뒤에 람다를 넣거나
  - 여기서는 람다 밖에 전달할 인자가 없어서 () 없이 람다만 썼다
- 이름 붙인 인자로 람다를 전달할 수 있다

다른 방법으로 널이 될 수 있는 함수 타입을 사용할 수 있다.
**널이 될 수 있는 함수 타입을 사용하는 경우 그 함수를 직접 호출할 수 없다.** 
NPE 발생 가능성이 있으므로 컴파일을 거부하게 된다. null 여부를 명시적으로 검사하는 것도 한 가지 해결 방법.

```kotlin
fun foo(callback: (() -> Unit)?) {
    // ...
    if(callback != null) {
        callback()
    }
}
```

전에 말했듯이 함수 타입은 `invoke()` 메소드를 구현하는 인터페이스다.
이 사실을 기억하면 일반 메소드처럼 `invoke()`도 
안전한 호출 구문(`callback?.invoke()`)으로 호출할 수 있다는 것을 알 수 있다.

```kotlin
fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = "",
    // 널이 될 수 있는 함수 타입의 파라미터를 선언
    transform: ((T) -> String)? = null
): String {
    val result = StringBuilder(prefix)
    for((index, element) in this.withIndex()) {
        if(index > 0) result.append(separator)
        // 안전 호출을 사용해 함수를 호출
        val str = transform?.invoke(element)
            ?: element.toString() // 엘비스 연산자를 사용해 람다를 인자로 받지 않은 경우 처리
        result.append(str)
    }

    result.append(postfix)
    return result.toString()
}
```

<br/>

## 8.1.5. 함수를 함수에서 반환

함수가 함수를 반환할 필요가 있는 경우보다는 함수가 함수를 인자로 받아야 할 필요가 있는 경우가 훨씬 더 많다.
하지만 함수를 반환하는 함수도 여전히 유용하다. 프로그램의 상태나 다른 조건에 따라 달라질 수 있는 로직이 있다고 생각해보자.
- 예) 사용자가 선택한배송수단에 따라 배송비를 계산하는 방법이 달라질 수 있다
- 이럴 때 적절한 로직을 선택해서 함수로 반환하는 함수를 정의해 사용할 수 있다.

```kotlin
enum class Delivery { STANDARD, EXPEDITED }

class Order(val itemCount: Int)

fun getShippingCostCaculator(
        delivery: Delivery
) : (Order) -> Double { // 함수를 반환하는 함수를 선언

    if (delivery == Delivery.EXPEDITED) {
        // 함수에서 람다를 반환
        return { order -> 6 + 2.1 * order.itemCount }
    }
    // (이것도) 함수에서 람다를 반환
    return { order -> 1.2 * order.itemCount }
}

>>> val calculator = getShippingCostCaculator(Delivery.EXPEDITED)
// 반환받은 함수를 호출
>>> println("Shipping costs ${calculator(Order(3))}")
Shipping costs 12.3
```

다른 함수를 반환하는 함수를 정의하려면 함수의 반환 타입으로 함수 타입을 지정해야 한다.
위의 코드에서 `getShippingCostCalculator` 함수는 order 을 받아서 double 을 반환하는 함수를 반환한다.
함수를 반환하려면 return 식에 람다나 멤버 참조, 함수 타입의 값을 계산하는 식 등을 넣으면 된다.

GUI 연락처 관리 앱을 만드는 데 UI의 상태에 따라 어떤 연락처 정보를 표시할지 결정해야 할 필요가 있다고 가정하자.

```kotlin
class ContactListFilters {
    var prefix: String = ""
    var onlyWithPhoneNumber: Boolean = false
}
```

이름이나 성이 D로 시작하는 연락처를 보기 위해 사용자가 D를 입력하면 prefix 값이 변한다.
연락처 목록 표시 로직과 연락처 필터링 UI를 분리하기 위해 연락처 목록을 필터링하는 술어 함수를 만드는 함수를 정의할 수 있다.

```kotlin
data class Person(
        val firstName: String,
        val lastName: String,
        val phoneNumber: String?
)

class ContactListFilters {
    var prefix: String = ""
    var onlyWithPhoneNumber: Boolean = false

    fun getPredicate(): (Person) -> Boolean { // 함수를 반환하는 함수를 정의한다. 
        val startsWithPrefix = { p: Person ->
            p.firstName.startsWith(prefix) || p.lastName.startsWith(prefix)
        }
        if (!onlyWithPhoneNumber) {
            return startsWithPrefix // 함수 타입의 변수를 반환한다. 
        }
        return { startsWithPrefix(it)
                    && it.phoneNumber != null } // 람다를 반환한다. 
    }
}


// --- Rusult ---
val contacts = listOf(Person("Dmitry", "Jemerov", "123-4567"),
  Person("Svetlana", "Isakova", null))
val contactListFilters = ContactListFilters()
with (contactListFilters) {
  prefix = "Dm"
  onlyWithPhoneNumber = true
}
println(contacts.filter(
  contactListFilters.getPredicate()))
// 결과: [Person(firstName=Dmitry, lastName=Jemerov, phoneNumber=123-4567)]
```

<br/>

## 8.1.6. 람다를 활용한 중복 제거


함수 타입과 람다 식은 재활용하기 좋은 코드를 만들 때 쓸 수 있는 훌륭한 도구다.
람다를 사용할 수 없는 환경에선 아주 복잡한 구조를 만들어야만 피할 수 있는 코드 중복도 람다를 활용한다면 
간결하고 쉽게 중복을 제거할 수 있다.
웹사이트 방문 기록을 분석하는 예를 보도록 하자.

```kotlin
data class SiteVisit(
    val path: String,
    val duration: Double,
    val os: OS
)

enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }

val log = listOf(
    SiteVisit("/", 34.0, OS.WINDOWS),
    SiteVisit("/", 22.0, OS.MAC),
    SiteVisit("/login", 12.0, OS.WINDOWS),
    SiteVisit("/signup", 8.0, OS.IOS),
    SiteVisit("/", 16.3, OS.ANDROID)
)


```

윈도우 사용자의 평균 방문 시간을 출력하고 싶다 하면 다음과 같다.

```kotlin
val averageWindowsDuration = log
    .filter { it.os == OS.WINDOWS }
    .map(SiteVisit::duration)
    .average()

println(averageWindowsDuration)
// 결과: 23.0
```

이제 맥 사용자의 평균 방문 시간을 출력할 것인데, 중복을 피하기 위해 OS를 파라미터로 뽑아낸다.

```kotlin
fun List<SiteVisit>.averageDurationFor(os: OS) =
        filter { it.os == os}.map(SiteVisit::duration).average()
        
>>> println(log.averageDurationFor(OS.WINDOWS))
23.0
>>> println(log.averageDurationFor(OS.MAC))
22.0
```

함수를 확장으로 정의함으로써 가독성이 좋아진 것을 볼 수 있다.
이 함수가 어떤 함수 내부에서만 쓰인다면 로컬 확장 함수로 정의할 수 있다.
모바일 디바이스 사용자(IOS, ANDROID)의 평균 방문 시간을 구하고 싶다면 다음과 같이 해야 한다.

```kotlin
val averageMobileDuration = log
    .filter { it.os in setOf(OS.IOS, OS.ANDROID) }
    .map(SiteVisit::duration)
    .average()
    
>>> println(averageMobileDuration)
12.15
```

플랫폼을 표현하는 간단한 파라미터로는 이런 상황을 처리할 수 없어 하드 코딩한 필터를 사용해야 한다.
"IOS 사용자의 /signup 페이지 평균 방문 시간?"과 같은 더 복잡한 질의를 사용해야 한다면 람다를 사용하면 된다.
함수 타입을 사용하면 필요한 조건을 파라미터로 뽑아낼 수 있다.

```kotlin
fun List<SiteVisit>.averageDurationFor(predicate: <SiteVisit) -> Boolean) =
        filter(predicate).map(SiteVisit::duration).average()
        
>>> println(log.averageDurationFor {
...    it.os in setOf(OS.ANDROID, OS.IOS) })
// 12.15

>>> println(log.averageDurationFor {
...    it.os == OS.IOS && it.path == "/signup"})
// 6.0
```

코드의 일부분을 복사해 붙여 넣고 싶은 경우가 있다면 그 코드를 람다로 만들면 중복을 제거할 수 있다.
변수, 프로퍼티, 파라미터 등을 사용해 데이터의 중복을 없앨 수 있는 것과 같이 람다를 사용해서 코드의 중복을 제거할 수 있다.

> 일부 잘 알려진 (객체 지향) 디자인 패턴틀 함수 타입과 람다식을 사용해 단순화할 수 있다.
> <span style="color:orange">전략 패턴</span>을 생각해보자.
> 람다 식이 없다면 인터페이스를 선언하고 그 인터페이스의 구현 클래스를 통해 전략을 정의해야 한다. 
> 함수 타입을 언어가 지원하면 일반 함수 타입을 사용해 전략을 표현할 수 있고
> 경우에 따라 다른 람다 식을 넘김으로써 여러 전략을 전달할 수 있다.

고차 함수를 여기저기 활용하면 전통적인 루프와 조건문을 사용할 때보다 더 느려지지 않을까?
다음 절에서는 람다를 활용한다고 코드가 항상 더 느려지지는 않는다는 사실을 설명하고
inline 키워드를 통해 어떻게 람다의 성능을 개선하는지 보여준다.

<br/>
<br/>
<br/>


## 8.2. 인라인 함수: 람다의 부가 비용 없애기


<br/>


## 8.2.1. 인라이닝이 작동하는 방식


<br/>

## 8.2.


<br/>

## 8.2.


<br/>

## 8.2.


<br/>

## 8.2.


<br/>



> ### ✅


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