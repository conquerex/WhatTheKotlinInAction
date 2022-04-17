# E. 코루틴과 Async/Await

## 입구
코틀린 1.3부터 코루틴이 표준 라이브러리에 정식 포함.
- 권태환님 자료
  - https://speakerdeck.com/taehwandev/kotlin-coroutines

(필자 : 아마도 안드로이드 개발자 사이에서 셀럽?)

## E.1. 코루틴이란?

위키피디아의 코루틴 정의를 번역

> 코루틴은 컴퓨터 프로그램 구성 요소 중 하나로
비선점형 멀티태스킹(non-preemptive multitasking)을 수행하는
일반화한 서브루틴(subroutine)이다.
코루틴은 실행을 일시 중단(suspend)하고 재개(resume)할 수 있는 여러 진입 지점(entry point)을 허용한다.

<span style="color:orange">서브루틴</span>(subroutine)은 
여러 명령어를 모아 이름을 부여해서 반복 호출을 할 수 있게 정의한 
프로그램 구성 요소(a.k.a. 함수).
객체지향 언어에서는 메서드도 서브루틴이라 할 수 있음.

서브루틴에 진입하는 방법은 오직 한 가지 뿐.
- 해당 함수를 호출하면 서브루틴의 맨 처음부터 실행 시작.
- 그때마다 활성 레코드(activation record)가 스택에 할당되면서 
  서브루틴 내부의 로컬 변수 등이 초기화 됨.

반면 서브루틴 안에서 여러 번 return 을 사용할 수 있다.
서브루틴이 실행을 중단하고 제어를 호출한쪽caller에게 돌려주는 지점은 여럿 있을 수 있다.

다만 일단 서브루틴에서 반환되고 나면 활성 레코드가 스택에서 사라지기 때문에 
실행 중이던 모든 상태를 잃어버린다. 
그래서 서브루틴을 여러 번 반복 실행해도 항상 같은 결과를 얻게 된다.
(전역 변수나 다른 부수 효과가 있지 않는 한)

<span style="color:orange">멀티태스킹</span>은 
여러 작업을 동시에 수행하는 것처럼 보이거나, 실제로 동시에 수행하는 것이다.


<span style="color:orange">비선점형</span>(non-preemptive)이란
멀티태스킹의 각 작업을 실행하는 참여자들의 실행을 
운영체제가 강제로 일시 중단시키고 다른 참여자를 실행하게 만들 수 없다는 뜻.
각 참여자들이 서로 자발적으로 협력해야만 비선점형 멀티태스킹이 제대로 작동할 수 있다.

따라서...

코루틴이란 서로 협력해서 실행을 주고받으면서 작동하는 여러 서브루틴을 말한다.
코루틴의 대표격인 제네레이터를 예로 들면
- 어떤 함수 A가 실행되다가 제너레이터인 코루틴 B를 호출하면
- A가 실행되던 스레드 안에서 코루틴 B의 실행이 시작
- 코루틴 B는 실행을 진행하다가 실행을 A에 양보한다.
  - yield라는 명령을 사용하는 경우가 많다
- A는 다시 코루틴을 호출했던 바로 다음 부분부터 실행을 계속 진행하다가 또 코루틴 B를 호출
  - 이때 B가 일반적인 함수라면 로컬 변수를 초기화하면서 처음부터 실행을 다시 시작하겠지만
  - 코루틴이면 이전 yield로 실행을 양보했던 지점부터 실행을 계속하게 된다.
  
아래는 코루틴의 제어 흐름과 일반적인 함수의 제어 흐름을 비교한 것.

////////////////////////


코루틴을 사용하는 경우 장점은...
- 일반적인 프로그램 로직을 기술하듯 코드를 작성하고
- 상대편 코루틴에 데이터를 넘겨야 하는 부분에서만 yield를 사용하면 된다는 점

예시. 제네레이터를 사용해 카운트다운을 구현하고 이터레이터처럼 불러와 사용하는 의사 코드
```kotlin
generator countdown(n) {
    while (n > 0) {
        yield n
        n -= 1
    }
}

for i in countdown(10) {
    println(i)
}
```

<br>

## E.2. 코틀린의 코루틴 지원: 일반적인 코루틴

언어에 따라 제네레이터 등 특정 형태의 코루티만을 지원하는 경우도 있고 
일반적인 코루틴을 만들 수 있는 기능을 언어가 기본 제공하고 
다양한 코루틴은 그런 기본 기능을 활용해 직접 사용자가 만들거나 라이브러리를 통해 사용하도록 하는 형태가 있다.

제네레이터만 제공하는 경우에도 yield 시 퓨처 등 비동기 처리가 가능한 객체를 넘기는 방법을 사용하면
async/await 등을 비교적 쉽게 구현할 수 있다.

코틀린은 코루틴을 구현할 수 있는 기본 도구를 언어가 제공하는 형태. 
(특정 코루틴을 언어가 지원하는 형태가 아님.)

코틀린의 코루틴 지원 기본 기능
- kotlin.coroutine 패키지 밑에 있고
- 코틀린 1.3부터는 별도의 설정 없이도 모든 기능 사용 가능

하지만 코틀린이 지원하는 기본 기능을 활용해 다양한 형태의 코루틴들은 kotlinx.coroutines 패키지 밑에 있으며
코루틴 github에서 소스코드를 볼 수 있다.

이제 프로젝트의 빌드 설정에 관련 의존성을 추가하고 코틀린 컴파일러를 1.3으로 지정해보자.

```
// 메이븐은 생략, 본 예시는 그래이들 (그리고 최신)
// 안드로이드 설정도 생략
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
}

plugins {
    kotlin("jvm") version "1.6.10"
}
```

<br>

### E.2.1. 여러 가지 코루틴

kotlinx.coroutines.core 모듈에 들어있는 코루틴. 
각각은 코루틴을 만들어주는 코루틴 빌더라고 부른다.
코틀린에서 코루틴 빌더에 원하는 동작을 람다로 넘겨서 코루틴을 만들어 실행하는 방식으로 코루틴을 활용한다.

#### 👉 kotlinx.coroutines.CoroutineScope.launch

launch는 코루틴을 잡(job)으로 반환하며,
만들어진 코루틴은 기본적으로 즉시 실행된다.
원하면 launch가 반환한 Job의 cancel을 호출해 코루틴 실행을 중단시킬 수 있다.

launch가 작동하려면 CoroutineScope 객체가 블록의 this로 지정돼야 하는데...
(API 문서나 소스를 보면 launch가 받는 블록의 타입이 
`suspend CoroutineScope.() -> Unit`임을 알 수 있다. 이해 안될 시 DSL 내용 다시 보기)
- 다른 suspend 함수 내부라면 해당 함수가 사용중인 CoroutineScope가 있겠지만
- 그렇지 않은 경우에는 GlobalScope를 사용하면 된다.

```kotlin
fun now() = ZonedDateTime.now().toLocalTime().truncatedTo((ChronoUnit.MILLIS))

fun log(msg:String) = println("${now()}:${Thread.currentThread()}: $msg")

fun launchInGlobalScope() {
    GlobalScope.launch {
        log("coroutine started.......")
    }
}

fun main() {
  log("main() started...")
  launchInGlobalScope()
  log("launchInGlobalScope() executed")
  Thread.sleep(2000L)
  log("main() terminated...")
}
```
```
01:04:53.115:Thread[main,5,main]: main() started...
01:04:53.223:Thread[main,5,main]: launchInGlobalScope() executed
01:04:53.226:Thread[DefaultDispatcher-worker-1,5,main]: coroutine started.......
01:04:55.229:Thread[main,5,main]: main() terminated...
```

유의할 점은 메인 함수와 `GlobalScope.launch`가 만들어낸 코루틴이 서로 다른 스레드에서 실행된다는 점.
`GlobalScope`는 메인 스레드가 실행중인 동안만 코루틴의 동작을 보장.
앞 코드에서 main의 끝에서 두번째 줄에 있는 sleep을 없애면 코루틴이 아예 실행되지 않을 것.

launchInGlobalScope가 호출한 launch는 스레드가 생성되고 시작되기 전에 
메인 스레드의 제어를 main에 돌려주기 때문에 따로 sleep을 하지 않으면 main이 바로 끝나고
메인 스레드가 종료되면서 바로 프로그램 전체가 끝나 버린다.
GlobalScope를 사용할 때는 조심해야 한다!!!!

이를 방지하려면 비동기적으로 launch를 실행하거나
launch가 모두 다 실행될 때까지 기다려야 한다.

특히 코루틴의 실행이 끝날 때까지 현재 스레드를 블록시키는 함수로 `runBlocking`이 있다.
runBlocking은 CoroutineScope의 확장 함수가 아닌 일반 함수이기 때문에
별도의 코루틴 스코프 객체 없이 사용 가능하다

launchInGlobalScope를 runBlockingExample이라는 이름으로 함수를 만들어보자.

```kotlin
fun runBlockingExample() {
    runBlocking {
        launch {
            log("GlobalScope.launch started.......")
        }
    }
}
```
```
01:46:11.884:Thread[main,5,main]: main() started...
01:46:11.954:Thread[main,5,main]: GlobalScope.launch started.......
01:46:11.954:Thread[main,5,main]: runBlockingExample() executed
01:46:13.959:Thread[main,5,main]: main() terminated...
```

스레드가 모두 main 스레드이다!!!

코루틴들은 서로 yield를 해주면서 협력할 수 있다. 다음 예시를 보자.

```kotlin
fun yieldExample() {
  runBlocking {
    launch {
      log("1")
      yield()
      log("3")
      yield()
      log("5")
    }
    log("after first launch...")
    launch {
      log("2")
      delay(500L)
      log("4")
      delay(500L)
      log("6")
    }
    log("after second launch...")
  }
}

// main
log("main() started...")
yieldExample()
log("yieldExample() executed")
Thread.sleep(2000L)
log("main() terminated...")
```
```
01:56:28.478:Thread[main,5,main]: main() started...
01:56:28.546:Thread[main,5,main]: after first launch...
01:56:28.550:Thread[main,5,main]: after second launch...
01:56:28.551:Thread[main,5,main]: 1
01:56:28.552:Thread[main,5,main]: 2
01:56:28.557:Thread[main,5,main]: 3
01:56:28.557:Thread[main,5,main]: 5
01:56:29.055:Thread[main,5,main]: 4
01:56:29.559:Thread[main,5,main]: 6
01:56:29.560:Thread[main,5,main]: yieldExample() executed
01:56:31.560:Thread[main,5,main]: main() terminated...
```

다음 특징을 확인할 수 있다.
- launch는 즉시 반환된다.
- runBlocking은 내부 코루틴이 모두 끝난 다음에 반환된다.
- delay를 사용한 코루틴은 그 시간이 지날 때까지 다른 코루틴에게 실행을 양보한다.
  - 앞 코드에서 delay 대신 yield를 쓰면 숫자가 차례로 표시될 것이다.
  - 흥미로운 것 : 첫번째 코루틴이 두번이나 yield를 했지만 두번째 코루틴이 delay 상태에 있었기 때문에 
    다시 제어가 첫번째 코루틴에게 돌아왔다는 것

<br>

## E.1. 코루틴이란?


<br>

## E.1. 코루틴이란?


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