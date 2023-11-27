## 🤝 ****Storage-TIL****

> Spring Boot를 활용한 RESTful api 및 Thymeleaf를 사용해 개발한 TIL 공유 블로그입니다.
> 

- JWT와 REFRESH TOKEN을 활용해 소셜 로그인 기능을 추가하였습니다.
- 댓글과 좋아요 기능을 통해 사용자들이 서로의 학습 내용에 대해 피드백을 주고받을 수 있습니다.
- Github Action 과 Elastic Beanstalk를 사용해 배포 및 CI/CD 파이프라인을 구축하였습니다.
- 배포주소: http://spring-til-env.eba-t6wzaegc.ap-northeast-2.elasticbeanstalk.com/boards

  <img width="321" alt="스크린샷 2023-10-02 오후 4 46 47" src="https://github.com/kimihiqq/spring-til/assets/134909318/e2db7ee5-4a32-4693-b587-94fda00722cf">



---

### 🛠 개발 환경

<!-- 백엔드 -->
<img src="https://img.shields.io/badge/Java-007396?style=flat-square&logo=java&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat-square&logo=SpringBoot&logoColor=white"/></a>
<img src="https://img.shields.io/badge/JPA-007396?style=flat-square&logo=hibernate&logoColor=white"/></a>
<img src="https://img.shields.io/badge/JWT Authentication-000000?style=flat-square"/></a>

<!-- 프론트엔드 -->
<img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=Thymeleaf&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Bootstrap-563D7C?style=flat-square&logo=bootstrap&logoColor=white"/></a>

<!-- 인프라 -->
<img src="https://img.shields.io/badge/AWS Elastic Beanstalk-232F3E?style=flat-square&logo=amazonaws&logoColor=white"/></a>

<!-- CI/CD -->
<img src="https://img.shields.io/badge/Github Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white"/></a>


---

### 💡 구현 사항
 ### Board와 Comment 관계 구현

기본적으로 'Board'와 'Comment' 엔티티는 1:N(일대다) 관계를 가지고 있기에 다음과 같이 entity를 구성했습니다.

먼저 'Board' 엔티티에서 **`@OneToMany`** 어노테이션을 사용하여 이 관계를 정의하였습니다.

```java
@OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
@JsonIgnore
private List<Comment> comments = new ArrayList<>();
```

Comment' 엔티티에서는 **`@ManyToOne`** 어노테이션을 사용하여 이 관계를 정의합니다.

```java
@ManyToOne
@JoinColumn(name = "board_id", nullable = false)
private Board board;

```

- **cascade = CascadeType.ALL**: 이 옵션을 통해 Board 엔티티가 수행하는 모든 데이터베이스 연산(CRUD)이 관련된 Comment 엔티티에게도 적용되게끔 했습니다. 이를 통해 데이터 무결성을 유지하고자 했습니다.
- **orphanRemoval = true:** 해당 옵션을 통해  Board에서 Comment를 제거하는 경우, 즉  Comment 엔티티가 더 이상 연결되지 않은 경우  자동으로 데이터베이스에서 제거되도록 설정했습니다.
- **fetch = FetchType.LAZY:** Board 엔티티를 로드할 때 Comment 엔티티는 로드되지 않고, Comment 엔티티를 직접 사용하는 시점에서 로드됩니다. 모든 관련 엔티티를 한 번에 로드하지 않으므로 성능상의 이점이 있지만, `N+1 문제`가 발생할 우려가 있기에, 다음과 같이 `JOIN FETCH`를 사용하였습니다. 이를 통해 `N+1 문제`를 해결하면서 필요한 데이터만을 로드할 수 있게끔 하였습니다.
 
```java
@Query("SELECT distinct b FROM Board b LEFT JOIN FETCH b.comments")
List<Board> findAllWithComments();
```

- **양방향 연관관계 관리를 위한 연관관계 편의 메서드 작성**: “addComment”와 “removeComment”와 같이 엔티티 내 연관 관계 편의 메서드를 사용해  Board 엔티티에서 Comment 엔티티를 추가하거나 제거하는 동시에, Comment 엔티티의 Board 참조도 적절하게 업데이트 되게끔 수정하였습니다.

 ### 로그인 구현 및 Refresh 토큰 사용

현재 프로젝트에서는 OAuth2를 이용한 로그인 후 JWT를 생성하고, 이를 사용자에게 넘겨줍니다. 이 후 사용자는 이 JWT를 아래 코드와 같이 로컬 스토리지에 저장하게 됩니다.

```jsx
.then(result => { 
                    localStorage.setItem('access_token', result.accessToken);
                    httpRequest(method, url, body, success, fail);
                })
```

이후 JWT는 사용자가 요청을 보낼 때마다 헤더에 추가하여 인증을 수행합니다.

```jsx
method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    refreshToken: getCookie('refresh_token'),
```

액세스 토큰은 사용자를 인증하는 데 사용되기 때문에 XSS(크로스 사이트 스크립팅) 공격에 노출될 수 있습니다. 따라서 액세스 토큰과 Refresh 토큰을 분리하여 저장하는 전략을 사용하였습니다. 액세스 토큰의 유효 기간은 짧게 두어 공격자가 토큰을 획득하더라도 제한된 시간 내에서만 이용할 수 있게끔 하였고, Refresh 토큰을 쿠키에 저장해, 액세스 토큰이 만료된 경우 사용될 수 있게끔 하였습니다.
