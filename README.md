### 아키텍처 구성도

![아키텍처 구성도](https://github.com/qkr107781/hhplus_e-commerce_server/blob/feature/docs/4.Architecture.png)

### DIP를 어떻게 구현 했는가

- Application Layer가 제일 바깥에 있는 Persistence Layer에 의존성을 갖지 않도록 Interface를 생성하여 Persistence Layer에서 implements하여 구체화 하도록 했습니다.

### 의존성 방향(저수준 → 고수준)

- Persistence Layer
    - Repository Class는 Repository Interface를 구현하여 **DIP 적용**
- Presentation Layer
    - Controller Class는 **Usecase Interface에 의존**
- Application Layer
    - UseCase Interface를 implements한 Service는 **Domain Class에 의존**
    - 복잡한 로직은 구현한 Facade Service 또한 UseCase Interface를 implements 했고 각 도메인들의 Service를 호출하는 오케스트레이션 역할 및 일부 로직 작성
    - Repository Interface 생성하여 Repository Class에게 구현 강요하여 **의존성 제거**
- Domain Layer
    - 어떤 Layer에도 의존하지 않아 핵심 비지니스 로직은 독립적으로 존재
    - **이번 프로젝트에서는 Entity와 일원화하여 영속성 관련 이점과 유지보수 관리의 이점을 가져가려고 함

### Facade 패턴 적용

- Service: 단순한 로직의 UseCase(잔액, 상품, 선착순 쿠폰)
    - 단순 로직은 구조를 단순하게 하여 가독성을 높임
- Facade Service: 여러 도메인을 사용하는 복잡한 로직의 UseCase(주문, 결제)
    - Facade는 UseCase에 대해 여러 비지니스 로직을 호출하는 오케스트레이션 역할
    - 각 기능이 구현된 Service를 호출함으로써 관심사 분리로 Service들이 명확한 책임을 가질 수 있음
    - Service들이 명확한 책임을 가지게 되어 재사용성이 높고 복잡성이 낮아짐

### Presentation Layer(root package: /presentation/[Domain]/)

- ~~Controller.java
    - @Controller
    - 사용자 요청 처리 및 반환

### Application Layer(root package: /application/[Domain]/)

- dto/
    - ~~Request.java
    - ~~Response.java
- facade/
    - ~~FacadeService.java
        - @Service, @Transactional
        - ~~Usecase.java Interface implements하여 내부 로직 구현
        - 여러 도메인을 사용해야하는 복잡한 비지니스 로직 실행 시 생성(주문, 결제)
- service/
    - ~~Service.java
        - @Service, @Transactional
        - ~~Usecase.java Interface implements하여 내부 로직 구현
        - 단순한 비지니스 로직 실행 시 생성(잔액 충전/조회, 상품 조회, 인기 상품 조회, 선착순 쿠폰 발급, 본인 쿠폰 조회, 상태별 쿠폰 조회)
    - ~~UseCase.java
        - Controller에서 호출 할 Application Layer 비지니스 로직
        - interface로 생성하여 내부 구현이 변경되도 영향받지 않도록 함
- repository/
    - ~~Repository.java
        - DIP 적용하여 Persistence Layer에 접근

### Domain Layer(root package: /domain/[Domain]/)

- [Domain].java
    - 핵심 비지니스 로직이 들어갈 객체, Entity의 역할도 동시에 수행(영속성 관리와 유지보수 측면에서 유리함)
    - 빌더 패턴을 통해 객체화

### Persistence Layer(root package: /persistence/[Domain]/)

- ~~Adapter.java
    - Application Layer ~~Repository.java interface 구현체로 DIP 적용을 위함
- ~~JpaRepository.java
    - JpaRepositoy를 extends하여 JPA CRUD 사용



## 프로젝트

## Getting Started

### Prerequisites

#### Running Docker Containers

`local` profile 로 실행하기 위하여 인프라가 설정되어 있는 Docker 컨테이너를 실행해주셔야 합니다.

```bash
docker-compose up -d
```
