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
| CSDL | MySQL 8 — dùng chung cho cả profile `dev` và `prod` (H2 in-memory chỉ dành cho test tự động) |
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
│  Entity / Database (JPA + MySQL 8)           │
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
├── controller      9 REST controllers
├── service / impl  Interface + implementation nghiệp vụ
├── repository      Spring Data JPA + CourseSpecifications (lọc động)
├── entity / enums  JPA entities + enum
├── dto
│   ├── request     DTO đầu vào (có Bean Validation)
│   ├── response    DTO đầu ra
│   └── mapper      Chuyển đổi Entity ↔ DTO
├── exception       GlobalExceptionHandler + 3 custom exception + ErrorResponse
├── factory         EnrollmentFactory, CertificateFactory (Factory Pattern)
├── aspect          AuditAspect — ghi nhật ký thao tác bằng Spring AOP
├── scheduler       EnrollmentReminderScheduler — job nhắc học định kỳ
├── common          ApiResponse, PageResponse
└── config          CacheConfig, JpaAuditingConfig, OpenApiConfig,
                    SchedulingConfig, DataSeeder
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

Kiểm chứng dữ liệu bền vững qua volume MySQL:
```bash
curl -X POST localhost:8080/api/categories -H "Content-Type: application/json" -d '{"name":"Test"}'
docker compose restart app
curl localhost:8080/api/categories/all     # dữ liệu vẫn còn
```

### Cách B — Chạy app ở máy, CSDL vẫn là MySQL (profile dev)
```bash
docker compose up -d mysql        # chỉ bật CSDL, không bật app
bash scripts/run-dev.sh
```
- Kết nối tới đúng MySQL 8 ở `localhost:3307` — **cùng engine, cùng dialect, cùng `ddl-auto` với prod**.
  Hai profile chỉ khác nhau ở mức log, dữ liệu mẫu và nguồn lấy thông tin kết nối.
- `scripts/run-dev.sh` đọc tài khoản CSDL từ `.env`, nên **không có mật khẩu nào nằm trong mã nguồn**.
- Lần chạy đầu trên CSDL rỗng, `DataSeeder` tự nạp **dữ liệu mẫu**; các lần sau không nạp lại.
- Đừng bật đồng thời container `app`, vì cả hai cùng chiếm cổng 8080.

### Chạy test
```bash
./mvnw test
```
Test chạy ở profile `test` với **H2 in-memory** — CSDL dựng lên rồi hủy ngay trong lần chạy, nên bộ test
không phụ thuộc và không làm hỏng MySQL thật, và chạy được trên máy chưa cài gì.

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
- **Xử lý đồng thời (có đo)**: ghi danh vượt sức chứa khi nhiều luồng chạy song song được sửa bằng **khóa bi quan** (`SELECT ... FOR UPDATE`) **kết hợp** ghim mức cô lập `READ_COMMITTED` — chỉ khóa thôi vẫn sai trên MySQL. Đo trên MySQL 8: **8/8 luồng lọt → 1/8**. Chi tiết ở [`docs/toi-uu-hieu-nang.md`](docs/toi-uu-hieu-nang.md).
- **Hiệu năng (có đo)**: chặn **N+1 query** ở màn danh sách khóa học bằng `@EntityGraph` + 3 truy vấn gộp `GROUP BY` — **29 → 4 câu truy vấn**, và số truy vấn không tăng theo số dòng (khóa chặt bằng test).
- **Công cụ**: Maven, **Docker + Docker Compose**, **GitHub Actions CI**, cấu hình qua biến môi trường.

---

## 6. Cấu hình môi trường

| Biến | Mặc định ở `dev` | Mặc định ở `prod` | Ý nghĩa |
|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | `prod` (đặt trong `.env`) | Chọn profile |
| `DB_URL` | `jdbc:mysql://localhost:3307/coursedb` | `jdbc:mysql://localhost:3306/coursedb` | JDBC URL — cổng khác nhau vì `dev` nối từ máy host vào container, `prod` chạy trong mạng Docker |
| `DB_USERNAME` / `DB_PASSWORD` | `course_user` / `course_pass` | đọc từ `.env` | Tài khoản CSDL |

Giá trị mặc định chỉ dùng cho máy cá nhân. Khi đã đổi mật khẩu trong `.env`, chạy `dev` bằng
`scripts/run-dev.sh` để script tự lấy tài khoản từ `.env`.

---

## 7. Tài liệu thiết kế

| Tài liệu | Nội dung |
|---|---|
| [`docs/database-schema.md`](docs/database-schema.md) | Sơ đồ ERD, mô tả chi tiết 9 bảng, ràng buộc, lý do thiết kế, đề xuất chỉ mục |
| [`docs/architecture.md`](docs/architecture.md) | Sơ đồ tầng, sơ đồ package, sơ đồ tuần tự luồng ghi danh, danh mục API, cấu hình profile, triển khai |
| [`docs/schema.sql`](docs/schema.sql) | DDL đầy đủ — **sinh trực tiếp từ metadata Hibernate**, không viết tay |
| [`docs/toi-uu-hieu-nang.md`](docs/toi-uu-hieu-nang.md) | Các lỗi đã sửa kèm **số liệu đo trước/sau**, và các hạn chế đã biết |
| [`scripts/seed-demo.sh`](scripts/seed-demo.sh) | Nạp dữ liệu mẫu qua REST API (dùng khi chạy profile `prod`, vì `DataSeeder` chỉ chạy ở `dev`/`test`) |
| [`scripts/run-dev.sh`](scripts/run-dev.sh) | Chạy app ở profile `dev`, tự lấy tài khoản CSDL từ `.env` |

Ảnh sơ đồ dùng cho báo cáo: `docs/database-schema.png`, `docs/architecture.png`,
`docs/architecture-package.png`, `docs/architecture-sequence-enrollment.png`, `docs/deployment.png`.
