# study-spring-rest-api
인프런 백기선 스프링 기반 REST API 개발

# 1. REST API 및 프로젝트 소개 

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

## Event domain 구현
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

# 2. 이벤트 생성 API 개발 
## 이벤트 API 테스트 클래스 생성
스프링 부트 슬라이스 테스트 
@MockMvc
- 스프링 MVC 테스트 핵심 클래스 
- 웹 서버를 띄우지 않고도 스프링 MVC(DispatcherServlet)가 요청을 처리하는 과정을 확인할 수 있기 때문에 컨트롤러 테스트용으로 자주 쓰임
- 가짜 DispatcherServlet을 만들어서 임의의 요청을 보내 응답을 확인할 수 있게 함.

```java
mockMvc.perform(요청)
```
## 201 응답 받기
## Event Repository 구현
@MockBean 
- Mockito를 사용해서 mock 객체를 만들고 빈으로 등록해줌.
- (주의) 기존 빈을 테스트용 빈이 대체한다.

MockMvc를 이용해서 기존의 Mock 웹이 있기 때문에 EventRepository를 이용한 테스트가 실패 -> @MockBean으로 EventRepository를 넣어준다.

## 입력값 이외에 에러 발생
ObjectMapper 커스터마이징    
ObjectMapper 확장 기능 사용    
json을 객체로 변환시키는 것 : deserialization    
객체를 json으로 변환시키는 것 : serialization

application.properties에     
spring.jackson.deserialization.fail-on-unknown-properties=true 속성 추가    
: 받을 수 없는 필드를 가졌을 때 fail 시키는 것

## Bad Request 처리
@Valid와 BindingResult(또는 Errors)
- BindingResult는 항상 @Valid 바로 다음 인자로 사용해야 함. (스프링 MVC)
- @NotNull, @NotEmpty, @Min, @Max, ... 사용해서 입력값 바인딩할 때 에러 확인할 수 있음

도메인 Validator 만들기
- Validator 인터페이스 사용하기
- 없이 만들어도 상관없음

## 매개변수를 이용한 테스트 

테스트에서 중복 제거    
JUnitParams（Junit4 이용 시）    
JUnit Jupiter Params（Junit5 이용 시)    

테스트에 Parameter를 주어 중복된 코드를 줄일 수 있다.    
테스트에 @ParameterizedTest 애노테이션 추가,    
- 파라미터를 직접 입력 : @CsvSource 애노테이션에 다음과 같이 파라미터 설정    
```java
@CsvSource({
            "0, 0, true",
            "100, 0, false",
            "0, 100, false"
    })
```
- 파라미터를 메서드로 만들어 입력 : @MethodSource("parametersForOffline") 애노테이션 추가,    
``` java 
    private static Stream<Arguments> parametersForOffline() {
        return Stream.of(
                Arguments.of("강남", true),
                Arguments.of(null, false),
                Arguments.of("      ", false)
        );
    }
```

참고 : https://medium.com/techwasti/junit5-parameterized-tests-dc9b90afdc74

# 3. HATEOAS와 Self-DescriptiveMessage 적용
## 스프링 HATEOAS 소개
스프링 HATEOAS
- 링크 만드는 기능
- 리소스 만드는 기능
	+ 리소스 : 데이터 + 링크
- 링크 찾아주는 기능 
	+ Traverson
	+ LinkDiscoverers
- 링크 
	+ href
	+ rel
		* self
		* profile
		* ...
		
HyperMedia를 통해서 동적으로 정보를 주고 받을 수 있어야 한다.

## 스프링 HATEOAS 적용
```java
public class EventResource extends RepresentationModel {
}
```
EventResource를 만들어준다 (링크를 추가하기 위해)

EventResource 만들기    
- extends RepresentationModel의 문제     
: 다시 테스트를 돌려보면 event따로 link영역이 따로 있는 것을 볼 수 있다.    
event로 구분하고 싶지 않다면 @JsonUnwrapped로 해결
	+ @JsonUnwrapped로 해결
	+ extends EntityModel<Event>

## 스프링 REST Docs 소개
Asciidoctor를 사용해 plain text파일을 html로 변환해준다.
REST Docs 자동 설정
- @AutoConfigureRestDocs

REST Docs 코딩
- andDo(document("doc-name", snippets))
- snippets
	+ links()
	+ requestParameters() + parameterWithName()
	+ pathParameters() + parametersWithName()
	+ requestParts() + partWithname()
	+ requestPartBody()
	+ requestPartFields()
	+ requestHeaders() + headerWithName()
	+ requestFields() + fieldWithPath()
	+ responseHeaders() + headerWithName()
	+ responseFields() + fieldWithPath()
- Relaxed
- Processor
	+ preprocessRequest(prettyPrint())
	+ preprocessResponse(prettyPrint())
	
문서 생성하기 
- mvc package
	+ test
	+ prepare-package :: proccess-asciidoc
	+ prepare-package :: copy-resources
- 문서 확인
	+ /docs/index.html

RestDocMockMvc 커스터마이징
- RestDocsMockMvcConfigurationCustomizer 구현한 빈 등록
- @TestConfiguration

## 스프링 REST Docs 각종 문서 조각 생성하기
Relaxed 접두어
- 장점 : 문서 일부분만 테스트 할 수 있다.
- 단점 : 정확한 문서를 생성하지 못한다.    
=> 권장하지 않음, 나중에 문서가 바뀌었을 때 알아차리지 못할 수 있음

## 테스트용 DB와 설정 분리하기
```
docker run --name rest -p 5432:5432 -e POSTGRES_PASSWORD=pass -d postgres
```
애플리케이션 설정과 테스트 설정 중복 어떻게 줄일 것인가?
- 프로파일과 @ActiveProfiles 활용 (test하는 Controller에 애노테이션 추가)


