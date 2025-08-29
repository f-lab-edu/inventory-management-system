# Warehouse Management API

창고 관리 REST API 모듈입니다.

## API 문서

### OpenAPI 스펙

- **파일 위치**: `src/main/resources/openapi.yaml`
- **버전**: OpenAPI 3.0.3
- **형식**: YAML

### API 문서 확인 방법

#### OpenAPI 파일 직접 확인

`src/main/resources/openapi.yaml` 파일을 다음 도구들로 확인 가능:

- [Swagger Editor](https://editor.swagger.io/)
- [Redoc](https://redocly.github.io/redoc/)

## API 엔드포인트

### 기본 URL

- **개발 환경**: `http://localhost:10001`

### 주요 엔드포인트

| 메서드    | 경로                        | 설명                |
|--------|---------------------------|-------------------|
| POST   | `/api/v1/warehouses`      | 창고 생성             |
| GET    | `/api/v1/warehouses`      | 창고 목록 조회 (페이지네이션) |
| GET    | `/api/v1/warehouses/{id}` | 창고 상세 조회          |
| PUT    | `/api/v1/warehouses/{id}` | 창고 정보 수정          |
| DELETE | `/api/v1/warehouses/{id}` | 창고 삭제             |

## 데이터 모델

### CreateWarehouseRequest

창고 생성 시 필요한 요청 데이터

```json
{
  "name": "서울 중앙 창고",
  "postcode": "12345",
  "baseAddress": "서울특별시 강남구 테헤란로 123",
  "detailAddress": "456동 789호",
  "managerName": "김관리",
  "managerContact": "010-1234-5678"
}
```

### WarehouseResponse

창고 정보 응답 데이터

```json
{
  "id": 1,
  "name": "서울 중앙 창고",
  "postcode": "12345",
  "baseAddress": "서울특별시 강남구 테헤란로 123",
  "detailAddress": "456동 789호",
  "managerName": "김관리",
  "managerContact": "010-1234-5678"
}
```

### PageResponse

페이지네이션 응답 데이터

```json
{
  "content": [
    ...
  ],
  "currentPageNumber": 0,
  "pageSize": 10,
  "totalElements": 25,
  "totalPages": 3,
  "hasNext": true,
  "hasPrevious": false
}
```

## 유효성 검증 규칙

### CreateWarehouseRequest

- `name`: 필수, 1-100자
- `postcode`: 필수, 5자리 숫자 (정규식: `^\d{5}$`)
- `baseAddress`: 필수, 최대 200자
- `detailAddress`: 선택, 최대 100자
- `managerName`: 필수, 1-50자
- `managerContact`: 필수

### UpdateWarehouseRequest

- 모든 필드가 선택사항
- 전송하지 않은 필드는 기존 값 유지
- 전송된 필드는 CreateWarehouseRequest와 동일한 유효성 검증 적용

## HTTP 상태 코드

| 코드  | 설명                 |
|-----|--------------------|
| 200 | 성공 (조회, 수정)        |
| 201 | 성공 (생성)            |
| 204 | 성공 (삭제)            |
| 400 | 잘못된 요청 (유효성 검증 실패) |
| 404 | 리소스 없음             |

## 에러 응답 형식

```json
{
  "status": "BAD_REQUEST",
  "message": "창고명은 필수입니다",
  "data": null
}
```

## 페이지네이션

### 쿼리 파라미터

- `currentPageNumber`: 현재 페이지 번호 (기본값: 0)
- `pageSize`: 페이지당 항목 수 (기본값: 10, 최대: 100)

### 예시

```
GET /api/v1/warehouses?currentPageNumber=0&pageSize=20
```

## 개발 환경 설정

### 필수 요구사항

- Java 17+
- Spring Boot 3.x
- Gradle 7.x+

### Postman 컬렉션

Postman에서 OpenAPI 파일을 import하여 테스트 가능:

1. Postman에서 "Import" 클릭
2. `openapi.yaml` 파일 선택
3. 자동으로 컬렉션 생성됨

## 참고 사항

- 현재 구현은 인메모리 저장소를 사용 (실제 운영 환경에서는 데이터베이스 연동 필요)
- 인증/인가 기능은 별도 구현 필요
- 로깅, 모니터링, 헬스체크 등 운영 관련 기능은 별도 구현 필요
