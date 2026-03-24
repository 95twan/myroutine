# MyRoutin (마이루틴)

판매자는 자유롭게 가게(Shop)를 개설하여 상품을 판매하고, 구매자는 필요한 상품을 단건 구매하거나 정기 구독할 수 있는 오픈마켓 플랫폼 입니다.

## 프로젝트 구조
___
이 프로젝트는 마이크로서비스 아키텍처(MSA)로 구성되어 있으며, 각 서비스의 역할은 다음과 같습니다.

**인프라**

| 서비스               | 포트          | 설명                |
|-------------------|-------------|-------------------|
| **redis**         | 6379        | 데이터 캐싱            |
| **rabbitmq**      | 5672, 15672 | 이메일 전송     |
| **pgvector**      | 5432        | 데이터베이스 + 유사도 검색   |
| **elasticsearch** | 9200        | 검색                |
| **nginx**         | 80, 443     | 프록시 + SSL         |
| **kafka**         | 9092, 9093  | 서비스 간 비동기 통신 |

**Spring Cloud**

| 서비스                 | 포트   | 설명                                |
|---------------------|------|-----------------------------------|
| **apigateway**      | 8000 | API Gateway Service (진입점)         |
| **discovery**       | 8761 | Service Discovery (Eureka Server) |
| **config**          | 8888 | Config Server (로컬 환경에서만 사용)       |

**비즈니스**

| 서비스                 | 포트   | 설명                               |
|---------------------|------|----------------------------------|
| **member-service**  | 8081 | 회원 및 상점 관리 서비스                   |
| **batch-service**   | 8089 | 대용량 배치 작업 서비스                    |
| **catalog-service** | 8082 | 상품 및 재고 관리 서비스                   |
| **order-service**   | 8083 | 주문 처리 및 구독 관리 서비스                |
| **payment-service** | 8088 | PG 결제 처리 서비스                     |
| **support-service** | 8087 | 보조 서비스(알림, 추천, 리뷰, 검색)           |
| **wallet-service**  | 8084 | 포인트 및 지갑 관리 서비스                  |



## 프로젝트 아키텍처


## 기술 스택
- **Language**: Java 17
- **Framework**: Spring Boot 3.5.8
- **Build Tool**: Gradle 8.14.3
- **Spring Cloud**: 
  - Spring Cloud Gateway
  - Spring Cloud Config
  - Eureka (Discovery)
  - OpenFeign
- **Spring Boot**:
  - Spring Security
  - Spring Batch
  - Spring Ai
- **Database**:
  - PostgreSQL 17 (pgvector)
  - Spring Data JPA
- **Cache**: Redis 7
- **Message Queue**:
  - Kafka 7.6.1 (KRaft Mode)
  - RabbitMQ 3.13
- **Search**: Elasticsearch 8.15.3
- **Proxy**: Nginx (NPM)
- **OAuth**:
  - Kakao
  - Google
- **PG**: TossPayments
- **AI**: OpenAi
- **Infra**:
  - AWS EC2
  - AWS S3
- **Container**:
  - Docker
  - Kubernetes(K3S)
- **CI/CD**: Github Actions

## 서비스 기능
- **회원**: 회원가입, 로그인, 회원 정보 관리
- **상품**: 상품 등록, 조회, 검색
- **주문**: 주문 생성, 조회, 주문 취소, 주문 환불
- **구독**: 정기 구독 신청, 해지, 갱신
- **정산**: 판매자 정산, 수수료 계산
- **지갑**: 예치금 충전, 사용, 환불

## API 명세 - Swagger
API 문서는 Swagger UI를 통해 확인할 수 있습니다.
- URL: [http://localhost:8000/webjars/swagger-ui/index.html](http://localhost:8000/webjars/swagger-ui/index.html)

