# 3장. 함수 정의와 호출

## 입구

* 컬렉션, 문자열, 정규식을 다루기 위한 함수
* 이름 붙인 인자, 디폴트 파라미터 값, 중위 호출 문법
* 확장 함수와 확장 프로퍼티
* 최상위 및 로컬 함수와 프로퍼티를 사용해 코드 구조화


<br/>

## 3.1. 코틀린에서 컬렉션 만들기


```kotlin
fun main() {
  // 집합 (Set)
  val numberSet = hashSetOf(1, 2, 3)

  // 리스트 (List)
  val numberArrayList = arrayListOf(4, 5, 6, 3)

  // 맵 (Map)
  // to가 언어가 제공하는 특별한 키워드가 아니라 일반 함수 - 나중에 더 자세히 다룸
  val numberMap = hashMapOf(
    1 to "one",
    2 to "two",
    3 to "three"
  )

  // [1, 2, 3]
  println(numberSet)

  // [4, 5, 6]
  println(numberArrayList)

  // {1=one, 2=two, 3=three}
  println(numberMap)
}
```

여기서 만든 객체가 어떤 클래스에 속하는지?

```kotlin
println(numberSet.javaClass) // javaClass는 자바 getClass에 해당하는 코틀린 코드
println(numberArrayList.javaClass)
println(numberMap.javaClass)
```

`javaClass` : 호출한 객체의 Class 타입을 반환해주는 제네릭 확장 함수


```kotlin
/**
  * Returns the runtime Java class of this object.
  */
public inline val <T : Any> T.javaClass: Class<T>
    @Suppress("UsePropertyAccessSyntax")
    get() = (this as java.lang.Object).getClass() as Class<T>
```

코틀린이 자신만의 컬렉션 기능을 제공하지 않는다는 뜻. 기존 자바 컬렉션을 활용할 수 있다는 뜻.
코틀린은 표준 자바 컬렉션을 활용함으로써, **자바 코드와 상호작용하기 쉽도록** 만들어져 있다.
자바와 코틀린 컬렉션을 서로 변환할 필요가 없다.
코틀린 컬렉션은 자바 컬렉션과 똑같은 클래스. 하지만 자바보다 더 많은 기능을 쓸 수 있다.

* 예1) 리스트의 마지막 원소를 가져오거나
* 예2) 수로 이뤄진 컬렉션에서 최댓값을 찾기 

Java를 활용하여 List에서 max 값 구하기

```java
public class CollectionJava {
    public static void main(String[] args) {
        final List<Integer> numberList = List.of(1, 2, 3);
        final Integer max = numberList.stream().max(Integer::compareTo).get();
        System.out.println(max); // 3
    }
}
```

Kotlin을 활용하여 List에서 max 값 구하기

```kotlin
fun main() {
  val numberArrayList = arrayListOf(1, 2, 3)
  println(numberArrayList.maxOrNull()) // 3
}
```

<br/>

## 3.2. 함수를 호출하기 쉽게 만들기

자바 컬렉션에는 디폴트로 toString 구현이 들어있다. 출력 형식은 고정돼 있고 그래서 필요한 형식이 아닐 수 있다.
함수 선언을 간단하게 만들 수 있도록 함수를 직접 구현한다. 그 후 코틀린답게 같은 함수를 구현한다.

joinToString 함수 : 
컬렉션의 원소를 StringBuilder의 뒤에 덧 붙인다. 이때 원소사이에 구분자를 추가, 
StringBuilder의 맨 앞과 맨 뒤에는 접두사와 접미사를 추가한다.

이 함수는 제네릭 하다. 즉 이 함수는 어떤 타입의 값을 원소로 하는 컬렉션이든 처리할 수 있다.
하지만 선언 부분을 좀 더 고민해봐야 한다.
함수를 호출할 때, 모든 인자를 전달하지 않고 기본 값을 제공하는 방법에 대해 살펴보자.

```kotlin
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

// 테스트
val numberList = listOf(1, 2, 3)
println(numberList)

val joinToString = joinToString(numberList, " or ", "<", ">")
println(joinToString)
```

<br/>

### 3.2.1 이름 붙인 인자

첫번째 문제, 함수 호출 부분의 가독성.
함수의 기본 값을 제공하는 방법을 살펴보기 전에, 함수 호출 부분의 가독성을 향상시켜보자.

코딩 스타일 : 
자바에서는 파라미터 이름을 주석에 넣으라고 요구하기도

```java
joinToString(collection, /* separator */ " ", /* prefix */ " ", /* postfix */ ".");
```

```kotlin
joinToString(collection, separator = " ", prefix = " ", postfix = ".")
```

<br/>

코틀린에서는 함수를 호출할 때, **인자에 이름을 명시할 수 있다.**

```kotlin
val joinToString = joinToString(numberList, "", "", "")
```

> 자바로 작성한 코드를 호출할 때는 이름 붙인 인자를 사용할 수 없다. 
클래스 파일에 함수 파라미터 정보를 넣는 것은 자바 8 이후 추가된 선택적 특징.
코틀린은 JDK 6와 호환된다. 그 결과 코틀린 컴파일러는 함수 시그니처의 파라미터 이름을 인식할 수 없고
호출 시 사용한 친자 이름과 함수 정의의 파라미터 이름을 비교할 수 없다. (필자 : 이게 무슨 말??)

<br/>

## 3.2.2. 디폴트 파라미터 값

자바에서는 일부 클래스에서 오버로딩(overloading)한 메서드가 너무 많아진다는 문제가 있다. 
* 하위 호환성을 유지하거나 
* API 사용자에게 편의를 더하는 등의 여러 가지 이유로 만들어진다. 

하지만 어느 경우든 중복이라는 결과는 같다.
* 파라미터 이름과 타입이 계속 반복되며, 
* 인자 중 일부가 생략된 오버로드 함수를 호출할 때 어떤 함수가 불릴지 모호한 경우가 생긴다. 

코틀린에서는 함수 선언에서 파라미터의 디폴트 값을 지정할 수 있으므로 이런 오버로드 중 상당수를 피할 수 있다. 
디폴트 값을 사용해 joinToString 함수를 개선해보자.

<br/>

대부분의 경우 아무 접두사나 접미사 없이 콤마로 원소를 구분하기 때문에, 해당 값 들을 디폴트로 지정해보자.

```kotlin
fun <T> joinToString(
  collection: Collection<T>,  
  separator: String = ", ",   // 디폴트 값이 지정된 파라미터
  prefix: String = "",
  postfix: String = "",
): String
```

이제 함수를 호출할 때 **모든 인자를 쓸 수도 있고, 일부를 생략할 수도** 있다.

```kotlin
// 1, 2, 3
joinToSTring(list, ", ", "", "")

// 1, 2, 3
joinToString(list) // separator, prefix, postfix 생략

// 1; 2; 3
joinToString(list, "; ") // separator를 "; "로 지정, prefix, postfix 생략 
```
일반 호출 문법을 사용하려면 함수를 선언할 때와 같은 순서로 인자를 지정해야 한다. 
그런 경우 일부를 생략하면 뒷부분의 인자들이 생략된다. (Java)

이름 붙인 인자를 사용하는 경우에는 인자 목록의 중간에 있는 인자를 생략하고, 
지정하고 싶은 인자만 이름을 붙여서 순서와 관계없이 지정할 수 있다.

함수의 **디폴트 파라미터 값**은 함수를 호출하는 쪽이 아니라 **함수 선언 쪽에서 지정**. 

> #### ✅디폴트 값과 자바
> 자바에서는 디폴트 파라미터 값이라는 개념이 없어서 코틀린 함수를 자바에서 호출하는 경우에는 
> 그 코틀린 함수가 디폴트 파라미터 값을 제공하더라도 모든 인자를 명시해야 한다.
> - @JvmOverloads 애너테이션을 함수에 추가 
>   - 자바에서 코틀린 함수를 자주 호출해야 한다면 자바 쪽에서 좀 더 편하게 코틀린 함수를 호출하고 싶을 것. 
>   - 코틀린 컴파일러가 자동으로 맨 마지막 파라미터로부터 파라미터를 하나씩 생략한 오버로딩한 자바 메서드를 추가.
> 
> 예를 들어 joinToString에 @JvmOverloads를 붙이면 다음과 같은 오버로딩한 함수가 만들어진다.
> 각각의 오버로딩한 함수들은 시그니처에서 생략된 파라미터에 대해 코틀린 함수의 디폴트 파라미터 값을 사용한다.

```java
String joinToString(Collection<T> collection, String separator, String prefix, String postfix);
String joinToString(Collection<T> collection, String separator, String prefix);
String joinToString(Collection<T> collection, String separator);
String joinToString(Collection<T> collection);
```

## 3.2.3. 정적인 유틸리티 클래스 없애기: 최상위 함수와 프로퍼티

객체지향 언어인 자바에서는 모든 코드를 클래스의 메서드로 작성해야. 
실전에서는 어느 한 클래스에 포함시키기 어려운 코드가 많이 생긴다. 

일부 연산에는 비슷하게 중요한 역할을 하는 클래스가 둘 이상 있을 수도 있다. 
중요한 객체는 하나뿐이지만 그 연산을 객체의 인스턴스 API에 추가해서 API를 너무 크게 만들고 싶지는 않은 경우도 있다.

그 결과 다양한 정적 메서드를 모아두는 역할만 담당하며, 특별한 상태나 인스턴스 메서드가 없는 클래스가 생겨난다. 
JDK의 Collections 클래스가 전형적인 예다. 

코틀린에서는 함수를 직접 소스파일의 최상위 수준, **모든 다른 클래스의 밖에 위치**시키면 된다. 
그런 함수들은 여전히 그 파일의 맨 앞에 정의된 패키지의 멤버 함수이므로 
다른 패키지에서 그 함수를 사용하고 싶을 때는 그 함수가 정의된 패키지를 임포트해야만 한다. 

joinToString 함수를 strings 패키지에 직접 넣어보자.

```kotlin
package strings

fun joinToString(...) : String { ... }
```

JVM이 클래스 안에 들어있는 코드만을 실행할 수 있기 때문에 
컴파일러는 이 파일을 컴파일할 때 새로운 클래스를 정의해준다. 
코틀린만 사용하는 경우에는 그냥 그런 클래스가 생긴다는 사실만 기억하면 된다. 

하지만 이 함수를 자바 등의 다른 JVM 언어에서 호출하고 싶다면 
코드가 어떻게 컴파일되는지 알아야 joinToString과 같은 최상위 함수를 사용할 수 있다. 
join.kt를 컴파일한 결과와 같은 클래스를 자바 코드로 써보면 다음과 같다.

```java
package strings;

public class JoinKt { // join.kt 파일에 해당하는 클래스
  public static String joinToSTring(...) { ...}
}
```

코틀린 파일의 모든 최상위 함수는 이 클래스의 정적인 메서드가 된다. 
따라서 자바에서 joinToString을 호출하기는 쉽다.

```java
import strings.JoinKt;
...
JoinKt.joinToString(list, ", ", "", "");
```

> #### ✅파일에 대응하는 클래스의 이름 변경하기
> 코틀린 최상위 함수가 포함되는 클래스의 이름을 바꾸고 싶다면 
> 파일에 @JvmName 애너테이션을 추가하라. 
> @JvmName 애너테이션은 파일의 맨 앞, 패키지 이름 선언 이전에 위치해야 한다.

```kotlin
@file:JvmName("StringFunctions") // 클래스 이름을 지정하는 애너테이션 

package strings // @file:JvmName 애너테이션 뒤에 패키지 문이 와야한다. 

fun joinToString(...) : String { ... }
```
```java
import strings.StringFunctions;

StringFunctions.joinToString(list, ", ", "", "");
```

#### 최상위 프로퍼티

함수와 마찬가지로 프로퍼티도 파일의 최상위 수준에 놓을 수 있다. (흔하지는 않다.)
예를 들어 어떤 연산을 수행한 횟수를 저장하는 var 프로퍼티를 만들 수 있다.

```kotlin
var opCount = 0  // 최상위 프로퍼티를 선언한다. 

fun performOperation() {
    opCount++ // 최상위 프로퍼티의 값을 변경한다. 
    // ...
}

fun reportOperationCount() {
  println("Operation performed $opCount times") // 최상위 프로퍼티의 값을 읽는다.
}
```

이런 프로퍼티의 값은 정적 필드에 저장된다.
최상위 프로퍼티를 활용해 코드에 상수를 추가할 수 있다.

기본적으로 최상위 프로퍼티도 다른 모든 프로퍼티처럼 접근자 메서드를 통해 자바 코드에 노출된다.
(val의 경우 게터, var의 경우 게터, 세터).

더 자연스럽게 사용하려면 이 상수를 public static final 필드로 컴파일해야 한다. 
const 변경자를 추가하면 프로퍼티를 public static final 필드로 컴파일하게 만들 수 있다.
(단, 원시 타입과 String 타입의 프로퍼티만 const로 지정할 수 있다).


<br/>


## 3.3. 메서드를 다른 클래스에 추가: 확장 함수와 확장 프로퍼티

기존 자바 API를 재작성하지 않고도 코틀린이 제공하는 여러 편리한 기능을 사용할 수 있다면. 
바로 **확장 함수**(extension function)가 그런 역할을 해줄 수 있다.

확장 함수는 어떤 클래스의 멤버 메서드인 것처럼 호출할 수 있지만 그 클래스의 밖에 선언된 함수. 
어떤 문자열의 마지막 문자를 돌려주는 메서드를 추가해보자.

```kotlin
package strings

fun String.lastChar(): Char = this.get(this.length - 1)
```

확장 함수를 만들려면 추가하려는 함수 이름 앞에 그 함수가 확장할 클래스의 이름을 덧붙이기만 하면 된다. 
클래스 이름을 수신 객체 타입(receiver type)이라 부르며, 
확장 함수가 호출되는 대상이 되는 값(객체)을 수신 객체(receiver object)라고 부른다.

예를 들어 위의 코드의 경우 `String: '수신 객체 타입'`, `this: '수신 객체'` 가 된다.
수신 객체의 타입은 확장이 정의될 클래스의 타입이며, 수신 객체는 그 클래스에 속한 인스턴스 객체다.

```kotlin
println("Kotlin".lastChar())
```

이 예제에서는 String이 수신 객체 타입이고 "Kotlin"이 수신 객체다.
어떤 면에서 이는 String 클래스에 새로운 메서드를 추가하는 것과 같다.  
심지어 String이 자바나 코틀린 등의 언어 중 어떤 것으로 작성됐는가는 중요하지 않다.

일반 메서드의 본문에서 this를 사용할 때와 마찬가지로 확장 함수 본문에도 this를 쓸 수 있다.

```kotlin
package strings

fun String.lastChar(): Char = get(length - 1) // 수신 객체 멤버에 this 없이 접근할 수 있다.
```

확장 함수 내부에서는 일반적인 인스턴스 메서드의 내부에서와 마찬가지로 
수신 객체의 메서드나 프로퍼티를 바로 사용할 수 있다. 
하지만 확장 함수가 캡슐화를 깨지는 않는다는 사실을 기억하라. 

클래스 안에서 정의한 메서드와 달리 확장 함수 안에서는 클래스 내부에서만 사용할 수 있는 
비공개(private) 멤버나 보호된(protected) 멤버를 사용할 수 없다.


### 3.3.1 임포트와 확장 함수
확장 함수를 사용하기 위해서는 그 함수를 다른 클래스나 함수와 마찬가지로 임포트해야만 한다.

```kotlin
import strings.lastChar

val c = "Kotlin".lastChar()
```
물론 *를 사용한 임포트도 잘 작동한다.
as 키워드를 사용하면 임포트한 클래스나 함수를 다른 이름으로 부를 수 있다.

```kotlin
import strings.lastChar as last

val c = "Kotlin".last()
```

한 파일 안에서 `다른 여러 패키지에 속해있는` 그리고 `이름이 같은 함수`를 가져와 사용해야 하는 경우 
이름을 바꿔서 임포트하면 이름 충돌을 막을 수 있다. 물론 일반적인 클래스나 함수라면 
그 전체 이름(FQN, Fully Qualified Name)을 써도 된다. (필자 : FQN?) 

하지만 코틀린 문법상 확장 함수는 반드시 짧은 이름을 써야 한다. 
따라서 임포트할 때 이름을 바꾸는 것이 확장 함수 이름 충돌을 해결할 수 있는 유일한 방법이다.


<br/>

### 3.3.2. 자바에서 확장 함수 호출

내부적으로 확장 함수는 **수신 객체를 첫 번째 인자로 받는 정적 메서드**다. 
그래서 확장 함수를 호출해도 다른 어댑터(adapter) 객체나 실행 시점 부가 비용이 들지 않는다.

이런 설계로 인해 자바에서는 확장 함수를 사용하기도 편하다. 
단지 정적 메서드를 호출하면서 첫 번째 인자로 수신 객체를 넘기기만 하면 된다. 
다른 최상위 함수와 마찬가지로 확장 함수가 들어있는 자바 클래스 이름도 확장 함수가 들어있는 파일 이름에 따라 결정된다. 
따라서 확장 함수를 StringUtil.kt 파일에 정의했다면...

```java
char c = StringUtilKt.lastChar("Java");
```

### 3.3.3. 확장 함수로 유틸리티 함수 정의

```kotlin
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

// main에서 실행
val list = listOf(11, 22, 33)
println(list.joinToString(separator = "; ", prefix = "(", postfix = ")"))
```

원소로 이뤄진 컬렉션에 대한 확장을 만든다. 그리고 모든 인자에 대한 디폴트 값을 지정한다. 
이제 joinToString을 마치 클래스의 멤버인 것처럼 호출할 수 있다.

```kotlin
println(list.joinToString3(" "))
```

확장 함수는 단지 정적 메서드 호출에 대한 문법적인 편의(syntatic sugar)일 뿐. 
그래서 클래스가 아닌 더 **구체적인 타입을 수신 객체 타입으로 지정**할 수도 있다. 
문자열의 컬렉션에 대해서만 호출할 수 있는 join 함수.


```kotlin
fun Collection<String>.join(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
) = joinToString(separator, prefix, postfix)

println(listOf("one", "two", "eight").join(" "))
// one two eight
```

이 함수를 객체의 리스트에 대해 호출할 수는 없다. (오직 문자열 컬렉션만)

확장 함수가 정적 메서드와 같은 특징을 가지므로, 확장 함수를 하위 클래스에서 오버라이드 할 수는 없다.

<br/>


### 3.3.4. 확장 함수는 오버라이드할 수 없다.
코틀린의 메서드 오버라이드도 일반적인 객체지향의 메서드 오버라이드와 마찬가지다. 
하지만 확장 함수는 오버라이드 할 수 없다.

View와 그 하위 클래스인 Button이 있는데, 
Button이 상위 클래스의 click 함수를 오버라이드하는 경우를 생각해보자.

```kotlin
open class View {
    open fun click() = println("View clicked")
}

class Button: View() { // Button은 View를 확장한다 
    override fun click() = println("Button clicked")
}
```

Button이 View의 하위 타입이기 때문에 View 타입 변수를 선언해도 Button 타입 변수를 그 변수에 대입할 수 있다. 
View 타입 변수에 대해 click과 같은 일반 메서드를 호출했는데, 
click을 Button 클래스가 오버라이드했다면 실제로는 Button이 오버라이드한 click이 호출된다.

```kotlin
val view: View = Button()
view.click() // "view"에 저장된 값의 실제 타입에 따라 호출할 메서드가 결정된다.
```

> 실행 시점에 객체 타입에 따라 동적으로 호출될 대상 메소드를 결정하는 방식 : 동적 디스패치
> 
> 반면 컴파일 시점에 알려진 변수 타입에 따라 정해진 메소드를 호출하는 방식 : 정적 디스패치
> 
> 프로그래밍 언어 용어에서 `정적`이라는 말은 컴파일 시점을 의미하고 `동적`이라는 말은 실행 시점을 의미한다.

하지만 확장함수는 이런 식으로 동작하지 않는다. 확장 함수는 클래스의 일부가 아니다. 

확장 함수는 클래스 밖에 선언된다. 
이름과 파라미터가 완전히 같은 확장 함수를 기반 클래스와 하위 클래스에 대해 정의해도 
실제로는 확장 함수를 호출할 때 **수신 객체로 지정한 변수의 정적 타입에 의해** 어떤 확장함수가 호출될지 결정되지, 
그 변수에 저장된 객체의 동적인 타입에 의해 확장 함수가 결정되지 않는다.

다음 예제는 View와 Button 클래스에 대해 선언된 두 showOff() 확장 함수를 보여준다.

```kotlin
fun View.showOff() = println("I'm a view!")
fun Button.showOff() = println("I'm a button")

val view: View = Button()
view.showOff() // 확장 함수는 정적으로 결정된다. 
```

view가 가리키는 객체의 실제 타입이 Button이지만, 
이 경우 view의 타입이 View이기 때문에 무조건 View의 확장 함수가 호출된다.

확장 함수를 **첫 번째 인자가 수신 객체인 정적 자바 메서드로 컴파일**한다는 사실을 기억한다면 
이런 동작을 쉽게 이해할 수 있다. 자바도 호출할 정적(static) 함수를 같은 방식으로 정적으로 결정한다.


```java
View view = new Button();
ExtensionsKt.showOff(view); // showOff 함수를 extensions.kt 파일에 정의했다.
```

위 예제와 같이 확장 함수를 오버라이드를 할 수는 없다. 코틀린은 호출될 확장 함수를 정적으로 결정하기 때문이다.

> 어떤 클래스를 확장함 함수와 그 클래스의 멤버 함수의 이름과 시그니처가 같다면 확장 함수가 아니라 
멤버 함수가 호출된다. (멤버 함수의 우선순위가 더 높다)

<br/>

### 3.3.5. 확장 프로퍼티

확장 프로퍼티를 사용하면 기존 클래스 객체에 대한 프로퍼티 형식의 구문으로 사용할 수 있는 API를 추가할 수 있다. 
프로퍼티라는 이름으로 불리기는 하지만 **상태를 저장할 적절한 방법이 없기 때문**에 
실제로 확장 프로퍼티는 아무 상태도 가질 수 없다. 
하지만 프로퍼티 문법으로 더 짧게 코드를 작성할 수 있어서 편한 경우가 있다.
(필자 : 확장함수와 모양이 비슷)

```kotlin
val String.lastChar: Char
    get() = get(length - 1)
```

확장 함수의 경우와 마찬가지로 확장 프로퍼티도 일반적인 프로퍼티와 같은데, 
단지 `수신 객체 클래스`가 추가됐을 뿐이다. 
뒷받침하는 필드가 없어서 기본 게터 구현을 제공할 수 없으므로 최소한 게터는 꼭 정의를 해야 한다. 
마찬가지로 초기화 코드에서 계산한 값을 담을 장소가 전혀 없으므로 초기화 코드도 쓸 수 없다.

```kotlin
// 변경 가능한 확장 프로퍼티 선언하기
var StringBuilder.lastChar: Char 
    get() = get(length - 1) // 프로퍼티의 게터 
    set(value: Char) {
        this.setCharAt(length - 1, value) // 프로퍼티의 세터, 마지막 글자 바꾸기
}
```

확장 프로퍼티를 사용하는 방법은 멤버 프로퍼티를 사용하는 방법과 같다.

```kotlin
println("Kotlin".lastChar)
val sb = StringBuilder("Kotlin?")
sb.lastChar = '!'
println(sb)
```

자바에서 확장 프로퍼티를 사용하고 싶다면 항상 StringUiltKt.getLastChar("Java") 처럼 
게터나 세터를 명시적으로 호출해야 한다.

<br/>

## 3.4. 컬렉션 처리: 가변 길이 인자, 중위 함수 호출, 라이브러리 지원

- `vararg` 키워드를 사용하면 호출 시 인자 개수가 달라질 수 있는 함수를 정의할 수 있다.
- `중위 infix` 함수 호출 구문을 사용하면 인자가 하나뿐인 메서드를 간편하게 호출할 수 있다.
- `구조 분해 선언`(destructuring declaration)을 사용하면 복합적인 값을 분해해서 여러 변수에 나눠 담을 수 있다.

### 3.4.1. 자바 컬렉션 API 확장

```kotlin
strings.last()
val numbers: Collection<Int> = setOf(1, 14, 2)
numbers.max()
```

어떻게 자바 라이브러리 클래스의 인스턴스인 컬렉션에 대해 코틀린이 새로운 기능을 추가할 수 있을까? 
last와 max는 모두 확장 함수였던 것이다. (필자 : max 함수를 찾지 못함. maxOrNull)

```kotlin
fun <T> List<T>.last(): T {
    /* 마지막 원소를 반환함 */
}

fun Collection<Int>.max(): Int {
    /*컬렉션의 최댓값을 찾음 */
}
```
컬렉션이나 다른 객체에 대해 사용할 수 있는 메서드나 
함수가 무엇인지 궁금할 때마다 IDE의 코드 완성 기능을 통해 그런 메서드나 함수를 살펴볼 수 있다.

표준 라이브러리 참조 매뉴얼을 살펴보면 
각 라이브러리 클래스가 제공하는 모든 메서드(멤버 메서드와 확장 함수)를 볼 수 있다.

### 3.4.2. 가변 인자 함수: 인자의 개수가 달라질 수 있는 함수 정의

```kotlin
val list = listOf(2, 3, 5, 7, 11)

// 라이브러리에서 이 함수의 정의를 보면 다음과 같다.
fun listOf<T>(vararg values: T): List<T> { ... }
```

자바의 `가변 길이 인자`(varargs) :  
가변 길이 인자는 메서드를 호출할 때 원하는 개수만큼 값을 인자로 넘기면 
자바 컴파일러가 배열에 그 값들을 넣어주는 기능이다.

코틀린의 가변 길이 인자도 자바와 비슷하다. 
타입 뒤에 ...를 붙이는 대신 코틀린에서는 파라미터 앞에 `vararg` 변경자를 붙인다.

- 이미 배열에 들어있는 원소를 가변 길이 인자로 넘길 때 
  - 자바에서는 배열을 그냥 넘기면 되지만 
  - 코틀린에서는 배열을 명시적으로 풀어서 배열의 각 원소가 인자로 전달되게 해야 한다. 

기술적으로는 스프레드 연산자`...`가 그런 작업을 해준다. 하지만 실제로는 전달하려는 배열 앞에 *를 붙이기만 하면 된다.

```kotlin
fun main(args: Array<String>) {
    val list = listOf("args: ", *args) // 스프레드 연산자가 배열의 내용을 펼쳐준다. 
    println(list)
}
```

이 예제는 스프레드 연산자를 통하면 배열에 들어있는 값과 다른 여러 값을 함께 써서 함수를 호출할 수 있음을 보여준다. 
이런 기능은 자바에서는 사용할 수 없다.

<br/>

### 3.4.3. 값의 쌍 다루기: 중위 호출과 구조 분해 선언
맵을 만들려면 mapOf 함수를 사용한다.

```kotlin
val map = mapOf(1 to "one", 7 to "seven", 53 to "fifty-three")
```


`to`라는 단어는 코틀린 키워드가 아니다. 
이 코드는 `중위 호출`(infix call)이라는 특별한 방식으로 to라는 일반 메서드를 호출한 것이다.
중위 호출 시에는 수신 객체와 유일한 메서드 인자 사이에 메서드 이름을 넣는다.
(이때 객체`1`, 메서드 이름`to`, 유일한 인자`one` 사이에는 공백이 들어가야 한다)

다음 두 호출은 동일하다.

```kotlin
1.to("one") // "to" 메서드를 일반적인 방식으로 호출함
1 to "one" // "to" 메서드를 중위 호출 방식으로 호출함 
```

- 인자가 하나뿐인 일반 메서드나 
- 인자가 하나뿐인 확장 함수에 중위 호출을 사용할 수 있다.
- 함수(메서드)를 중위 호출에 사용하게 허용하고 싶으면 
  - infix 변경자를 함수(메서드) 선언 앞에 추가해야 한다. 

다음은 to 함수의 정의를 간략하게 줄인 코드다.

```kotlin
infix fun Any.to(other: Any) = Pair(this, other)
```

이 to 함수는 Pair의 인스턴스를 반환한다. 
Pair는 코틀린 표준 라이브러리 클래스로, 두 원소로 이뤄진 순서쌍을 표현한다. (실제로 to는 제네릭 함수)

Pair의 내용으로 두 변수를 즉시 초기화할 수 있다.

```kotlin
val (number, name) = 1 to "one"
```

이런 기능을 `구조 분해 선언`(desrtucturing declaration)이라고 부른다.
다른 객체에도 구조 분해를 적용할 수 있다. 

루프에서 구조 분해 선언 활용 :  
joinToString에서 본 withIndex를 구조 분해 선언과 조합하면 
컬렉션 원소의 인덱스의 값을 따로 변수에 담을 수 있다.

```kotlin
for ((index, element) in collection.withIndex()) {
    println("$index: $element")
}
```

to 함수는 확장 함수다. 타입과 관계없이 임의의 순서쌍을 만들 수 있다. 
이는 to의 수신 객체가 제네릭하다는 뜻. 

1 to "one", "one" to 1, list to list.size() 등의 호출이 모두 잘 작동한다. 

```kotlin
// mapOf 함수의 선언
fun <K,V> mapOf(vararg values: Pair<K, V>) : Map<K, V>
```

mapOf에도 원하는 개수만큼 인자를 전달할 수 있다. 
하지만 mapOf의 경우에는 각 인자가 키와 값으로 이뤄진 순서쌍이어야 한다.

새로운 맵을 만드는 구문은 코틀린이 맵에 대해 제공하는 특별한 문법인 것? 아님. 

실제로는 일반적인 함수를 더 간결한 구문으로 호출하는 것 뿐이다. 

```kotlin
// 진짜 to 함수 - 수신객체가 제너릭함
public infix fun <A, B> A.to(that: B): Pair<A, B> = Pair(this, that)
```

<br/>
<br/>

## 3.5. 문자열과 정규식 다루기

코틀린 문자열은 자바 문자열과 같다. 

- 코틀린은 다양한 확장 함수를 제공함으로써 표준 자바 문자열을 더 즐겁게 다루게 해준다. 
- 또한 혼동이 야기될 수 있는 일부 메서드에 대해 더 명확한 코틀린 확장 함수를 제공함. 

### 3.5.1. 문자열 나누기
자바의 String의 split 메서드. 
자바 split 메서드로는 점(.)을 사용해 문자열을 분리할 수 없습니다." 라는 질문.

```java
"12.345-6.A".split(".")
```

하지만 자바의 split 메서드는 빈배열을 반환한다! 
split의 구분 문자열을 실제로는 정규식(regular expression)이기 때문이다. 
따라서 마침표(.)는 모든 문자를 나타내는 정규식으로 해석된다.

코틀린에서는 자바의 split 대신에 여러 다른 조합의 파라미터를 받는 split 확장 함수를 제공함으로써 혼동을 감소. 

**정규식을 파라미터로 받는 함수는 String이 아닌 Regex 타입의 값을 받는다.** 
따라서 코틀린에서는 split 함수에 전달하는 값의 타입에 따라 
정규식이나 일반 텍스트 중 어느 것으로 문자열을 분리하는지 쉽게 알 수 있다.

```kotlin
// 마침표나 대시(-)로 문자열을 분리하는 예
println("12.345-6.A".split("//.|-".toRegex())) // 정규식을 명시적으로 만든다.
```

코틀린 정규식 문법은 자바와 똑같다. 여기 있는 패턴은 마침표나 대시와 매치된다.
(정규식 안에서 마침표가 와일드카드 문자가 아닌 문자 자체로 쓰이게 하기 위해 **마침표를 이스케이프** 시켰다). 

정규식을 처리하는 API는 표준 자바 라이브러리 API와 비슷하지만 좀 더 코틀린답게 변경됐다. 
예를 들어 코틀린에서는 toRegex 확장 함수를 사용해 문자열을 정규식으로 변환할 수 있다.

split 확장 함수를 오버로딩한 버전 중에는 구분 문자열을 하나 이상 인자로 받는 함수가 있다.

```kotlin
println("12.345-6.A".split(".", "-")) // 여러 구분 문자열을 지정한다.
```

여러 문자를 받을 수 있는 코틀린 확장 함수는 자바에 있는 단 하나의 문자만 받을 수 있는 메서드를 대신한다.

### 3.5.2. 정규식과 3중 따옴표로 묶은 문자열

- 첫 번째 구현은 String을 확장한 함수를 사용하고 
- 두 번째 구현은 정규식을 사용한다. 

우리가 할 일은 파일의 전체 경로명을 디렉터리, 파일 이름, 확장자로 구분하는 것. 
코틀린 표준 라이브러리에는 어떤 문자열에서 구분 문자열이 
맨 나중(또는 처음)에 나타난 곳 뒤(또는 앞)의 부분 문자열을 반환하는 함수가 있다. 
이런 함수를 사용해 경로 파싱을 구현한 버전은 다음고 같다.

```
<예시 경로>
"/Users/yole/kotlin-book/chapter.adoc"
- 디렉터리: "/Users/yole/kotlin-book"
- 파일 이름: "chapter"
- 확장자: "adoc"
```

```kotlin
// 첫 번째 구현 : String을 확장한 함수를 사용
fun parsePath(path: String) {
  val directory = path.substringBeforeLast("/")
  val fullName = path.substringAfterLast("/")

  val fileName = fullName.substringBeforeLast(".")
  val extension = fullName.substringAfterLast(".")

  println("Dir: $directory, name: $fileName, ext: $extension")
}

// 실행
parsePath("/Users/yole/kotlin-book/chapter.adoc")
```

코틀린에서는 정규식을 사용하지 않고도 문자열을 쉽게 파싱할 수 있다. 
정규식은 강력하기는 하지만 나중에 알아보기 힘든 경우가 많다. 
정규식이 필요할 때는 코틀린 라이브러리를 사용하면 더 편하다.

```kotlin
// 두 번째 구현 : 정규식을 사용
fun parsePath(path: String) {
    val regex = """(.+)/(.+)\.(.+)""".toRegex()
    val matchResult = regex.matchEntire(path)
    if (matchResult != null) {
        val (directory, filename, extension) = matchResult.destructured
        println("Dir: $directory, name: $filename, ext: $extension")
    }
}
```

3중 따옴표 문자열`"""`에서는 역슬래시(\)를 포함한 어떤 문자도 이스케이프할 필요가 없다. 
- 예를 들어 일반 문자열을 사용해 정규식을 작성하는 경우 
  - 마침표 기호를 이스케이프하려면 \\. 라고 써야하지만, 
  - 3중 따옴표 문자열에서는 \. 라고 쓰면 된다. 

위의 예제에서 쓴 정규식은 슬래시와 마침표를 기준으로 경로를 세 그룹으로 분리한다. 
- 패턴 .은 임의의 문자와 매치될 수 있다. 
  - 따라서 첫번째 그룹인 (.+)는 마지막 슬래시까지 모든 문자와 매치된다. 
  - 이 부분 문자열에는 마지막 슬래시를 제외한 모든 슬래시도 들어간다. 
- 비슷한 이유로 두번째 그룹에도 마지막 마침표 전까지 모든 문자가 들어간다. 
- 세번째 그룹에는 나머지 모든 문자가 들어간다.

parsePath 함수 구현에 대한 설명은 7.4절에서 더 자세히...

<br/>

### 3.5.3. 여러 줄 3중 따옴표 문자열

3중 따옴표 문자열에는 줄 바꿈을 표현하는 아무 문자열이나 (이스케이프 없이) 그대로 들어간다.


```kotlin
// 아스키코드로 글자만 사용해 그린 그림 출력
val kotlinLogo = """|  //
                   .| //
                   .|/ \"""
                    
println(kotlinLogo.trimMargin("."))
```

여러 줄 문자열을 코드에서 더 보기 좋게 표현하고 싶다면 들여쓰기를 하되 
들여쓰기의 끝부분을 특별한 문자열로 표시하고, trimMargin을 사용해 그 문자열과 그 직전의 공백을 제거한다.
(위 코드에서는 .을 사용해 들여쓰기 한 부분을 표시하고, 
trimMargin(".") 메서드를 통해 .과 직전의 공백을 모두 제거했다.)

- 경로를 일반 문자열로 표현한다면 `C:\\Users\\yole\\kotlin-book`
- 3중 따옴표 문자열로 쓰면 `"""C:\Users\yole\kotlin-book"""`

3중 따옴표 문자열 안에서는 이스케이프가 불가능하다.
그러나 만약 문자열 템플릿의 시작을 의미하는 $를 3중 따옴표 문자열 안에 넣어야 한다면
`val price = """${'$'}99.9"""` 처럼 문자열 템플릿 안에 '$' 문자를 넣어서 써야한다.

3중 따옴표 문자열은 복잡하게 이스케이프를 쓰거나 외부 파일에서 텍스트를 불러올 필요가 없으므로 
테스트 등에서 매우 유용한 기능이다.

> 이제 확장 함수가 기존 라이브러리의 API를 확장하고 기존 라이브러리를 새로운 언어에 맞춰 사용할 수 있게 도와주는 
> 강력한 방법임을 알았을 것이다. 이런 식으로 기존 라이브러리를 새 언어에서 활용하는 패턴을 
> `라이브러리 알선(Pimp My Library)` 패턴이라 부른다. 
> 실제로 코틀린 표준 라이브러리 중 상당 부분은 표준 자바 클래스의 확장으로 이뤄졌다. 
> 젯브레인스가 만든 안코(Anko) 라이브러리(필자 : 더 이상 안씀)도 안드로이드 API를 코틀린에 맞춰 확장한 함수를 제공한다. 
> 그 외에도 스프링과 같은 여러 주요 서드파디 라이브러리를 코틀린에 맞게 확장해주는 코틀린 커뮤니티가 만든 수많은 라이브러리를 찾아볼 수 있다.

<br/>
<br/>


### 3.6. 코드 다듬기: 로컬 함수와 확장
`반복하지 말라`(DRY, Don't Repeat Yourself)
자바 코드를 작성할 때는 DRY 원칙을 피하기가 쉽지 않다.
- 많은 경우 메소드 추출(Extract Method) 리팩토링을 적용해서 긴 메소드를 부분부분 나눠서 각 부분을 재활용할 수 있다.
  - 하지만 그렇게 코드를 리팩토링하면 클래스 안에 작은 메소드가 많아지고 
  - 각 메소드 사이의 관계를 파악하기 힘들어서 코드를 이해하기 더 어려워질 수도 있다.
- 리팩토링을 진행해서 추출한 메소드를 별도의 내부 클래스(inner class)안에 넣으면
  - 코드를 깔끔하게 조직할 수는 있지만,
  - 불필요한 준비 코드가 늘어난다.

코틀린에서는 함수에서 추출한 함수를 원 함수 내부에 중첩시킬 수 있다.
문법적인 부가 비용을 들이지 않고도 깔끔하게 코드를 조직할 수 있다.

```kotlin
class User(val id: Int, val name: String, val address: String)

fun saveUser(user: User) {
  if (user.name.isEmpty()) {      // 필드 검증 중복
    throw IllegalArgumentException(
      "Can't save user ${user.id}: empty Name"
    )
  }
  if (user.address.isEmpty()) {   // 필드 검증 중복
    throw IllegalArgumentException(
      "Can't save user ${user.id}: empty Address"
    )
  }
  // user를 데이터베이스에 저장한다.
}

// 실행 에러 : 
// Exception in thread "main" java.lang.IllegalArgumentException: Can't save user 1: empty Name
saveUser(User(1, "", ""))
```

코드 중복은 많지 않지만 
클래스가 사용자의 필드를 검증할 때 필요한 여러 경우를 하나씩 처리하는 메소드로 넘쳐나기를 바라지는 않을 것.
검증 코드를 로컬 함수로 분리하면 중복을 없애는 동시에 코드 구조를 깔끔하게 유지할 수 있다.

```kotlin
fun saveUser2(user: User) {
  fun validate(
    user: User,    // 한 필드를 검증하는 로컬 함수 정의
    value: String,
    fieldName: String
  ) {
    if (value.isEmpty()) {
      throw IllegalArgumentException(
        "Can't save user ${user.id}: empty $fieldName"
      )
    }
  }

  // 로컬 함수를 호출해서 각 필드를 검증
  validate(user, user.name, "Name")
  validate(user, user.address, "Address")
  // user을 데이터베이스에 저장한다.
}
```

- 검증 로직 중복은 사라졌고
- 필요하면 User의 다른 필드에 대한 검증도 쉽게 추가할 수 있다
- 하지만 User 객체를 로컬 함수에게 하나씩 전달해야 한다는 점은 아쉽.

로컬 함수는 자신이 속한 바깥 함수의 모든 파라미터와 변수를 사용할 수 있다.

```kotlin
fun saveUser3(user: User) {
  fun validate(value: String, fieldName: String) { // 이제 saveUser 함수의 user 파라미터를 중복 사용하지 않는다.
    if (value.isEmpty()) {
      throw IllegalArgumentException(
        "Can't save user ${user.id}: " + // 바깥 함수의 파라미터에 직접 접근 가능
                "empty $fieldName"
      )
    }
  }
  validate(user.name, "Name")
  validate(user.address, "Address")
}
```

검증 로직 - User 클래스를 확장 함수로 만들수 있다.

```kotlin
fun User.validateBeforeSave() {
  fun validate(value: String, fieldName: String) {
    if (value.isEmpty()) {
      throw IllegalArgumentException(
        "Can't save user $id: empty $fieldName"
      )    // User의 프로퍼티를 직접 사용할 수 있다.
    }
  }
  validate(name, "Name")
  validate(address, "Address")
}

fun saveUser4(user: User) {
  user.validateBeforeSave()   // 확장 함수 호출
}
```

이 경우 검증 로직은 User를 사용하는 다른 곳에서 쓰이지 않을 기능이기 때문에 
User에 포함시키고 싶지는 않고 User를 간결하게 유지하려면 검증 로직을 확장 함수로 작성할 수 도 있다.

한 객체만을 다루면서 객체의 비공개 데이터를 다룰 필요는 없는 함수는 확장 함수로 만들면 
`객체.멤버`처럼 수신 객체를 지정하지 않고도 공개된 멤버 프로퍼티나 메서드에 접근할 수 있다.

확장 함수를 로컬 함수로 정의할 수 있다. 하지만 중첩된 함수의 깊이가 깊어지면 코드 읽기가 어려워짐다.
따라서 일반적으로는 한단계만 함수를 중첩시카라고 권장.

<br/>
<br/>

## 3.7. 요약

- 코틀린은 자체 컬렉션 클래스를 정의하지 않지만 자바 클래스를 확장해서 더 풍부한 API를 제공한다.
- 함수 파라미터의 디폴트 값을 정의하면 오버로딩한 함수를 정의할 필요성이 줄어든다. 이름붙인 인자를 사용하면 함수의 인자가 많을 때 함수 호출의 가독성을 더 향상시킬 수 있다.
- 코틀린 파일에서 클래스 멤버가 아닌 최상위 함수와 프로퍼티를 직접 선언할 수 있다. 이를 활용하면 코드 구조를 더 유연하게 만들 수 있다.
- 확장 함수와 프로퍼티를 사용하면 외부 라이브러리에 정의된 클래스를 포함해 모든 클래스의 API를 그 클래스의 소스코드를 바꿀 필요 없이 확장할 수 있다. 확장 함수를 사용해도 실행 시점에 부가 비용이 들지 않는다.
- 중위 호출을 통해 인자가 하나 밖에 없는 메소드나 확장 함수를 더 깔끔한 구문으로 호출할 수 있다.
- 코틀린은 정규식과 일반 문자열을 처리할 때 유용한 다양한 문자열 처리 함수를 제공한다.
- 자바 문자열로 표현하려면 수많은 이스케이프가 필요한 문자열의 경우 3중 따옴표 문자열을 사용하면 더 깔끔하게 표현할 수 있다.
- 로컬 함수를 써서 코드를 더 깔끔하게 유지하면서 중복을 제거할 수 있다.

<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>