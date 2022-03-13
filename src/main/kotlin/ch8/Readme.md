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

각 인터페이스에는 `invoke` 메소드 정의가 하나 들어있다. 
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




<br/>



## 8.1.


<br/>



## 8.1.


<br/>
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