# 10장. 애노테이션과 리플렉션

## 입구
- 애노테이션 적용과 정의
- 리플렉션을 사용해 실행 시점에 객체 내부 관찰
- 코틀린 실전 프로젝트 예제

어떤 함수를 호출하려면 그 함수가 정의된 클래스의 이름과 함수 이름, 파라미터 이름 등을 알아야만 했다. 
애노테이션과 리플렉션을 사용하면 그런 제약을 벗어나서 미리 알지 못하는 임의의 클래스를 다룰 수 있다.
<span style="color:orange">애노테이션</span>을 사용하면 
라이브러리가 요구하는 의미를 클래스에게 부여할 수 있고,
<span style="color:orange">리플렉션</span>을 사용하면 실행 시점에 컴파일러 내부 구조를 분석할 수 있다.


<br/>
<br/>


## 10.1. 애노테이션 선언과 적용

메타데이터를 선언에 추가하면 애노테이션을 처리하는 도구가 컴파일 시점이나 실행 시점에 적절한 처리를 해준다.

<br/>

## 10.1.1. 애노테이션 적용

애노테이션을 적용하려면 적용하려는 대상(함수나 클래스) 앞에 애노테이션을 붙이면 된다.
@와 애노테이션 이름으로 이뤄진다.

```kotlin
import org.junit.*
class MyTest {
    // @Test 애노테이션을 사용해 제이유닛 프레임워크에게
    // 이 메소드를 테스트로 호출하라고 지시한다
    @Test fun testTrue() {
        Assert.assertTrue(true)
    }
}
```

@Depreacted 애노테이션을 예로 살펴보면, 자바와 코틀린에서 의미는 동일하다.
하지만 코틀린에서는 replaceWith 파라메터를 통해 이전 버전을 대신할 패턴을 제시할 수 있다.

```kotlin
@Deprecated("Use removeAt(index) instead.", ReplaceWith("removeAt(index)"))
fun remove(index: Int) { ... }
```

애노테이션에 인자를 넘 때는 일반 함수와 마찬가지로 괄호 안에 인자를 넣는다
애노테이션의 인자로는 ...
- 원시 타입의 값
- 문자열
- enum
- 클래스 참조
- 다른 애노테이션 클래스
- 위 요소들로 이뤄진 배열

어노테이션 인자를 지정하는 문법은 자바와 약간 다르다.
- 클래스를 어노테이션 인자로 지정할 때는 클래스 이름 뒤에 ::class를 넣는다.
- 다른 어노테이션을 인자로 지정할 때는 인자로 들어가는 어노테이션의 이름 앞에 @를 붙이지 않는다. 
  위의 Deprecated와 ReplaceWith의 예가 그러하다.
- 배열을 인자로 지정하려면 @RequestMapping(path=arrayOf(“/foo”, “/bar”)) 처럼 
  arrayOf 함수를 사용한다. 
  자바에서 지정한 애노테이션 클래스를 이용한다면, value라는 이름의 가변 길이 인자로 변환된다. 
  따라서 @JavaAnnotationWithArrayValue("abc", "foo", "bar")처럼 arrayOf함수를 안써도 된다.

어노테이션 인자를 컴파일 시점에 알 수 있어야 한다. 
따라서 임의의 프로퍼티를 인자로 지정할 수 없다.
프로퍼티를 어노테이션 인자로 사용하기 위해서는 앞에 const 를 붙여야 한다. 
컴파일러는 const가 붙은 프로퍼티를 컴파일 시점에 상수로 취급한다.

```kotlin
const val TEST_TIMEOUT = 100L
@Test(timeout = TEST_TIMEOUT) fun testMethod(){ ... }
```

const가 붙은 프로퍼티는 파일의 최상위나 object 안에 선언 해야 하며 
원시타입 또는 String으로만 초기화가 가능하다.,

<br/>

## 10.1.2. 애노테이션의 대상

코틀린에서의 선언(필자 : 애노테이션 사용 얘기인가?)을 컴파일한 결과가 여러 자바 선언과 대응하는 경우가 있다.
이때, 코틀린 선언과 대응하는 여러 자바 선언에 각각 어노테이션을 붙여야 할 때가 있다.

예를 들어...
코틀린 프로퍼티는 기본적으로 자바 필드와 게터 메소드 선언과 대응한다. 
프로퍼티가 변경 가능하면 세터에 대응하는 자바 세터 메소드와 세터 파라미터가 추가된다. 
이외에도 생성자와 관련해서도 존재하며, 어노테이션을 붙일 때, 
이런 요소 중 어떤 요소에 어노테이션을 붙일지 표시할 필요가 있다.

<span style="color:orange">사용 지점 대상</span> 선언으로 어노테이션 붙일 요소를 정할 수 있다. 
@ 기호와 어노테이션 이름 사이에 붙으며, 어노테이션 이름과는 콜론(:)으로 분리된다.

![그림 10.1](/Users/barley.son/dev/WhatTheKotlinInAction/src/main/kotlin/ch10/img10-1.png)

규칙을 지정하려면 공개 필드나 메소드 앞에 @Rule을 붙여야 한다. 
하지만 코틀린 테스트 클래스의 folder라는 프로퍼티 앞에 @Rule을 붙이면
"The @Rule ‘folder’ must be public"(@Rule을 지정한 folder는 public이어야 함)라는 
JUnit 예외가 발생한다. 

@Rule은 필드에 적용되었지만, 코틀린의 필드는 기본적으로 비공개이기 때문에 이런 예외가 발생한다. 

- 4장 참고
  - 디컴파일 시 필드는 private이 되지만, 
  - getter, setter는 public이기 때문에 프로퍼티 입장으로 볼 때는 public 이라고 할 수 있다.

@Rule 어노테이션을 정확한 대상에 적용하려면...

```kotlin
class HasTempFolder{
  @get:Rule
  val folder = TemporaryFolder()
  
  @Test
  fun testUsingTempFolder(){
    val createdFile = folder.newFile("myFile.txt")
    val createdFolder = folder.newFolder("subfolder")
    ...
  }
}
```


자바에 선언된 애노테이션을 사용해 프로퍼티(필드+get/set)에 애노테이션을 붙이면 
일반적으로 프로퍼티의 필드에 그 애노테이션이 붙는다.

코틀린으로 애노테이션을 선언하면 프로퍼티에 직접 적용할 수는 있는 애노테이션을 만들 수 있다.
사용 지점 대상을 지정할 때, 지원하는 대상 목록은 다음과 같다.
- property : 프로퍼티 전체. 자바에서 선언된 어노테이션에는 이 사용 지점 대상을 사용할 수 없다.
- field : 프로퍼티에 의해 생성되는 (뒷받침하는) 필드
- get : 프로퍼티 게터
- set : 프로퍼티 세터
- receiver : 확장 함수나 프로퍼티의 수신 객체 파라미터
- param : 생성자 파라미터
- setparam : 세터 파라미터
- delegate : 위임 프로퍼티의 위임 인스턴스를 담아둔 필드
- file : 파일 안에 선언된 최상위 함수와 프로퍼티를 담아두는 클래스

file 대상을 사용하는 애노테이션은 package 선언 앞에서 파일의 최상위 수준에만 적용할 수 있다.
자바와 달리 코틀린에서는 애노테이션 인자로 클래스나 함수 선언이나 타입 외에 임의의 식을 허용한다.
가장 흔히 쓰이는 예로는 컴파일러 경고를 무시하기 위한 @Suppress 애노테이션이 있다.

```kotlin
fun test(list: List<*>) {
    @Suppress("UNCHECKED_CAST")
    val strings = list as List<String>
    // ...
}
```

인텔리J 아이디어에서는 컴파일러 경고에서 Alt-Enter(필자 : MacOS - option + enter)를 누르거나
인텐션 옵션 메뉴(컴파일러 경고로 반전된 소스코드 영역에 커서를 가져가면 뜨는 전구 아이콘에 붙어 있는 메뉴)에서
Suppress를 선택하면 이 애노테이션을 추가해준다.

> ### ✅ 자바 API를 어노테이션으로 제어하기
> 코틀린은 코틀린으로 선언한 내용을 자바 바이트 코드로 컴파일하는 방법과 
> 코틀린 선언을 자바에 노출하는 방법을 제어하기 위한 어노테이션을 많이 제공한다. 
> 다음은 코틀린 선언을 자바에 노출시키는 방법을 변경할 수 있다.
> 
> - @JvmName : 코틀린 선언이 만들어내는 자바 필드나 메소드 이름을 변경한다. 
> - @JvmStatic : 메소드, 객체 선언, 동반 객체에 적용하면 그 요소가 자바 정적 메소드로 노출된다. 
> - @JvmOverloads : 디폴트 파라미터 값이 있는 함수에 대해 컴파일러가 자동으로 오버로딩한 함수를 생성해준다. 
> - @JvmField : 프로퍼티에 사용하면 게터나 세터가 없는 공개된 자바 필드로 프로퍼티를 노출시킨다.


<br/>

## 10.1.3. 어노테이션을 활용한 JSON 직렬화 제어

<span style="color:orange">직렬화</span>(serialization)는 객체를 저장장치에 저장하거나 
네트워크를 통해 전송하기 위해 텍스트나 이진 형식으로 변환하는 것이다.
반대로 <span style="color:orange">역직렬화</span>(deserialization)는 
텍스트나 이진 형식으로 저장된 데이터로부터 원래의 객체를 만들어낸다.

직렬화에 자주 쓰이는 형식에 JSON이 있다. 
자바와 JSON을 변환할 때 자주 쓰이는 라이브러리로 잭슨(Jackson)과 지슨(GSON)이 있다. 
이들 또한 다른 자바 라이브러리들처럼 코틀린과 완전 호환된다.

JSON에는 객체의 타입이 저장되지 않기 때문에 
JSON 데이터로부터 인스턴스를 만들려면 타입 인자로 클래스를 명시해야 한다.

JSON 직렬화를 위한 제이키드라는 순수 코틀린 라이브러리를 구현하는 과정을 알아보자.

> ### ✅ 제이키드 라이브러리 소스코드와 연습문제
> (필자 : 서적 내용 참고)
> - https://www.manning.com/books/kotlin-in-action
> - https://github.com/yole/jkid

```kotlin
class Person(val name: String, val age: Int)
>>> val person = Person("Alice", 29)
>>> println(serialize(person))

{"age": 29, "name": "Alice"}
```

객체 인스턴스의 JSON 표현은 키/값 쌍으로 이뤄진 객체를 표현. 
키/값쌍은 각 인스턴스의 프로퍼티 이름과 값을 표현한다.

```kotlin
>>> val json = """{"name":"Alice", "age":29}"""
>>> println(deserialize<Person>(json))

Person(name=Alice, age=29)
```

JSON에는 객체의 타입이 저장되지 않기 때문에 JSON 데이터로부터 인스턴스를 만들려면, 
타입인자로 클래스를 명시해야 한다. 여기서는 Person 클래스를 타입 인자로 넘겼다.

아래는 객체와 JSON 표현 사이의 동등성 관계를 보여준다.
원시 타입이나  String 타입의 프로퍼티만 직렬화하는 클래스에 안에 들어있지만, 
실제로는 다른 값 객체 클래스나 여러 값으로 이루어진 컬렉션 타입의 프로퍼티도 들어갈 수 있다.

애노테이션을 활용해 객체를 직렬화하거나 역직렬화하는 방법을 제어할 수 있다.
객체를 JSON으로 직렬화할 때 제이키드 라이브러리는 
기본적으로 모든 프로퍼티를 직렬화하며 프로퍼티 이름을 키로 사용한다. 
애노테이션을 사용하면 이런 동작을 변경할 수 있다.

- @JsonExclude 애노테이션을 사용하면 직렬화나 역직렬화 시 그 프로퍼티를 무시할 수 있다.
- @JsonName 애노테이션을 사용하면 프로퍼티를 표현하는 키/값 쌍의 키로 
  프로퍼티 이름 대신 애노테이션이 지정한 이름을 쓰게 할 수 있다.

```kotlin
data class Person(
    @JsonName("alias") val firstName: String,
    @JsonExclude val age: Int? = null
}
```

fisrtName 프로퍼티를 JSON으로 저장할 떄 사용하는 키를 변경하기 위해 @JsonName 애노테이션을 사용하고,
age 프로퍼티를 직렬화나 역직렬화에 사용하지 않기 위해 @JsonExclude 애노테이션을 사용한다.
직렬화 대상에서 제외할 age 프로퍼티에는 반드시 디폴트 값을 지정해야만 한다.
지정하지 않았다면 역직렬화 시 Person의 인스턴스를 새로 만들 수 없다.

![그림 10.2](/Users/barley.son/dev/WhatTheKotlinInAction/src/main/kotlin/ch10/img10-2.png)


<br/>

## 10.1.4. 애노테이션 선언

@JsonExclude 애노테이션은 아무 파라미터도 없는 가장 단순한 애노테이션이다.

```kotlin
annotation class JsonExclude
```

일반 클래스 선언처럼 보이기도 하지만, class 키워드 앞에 `annotation`이라는 변경자가 붙어있다는 차이점이 있다.
애노테이션 클래스는 오직 선언이나 식과 관련있는 메타데이터의 구조를 정의하기 때문에 
내부에 아무 코드도 들어있을 수 없다.
따라서 애노테이션 클래스에 본문을 정의하지 못하도록 컴파일러가 막는다.

파라미터가 있는 애노테이션을 정의하려면 애노테이션 클래스의 주 생성자에 파라미터를 선언해야 한다.

```kotlin
annotation class JsonName(val name: String)
```

일반 클래스의 주 생성자 선언 구문을 똑같이 사용하지만 
애노테이션 클래스에서는 모든 파라미터 앞에 val을 붙여야 한다.
자바 애노테이션 선언과 비교해보자.

```java
public @interface JsonName{
    String value();
}
```

자바 애노테이션 선언 value 메소드를 사용한 점을 유의하자. 자바의 value()는 특별하다.
애노테이션을 적용할때 value를 제외한 모든 애트리뷰트에는 이름을 명시해야된다.

반면 코틀린의 애노테이션 적용 문법은 일반적인 생성자 호출과 동일하다. 
따라서 인자에 이름을 명하기 위해 이름붙인 인자를 사용할 수도 있고 이름을 생략할 수도 있다.
`@Jsonname(name = "first_name")`과 `@Jsonname("first_name")`은 같다.

자바에서 선언한 어노테이션을 코틀린의 구성 요소에 적용할 때는 
value를 제외한 모든 인자에 대해 이름 붙은 인자 구문을 사용해야만 한다. 
코틀린도 자바 어노테이션에 정의된 value를 특별하게 취급한다.

<br/>


## 10.1.5. 메타애노테이션: 애노테이션을 처리하는 방법 제어

코틀린 어노테이션 클래스에도 어노테이션을 붙일 수 있다.
어노테이션 클래스에 적용할 수 있는 어노테이션을
<span style="color:orange">메타어노테이션</span>이라 한다.

표준 라이브러리에는 몇 가지 메타 어노테이션이 있으며, 
그런 메타 어노테이션들은 컴파일러가 어노테이션을 처리하는 방법을 제어한다.

가장 흔히 쓰이는 메타 어노테이션은 @Target이다. 
앞서 살펴본 제이키드의 JsonExclude, JsonName 어노테이션에도 
적용 가능 대상을 지정하기 위해 @Target을 사용한다.

```kotlin
@Target(AnnotationTarget.PROPERTY)
annotation class JsonExclude
```

@Target 메타 어노테이션은 어노테이션을 적용할 수 있는 요소의 유형을 지정한다. 
어노테이션 클래스에 대해 구체적인 @Target을 지정하지 않으면 
모든 선언에 적용할 수 있는 어노테이션이 된다. 
제이키드 라이브러리는 프로퍼티 어노테이션만을 사용하므로 어노테이션 클래스에 @Target을 꼭 지정해야 한다.

어노테이션이 붙을 수 있는 대상이 정의된 enum은 AnnotationTarget이다. 
이 안에는 클래스, 파일, 프로퍼티, 프로퍼티 접근자, 타입, 식 등에 대한 enum 정의가 들어있다.
둘 이상의 대상을 한꺼번에 선언할 수도 잇다.

메타 어노테이션을 직접 만들어야 한다면 ANNOTATION_CLASS를 대상으로 지정하면 된다.

```kotlin
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class BindingAnnotation

@BindingAnnotation
annotation class MyBinding
```

대상을 PROPERTY로 지정한 어노테이션은 자바에서 사용할 수 없다. 
자바에서 사용하려면 AnnotationTarget.FIELD를 두번째 대상으로 추가해줘야 한다. 
그렇게 하면 어노테이션을 코틀린 프로퍼티와 자바 필드에 적용할 수 있다.

> ### ✅ @Retention 애노테이션
> @Retention은 정의 중인 어노테이션 클래스를 소스 수준에서만 유지할지, 
> .class 파일에 저장할지, 실행 시점에 리플렉션을 사용해 접근할 수 있게 할지를 지정하는 메타 어노테이션이다.
> 
> 자바 컴파일러는 기본적으로 어노테이션을 .class 파일에 저장하지만, 런타임에는 사용할 수 없다. 
> 하지만 대부분의 어노테이션은 런타임에도 사용할 수 있어야 하므로 
> 코틀린에서는 기본적으로 어노테이션의 @Retention을 RUNTIME으로 지정한다.

<br/>

## 10.1.6. 어노테이션 파라미터로 클래스 사용

클래스 참조를 파라미터로 하는 어노테이션 클래스를 선언하면 어떤 클래스를 선언 메타 데이터로 참조할 수 있다.
Ex) jkid의 @DeserializeInterface -> 인터페이스 타입인 프로퍼티에 대한 역직렬화를 제어할 때 쓰는 어노테이션이다.
인터페이스의 인스턴스를 직접 만들 수 없다. 따라서 역직렬화 시 어떤 클래스를 사용해 인터페이스를 구현할 지를 지정할 수 있어야 한다.

```kotlin
interface Company{
  val name: String
}

data class CompanyImpl(override val name: String) : Company

data class Person{
  val name: String,
  @DeserializeInterface(CompanyImpl::class) val company: Company
}
```


<br/>



## 10.1.7. 어노테이션 파라미터로 제네릭 클래스 받기


<br/>



## 10.2. 리플렉션


<br/>




## 10.2.


<br/>




## 10.2.


<br/>




## 10.2.


<br/>




## 10.2.


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