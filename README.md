# 🧾 Delivery Manager Service

배송담당자 생성/조회/배정/삭제를 담당하는 마이크로서비스입니다.

---

## 📌 서비스 정보

- **Service name**: `delivery-manager-service` (`spring.application.name`)
- **Port**: `8084` (`project-configs/configs/delivery-manager-service/delivery-manager-service.yml`)
- **External API base path**: `/api/v1/delivery-managers`
- **Internal API base path**: `/internal/v1/delivery-managers` (서비스 간 통신 전용, Gateway 라우팅 제외)

---

## 🏗️ API 엔드포인트 요약

### External (클라이언트 → Gateway → delivery-manager-service)

- `POST /api/v1/delivery-managers` 배송담당자 생성
- `GET /api/v1/delivery-managers` 배송담당자 목록 조회
- `GET /api/v1/delivery-managers/{deliveryManagerId}` 배송담당자 단건 조회
- `DELETE /api/v1/delivery-managers/{deliveryManagerId}` 배송담당자 삭제(소프트/권한 정책에 따름)
- `GET /api/v1/delivery-managers/me` 배송담당자 본인 조회
- `PATCH /api/v1/delivery-managers/me/deliveries/{deliveryId}/complete` 배송담당자 배송 완료

### Internal (서비스 ↔ 서비스)

- `POST /internal/v1/delivery-managers/assignments` 배송담당자 배정

---

## 🔐 인증/헤더 규칙 (Gateway 사용 시)

Gateway는 JWT 검증 후 아래 헤더를 하위 서비스로 전달합니다.

- `X-User-Id`: 사용자 UUID (Keycloak `sub`)
- `X-User-Email`: 사용자 이메일
- `X-User-Role`: 사용자 권한 (예: `ADMIN`, `HUB_ADMIN`, `DELIVERY`, `COMPANY`)

클라이언트(Postman)는 Gateway로 요청할 때 보통 아래만 넣으면 됩니다.

```http
Authorization: Bearer {JWT_TOKEN}
```

> `delivery-manager-service` 컨트롤러는 `X-User-Id`/`X-User-Role` 헤더를 사용합니다.  
> (Gateway가 자동 주입. delivery-manager-service를 직접 호출하는 경우에는 Postman에서 직접 넣어야 합니다.)

---

## 🧪 Postman 테스트 (복붙용)

### 1) Gateway로 호출 (추천)

**Base URL**

```text
http://localhost:8080
```

#### 배송담당자 생성

```text
POST http://localhost:8080/api/v1/delivery-managers
```

Headers:

```text
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>
```

Body:

```json
{
  "targetUserId": "123e4567-e89b-12d3-a456-426614174000",
  "type": "HUB"
}

```

#### 배송담당자 목록 조회

```text
GET http://localhost:8080/api/v1/delivery-managers?page=0&size=10&sortBy=createdAt
```

Headers:

```text
Authorization: Bearer <JWT_TOKEN>
```

---

### 2) delivery-manager-service 직접 호출 (개발/디버깅용)

**Base URL**

```text
http://localhost:8084
```

#### 배송담당자 생성

```text
POST http://localhost:8084/api/v1/delivery-managers
```

Headers:

```text
Content-Type: application/json
X-User-Id: <USER_UUID>
```

#### 배송담당자 목록/단건 조회

```text
GET http://localhost:8084/api/v1/delivery-managers?page=0&size=10&sortBy=createdAt
GET http://localhost:8084/api/v1/delivery-managers/<DELIVERY_MANAGER_ID_UUID>
```

Headers:

```text
X-User-Id: <USER_UUID>
X-User-Role: ADMIN
```

> `X-User-Role` 가능한 값: `ADMIN | HUB_ADMIN`


---

## 🧩 의존 서비스

배송담당자 생성 플로우에서 외부 서비스 호출이 포함되어 있습니다(Feign).

- `user-service`: 배송매니저 사용자 정보 조회

> 위 서비스들이 내려가 있으면 배송담당자 생성 시 실패할 수 있습니다.

---

## 실행 전제
아래 인프라가 먼저 실행되어 있어야 합니다.
- Config Server
- Eureka Server
- PostgreSQL
- Redis
- Keycloak
- Zipkin

위 인프라는 infra-repo에서 실행할 수 있습니다.

---
