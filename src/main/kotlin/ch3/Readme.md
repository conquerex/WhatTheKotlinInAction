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




<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>