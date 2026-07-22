# Hệ thống quản lý khóa học (Course Management System)

[![CI](https://github.com/hmddevv/CourseManagementSystem/actions/workflows/ci.yml/badge.svg)](https://github.com/hmddevv/CourseManagementSystem/actions/workflows/ci.yml)

> Đồ án cuối kỳ — **Lập trình ứng dụng với Java (14113014)** — HK3 2025-2026
> Đề tài STT 4: *Hệ thống quản lý khóa học*.

Ứng dụng Spring Boot hoàn chỉnh: **Backend RESTful API + Cơ sở dữ liệu + Frontend** tích hợp API.

---

## 1. Công nghệ sử dụng

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Java 17 |
| Framework | Spring Boot 3.4 (Web, Data JPA, Validation, Actuator) |
| ORM | Hibernate / JPA |
| CSDL | H2 (profile `dev`) · MySQL 8 (profile `prod`) |
| Tài liệu API | springdoc-openapi (Swagger UI) |
| Build | Maven (kèm Maven Wrapper `mvnw`) |
| Đóng gói | Docker (multi-stage) + Docker Compose |
| CI | GitHub Actions |
| Frontend | HTML + CSS + JavaScript thuần (Fetch API) |

---

## 1b. Tính năng nâng cao

| Tính năng | Mô tả | Cơ chế Spring |
|---|---|---|
| **Đánh giá & xếp hạng** | Học viên đã ghi danh chấm 1–5 sao kèm nhận xét; điểm trung bình tính bằng truy vấn gộp, có bảng xếp hạng | JPA aggregate query, ràng buộc `UNIQUE(student_id, course_id)` |
| **Chứng chỉ hoàn thành** | Đạt 100% tiến độ → cấp chứng chỉ với mã tra cứu duy nhất, cấp trong cùng transaction, không cấp trùng | Factory Pattern, `@Transactional`, `@OneToOne` |
| **Nhắc học định kỳ** | 8h sáng mỗi ngày quét ghi danh không hoạt động quá 7 ngày | `@Scheduled` + `@EnableScheduling` |
| **Cache** | Danh mục và thống kê dashboard được cache, tự mất hiệu lực khi có thao tác ghi | `@Cacheable` / `@CacheEvict` |
| **Nhật ký thao tác** | Mọi thao tác ghi ở tầng Service được ghi lại tự động, không chèn code vào Service | Spring AOP `@Aspect` + `@AfterReturning` |
| **Audit người thao tác** | Cột `created_by` / `updated_by` tự điền trên mọi bảng | Spring Data JPA Auditing + `AuditorAware` |

---

## 2. Kiến trúc (Layered Architecture)

```
Client (Browser / Swagger)
        │  HTTP + JSON
        ▼
┌─────────────────────────────────────────────┐
│  Controller   (@RestController)              │  ← nhận request, validate (@Valid), trả ApiResponse
├─────────────────────────────────────────────┤
│  Service      (@Service, @Transactional)     │  ← nghiệp vụ, quy tắc, transaction
├─────────────────────────────────────────────┤
│  Repository   (Spring Data JPA)              │  ← truy vấn DB
├─────────────────────────────────────────────┤
│  Entity / Database (JPA + MySQL/H2)          │
└─────────────────────────────────────────────┘
   DTO  ←→  Mapper  ←→  Entity   (tách biệt lớp API với lớp CSDL)
```

**Data flow một request** (ví dụ ghi danh):
`POST /api/enrollments` → `EnrollmentController` (validate DTO) → `EnrollmentService`
(kiểm tra khóa học PUBLISHED, còn chỗ, chưa trùng → dùng `EnrollmentFactory` tạo bản ghi)
→ `EnrollmentRepository.save` → DB → Mapper → `EnrollmentResponse` → JSON.

### Package
```
com.university.coursemanagement
├── controller      REST controllers
├── service / impl  Interface + implementation nghiệp vụ
├── repository      Spring Data JPA + Specifications (lọc động)
├── entity / enums  JPA entities + enum
├── dto
│   ├── request     DTO đầu vào (có Bean Validation)
│   ├── response    DTO đầu ra
│   └── mapper      Chuyển đổi Entity ↔ DTO
├── exception       GlobalExceptionHandler + custom exceptions
├── factory         EnrollmentFactory (Factory Pattern)
├── common          ApiResponse, PageResponse
└── config          OpenAPI, DataSeeder
```

### Mô hình dữ liệu (quan hệ)
- **Category** 1—N **Course**
- **Instructor** 1—N **Course**
- **Course** 1—N **Lesson** (cascade + orphanRemoval)
- **Student** N—N **Course** thông qua **Enrollment** (mang thêm trạng thái, tiến độ)
- Ràng buộc `UNIQUE(student_id, course_id)` trên `enrollments`; khóa ngoại `NOT NULL` chặt chẽ.

---

## 3. Cách chạy

### Cách A — Docker Compose (khuyến khích, gồm MySQL)
```bash
cp .env.example .env      # rồi đổi mật khẩu trong .env
docker compose up --build
```
- Ứng dụng: <http://localhost:8080>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Chạy ở profile `prod` (MySQL). Dữ liệu MySQL lưu ở volume `mysql_data`.
- Container ứng dụng chỉ khởi động sau khi MySQL báo `healthy` (`depends_on: service_healthy`).
- Mọi thông tin đăng nhập đọc từ `.env` — file này **không** được commit.

Kiểm chứng dữ liệu bền vững (khác hẳn H2 in-memory ở profile `dev`):
```bash
curl -X POST localhost:8080/api/categories -H "Content-Type: application/json" -d '{"name":"Test"}'
docker compose restart app
curl localhost:8080/api/categories/all     # dữ liệu vẫn còn
```

### Cách B — Chạy nhanh bằng H2 (profile dev, không cần DB ngoài)
```bash
./mvnw spring-boot:run            # Linux/macOS
mvnw.cmd spring-boot:run          # Windows
```
- Tự nạp **dữ liệu mẫu** (DataSeeder) + H2 console tại `/h2-console`.

### Chạy test
```bash
./mvnw test
```

---

## 4. API chính

| Nhóm | Method & Endpoint | Mô tả |
|---|---|---|
| Courses | `GET /api/courses` | Tìm kiếm + lọc (keyword, category, level, status, giá) + **paging/sorting** |
| | `POST /api/courses` | Tạo (mặc định DRAFT) |
| | `PATCH /api/courses/{id}/publish` | Xuất bản (cần ≥1 bài học) |
| | `PATCH /api/courses/{id}/archive` | Lưu trữ |
| | `GET /api/courses/statistics` | Thống kê dashboard |
| Lessons | `POST /api/courses/{courseId}/lessons` | Thêm bài học |
| Enrollments | `POST /api/enrollments` | Ghi danh (kiểm tra sức chứa, trùng lặp) |
| | `PATCH /api/enrollments/{id}/progress` | Cập nhật tiến độ (100% → COMPLETED) |
| | `PATCH /api/enrollments/{id}/cancel` | Hủy ghi danh |
| Categories / Instructors / Students | `GET/POST/PUT/DELETE` | CRUD đầy đủ |
| Reviews | `POST /api/courses/{id}/reviews` | Đánh giá 1–5 sao (chỉ học viên đã ghi danh) |
| | `GET /api/courses/top-rated` | Bảng xếp hạng theo điểm trung bình |
| Certificates | `GET /api/certificates/{code}` | Tra cứu chứng chỉ theo mã |
| | `GET /api/certificates/students/{id}` | Chứng chỉ của một học viên |
| Audit Logs | `GET /api/audit-logs` | Nhật ký thao tác (lọc theo thực thể / hành động) |

Mọi response bọc trong `ApiResponse { success, message, data, timestamp }`.
Lỗi trả về `ErrorResponse { status, error, message, path, fieldErrors }` qua **Global Exception Handling**.

---

## 5. Điểm nổi bật theo tiêu chí chấm

- **Nghiệp vụ & validate**: Bean Validation trên mọi DTO; quy tắc nghiệp vụ (khóa đầy, publish khi chưa có bài học, xóa danh mục đang dùng…) ném exception được xử lý tập trung.
- **Kiến trúc & code**: Layered rõ ràng, **Dependency Injection** qua constructor, **DTO tách Entity**, **Builder Pattern** (Lombok `@Builder`) + **Factory Pattern** (`EnrollmentFactory`), tách **Profile dev/prod**.
- **CSDL & API**: quan hệ + khóa ngoại chặt, RESTful, JSON chuẩn, **Paging/Sorting**, **Swagger**, `@Transactional`, **Global Exception Handler**, optimistic locking (`@Version`).
- **Công cụ**: Maven, **Docker + Docker Compose**, **GitHub Actions CI**, cấu hình qua biến môi trường.

---

## 6. Cấu hình môi trường (prod)

| Biến | Mặc định | Ý nghĩa |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | Chọn profile |
| `DB_URL` | `jdbc:mysql://localhost:3306/coursedb` | JDBC URL |
| `DB_USERNAME` / `DB_PASSWORD` | `course_user` / `course_pass` | Tài khoản DB |

---

## 7. Tài liệu thiết kế

| Tài liệu | Nội dung |
|---|---|
| [`docs/database-schema.md`](docs/database-schema.md) | Sơ đồ ERD, mô tả chi tiết 6 bảng, ràng buộc, lý do thiết kế, đề xuất chỉ mục |
| [`docs/architecture.md`](docs/architecture.md) | Sơ đồ tầng, sơ đồ package, sơ đồ tuần tự luồng ghi danh, danh mục API, cấu hình profile, triển khai |
| [`docs/schema.sql`](docs/schema.sql) | DDL đầy đủ — **sinh trực tiếp từ metadata Hibernate**, không viết tay |
| [`docs/toi-uu-hieu-nang.md`](docs/toi-uu-hieu-nang.md) | Các lỗi đã sửa kèm **số liệu đo trước/sau**, và các hạn chế đã biết |
| [`docs/bao-cao/bao-cao-tieu-luan.md`](docs/bao-cao/bao-cao-tieu-luan.md) | Bản thảo báo cáo tiểu luận (11 chương) |

Ảnh sơ đồ dùng cho báo cáo: `docs/database-schema.png`, `docs/architecture.png`,
`docs/architecture-package.png`, `docs/architecture-sequence-enrollment.png`, `docs/deployment.png`.
