# Member Service API Spec

- Service: `member-service`
- Base URL: `${api.v1} = /api/v1`
- 공통 헤더(인증 필요 API): `Member-Id: <UUID>`
- 공통 에러 응답:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "에러 메시지"
}
```

## 1. Auth API

### 1.1 OAuth 로그인
- Method/Path: `POST /api/v1/auth/oauth/login`
- Request Body:

```json
{
  "provider": "google",
  "providerCode": "oauth-code",
  "redirectUrl": "https://example.com/callback"
}
```

- Response 200 (`LoginInfoResponse`):

```json
{
  "memberInfo": {
    "id": "UUID",
    "name": "홍길동",
    "nickname": "길동"
  },
  "accessToken": "jwt",
  "refreshToken": "jwt",
  "temporaryToken": null
}
```

- 신규 회원 후보일 때:

```json
{
  "memberInfo": null,
  "accessToken": null,
  "refreshToken": null,
  "temporaryToken": "temporary-token"
}
```

### 1.2 OAuth 회원가입
- Method/Path: `POST /api/v1/auth/oauth/register`
- Request Body:

```json
{
  "temporaryToken": "temporary-token",
  "email": "user@example.com",
  "name": "홍길동",
  "nickname": "길동",
  "phoneNumber": "01012345678",
  "address": "서울시 ..."
}
```

- Response 201: `LoginInfoResponse` (로그인 응답과 동일 구조)

### 1.3 이메일 인증 코드 전송
- Method/Path: `POST /api/v1/auth/email/send`
- Request Body:

```json
{
  "temporaryToken": "temporary-token",
  "email": "user@example.com"
}
```

- Response 200: empty body

### 1.4 이메일 인증 코드 확인
- Method/Path: `POST /api/v1/auth/email/verify`
- Request Body:

```json
{
  "email": "user@example.com",
  "verificationCode": "123456"
}
```

- Response 200: empty body

### 1.5 토큰 재발급
- Method/Path: `POST /api/v1/auth/refresh-token`
- Request Body:

```json
{
  "refreshToken": "jwt"
}
```

- Response 200 (`TokenResponse`):

```json
{
  "accessToken": "new-access-token",
  "refreshToken": "new-refresh-token"
}
```

### 1.6 로그아웃
- Method/Path: `POST /api/v1/auth/logout`
- Headers: `Member-Id`
- Response 200: empty body

## 2. Member API

### 2.1 내 정보 조회
- Method/Path: `GET /api/v1/members/me`
- Headers: `Member-Id`
- Response 200 (`MemberInfoResponse`):

```json
{
  "id": "UUID",
  "email": "user@example.com",
  "name": "홍길동",
  "nickname": "길동",
  "phoneNumber": "01012345678",
  "address": "서울시 ..."
}
```

### 2.2 내 정보 수정
- Method/Path: `PUT /api/v1/members/me`
- Headers: `Member-Id`
- Request Body:

```json
{
  "name": "홍길동",
  "nickname": "길동2",
  "phoneNumber": "01012345678",
  "address": "서울시 ..."
}
```

- Response 200: `MemberInfoResponse`

### 2.3 회원 탈퇴
- Method/Path: `DELETE /api/v1/members/me`
- Headers: `Member-Id`
- Response 200: empty body
- 후속 이벤트: `MemberDeletedEvent` Kafka 발행

## 3. Admin Member API

### 3.1 회원 목록 조회
- Method/Path: `GET /api/v1/admin/members`
- Headers: `Member-Id` (관리자)
- Query: `page`, `size`, `sort`
- Response 200: `Page<MemberInfoAdminResponse>`

`MemberInfoAdminResponse`:
- `id`, `email`, `name`, `nickname`, `phoneNumber`, `address`
- `roles: string[]`
- `status`
- `createdAt`

### 3.2 회원 역할 목록 조회
- Method/Path: `GET /api/v1/admin/members/roles`
- Response 200:

```json
{
  "roles": ["USER", "SELLER", "ADMIN"]
}
```

### 3.3 회원 상태 목록 조회
- Method/Path: `GET /api/v1/admin/members/statuses`
- Response 200:

```json
{
  "statuses": ["ACTIVE", "BANNED", "DELETED"]
}
```

### 3.4 회원 상태 변경
- Method/Path: `PATCH /api/v1/admin/members/{memberId}/status`
- Headers: `Member-Id` (관리자)
- Body:

```json
{
  "status": "BANNED"
}
```

- 허용 상태: `ACTIVE`, `BANNED`, `DELETED`
- Response 200: empty body

## 4. Inquiry API

### 4.1 내 문의 목록 조회
- Method/Path: `GET /api/v1/inquiries`
- Headers: `Member-Id`
- Query: `page`, `size`, `sort` (기본: `createdAt,DESC`)
- Response 200: `Page<InquiryListResponse>`

`InquiryListResponse`:
- `id`, `title`, `status`, `inquiryCategory`

### 4.2 내 문의 상세 조회
- Method/Path: `GET /api/v1/inquiries/{inquiryId}`
- Headers: `Member-Id`
- Response 200 (`InquiryInfoResponse`)
- 필드: `id`, `memberId`, `title`, `message`, `inquiryCategory`, `status`, `createdAt`, `modifiedAt`, `inquiryAnswer`

### 4.3 문의 등록
- Method/Path: `POST /api/v1/inquiries`
- Headers: `Member-Id`
- Body:

```json
{
  "title": "배송 문의",
  "message": "언제 오나요?",
  "inquiryCategory": "SHIPPING"
}
```

- 허용 카테고리: `PRODUCT`, `SUBSCRIPTION`, `SHIPPING`, `PAYMENT`, `ACCOUNT`, `ETC`
- Response 201: empty body

### 4.4 문의 수정
- Method/Path: `PUT /api/v1/inquiries/{inquiryId}`
- Headers: `Member-Id`
- Body: 문의 등록과 동일
- Response 200: empty body

### 4.5 문의 삭제
- Method/Path: `DELETE /api/v1/inquiries/{inquiryId}`
- Headers: `Member-Id`
- Response 200: empty body

## 5. Inquiry Admin API

### 5.1 문의 목록 조회(관리자)
- Method/Path: `GET /api/v1/admin/inquiries`
- Query: `status`(optional), `page`, `size`, `sort`
- `status`: `RECEIVED`, `IN_PROGRESS`, `ANSWERED`
- Response 200: `Page<InquiryListResponse>`

### 5.2 문의 상세 조회(관리자)
- Method/Path: `GET /api/v1/admin/inquiries/{inquiryId}`
- Response 200: `InquiryInfoResponse`

### 5.3 문의 답변 등록
- Method/Path: `POST /api/v1/admin/inquiries/{inquiryId}/answer`
- Headers: `Member-Id` (관리자)
- Body:

```json
{
  "message": "답변 내용"
}
```

- Response 201: empty body

### 5.4 문의 답변 수정
- Method/Path: `PUT /api/v1/admin/inquiries/{inquiryId}/answer`
- Headers: `Member-Id` (관리자)
- Body: 답변 등록과 동일
- Response 200: empty body

### 5.5 문의 답변 삭제
- Method/Path: `DELETE /api/v1/admin/inquiries/{inquiryId}/answer`
- Response 200: empty body

## 6. Endpoint Admin API

### 6.1 엔드포인트 권한 목록 조회
- Method/Path: `GET /api/v1/admin/endpoints`
- Query: `page`, `size`, `sort` (기본 sort: `pathPattern,httpMethod`)
- Response 200: `Page<EndPointInfoResponse>`

`EndPointInfoResponse`:
- `id`, `role`, `httpMethod`, `pathPattern`

### 6.2 엔드포인트 권한 등록
- Method/Path: `POST /api/v1/admin/endpoints`
- Body:

```json
{
  "role": "ADMIN",
  "httpMethod": "GET",
  "pathPattern": "/api/v1/admin/**"
}
```

- Response 200: empty body

### 6.3 엔드포인트 권한 수정
- Method/Path: `PUT /api/v1/admin/endpoints/{endPointId}`
- Body: 등록과 동일
- Response 200: empty body

### 6.4 엔드포인트 권한 삭제
- Method/Path: `DELETE /api/v1/admin/endpoints/{endPointId}`
- Response 200: empty body

### 6.5 권한 캐시 갱신
- Method/Path: `GET /api/v1/admin/endpoints/cache/refresh`
- Response 200: empty body

## 7. Internal API

### 7.1 인가 확인
- Method/Path: `POST /internal/auth/authorize`
- Body:

```json
{
  "memberId": "UUID",
  "httpMethod": "GET",
  "path": "/api/v1/shops"
}
```

- Response 200: `true | false`

### 7.2 회원 이메일 조회
- Method/Path: `GET /internal/members/{memberId}/email`
- Response 200: `"user@example.com"`

### 7.3 회원 닉네임 조회
- Method/Path: `GET /internal/members/nickname`
- Headers: `Member-Id`
- Response 200: `"nickname"`

## 8. Kafka 통신 명세

### 8.1 통신 방식
- 유형: 비동기 이벤트 기반 통신 (Kafka Pub/Sub)
- 패턴: Saga 보상/상태전이 이벤트
- Consumer Group: `member-service`
- 재시도: `500ms * 3회`, 실패 시 `.dlt`

### 8.2 토픽별 통신 요약
- `shop-service.member-role-change-requested.v1` (Consume)
  - 통신 유형: Saga `Command` 이벤트 수신
  - 이벤트: `MemberRoleChangeRequestedEvent`
  - 동작: SELLER 권한 추가/제거 처리

- `shop-service.member-role-change-requested.v1.dlt` (Consume)
  - 통신 유형: 실패 이벤트 후처리(DLT)
  - 동작: `MemberRoleChangeDeadEvent` 변환 발행

- `member-service.member-role-change-completed.v1` (Produce)
  - 통신 유형: Saga 성공 `Result` 이벤트
  - 이벤트: `MemberRoleChangeCompletedEvent`

- `member-service.member-role-change-failed.v1` (Produce)
  - 통신 유형: Saga 실패 `Result` 이벤트
  - 이벤트: `MemberRoleChangeFailedEvent`

- `member-service.member-role-change-dead.v1` (Produce)
  - 통신 유형: Saga 최종 실패 `Result` 이벤트
  - 이벤트: `MemberRoleChangeDeadEvent`

- `member-service.member-deleted.v1` (Produce)
  - 통신 유형: 도메인 상태 변경 알림 이벤트
  - 이벤트: `MemberDeletedEvent`
