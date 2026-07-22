# BÁO CÁO TIỂU LUẬN — XÂY DỰNG HỆ THỐNG QUẢN LÝ KHÓA HỌC

> **Cách dùng file này**
>
> Đây là **bản thảo duy nhất** của cả nhóm. Ba thành viên nộp **cùng một nội dung**, chỉ khác
> nhau ở **trang bìa** (STT, họ tên, MSSV) và **tên file**:
>
> | STT | Sinh viên | MSSV | Tên file nộp |
> |---|---|---|---|
> | 8 | Hồ Minh Đảo | 24150199 | `8_Ho Minh Dao_24150199.docx` |
> | 45 | Lê Đình Thành | 24150127 | `45_Le Dinh Thanh_24150127.docx` |
> | 7 | Nguyễn Chí Cường | 24150545 | `7_Nguyen Chi Cuong_24150545.docx` |
>
> Quy trình: mở file mẫu `001382_LTUD-Java_Mau trinh bay tieu luan.docx` → lưu thành 3 bản →
> dán nội dung dưới đây vào mục "NỘI DUNG BÀI TIỂU LUẬN" → mỗi bản chỉ sửa **trang bìa** →
> đổi tên file theo bảng trên.
>
> Số liệu trong tài liệu lấy trực tiếp từ repo. Trước khi nộp hãy chạy lại các lệnh ở
> mục 7.3 để cập nhật nếu con số đã thay đổi.

---

## Thông tin trang bìa (giống nhau ở cả ba bản)

| Mục | Giá trị |
|---|---|
| Ngành | CÔNG NGHỆ THÔNG TIN |
| Chuyên ngành | KỸ THUẬT PHẦN MỀM |
| Học phần | LẬP TRÌNH ỨNG DỤNG VỚI JAVA (14113014) |
| Lớp học phần | 020100138201 |
| Tên đề tài | XÂY DỰNG HỆ THỐNG QUẢN LÝ KHÓA HỌC |
| Giảng viên hướng dẫn | LÝ NGỌC HƯNG |
| Địa điểm, thời gian | TP. Hồ Chí Minh, tháng 07 năm 2026 |

Định dạng bìa theo mẫu trường: TIỂU LUẬN (đậm, in hoa, cỡ 30); ngành/chuyên ngành (đậm, in hoa,
cỡ 16); GVHD/SVTH/MSSV/lớp học phần (đậm, in hoa, cỡ 14); địa điểm + tháng năm (thường, cỡ 14).

**Link mã nguồn:** <https://github.com/hmddevv/CourseManagementSystem>

---

# Chương 1. Giới thiệu

## 1.1. Lý do chọn đề tài

Đào tạo trực tuyến đã trở thành hình thức học phổ biến. Một nền tảng khóa học dù lớn hay nhỏ
đều phải giải quyết cùng một nhóm bài toán: quản lý danh mục và nội dung khóa học, quản lý
giảng viên và học viên, kiểm soát việc ghi danh trong giới hạn sức chứa, theo dõi tiến độ học,
và tổng hợp số liệu cho người quản trị.

Nhóm chọn đề tài **Hệ thống quản lý khóa học** vì bài toán này đủ rộng để thể hiện được toàn bộ
nội dung học phần: ánh xạ quan hệ nhiều–nhiều có dữ liệu kèm theo, quy tắc nghiệp vụ cần giao
dịch, xử lý truy cập đồng thời, phân trang, xử lý lỗi tập trung, và triển khai bằng Docker.

## 1.2. Mục tiêu

1. Xây dựng ứng dụng web hoàn chỉnh: RESTful API + cơ sở dữ liệu quan hệ + giao diện người dùng.
2. Áp dụng đúng **kiến trúc phân tầng** và các mẫu thiết kế đã học.
3. Bảo đảm tính đúng đắn của nghiệp vụ, kể cả khi có nhiều người dùng thao tác đồng thời.
4. Triển khai được bằng Docker và kiểm thử tự động bằng CI.

## 1.3. Phạm vi

**Trong phạm vi:** quản lý danh mục, giảng viên, học viên, khóa học, bài học, ghi danh, tiến độ
học, đánh giá khóa học, chứng chỉ hoàn thành, nhật ký thao tác, thống kê.

**Ngoài phạm vi:** xác thực và phân quyền người dùng, thanh toán học phí, phát video bài giảng,
gửi email thật. Lý do và hướng bổ sung được trình bày ở chương 9.

## 1.4. Đối tượng sử dụng

| Vai trò | Nhu cầu chính |
|---|---|
| Quản trị viên | Tạo và quản lý danh mục, giảng viên, khóa học; xem thống kê và nhật ký thao tác |
| Giảng viên | Soạn bài học, xuất bản khóa học, theo dõi học viên |
| Học viên | Tìm khóa học, ghi danh, cập nhật tiến độ, đánh giá, nhận chứng chỉ |

---

# Chương 2. Cơ sở lý thuyết

## 2.1. Spring Boot

Spring Boot là bộ khung xây dựng trên Spring Framework, cung cấp *auto-configuration*: dựa vào
các thư viện có trong classpath, nó tự cấu hình sẵn các thành phần thường dùng. Ví dụ, chỉ cần
có `spring-boot-starter-data-jpa` và một driver CSDL là Spring Boot tự tạo `DataSource`,
`EntityManagerFactory` và `TransactionManager` mà không cần khai báo XML.

Ứng dụng đóng gói thành một file `.jar` chạy độc lập, đã nhúng sẵn máy chủ Tomcat — đây là điều
kiện để đóng gói bằng Docker chỉ với một lệnh `java -jar`.

## 2.2. IoC container và Dependency Injection

**IoC (Inversion of Control — đảo ngược điều khiển)** là nguyên lý: đối tượng **không tự tạo**
các phụ thuộc của mình, mà để một thành phần bên ngoài (container) tạo và đưa vào.

**DI (Dependency Injection)** là cách hiện thực IoC phổ biến nhất.

Trong dự án, Spring quét các lớp có `@RestController`, `@Service`, `@Repository`, `@Component`,
tạo ra các đối tượng (bean) và tiêm vào nhau qua **constructor**:

```java
@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateService certificateService;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 CertificateService certificateService) {
        this.enrollmentRepository = enrollmentRepository;
        this.certificateService = certificateService;
    }
}
```

**Vì sao dùng constructor injection thay vì `@Autowired` trên trường?**

- Đối tượng **không bao giờ tồn tại ở trạng thái thiếu phụ thuộc** — không thể null.
- Trường khai báo được `final`, không bị thay đổi ngoài ý muốn.
- Viết unit test dễ: chỉ cần truyền đối tượng giả vào hàm khởi tạo, không cần framework.
- Phụ thuộc vòng tròn bị phát hiện **ngay khi khởi động** thay vì lỗi lúc chạy.

## 2.3. Kiến trúc phân tầng (Layered Architecture)

```
Controller  →  Service  →  Repository  →  Entity / CSDL
```

Mỗi tầng chỉ gọi tầng ngay dưới. Trách nhiệm tách bạch:

| Tầng | Chịu trách nhiệm | **Không** chịu trách nhiệm |
|---|---|---|
| Controller | Giao thức HTTP, định tuyến, kiểm tra dữ liệu vào, mã trạng thái | Quy tắc nghiệp vụ, truy vấn CSDL |
| Service | Quy tắc nghiệp vụ, ranh giới giao dịch | Biết đến HTTP hay JSON |
| Repository | Truy vấn dữ liệu | Quyết định nghiệp vụ |
| Entity | Ánh xạ bảng CSDL | Logic trình bày |

Lợi ích cụ thể trong dự án: quy tắc "khóa học đầy thì không cho ghi danh" nằm ở Service nên
áp dụng được cho cả lời gọi từ REST API lẫn từ job định kỳ hay lệnh nhập liệu hàng loạt.

## 2.4. JPA và Hibernate

- **JPA (Jakarta Persistence API)** là **đặc tả** — tập hợp interface và annotation
  (`@Entity`, `@ManyToOne`, `EntityManager`). Bản thân JPA không chạy được.
- **Hibernate** là **bản hiện thực** cụ thể của đặc tả đó, thực sự sinh câu SQL.
- **Spring Data JPA** là lớp tiện ích phía trên: chỉ cần khai báo interface kế thừa
  `JpaRepository`, Spring tự sinh cài đặt lúc chạy, kể cả suy ra câu truy vấn từ **tên phương
  thức** (`findByStudentIdAndCourseId` → `WHERE student_id = ? AND course_id = ?`).

Nhờ tách đặc tả khỏi hiện thực, về lý thuyết có thể đổi Hibernate sang EclipseLink mà không
phải sửa mã nghiệp vụ.

## 2.5. REST API

REST là kiểu kiến trúc dùng chính các phương thức HTTP để diễn đạt thao tác trên **tài nguyên**:

| Phương thức | Ý nghĩa | Ví dụ trong dự án |
|---|---|---|
| `GET` | Đọc, không thay đổi dữ liệu | `GET /api/courses?page=0&size=10` |
| `POST` | Tạo mới | `POST /api/enrollments` |
| `PUT` | Thay thế **toàn bộ** tài nguyên | `PUT /api/courses/{id}` |
| `PATCH` | Sửa **một phần** | `PATCH /api/enrollments/{id}/progress` |
| `DELETE` | Xóa | `DELETE /api/lessons/{id}` |

Dự án dùng `PATCH` cho các thao tác đổi trạng thái (`publish`, `archive`, `cancel`, `progress`)
đúng ngữ nghĩa: chỉ một phần tài nguyên thay đổi, không phải thay thế cả bản ghi.

## 2.6. DTO và Entity

**Entity** ánh xạ bảng CSDL. **DTO** (Data Transfer Object) là dữ liệu trao đổi với client.
Dự án **không bao giờ** trả Entity trực tiếp ra API, vì bốn lý do:

1. **Rò rỉ cấu trúc CSDL:** client thấy được đúng tên cột, tên bảng — khó đổi lược đồ về sau.
2. **Lỗi lazy loading:** khi Jackson chuyển Entity thành JSON, nó gọi hết các getter, chạm vào
   quan hệ `LAZY` sau khi phiên làm việc đã đóng → `LazyInitializationException`.
3. **Không kiểm soát được trường lộ ra:** mọi trường của Entity đều bị đưa ra ngoài.
4. **Hai lớp thay đổi độc lập:** API đổi được mà không phải đổi lược đồ CSDL và ngược lại.

Việc chuyển đổi do các lớp `Mapper` đảm nhận (`CourseMapper`, `EnrollmentMapper`…), là bean
Spring nên tiêm được vào Service.

## 2.7. Design Pattern áp dụng

**Builder Pattern** (qua Lombok `@Builder`):

```java
Course.builder().title("Java").level(BEGINNER).price(BigDecimal.ZERO).capacity(30).build();
```

Giải quyết vấn đề *telescoping constructor*: khi một lớp có 8–10 tham số, hàm khởi tạo trở nên
khó đọc và rất dễ truyền nhầm thứ tự hai tham số cùng kiểu. Builder gọi tên từng trường nên
đọc là hiểu, và bỏ qua được trường không bắt buộc.

**Factory Pattern** (`EnrollmentFactory`, `CertificateFactory`):

Tập trung quy tắc **khởi tạo** vào một nơi. Ví dụ mọi bản ghi ghi danh mới đều phải có
`enrolledAt = now`, `status = ACTIVE`, `progressPercent = 0`. Nếu để Service tự dựng đối tượng
thì quy tắc này bị lặp lại ở nhiều chỗ và dễ quên. Khi quy tắc đổi, chỉ sửa đúng một file.

**Repository Pattern** (Spring Data JPA): tách hoàn toàn logic truy vấn khỏi logic nghiệp vụ.

## 2.8. Aspect-Oriented Programming (AOP)

AOP giải quyết các mối quan tâm **xuyên suốt** (cross-cutting concerns) — những việc phải làm ở
rất nhiều chỗ nhưng không thuộc về nghiệp vụ chính, ví dụ ghi nhật ký, đo thời gian, kiểm tra
quyền.

Nếu viết thẳng vào từng phương thức Service thì cùng một đoạn mã bị lặp lại hàng chục lần và
làm loãng logic nghiệp vụ. AOP tách đoạn mã đó ra thành một **aspect** riêng và khai báo **nơi
nó được chèn vào** bằng *pointcut*.

Spring hiện thực AOP bằng **proxy**: khi một bean khớp pointcut, Spring không đưa đối tượng gốc
vào container mà đưa một đối tượng bọc ngoài. Mọi lời gọi đi qua proxy, proxy chạy phần mã phụ
rồi mới ủy quyền cho đối tượng thật.

Hệ quả cần nhớ: **lời gọi nội bộ trong cùng một lớp không đi qua proxy**, nên `@Transactional`
hay aspect không có tác dụng khi một phương thức gọi thẳng phương thức khác của chính nó.

## 2.9. Docker

Docker đóng gói ứng dụng cùng toàn bộ môi trường chạy (JRE, thư viện, cấu hình) thành một
**image** bất biến. Cùng một image chạy giống nhau trên máy lập trình viên, máy CI và máy chủ
thật — loại bỏ vấn đề "máy tôi chạy được".

**Docker Compose** mô tả nhiều container và quan hệ giữa chúng trong một file YAML. Dự án dùng
Compose để chạy đồng thời MySQL và ứng dụng, với ràng buộc ứng dụng chỉ khởi động sau khi MySQL
đã thực sự sẵn sàng.

---

# Chương 3. Phân tích yêu cầu

## 3.1. Yêu cầu chức năng

| Mã | Chức năng | Mô tả |
|---|---|---|
| F01 | Quản lý danh mục | Thêm / sửa / xóa / xem. Tên danh mục không trùng. Không xóa danh mục còn khóa học |
| F02 | Quản lý giảng viên | CRUD. Email không trùng. Không xóa giảng viên đang phụ trách khóa học |
| F03 | Quản lý học viên | CRUD. Email không trùng. Không xóa học viên còn lịch sử ghi danh |
| F04 | Quản lý khóa học | CRUD, thuộc một danh mục và một giảng viên, có trình độ, giá, sức chứa |
| F05 | Tìm kiếm khóa học | Lọc theo từ khóa, danh mục, giảng viên, trình độ, trạng thái, khoảng giá; có phân trang và sắp xếp |
| F06 | Quản lý bài học | Thêm / sửa / xóa bài học trong khóa, có thứ tự |
| F07 | Xuất bản khóa học | `DRAFT → PUBLISHED`, yêu cầu có ít nhất một bài học |
| F08 | Lưu trữ khóa học | `→ ARCHIVED`, ngừng nhận ghi danh mới |
| F09 | Ghi danh | Chỉ khóa `PUBLISHED`, còn chỗ, chưa ghi danh trùng |
| F10 | Cập nhật tiến độ | 0–100%. Đạt 100% → `COMPLETED` và cấp chứng chỉ |
| F11 | Hủy ghi danh | `→ CANCELLED`, trả lại chỗ trống |
| F12 | Đánh giá khóa học | 1–5 sao kèm nhận xét. Chỉ học viên đã ghi danh, mỗi người một lần |
| F13 | Xếp hạng khóa học | Sắp theo điểm trung bình giảm dần, có phân trang |
| F14 | Chứng chỉ | Cấp tự động khi hoàn thành, mã tra cứu duy nhất |
| F15 | Nhắc học | Job 8h sáng, tìm ghi danh không hoạt động quá 7 ngày |
| F16 | Nhật ký thao tác | Ghi tự động mọi thao tác ghi, tra cứu có lọc và phân trang |
| F17 | Thống kê | Tổng số khóa học, đã xuất bản, bản nháp, học viên, lượt đang học, top khóa học phổ biến |

## 3.2. Yêu cầu phi chức năng

| Mã | Yêu cầu | Cách đáp ứng |
|---|---|---|
| N01 | Dữ liệu vào phải được kiểm tra | Bean Validation trên mọi DTO đầu vào |
| N02 | Lỗi trả về thống nhất, dễ hiểu | `@RestControllerAdvice` xử lý tập trung |
| N03 | Không được vượt sức chứa khóa học kể cả khi ghi danh đồng thời | Khóa bi quan `SELECT … FOR UPDATE` |
| N04 | Số truy vấn không tăng theo số dòng trả về | `@EntityGraph` + truy vấn gộp `GROUP BY` |
| N05 | Có tài liệu API | springdoc-openapi / Swagger UI |
| N06 | Chạy được không cần cài đặt gì ngoài JDK | Maven Wrapper + H2 in-memory ở profile `dev` |
| N07 | Triển khai lặp lại được | Docker multi-stage + Docker Compose |
| N08 | Có kiểm thử tự động | 30 test, chạy trên GitHub Actions ở mọi lần push |
| N09 | Không commit thông tin nhạy cảm | Mật khẩu đọc từ `.env`, `.env` nằm trong `.gitignore` |

## 3.3. Các tác nhân và use case chính

```
Quản trị viên ──► Quản lý danh mục / giảng viên / học viên
              ──► Tạo, sửa, xuất bản, lưu trữ khóa học
              ──► Xem thống kê, xem nhật ký thao tác

Giảng viên    ──► Soạn bài học cho khóa học
              ──► Theo dõi danh sách học viên

Học viên      ──► Tìm kiếm khóa học
              ──► Ghi danh / hủy ghi danh
              ──► Cập nhật tiến độ ──► (tự động) Nhận chứng chỉ
              ──► Đánh giá khóa học

Hệ thống      ──► Job nhắc học 8h sáng mỗi ngày
              ──► Ghi nhật ký mọi thao tác ghi
```

---

# Chương 4. Thiết kế hệ thống

> Chèn vào Word: **Hình 4.1** `docs/database-schema.png` (ERD),
> **Hình 4.2** `docs/architecture.png` (sơ đồ tầng),
> **Hình 4.3** `docs/architecture-package.png` (sơ đồ package).

## 4.1. Mô hình dữ liệu

Hệ thống gồm **9 bảng**: `categories`, `instructors`, `students`, `courses`, `lessons`,
`enrollments`, `reviews`, `certificates`, `audit_logs`.

| Quan hệ | Kiểu | Khóa ngoại |
|---|---|---|
| Category → Course | 1 — N | `courses.category_id` NOT NULL |
| Instructor → Course | 1 — N | `courses.instructor_id` NOT NULL |
| Course → Lesson | 1 — N | `lessons.course_id` NOT NULL, cascade + orphanRemoval |
| Student ↔ Course | N — N | qua `enrollments` |
| Student / Course → Review | 1 — N | `reviews.student_id`, `reviews.course_id` |
| Enrollment → Certificate | 1 — 0..1 | `certificates.enrollment_id` NOT NULL UNIQUE |

Ba ràng buộc duy nhất mang ý nghĩa nghiệp vụ:

```sql
UNIQUE (student_id, course_id)  -- enrollments : không ghi danh trùng
UNIQUE (student_id, course_id)  -- reviews     : không đánh giá hai lần
UNIQUE (enrollment_id)          -- certificates: một ghi danh một chứng chỉ
```

Toàn bộ DDL nằm ở `docs/schema.sql`, **sinh trực tiếp từ metadata Hibernate** chứ không viết
tay, nên sơ đồ luôn khớp 100% với mã nguồn.

## 4.2. Ánh xạ quan hệ JPA

Trong CSDL quan hệ, quan hệ 1–N chỉ được biểu diễn bằng **một** thứ: cột khóa ngoại nằm ở
bảng "nhiều". Bảng `courses` có cột `category_id`, không phải bảng `categories` chứa danh sách
khóa học. JPA cho phép nhìn quan hệ đó từ **cả hai phía**:

```java
// Phía "nhiều" — phía SỞ HỮU quan hệ, nơi thật sự có khóa ngoại
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "category_id", nullable = false)
private Category category;

// Phía "một" — phía NGHỊCH ĐẢO, chỉ là ánh xạ ngược, không sinh cột nào
@OneToMany(mappedBy = "category")
private List<Course> courses = new ArrayList<>();
```

`mappedBy = "category"` nói với Hibernate: *quan hệ này đã được quản lý bởi trường `category`
bên `Course` rồi, đừng tạo thêm bảng nối hay cột nào.* Nếu quên `mappedBy`, Hibernate hiểu đây
là hai quan hệ riêng biệt và sinh thêm một bảng nối thừa.

`optional = false` sinh ra `NOT NULL` cho cột khóa ngoại — mỗi khóa học **bắt buộc** thuộc một
danh mục, đúng quy tắc nghiệp vụ.

### `FetchType.LAZY` và `EAGER`

| | `EAGER` | `LAZY` |
|---|---|---|
| Khi nạp entity cha | Nạp luôn entity liên quan | Chỉ nạp khi thật sự gọi tới |
| Mặc định của JPA | `@ManyToOne`, `@OneToOne` | `@OneToMany`, `@ManyToMany` |
| Cơ chế | Thêm `JOIN` hoặc câu `SELECT` phụ | Đặt một đối tượng **proxy** vào chỗ đó |

Dự án đặt **`LAZY` cho tất cả** `@ManyToOne`, ghi đè mặc định. Lý do rất cụ thể: mặc định
`EAGER` nghĩa là mỗi lần nạp một `Course` là kéo theo cả `Category` và `Instructor`, kể cả khi
chỉ cần lấy tiêu đề khóa học. Liệt kê 20 khóa học sẽ sinh 40 truy vấn thừa.

`LAZY` trả lại quyền quyết định cho lập trình viên: **cần thì nạp, và nạp có kiểm soát**.

**Cái giá phải trả:** proxy chỉ hoạt động khi phiên Hibernate còn mở. Chạm vào nó sau khi giao
dịch đã đóng sẽ nhận `LazyInitializationException`. Dự án xử lý bằng cách đặt
`spring.jpa.open-in-view: false` và **luôn chuyển sang DTO trước khi ra khỏi tầng Service** —
Controller không bao giờ chạm vào Entity.

### Cascade và orphanRemoval

```java
// Course → Lesson: bài học phụ thuộc hoàn toàn vào khóa học
@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
@OrderBy("orderIndex ASC")
private List<Lesson> lessons = new ArrayList<>();

// Course → Enrollment: KHÔNG cascade
@OneToMany(mappedBy = "course")
private List<Enrollment> enrollments = new ArrayList<>();
```

`cascade = ALL` nghĩa là mọi thao tác trên `Course` được lan sang `Lesson`. `orphanRemoval = true`
mạnh hơn: chỉ cần **gỡ** một bài học khỏi danh sách `course.getLessons()` là nó bị xóa khỏi CSDL,
dù không gọi `delete` lần nào. Đây là quan hệ **composition** — bài học không có ý nghĩa nếu
tách khỏi khóa học.

Ngược lại, ghi danh thuộc về **cả** học viên lẫn khóa học. Xóa khóa học mà xóa luôn ghi danh là
làm mất dữ liệu học tập của học viên. Vì vậy không cascade, và tầng Service chặn xóa khóa học
còn lịch sử ghi danh, gợi ý dùng chức năng lưu trữ (`archive`) thay thế.

`@OrderBy("orderIndex ASC")` thêm `ORDER BY order_index ASC` vào câu truy vấn nạp bài học —
sắp xếp ở CSDL, không phải sắp trong Java.

## 4.3. Lý do các quyết định thiết kế

1. **Vì sao dùng bảng trung gian `Enrollment` thay cho `@ManyToMany`?** Quan hệ học viên – khóa
   học về bản chất là nhiều–nhiều, nhưng phải mang thêm dữ liệu (trạng thái, tiến độ, thời điểm
   hoàn thành). `@ManyToMany` chỉ sinh bảng nối chứa hai khóa ngoại, không chứa được gì thêm.
   Vì vậy bảng nối phải trở thành một thực thể độc lập với hai quan hệ `@ManyToOne` — mẫu chuẩn
   *association entity*.
2. **Vì sao mọi entity kế thừa `BaseEntity`?** `BaseEntity` là `@MappedSuperclass` nên **không
   sinh bảng riêng**; sáu cột chung (`id`, `created_at`, `updated_at`, `created_by`,
   `updated_by`, `version`) được nhúng thẳng vào từng bảng con, tránh lặp lại ở chín entity.
3. **Vì sao có cột `version`?** Optimistic locking, chống mất cập nhật (*lost update*) khi hai
   người sửa cùng một bản ghi.
4. **Vì sao điểm đánh giá không lưu sẵn trên `courses`?** Tránh dữ liệu trùng lặp có thể lệch
   nhau; tính bằng truy vấn gộp chỉ tốn thêm một truy vấn cho cả trang (chi tiết ở mục 6.1).
5. **Vì sao `certificates` lưu lại tên học viên và tên khóa học?** Chứng chỉ đã phát hành ra
   ngoài, không được đổi theo dữ liệu gốc (chi tiết ở mục 6.2).
6. **Vì sao `audit_logs` không có khóa ngoại?** Nhật ký phải sống lâu hơn dữ liệu nó ghi lại
   (chi tiết ở mục 6.3).
7. **Vì sao lưu enum dạng `STRING` chứ không phải `ORDINAL`?** Nếu lưu số thứ tự, chỉ cần chèn
   thêm một hằng số vào giữa danh sách enum là toàn bộ dữ liệu cũ bị diễn giải sai.

## 4.4. Sơ đồ kiến trúc

Bốn tầng Controller → Service → Repository → Entity, cùng lớp DTO/Mapper tách biệt API với CSDL.
Nguyên tắc phụ thuộc là một chiều: tầng trên gọi tầng dưới, **không bao giờ ngược lại**. Chi
tiết xem Hình 4.2 và 4.3.

---

# Chương 5. Cài đặt

## 5.1. Cấu trúc dự án

```
src/main/java/com/university/coursemanagement/
├── controller   (9 lớp)   REST endpoint
├── service      (9 interface) + service/impl (9 lớp)
├── repository   (9 lớp)   Spring Data JPA + CourseSpecifications
├── entity       (9 entity + BaseEntity) + entity/enums (4 enum)
├── dto/request · dto/response · dto/mapper
├── exception    GlobalExceptionHandler + 3 exception nghiệp vụ
├── factory      EnrollmentFactory · CertificateFactory
├── aspect       AuditAspect (AOP)
├── scheduler    EnrollmentReminderScheduler
├── common       ApiResponse · PageResponse
└── config       OpenApiConfig · DataSeeder · CacheConfig
                 JpaAuditingConfig · SchedulingConfig
```

Tổng cộng **97 file Java** ở mã nguồn chính (khoảng 4.500 dòng) và **10 file** kiểm thử.

## 5.2. Luồng dữ liệu đầy đủ của một request

> Chèn vào Word: **Hình 5.1** `docs/architecture-sequence-enrollment.png`

Ví dụ `POST /api/enrollments` — luồng nghiệp vụ phức tạp nhất hệ thống:

1. `DispatcherServlet` nhận request, định tuyến tới `EnrollmentController.enroll()`.
2. Bean Validation kiểm tra `EnrollmentRequest` qua `@Valid`. Sai → `GlobalExceptionHandler`
   trả `400` kèm danh sách `fieldErrors`.
3. Controller gọi `EnrollmentService.enroll()`. Spring mở **giao dịch** vì phương thức có
   `@Transactional`.
4. Service nạp `Student`, rồi nạp `Course` bằng `findByIdForUpdate()` — câu
   `SELECT … FOR UPDATE` **khóa hàng** khóa học lại.
5. Service kiểm tra ba quy tắc: khóa `PUBLISHED`, chưa ghi danh trùng, còn chỗ trống.
6. `EnrollmentFactory` tạo bản ghi mới (hoặc kích hoạt lại bản ghi `CANCELLED` cũ).
7. `EnrollmentRepository.save()` → Hibernate sinh `INSERT`.
8. Giao dịch **commit**, khóa được nhả.
9. `EnrollmentMapper` chuyển Entity → `EnrollmentResponse`, bọc trong `ApiResponse`.
10. Jackson chuyển thành JSON, trả về `201 Created`.

### Quy tắc nghiệp vụ kiểm tra ở tầng Service

| # | Quy tắc | Lỗi trả về |
|---|---|---|
| 1 | Học viên phải tồn tại | `404` |
| 2 | Khóa học phải tồn tại | `404` |
| 3 | Khóa học phải ở trạng thái `PUBLISHED` | `400` — "Khóa học chưa được xuất bản" |
| 4 | Chưa có bản ghi ghi danh `ACTIVE`/`COMPLETED` | `400` — "Học viên đã ghi danh khóa học này" |
| 5 | Số học viên `ACTIVE` phải nhỏ hơn `capacity` | `400` — "Khóa học đã đầy (n/m)" |

**Vì sao kiểm tra ở Service mà không ở Controller?** Controller chỉ chịu trách nhiệm về giao
thức HTTP. Quy tắc nghiệp vụ đặt ở Service để tái sử dụng được khi gọi từ nơi khác (job định
kỳ, nhập liệu hàng loạt, giao diện khác) và để nằm trọn trong ranh giới giao dịch.

## 5.3. Kiểm tra dữ liệu đầu vào

```java
public record CourseRequest(
        @NotBlank(message = "Tieu de khoa hoc khong duoc de trong")
        @Size(max = 200) String title,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotNull @Min(1) @Max(100000) Integer capacity,
        ...
) {}
```

Dữ liệu sai bị chặn **trước khi** vào tầng Service, nên Service không phải kiểm tra lại những
điều kiện đơn giản và tập trung được vào quy tắc nghiệp vụ thật sự.

## 5.4. Xử lý lỗi tập trung

`GlobalExceptionHandler` đánh dấu `@RestControllerAdvice`. Spring bọc mọi Controller bằng cơ
chế này: khi một exception thoát ra khỏi Controller, Spring tìm phương thức `@ExceptionHandler`
khớp kiểu và dùng nó để tạo phản hồi.

| Exception | HTTP | Khi nào |
|---|---|---|
| `MethodArgumentNotValidException` | 400 | Dữ liệu vào không hợp lệ, kèm `fieldErrors` |
| `BusinessException` | 400 | Vi phạm quy tắc nghiệp vụ |
| `ResourceNotFoundException` | 404 | Không tìm thấy bản ghi |
| `DuplicateResourceException` | 409 | Trùng dữ liệu duy nhất |
| Khác | 500 | Ghi log, không lộ stack trace ra client |

Nhờ vậy **không một Controller nào** phải viết `try/catch`, định dạng lỗi thống nhất trên toàn
bộ API, và khi đổi cách trả lỗi chỉ phải sửa một chỗ.

## 5.5. Giao dịch và xử lý truy cập đồng thời

### `@Transactional`

Lớp Service đánh dấu `@Transactional(readOnly = true)`; các phương thức ghi ghi đè bằng
`@Transactional`. Ranh giới giao dịch đặt ở Service chứ không phải Controller, vì một thao tác
nghiệp vụ có thể gồm nhiều lần ghi phải cùng thành công hoặc cùng thất bại — ví dụ cập nhật
tiến độ lên 100% vừa đổi trạng thái ghi danh vừa cấp chứng chỉ.

**Quy tắc rollback:** mặc định Spring cuộn ngược giao dịch khi gặp `RuntimeException` và
`Error`. **Checked exception mặc định KHÔNG gây rollback** — muốn vậy phải khai báo
`@Transactional(rollbackFor = ...)`. Mọi exception nghiệp vụ trong dự án đều kế thừa
`RuntimeException` nên hành vi rollback là mặc định và nhất quán.

### Optimistic locking (`@Version`)

Cột `version` trên mọi bảng bật khóa lạc quan. Hibernate thêm `WHERE version = ?` vào câu
`UPDATE`; nếu có người khác đã sửa trước, câu lệnh cập nhật 0 dòng và Hibernate ném
`OptimisticLockException`.

Chọn lạc quan thay vì bi quan cho trường hợp chung vì hệ thống đọc nhiều hơn ghi và xung đột
hiếm; khóa bi quan sẽ giữ lock ở CSDL và làm giảm thông lượng.

### Pessimistic locking — bài toán vượt sức chứa

Tuy nhiên có một tình huống mà `@Version` **không** cứu được. Luồng ghi danh làm hai bước:
**đếm** số ghi danh đang hoạt động, rồi **ghi** bản ghi mới. Giữa hai bước không có gì bảo vệ.
Hai request đồng thời vào **chỗ trống cuối cùng** đều đếm thấy "còn chỗ" và đều ghi thành công.

`@Version` chỉ bảo vệ việc ghi đè trên **chính bản ghi `Course`** — mà luồng này không `UPDATE`
dòng `Course` nào. Đây là chỗ rất dễ hiểu nhầm.

Giải pháp là **khóa bi quan** trên hàng khóa học:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT c FROM Course c WHERE c.id = :id")
Optional<Course> findByIdForUpdate(@Param("id") Long id);
```

`enroll()` nạp `Course` bằng phương thức này **trước khi** đếm. Hai giao dịch ghi danh vào cùng
một khóa học bị tuần tự hóa: giao dịch thứ hai phải chờ giao dịch thứ nhất commit rồi mới đếm
lại và thấy đúng hiện trạng.

**Kết quả đo** (test `EnrollmentConcurrencyTest`, 8 luồng ghi danh vào khóa `capacity = 1`):

| | Số luồng thành công | Số bản ghi `ACTIVE` | Kết luận |
|---|---|---|---|
| **Trước khi sửa** | **8 / 8** | **8** | Vượt sức chứa 800% |
| **Sau khi sửa** | **1 / 8** | **1** | Đúng sức chứa |

| | Optimistic locking | Pessimistic locking |
|---|---|---|
| Cơ chế | Kiểm tra `version` lúc ghi | Khóa hàng ngay lúc đọc |
| Chi phí | Rẻ, không giữ khóa | Giữ khóa tới hết giao dịch |
| Khi xung đột | Ném exception, phải thử lại | Giao dịch sau chờ |
| Dùng khi | Xung đột hiếm, đọc nhiều | Xung đột thường xuyên, phải đúng tuyệt đối |

## 5.6. Truy vấn động và vấn đề N+1

### Specification API — lọc động

Endpoint tìm kiếm có 7 tiêu chí lọc, tất cả đều tùy chọn. Viết sẵn phương thức cho từng tổ hợp
sẽ cần tới `2⁷ = 128` phương thức — không khả thi. `Specification` cho phép **ghép điều kiện
lúc chạy**:

```java
public static Specification<Course> hasStatus(CourseStatus status) {
    if (status == null) {
        return null;              // null => Spring bỏ qua điều kiện này
    }
    return (root, query, cb) -> cb.equal(root.get("status"), status);
}

Specification<Course> spec = Specification.allOf(
        CourseSpecifications.keywordLike(c.keyword()),
        CourseSpecifications.hasCategory(c.categoryId()),
        CourseSpecifications.hasLevel(c.level()),
        CourseSpecifications.hasStatus(c.status()),
        CourseSpecifications.priceGreaterOrEqual(c.minPrice()),
        CourseSpecifications.priceLessOrEqual(c.maxPrice()));
```

Mỗi `Specification` là một hàm nhận `(root, query, criteriaBuilder)` và trả về một `Predicate`.
Hibernate ghép các predicate không null bằng `AND` rồi sinh đúng một câu SQL.

`JpaSpecificationExecutor` còn cung cấp sẵn `count(spec)`:

```java
// Trước: findAll(spec).size() — tải TOÀN BỘ entity vào bộ nhớ chỉ để đếm
// Sau:   count(spec) — SELECT COUNT(*) chạy ngay trên CSDL
long published = courseRepository.count(CourseSpecifications.hasStatus(PUBLISHED));
```

### Vấn đề N+1 query

**Định nghĩa:** thực hiện 1 truy vấn lấy N bản ghi, rồi vô tình thực hiện thêm N truy vấn nữa —
mỗi bản ghi một truy vấn — để lấy dữ liệu liên quan.

**Nơi nó xảy ra (trước khi sửa):** endpoint `GET /api/courses`. Với mỗi khóa học trong trang,
mã cũ chạm vào bốn thứ, mỗi thứ sinh một truy vấn:

```java
course.getCategory().getName()        // proxy LAZY → SELECT categories
course.getInstructor().getFullName()  // proxy LAZY → SELECT instructors
lessonRepository.countByCourseId(id)  // SELECT COUNT
enrollmentRepository.countByCourseIdAndStatus(id, ACTIVE)  // SELECT COUNT
```

**Ba cách khắc phục:**

| Cách | Nguyên lý | Nhược điểm |
|---|---|---|
| `JOIN FETCH` trong JPQL | Viết câu truy vấn có `JOIN FETCH` tường minh | Phải viết tay từng câu; khó kết hợp với Specification động |
| `@EntityGraph` | Khai báo trường cần nạp sẵn ngay trên phương thức repository | Chỉ nạp được quan hệ, không nạp được số đếm |
| `default_batch_fetch_size` | Gom nhiều khóa lại nạp bằng một câu `IN (...)` | Vẫn tốn thêm truy vấn, chỉ giảm số lượng |

Nhóm dùng **cả ba, mỗi cái đúng chỗ**:

```java
// 1. @EntityGraph cho quan hệ
@Override
@EntityGraph(attributePaths = {"category", "instructor"})
Page<Course> findAll(Specification<Course> spec, Pageable pageable);

// 2. Truy vấn gộp GROUP BY cho số đếm — đếm một lần cho CẢ TRANG
@Query("""
        SELECT l.course.id AS courseId, COUNT(l) AS total
        FROM Lesson l WHERE l.course.id IN :courseIds
        GROUP BY l.course.id
        """)
List<CourseCount> countGroupedByCourseIds(@Param("courseIds") Collection<Long> courseIds);
```

```yaml
# 3. batch_fetch_size làm lưới an toàn chung
spring.jpa.properties.hibernate.default_batch_fetch_size: 20
```

**Kết quả đo** (bằng `Hibernate Statistics.getPrepareStatementCount()`):

| Kích thước trang | Trước | Sau |
|---|---|---|
| 5 dòng | 19 truy vấn | **4** |
| 10 dòng | 29 truy vấn | **4** |

Điều quan trọng hơn con số tuyệt đối: trước khi sửa, thêm 5 dòng tốn thêm 10 truy vấn — **số
truy vấn tăng theo số dòng**. Sau khi sửa, số truy vấn là **hằng số 4** bất kể trang có bao
nhiêu dòng. Test `CourseSearchQueryCountTest` khẳng định điều này bằng cách so sánh số truy vấn
của trang 5 dòng với trang 10 dòng, nên nếu ai đó vô tình thêm lại một lời gọi LAZY trong vòng
lặp thì test đỏ ngay.

## 5.7. Phân trang và sắp xếp

Mọi endpoint danh sách nhận `Pageable`:

```
GET /api/courses?page=0&size=10&sort=price,desc&sort=title,asc
```

Spring tự chuyển tham số thành `Pageable`, Hibernate sinh `LIMIT ? OFFSET ?` kèm một câu
`COUNT(*)` để biết tổng số trang. Kết quả bọc trong `PageResponse` gồm `content`, `page`,
`size`, `totalElements`, `totalPages`, `first`, `last` — thay vì trả nguyên đối tượng `Page`
của Spring vốn chứa nhiều metadata thừa và ràng buộc client vào cấu trúc nội bộ của framework.

**Hạn chế của phân trang `OFFSET`:** để lấy trang thứ 1.000 với kích thước 10, CSDL vẫn phải
quét qua 10.000 dòng rồi bỏ đi 9.990 dòng đầu. Càng về sau càng chậm. Giải pháp khi dữ liệu lớn
là **keyset pagination**:
`WHERE (created_at, id) < (:lastCreatedAt, :lastId) ORDER BY created_at DESC, id DESC LIMIT 10`.
Đánh đổi: không nhảy được tới một trang bất kỳ, chỉ đi tiếp hoặc lùi.

## 5.8. Thành phần xuyên suốt và thứ tự proxy

Ba cơ chế dưới đây hoạt động **không cần sửa tầng Service**, nhờ proxy do Spring tạo:

| Cơ chế | Kích hoạt bởi | Chạy khi nào |
|---|---|---|
| Cache | `@EnableCaching` + `@Cacheable` / `@CacheEvict` | Trước khi vào phương thức |
| Nhật ký thao tác (AOP) | `@Aspect` + `@AfterReturning` | Sau khi phương thức ghi chạy xong và giao dịch đã commit |
| JPA Auditing | `@EnableJpaAuditing` + `AuditorAware` | Khi Hibernate lưu entity |
| Job định kỳ | `@EnableScheduling` + `@Scheduled` | 8h sáng mỗi ngày |

```
Request
  → AuditAspect        (@Order(1) — ngoài cùng)
    → Cache proxy      (@Cacheable / @CacheEvict)
      → Transaction proxy (@Transactional)
        → phương thức Service thật
```

`AuditAspect` đặt `@Order(1)` để nằm **ngoài** proxy giao dịch (proxy giao dịch có độ ưu tiên
thấp nhất). Nhờ vậy `@AfterReturning` chỉ chạy sau khi giao dịch nghiệp vụ đã commit thành công
— không ghi nhật ký cho thao tác bị cuộn ngược.

## 5.9. Cấu hình theo môi trường (Spring Profiles)

| Cấu hình | `dev` | `prod` |
|---|---|---|
| CSDL | H2 in-memory | MySQL 8 |
| `ddl-auto` | `create-drop` | `update` |
| Dữ liệu mẫu | `DataSeeder` tự nạp | không nạp |
| H2 Console | bật | tắt |
| Thông tin kết nối | giá trị mặc định | đọc từ biến môi trường |
| Log SQL | hiển thị | tắt |

Kích hoạt bằng biến môi trường `SPRING_PROFILES_ACTIVE`; mặc định là `dev` để chạy được ngay
sau khi clone mà **không cần cài đặt CSDL** nào.

**`spring.jpa.open-in-view: false`** — mặc định Spring Boot giữ `EntityManager` mở đến hết vòng
đời request, cho phép lazy loading ngay cả ở tầng trình bày. Nghe tiện nhưng có hại: truy vấn
phát sinh ngoài tầm kiểm soát nên khó phát hiện N+1, và giữ kết nối CSDL lâu hơn cần thiết nên
cạn connection pool khi tải cao. Tắt đi buộc mọi dữ liệu cần thiết phải được nạp xong trong
Service.

## 5.10. Tài liệu API

springdoc-openapi quét các annotation và sinh đặc tả OpenAPI 3 tại `/v3/api-docs`, kèm giao
diện thử API tại `/swagger-ui.html`. Toàn hệ thống có **44 endpoint** chia theo 9 nhóm tài
nguyên. Mọi phản hồi thành công bọc trong `ApiResponse { success, message, data, timestamp }`.

---

# Chương 6. Tính năng nâng cao

## 6.1. Đánh giá và xếp hạng khóa học

### Bài toán

Học viên chấm khóa học 1–5 sao kèm nhận xét. Hệ thống hiển thị điểm trung bình trên mỗi khóa
học và có bảng xếp hạng theo điểm.

### Quyết định thiết kế then chốt: **không lưu sẵn điểm trung bình**

Cách làm "hiển nhiên" là thêm cột `average_rating` vào bảng `courses` và cập nhật mỗi khi có
đánh giá mới. Nhóm **không** làm vậy.

Lý do: đó là **dữ liệu trùng lặp**. Cùng một sự thật ("khóa học này được chấm trung bình bao
nhiêu") được ghi ở hai nơi: trong bảng `reviews` và trong cột `courses.average_rating`. Chỉ cần
một đường ghi quên cập nhật — xóa đánh giá, sửa điểm, nhập liệu trực tiếp vào CSDL — là hai con
số lệch nhau **vĩnh viễn**, và không có cách nào biết bên nào đúng.

Cách nhóm làm: tính bằng **truy vấn gộp** nhận vào **danh sách** id, nên cả trang khóa học chỉ
tốn thêm **một** truy vấn — không phá vỡ tính chất "số truy vấn là hằng số" đã đạt được ở 5.6:

```java
@Query("""
        SELECT r.course.id AS courseId, AVG(r.rating) AS average, COUNT(r) AS total
        FROM Review r WHERE r.course.id IN :courseIds
        GROUP BY r.course.id
        """)
List<CourseRatingAggregate> aggregateByCourseIds(@Param("courseIds") Collection<Long> courseIds);
```

**Khi nào thì nên lưu sẵn?** Khi bảng `reviews` lớn tới mức `AVG` trở thành nút thắt đo được.
Lúc đó phải kèm cơ chế đồng bộ (trigger, event, hoặc tính lại định kỳ) và chấp nhận rủi ro lệch
dữ liệu — một đánh đổi có ý thức, không phải mặc định.

### Quy tắc nghiệp vụ

| # | Quy tắc | Cách bảo đảm |
|---|---|---|
| 1 | Chỉ học viên **đã ghi danh** khóa học mới được đánh giá | Kiểm tra ở Service |
| 2 | Mỗi học viên chỉ đánh giá **một lần** cho mỗi khóa | Kiểm tra ở Service **và** `UNIQUE(student_id, course_id)` ở CSDL |
| 3 | Điểm phải nằm trong 1–5 | `@Min(1) @Max(5)` trên DTO đầu vào |
| 4 | Chỉ người viết mới sửa được đánh giá của mình | Kiểm tra ở Service |

**Vì sao quy tắc 2 kiểm tra ở cả hai nơi?** Kiểm tra ở Service để trả về thông báo lỗi thân
thiện thay vì lỗi kỹ thuật. Ràng buộc ở CSDL là **chốt chặn cuối cùng** cho trường hợp hai
request chạy đồng thời cùng vượt qua bước kiểm tra.

### Bảng xếp hạng

```java
@Query("""
        SELECT r.course.id AS courseId, AVG(r.rating) AS average, COUNT(r) AS total
        FROM Review r
        GROUP BY r.course.id
        ORDER BY AVG(r.rating) DESC, COUNT(r) DESC
        """)
List<CourseRatingAggregate> rankCoursesByRating();
```

Dùng `INNER JOIN` ngầm nên khóa học **chưa có đánh giá nào** không xuất hiện — đúng ý nghĩa của
một bảng xếp hạng. Tiêu chí phụ `COUNT(r) DESC` để khóa 5 sao từ 50 lượt xếp trên khóa 5 sao từ
1 lượt. Hệ thống thật thường dùng công thức có trọng số (Bayesian average) để tránh khóa mới
với 1 đánh giá 5 sao nhảy lên đầu bảng.

**Endpoint:** `POST /api/courses/{id}/reviews`, `GET /api/courses/{id}/reviews`,
`GET /api/courses/top-rated`, `PUT /api/reviews/{id}`, `DELETE /api/reviews/{id}`.

## 6.2. Chứng chỉ hoàn thành và job nhắc học

### Cấp chứng chỉ

Khi tiến độ đạt 100%, ghi danh chuyển sang `COMPLETED` và chứng chỉ được cấp **ngay trong cùng
giao dịch**:

```java
@Override
@Transactional
public EnrollmentResponse updateProgress(Long enrollmentId, UpdateProgressRequest request) {
    ...
    if (progress >= 100) {
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        if (enrollment.getCompletedAt() == null) {
            enrollment.setCompletedAt(LocalDateTime.now());
        }
        certificateService.issueFor(enrollment);
    }
    ...
}
```

Nếu cấp chứng chỉ thất bại thì tiến độ cũng không được lưu — tránh trạng thái "đã hoàn thành
nhưng không có chứng chỉ".

`issueFor` là hàm **idempotent**: gọi lại trên cùng một ghi danh trả về chứng chỉ đã có thay vì
ném lỗi hay tạo bản ghi thứ hai. Nhờ vậy đặt tiến độ = 100% nhiều lần không sinh chứng chỉ
trùng. Ràng buộc `UNIQUE(enrollment_id)` ở CSDL là chốt chặn cuối cùng.

### Factory sinh mã chứng chỉ

```java
// CERT-{courseId}-{studentId}-{yyyyMMdd}-{6 ký tự ngẫu nhiên}
// Ví dụ: CERT-1-1-20260722-UQQRN4
```

Phần ngẫu nhiên dùng `SecureRandom` để không đoán được mã của người khác, và bỏ các ký tự dễ
nhầm (`I`, `O`, `0`, `1`) khỏi bảng chữ cái.

### Vì sao chứng chỉ lưu lại tên học viên và tên khóa học?

Đây là **cố ý** phá vỡ chuẩn hóa. Chứng chỉ là giấy tờ **đã phát hành ra ngoài** — học viên có
thể đã tải về, in ra, gửi cho nhà tuyển dụng. Nếu sau này khóa học đổi tên hoặc học viên đổi họ
tên, bản chứng chỉ tra cứu online mà đổi theo thì sẽ không khớp bản đã phát hành. Đây là kiểu
dữ liệu *snapshot* — cùng lý do với việc hóa đơn lưu lại giá bán tại thời điểm mua.

### Job nhắc học định kỳ

```java
@Scheduled(cron = "${app.reminder.cron:0 0 8 * * *}")   // 8h sáng mỗi ngày
public void remindInactiveStudents() { ... }
```

Job tìm các ghi danh còn `ACTIVE` nhưng không có thay đổi trong 7 ngày và ghi log nhắc nhở.

**Cố ý không gửi email thật:** sẽ phụ thuộc máy chủ SMTP, làm buổi demo dễ hỏng và khiến test
chậm. Điểm cần trình bày ở đây là cơ chế `@Scheduled`, còn việc đổi từ ghi log sang gửi email
chỉ là thay phần thân của hàm.

**Hạn chế đã biết:** mặc định Spring dùng **một luồng duy nhất** cho mọi job đã lịch, nên job
chạy lâu sẽ làm trễ job khác. Và nếu triển khai nhiều instance, **mọi instance đều chạy job
này** nên một học viên có thể bị nhắc nhiều lần. Giải pháp đúng là khóa phân tán (ShedLock,
Quartz cluster) hoặc đẩy việc vào hàng đợi.

**Endpoint:** `GET /api/certificates/{code}`, `GET /api/certificates/students/{id}`,
`GET /api/certificates/enrollments/{id}`. Không có endpoint tạo chứng chỉ — chứng chỉ chỉ được
cấp tự động, không cấp thủ công được.

## 6.3. Cache và nhật ký thao tác

### Spring Cache

```java
@Cacheable(CacheConfig.CATEGORIES_CACHE)
public List<CategoryResponse> getAllSimple() { ... }

@CacheEvict(value = CacheConfig.CATEGORIES_CACHE, allEntries = true)
public CategoryResponse create(CategoryRequest request) { ... }
```

Spring tạo proxy quanh bean: lần gọi đầu chạy thật và lưu kết quả, các lần sau trả thẳng từ bộ
nhớ **không chạm tới CSDL** cho đến khi cache bị xóa.

Chỉ cache dữ liệu **đọc nhiều, ghi ít**:

- `categories` — danh mục gần như không đổi nhưng được gọi ở mọi màn hình có dropdown.
- `courseStatistics` — thống kê dashboard, tính bằng nhiều truy vấn gộp.

`@CacheEvict(allEntries = true)` đặt trên **mọi** hàm ghi tương ứng, nên cache không thể trả dữ
liệu cũ. **Rủi ro của cache** chính là ở đây: nếu quên gắn `@CacheEvict` vào một đường ghi, hoặc
dữ liệu bị sửa trực tiếp trong CSDL không qua ứng dụng, thì người dùng đọc phải dữ liệu cũ mà
không có cảnh báo nào.

Dự án dùng `ConcurrentMapCacheManager` (cache trong bộ nhớ tiến trình) vì chạy một instance. Khi
triển khai nhiều instance, mỗi instance có bản cache riêng và có thể lệch nhau — lúc đó phải
chuyển sang cache phân tán (Redis). Đổi lại chỉ phải đổi bean `CacheManager`, không phải sửa
tầng Service.

### Nhật ký thao tác bằng AOP

```java
@Aspect
@Component
@Order(1)
public class AuditAspect {

    @Pointcut("execution(public * com.university.coursemanagement.service.impl.*ServiceImpl.*(..)) "
            + "&& !within(com.university.coursemanagement.service.impl.AuditLogServiceImpl)")
    public void serviceLayer() { }

    @Pointcut("execution(* create*(..)) || execution(* update*(..)) || execution(* delete*(..)) "
            + "|| execution(* publish*(..)) || execution(* archive*(..)) "
            + "|| execution(* enroll*(..)) || execution(* cancel*(..)) || execution(* issue*(..))")
    public void writeOperation() { }

    @AfterReturning(pointcut = "serviceLayer() && writeOperation()", returning = "result")
    public void recordWrite(JoinPoint joinPoint, Object result) { ... }
}
```

**Không một dòng mã nào** được chèn vào tầng Service. Mọi thao tác ghi đều được ghi lại tự động.

Ba quyết định thiết kế đáng nêu:

1. **`@Order(1)`** đặt aspect **ngoài** proxy giao dịch, nên nhật ký chỉ ghi sau khi giao dịch
   nghiệp vụ đã commit thành công — không ghi nhầm thao tác bị cuộn ngược.
2. **`AuditLogService.record` chạy `REQUIRES_NEW`** — giao dịch riêng. Nhật ký là dữ liệu quan
   sát, không được phép làm hỏng nghiệp vụ.
3. **Chỉ audit thao tác GHI.** Audit cả thao tác đọc sẽ làm phình bảng nhật ký và làm chậm mọi
   request danh sách.

**Vì sao `audit_logs` không có khóa ngoại?** Nhật ký phải sống lâu hơn dữ liệu nó ghi lại. Nếu
đặt khóa ngoại tới `courses`, thì hoặc là không xóa được khóa học, hoặc là xóa khóa học sẽ xóa
luôn nhật ký về nó — cả hai đều làm mất ý nghĩa của nhật ký. Bảng chỉ lưu `entity_name` +
`entity_id` dạng phẳng, kèm hai chỉ mục tường minh để tra cứu vẫn nhanh khi bảng lớn dần.

### JPA Auditing

`@EnableJpaAuditing` + `AuditorAware` khiến Spring Data tự điền hai cột `created_by` /
`updated_by` trên **mọi** bảng, không phải viết mã ở bất kỳ Service nào.

```java
@Bean
public AuditorAware<String> auditorAware() {
    return () -> Optional.of(currentActor());   // header X-User, mặc định "system"
}
```

Khi bổ sung Spring Security sau này, chỉ cần đổi thân hàm này sang đọc `SecurityContextHolder`
— **không phải sửa bất kỳ entity nào**.

**Endpoint:** `GET /api/audit-logs?entityName=&action=&page=&size=&sort=` — chỉ đọc. Nhật ký
không được phép sửa hay xóa qua API, nếu không nó mất ý nghĩa làm bằng chứng.

---

# Chương 7. Kiểm thử và triển khai

## 7.1. Danh sách test

| File test | Số test | Kiểm tra điều gì |
|---|---|---|
| `CourseManagementApplicationTests` | 1 | Ứng dụng khởi động, mọi bean tạo được |
| `EnrollmentServiceTest` | 2 | Chặn ghi danh khi khóa đầy; chặn khi khóa chưa xuất bản |
| `EnrollmentConcurrencyTest` | 1 | 8 luồng ghi danh đồng thời vào khóa `capacity = 1` |
| `CourseServiceTest` | 5 | Điều kiện xuất bản, chặn xóa khi có lịch sử ghi danh, thống kê |
| `CourseSearchQueryCountTest` | 2 | Số truy vấn của endpoint tìm kiếm là hằng số (chặn hồi quy N+1) |
| `StudentServiceTest` | 4 | Chặn xóa học viên còn ghi danh, trùng email, đếm ghi danh |
| `ReviewServiceTest` | 5 | Chỉ người đã ghi danh được đánh giá, không đánh giá hai lần, xếp hạng |
| `CertificateServiceTest` | 5 | Cấp chứng chỉ khi đạt 100%, không cấp trùng, tra cứu theo mã |
| `CacheAndAuditTest` | 3 | Cache có hiệu lực và bị xóa khi ghi, thao tác đọc không bị ghi nhật ký |
| `EnrollmentReminderSchedulerTest` | 2 | Job chỉ chọn ghi danh `ACTIVE` quá hạn |
| **Tổng** | **30** | Tất cả **PASS** |

Điểm đáng chú ý: hai test không kiểm tra "chức năng chạy đúng" mà kiểm tra **tính chất của hệ
thống** — `EnrollmentConcurrencyTest` kiểm tra tính đúng đắn dưới truy cập đồng thời, và
`CourseSearchQueryCountTest` kiểm tra số truy vấn không tăng theo số dòng. Cả hai đều **thất bại
với mã cũ và thành công với mã đã sửa**, nên chúng thật sự chứng minh được điều chúng khẳng định.

## 7.2. Kết quả đo hiệu năng

> Chèn vào Word: ảnh chụp kết quả chạy `./mvnw clean test`.

| Hạng mục | Trước | Sau |
|---|---|---|
| 8 luồng ghi danh vào khóa `capacity = 1` | **8 luồng thành công** (vượt sức chứa 800%) | **1 luồng thành công** |
| Số truy vấn `GET /api/courses?size=5` | 19 | **4** |
| Số truy vấn `GET /api/courses?size=10` | 29 | **4** (không đổi theo số dòng) |

Chi tiết và cách tái tạo xem `docs/toi-uu-hieu-nang.md` trong repo.

## 7.3. Lệnh kiểm chứng số liệu trước khi nộp

```bash
./mvnw clean test                             # số test PASS
git rev-list --count HEAD                     # số commit
git shortlog -sne                             # phân bố đóng góp
find src/main/java -name '*.java' | wc -l     # số file Java
```

## 7.4. Đóng gói bằng Docker

**Dockerfile multi-stage:** giai đoạn 1 dùng image có JDK + Maven để biên dịch; giai đoạn 2 chỉ
copy file `.jar` sang image JRE Alpine. Image cuối **không chứa** mã nguồn, Maven hay trình biên
dịch — nhỏ hơn nhiều và giảm bề mặt tấn công. Ứng dụng chạy bằng user không phải `root`, nên
nếu bị khai thác thì kẻ tấn công không có quyền root bên trong container.

**Docker Compose:** MySQL 8 + ứng dụng. Ứng dụng chỉ khởi động sau khi MySQL báo `healthy` nhờ
`depends_on: condition: service_healthy` — nếu chỉ dùng `depends_on` thường, ứng dụng có thể
khởi động trước và crash vì chưa kết nối được CSDL. Dữ liệu MySQL lưu ở volume nên tồn tại qua
các lần khởi động lại. Mọi thông tin đăng nhập đọc từ `.env` (không commit vào git).

**Kết quả kiểm chứng thực tế:**

| Bước kiểm chứng | Kết quả |
|---|---|
| `docker compose up --build` từ trạng thái sạch | MySQL `healthy` → app khởi động → `/actuator/health` trả `{"status":"UP"}` |
| Thứ tự khởi động | App **không** khởi động trước khi MySQL sẵn sàng |
| Dữ liệu bền vững | Tạo danh mục qua API → `docker compose restart app` → **dữ liệu vẫn còn** |
| `docker compose ps` | Cả hai container `Up (healthy)` |

Đây chính là lý do có hai profile chứ không phải chỉ để đổi chuỗi kết nối: profile `dev` dùng H2
in-memory với `ddl-auto: create-drop` nên **mất sạch dữ liệu** mỗi lần tắt ứng dụng; profile
`prod` dùng MySQL với volume nên dữ liệu sống qua các lần khởi động lại.

## 7.5. Tích hợp liên tục (CI)

GitHub Actions chạy ở mọi lần push lên `main`:

1. Cài JDK 17 (có cache Maven)
2. `mvn clean verify` — biên dịch và chạy toàn bộ 30 test
3. `docker build` — xác nhận image build được

Nếu test đỏ thì workflow thất bại và badge trên README chuyển sang màu đỏ, nên lỗi được phát
hiện ngay chứ không đợi tới lúc demo.

---

# Chương 8. Kết quả đạt được

> Chèn vào Word các ảnh chụp màn hình sau:
>
> - **Hình 8.1** Swagger UI liệt kê đầy đủ endpoint
> - **Hình 8.2** Giao diện danh sách khóa học có cột điểm đánh giá
> - **Hình 8.3** Bảng xếp hạng khóa học
> - **Hình 8.4** Nhật ký thao tác
> - **Hình 8.5** Chứng chỉ hoàn thành
> - **Hình 8.6** Một request lỗi validate trả về JSON có `fieldErrors`
> - **Hình 8.7** Kết quả `./mvnw clean test` — 30 test PASS
> - **Hình 8.8** `docker compose ps` — hai container `Up (healthy)`
> - **Hình 8.9** `git shortlog -sne` — phân bố đóng góp

| Chỉ số | Giá trị |
|---|---|
| File Java (mã chính) | 97 |
| Dòng mã chính | ~4.500 |
| Entity | 9 (+ `BaseEntity`) |
| Endpoint REST | 44 |
| Test tự động | 30, tất cả PASS |
| Bảng CSDL | 9 |

---

# Chương 9. Đánh giá và hướng phát triển

## 9.1. Ưu điểm

- Kiến trúc phân tầng rõ ràng, ranh giới trách nhiệm tách bạch, dễ mở rộng.
- Quy tắc nghiệp vụ được kiểm tra ở đúng tầng và có test chứng minh.
- Xử lý được tình huống truy cập đồng thời — có số liệu đo trước/sau.
- Không có vấn đề N+1 ở endpoint danh sách, có test chặn hồi quy.
- Triển khai lặp lại được bằng Docker, có CI tự động.

## 9.2. Hạn chế

| Hạn chế | Ảnh hưởng |
|---|---|
| **Chưa có xác thực / phân quyền** — danh tính lấy từ request | Không dùng được cho hệ thống thật; nhật ký thao tác chỉ mang tính tham khảo |
| `ddl-auto: update` ở profile `prod` | Không kiểm soát được phiên bản lược đồ, không rollback được |
| Cache trong bộ nhớ tiến trình | Nhiều instance sẽ có bản cache lệch nhau |
| Job `@Scheduled` chạy trên mọi instance | Học viên có thể bị nhắc trùng khi chạy nhiều instance |
| Phân trang `OFFSET` | Chậm dần khi bảng rất lớn |
| Chưa có index cho cột lọc nóng | Chưa ảnh hưởng ở quy mô hiện tại |

**Nói rõ về hạn chế lớn nhất.** Toàn bộ hệ thống không có đăng nhập, nên mọi endpoint nhận danh
tính người dùng **từ chính request** (`studentId` nằm trong body). Hệ quả là bất kỳ ai gọi được
API đều có thể đổi số `studentId` để hành động nhân danh học viên khác. Kiểm tra
`review.getStudent().getId().equals(request.studentId())` **trông giống** kiểm tra quyền sở hữu
nhưng thực chất **không phải**, vì cả hai vế đều do người gọi cung cấp.

Nhóm chọn làm đúng phần trong phạm vi và **nêu rõ hạn chế** thay vì làm một lớp xác thực nửa
vời tạo cảm giác an toàn giả.

## 9.3. Đề xuất tối ưu

1. **Spring Security + JWT.** Lấy danh tính từ token đã xác thực thay vì từ body request; bỏ
   trường `studentId` khỏi DTO. Phân vai `ADMIN` / `INSTRUCTOR` / `STUDENT`. Chỉ cần đổi thân
   `AuditorAware` là cột `created_by` / `updated_by` tự đúng — không phải sửa entity nào.
   Đánh đổi: khối lượng công việc lớn (đăng ký, đăng nhập, mã hóa mật khẩu, làm mới token).
2. **Flyway migration.** Mỗi thay đổi lược đồ là một file `V__.sql` được version hóa cùng mã
   nguồn; đổi `ddl-auto` thành `validate`. Đánh đổi: thêm một bước phải nhớ khi sửa entity.
3. **Thêm index** cho `courses.status` và `enrollments(course_id, status)`. Đánh đổi: mỗi index
   làm chậm thao tác ghi và tốn dung lượng — chỉ thêm khi đo được lợi ích thật.
4. **Keyset pagination** thay cho `OFFSET` khi bảng vượt vài triệu dòng. Đánh đổi: không nhảy
   được tới trang bất kỳ, chỉ đi tiếp/lùi.
5. **Redis** thay cache trong bộ nhớ khi triển khai nhiều instance. Đánh đổi: thêm một thành
   phần hạ tầng phải vận hành.
6. **ShedLock** cho job định kỳ, bảo đảm chỉ một instance chạy job.
7. **Hàng đợi tin nhắn** khi cần gửi email thật, để việc gửi không chặn luồng nghiệp vụ.
8. **Rate limiting + idempotency key** cho endpoint ghi danh, chống bấm nhiều lần và lạm dụng.

---

# Chương 10. Phân công công việc

| Thành viên | MSSV | Mảng phụ trách | Tính năng nâng cao |
|---|---|---|---|
| Hồ Minh Đảo | 24150199 | Catalog: Category, Instructor, Course, Lesson | Đánh giá & xếp hạng khóa học (mục 6.1) |
| Lê Đình Thành | 24150127 | Nghiệp vụ ghi danh, giao dịch, khóa dữ liệu | Chứng chỉ hoàn thành + job nhắc học (mục 6.2) |
| Nguyễn Chí Cường | 24150545 | Hạ tầng, thành phần xuyên suốt, Docker/CI | Cache + nhật ký thao tác AOP (mục 6.3) |

Nguyên tắc chia việc: **chia dọc theo luồng dữ liệu**, không chia ngang theo tầng. Nếu chia
ngang (một người làm Controller, một người làm Service, một người làm Repository) thì khi được
hỏi "luồng dữ liệu chức năng ghi danh chạy thế nào?" sẽ không ai trả lời trọn vẹn được. Chia
dọc bảo đảm mỗi người nắm trọn một luồng từ Controller xuống CSDL.

> Chèn vào Word: kết quả `git shortlog -sne` làm bằng chứng đóng góp.

**Link mã nguồn:** <https://github.com/hmddevv/CourseManagementSystem>

---

# Chương 11. Tài liệu tham khảo

1. Spring Boot Reference Documentation — <https://docs.spring.io/spring-boot/docs/current/reference/html/>
2. Spring Data JPA Reference — <https://docs.spring.io/spring-data/jpa/reference/>
3. Spring Framework — Aspect Oriented Programming with Spring — <https://docs.spring.io/spring-framework/reference/core/aop.html>
4. Hibernate ORM User Guide — <https://docs.jboss.org/hibernate/orm/6.6/userguide/html_single/>
5. Jakarta Bean Validation Specification — <https://jakarta.ee/specifications/bean-validation/>
6. Docker Documentation — <https://docs.docker.com/>
7. Tài liệu học phần *Lập trình ứng dụng với Java (14113014)*, Chương 0–3, Lab 4, Lab 5.
