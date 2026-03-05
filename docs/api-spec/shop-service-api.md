# Shop Service API Spec

- Service: `shop-service`
- Base URL: `${api.v1} = /api/v1`
- 공통 헤더(인증 필요 API): `Member-Id: <UUID>`
- 공통 에러 응답:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "에러 메시지"
}
```

## 1. Shop API

### 1.1 내 상점 목록 조회
- Method/Path: `GET /api/v1/shops`
- Headers: `Member-Id`
- Query: `page`, `size`, `sort` (기본 size=10, page=0, sort=createdAt)
- Response 200: `Page<ShopListResponse>`

`ShopListResponse`:
- `shopId`, `shopName`, `status`

상태값:
- 등록 플로우 기준: `ACTIVE`, `REGISTERING`, `REGISTER_FAILED`, `REGISTER_DEAD`
- 삭제 플로우 기준: `DELETED`, `DELETING`, `DELETE_FAILED`, `DELETE_DEAD`

### 1.2 내 상점 상세 조회
- Method/Path: `GET /api/v1/shops/{shopId}`
- Headers: `Member-Id`
- Response 200 (`ShopInfoResponse`):

```json
{
  "id": "UUID",
  "shopName": "가게명",
  "shopEmail": "shop@example.com",
  "shopPhoneNumber": "01012345678",
  "shopAddress": "서울시 ...",
  "status": "ACTIVE"
}
```

### 1.3 상점 등록
- Method/Path: `POST /api/v1/shops`
- Headers: `Member-Id`
- Request Body:

```json
{
  "shopEmail": "shop@example.com",
  "shopName": "가게명",
  "shopPhoneNumber": "01012345678",
  "shopRegistrationNumber": "123-45-67890",
  "shopAddress": "서울시 ..."
}
```

- Response 201: empty body
- 비동기 Saga 진행:
  - `MemberRoleChangeRequestedEvent(action=ADD_SELLER, sagaType=SHOP_REGISTRATION)` 발행
  - 이후 완료/실패/Dead 이벤트 소비로 최종 상태 확정

### 1.4 내 상점 정보 수정
- Method/Path: `PUT /api/v1/shops/{shopId}`
- Headers: `Member-Id`
- Request Body:

```json
{
  "shopEmail": "new-shop@example.com",
  "shopName": "새 가게명",
  "shopPhoneNumber": "01012345678",
  "shopAddress": "서울시 ..."
}
```

- Response 200: empty body

### 1.5 내 상점 삭제
- Method/Path: `DELETE /api/v1/shops/{shopId}`
- Headers: `Member-Id`
- Response 200: empty body
- 비동기 Saga 진행:
  - `MemberRoleChangeRequestedEvent(action=REMOVE_SELLER, sagaType=SHOP_DELETION)` 발행
  - 완료 시 `ShopDeletedEvent` 발행

## 2. Settlement API

### 2.1 정산 내역 조회
- Method/Path: `GET /api/v1/settlements/history`
- Query:
  - `shopId` (UUID)
  - `startDate` (`yyyy-MM`)
  - `endDate` (`yyyy-MM`)
  - `page` (default `0`)
- 제약: `startDate <= endDate`
- Response 200 (`SettlementListInfo`)

```json
{
  "pageInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "hasNext": true
  },
  "settlementList": [
    {
      "settlementId": "UUID",
      "targetYm": "2026.01",
      "status": "지급 완료",
      "salesAmount": 100000,
      "feeRate": 0.03,
      "feeAmount": 3000,
      "payoutAmount": 97000,
      "payoutDate": "2026.02.01 03:00:00"
    }
  ]
}
```

## 3. Internal API

### 3.1 상점 소유자 조회
- Method/Path: `GET /internal/shops/{shopId}/member-id`
- Response 200: `UUID`

### 3.2 회원 소유 상점 ID 목록 조회
- Method/Path: `GET /internal/shops/ids?memberId={memberId}`
- Response 200: `UUID[]`

### 3.3 정산 소스 적재
- Method/Path: `POST /internal/settlements/source`
- Request Body: `SettlementSourceItem[]`

```json
[
  {
    "productId": "UUID",
    "shopId": "UUID",
    "orderId": "UUID",
    "itemAmount": 35000,
    "createdAt": "2026-02-25T12:00:00"
  }
]
```

- Response 200: empty body

### 3.4 진행중 정산 여부 확인
- Method/Path: `POST /internal/settlements/in-progress`
- Request Body: `UUID[]` (shopId 리스트)
- Response 200:

```json
true
```

## 4. Kafka 통신 명세

### 4.1 통신 방식
- 유형: 비동기 이벤트 기반 통신 (Kafka Pub/Sub)
- 패턴: Saga 상태 전이 이벤트 + 도메인 이벤트 브로드캐스트
- Consumer Group: `shop-service`
- 재시도: `500ms * 3회`, 실패 시 `.dlt`

### 4.2 토픽별 통신 요약
- `shop-service.member-role-change-requested.v1` (Produce)
  - 통신 유형: Saga `Command` 이벤트 발행
  - 이벤트: `MemberRoleChangeRequestedEvent`
  - 시점: 상점 등록/삭제 요청 처리 시작

- `member-service.member-role-change-completed.v1` (Consume)
  - 통신 유형: Saga 성공 `Result` 이벤트 수신
  - 동작: 상점 등록/삭제 완료 처리

- `member-service.member-role-change-failed.v1` (Consume)
  - 통신 유형: Saga 실패 `Result` 이벤트 수신
  - 동작: 실패 상태 반영

- `member-service.member-role-change-dead.v1` (Consume)
  - 통신 유형: Saga 최종 실패 `Result` 이벤트 수신
  - 동작: Dead 상태 반영

- `member-service.member-deleted.v1` (Consume)
  - 통신 유형: 도메인 상태 변경 알림 이벤트 수신
  - 동작: 회원 소유 상점 일괄 삭제 처리

- `shop-service.shop-deleted.v1` (Produce)
  - 통신 유형: 도메인 상태 변경 알림 이벤트 발행
  - 이벤트: `ShopDeletedEvent`
  - 시점: 상점 삭제 완료 커밋 후
