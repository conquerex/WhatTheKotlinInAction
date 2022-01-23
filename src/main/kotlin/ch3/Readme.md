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

자바로 작성한 코드를 호출할 때는 이름 붙인 인자를 사용할 수 없다. 
클래스 파일에 함수 파라미터 정보를 넣는 것은 자바 8 이후 추가된 선택적 특징.
코틀린은 JDK 6와 호환된다. 그 결과 코틀린 컴파일러는 함수 시그니처의 파라미터 이름을 인식할 수 없고
호출 시 사용한 친자 이름과 함수 정의의 파라미터 이름을 비교할 수 없다. (필자 : 이게 무슨 말??)

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