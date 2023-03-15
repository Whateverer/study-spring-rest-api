# study-spring-rest-api
인프런 백기선 스프링 기반 REST API 개발

# REST API 및 프로젝트 소개 

## 그런 REST API로 괜찮은가

### REST
"Representational State Transfer"

### API
XML-RPC -> Salesforce API -> filcker API

"REST APIs must be hypertext-driven"
"REST API를 위한 최고의 버저닝 전략은 버저닝을 안 하는 것"

### REST API    
REST 아키텍쳐 스타일을 따르는 API

### REST    
분산 하이퍼미디어 시스템(예: 웹)을 위한 아키텍쳐 스타일(제약조건의 집합)

제약조건들을 모두 지켜야 REST라고 할 수 있다.

### REST를 구성하는 스타일
- client-server
- stateless
- cache
- **uniform interface**
- layered system
- code-on-demand(optional) - 서버에서 코드를 클라이언트에 보내서 실행할 수 있어야 한다. ex. JavaScript

### Uniform Interface의 제약조건
- identification of resource : resource가 URI로 식별되면 된다.
- manipulation of resources through representations : representation 전송을 통해서 resource를 조작해야 한다.
- **self-descriptive messages** : 메시지는 스스로를 설명해야한다.
- **hypermedia as the engine of application state (HATEOAS)**

#### Self-descriptive message
```
GET /HTTP/1.1
```
-> self-descriptvie 하지 못하다. (목적지가 빠져있음)

```
GET /HTTP/1.1
Host : www.example.org
```
-> 목적지를 추가하면 이제 self-descriptive

또, Content-Type header가 반드시 들어있어야 한다.

#### HATEOAS
하이퍼링크를 통해 그 다음 상태로 전이가 가능한 것

왜 Uniform Interface인가??

#### 독립적 진화
- 서버와 클라이언트가 각각 독립적으로 진화한다.
- 서버의 기능이 변경되어도 클라이언트를 업데이트할 필요가 없다.
- REST를 만들게 된 계기 : "How do I improve HTTP without breaking the Web."

#### 웹
- 웹 페이지를 변경했다고 웹 브라우저를 업데이트 할 필요는 없다.
- 웹 브라우저를 업데이트했다고 웹 페이지를 변경할 필요도 없다.
- HTTP 명세가 변경되어도 웹은 잘 동작한다.
- HTML 명세가 변경되어도 웹은 잘 동작한다.

#### Self-descriptive
확장 가능한 커뮤니케이션    
서버나 클라이언트가 변경되더라도 오고가는 메시지는 언제나 self-descriptive(스스로 명세가 가능) 하므로 언제나 해석이 가능하다.

#### HATEOAS
애플리케이션 상태 전이의 late binding    
어디서 어디로 전이가 가능한지 미리 결정되지 않는다. 어떤 상태로 전이가 완료되고 나서야 그 다음 전이될 수 있는 상태가 결정된다.
쉽게말해서 링크는 동적으로 변경될 수 있다. (서버에서 동적으로 다음에 이동할 링크를 설정할 수 있다.)

**정리**    
- 오늘날 대부분의 "REST API"는 사실 REST를 따르지 않고 있다.
- REST의 제약조건 중에서 특히 **Self-descriptive와 HATEOAS**를 잘 만족하지 못한다.
- REST는 **긴 시간에 걸쳐(수십년) 진화**하는 웹 애플리케이션을 위한 것이다.
- REST를 따를 것인지는 API를 설계하는 이들이 스스로 판단하여 결정해야 한다.
- REST를 따르겠다면, **Self-descriptive와 HATEOAS**를 만족시켜야 한다.
	+ Self-descriptive는 **custom media type**이나 **profile link relation** 등으로 만족시킬 수 있다.
	+ HATEOAS는 HTTP 헤더나 본문에 **링크**를 담아 만족시킬 수 있다.
- REST를 따르지 않겠다면, "REST를 만족하지 않는 REST API"를 뭐라고 부를지 결정해야 할 것이다.


## Event Project

### Event domain 구현
```java
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {
```
- 왜 @EqualsAndHashCode에서 of를 사용하는가?    
: Equals와 hashCode를 사용할 때 모든 필드를 기본으로 사용한다. 그런데 필드 간 연관관계가 상호참조일 때, Equlas와 hashCode를 구현한 코드에서 stackoverflow가 발생할 수 있기 때문, 그래서 주로 id의 값만 가지고 equals와 hashCode를 비교하도록 만들게 해놨다.
원한다면 다른 몇 가지의 필드도 추가할 수 있다.
- 왜 @Builder를 사용할 때 @AllArgsConstructor가 필요한가?    
: Builder가 모든 필드에 접근하려면 모든 필드가 들어간 생성자가 필요하다.
- 애노테이션을 줄일 수 없나?    
: 커스텀 애노테이션을 만들어서 사용할 수 있지만, lombok은 메타애노테이션을 지원하지 않는다. -> 줄일 수 있는 방법이 없다.
- @Data를 쓰지 않는 이유    
: Entity에 @Data를 쓰면 상호참조때문에 stackoverflow 예외가 발생할 수 있기때문.
