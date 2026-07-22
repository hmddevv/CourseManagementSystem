# Kết quả tối ưu & bằng chứng đo đạc

> Tài liệu ghi lại các lỗi đã phát hiện, cách khắc phục và **số liệu đo thực tế**
> trước/sau khi sửa. Mọi con số dưới đây đều tái tạo được bằng test trong repo.

---

## 1. Race condition khi ghi danh — vượt sức chứa khóa học

### Vấn đề

`EnrollmentServiceImpl.enroll()` đếm số ghi danh `ACTIVE` rồi mới `save()`. Giữa hai bước
không có khóa, nên nhiều request đồng thời vào **chỗ trống cuối cùng** đều đọc thấy
"còn chỗ" và đều insert thành công.

`@Version` (optimistic locking) trên `BaseEntity` **không** cứu được tình huống này: nó chỉ
chống ghi đè trên chính bản ghi `Course`, không bảo vệ điều kiện **đếm** trên bảng
`enrollments` — không có dòng `Course` nào bị `UPDATE` trong luồng ghi danh.

### Cách sửa

Thêm khóa ghi bi quan vào `CourseRepository`:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT c FROM Course c WHERE c.id = :id")
Optional<Course> findByIdForUpdate(@Param("id") Long id);
```

`enroll()` nạp `Course` bằng phương thức này **trước khi** đếm. Hai giao dịch ghi danh vào
cùng một khóa học bị tuần tự hóa: giao dịch thứ hai phải chờ giao dịch thứ nhất commit rồi
mới đếm lại.

### Kết quả đo

Test `EnrollmentConcurrencyTest`: **8 luồng** cùng ghi danh vào khóa học có `capacity = 1`.

| | Số luồng ghi danh thành công | Số bản ghi `ACTIVE` trong DB | Kết luận |
|---|---|---|---|
| **Trước khi sửa** | **8 / 8** | **8** | Vượt sức chứa 800% |
| **Sau khi sửa** | **1 / 8** | **1** | Đúng sức chứa |

Cách tái tạo: đổi `findByIdForUpdate` thành `findById` trong `EnrollmentServiceImpl` rồi chạy

```bash
./mvnw test -Dtest=EnrollmentConcurrencyTest
```

Test sẽ báo `expected: 1 but was: 8`.

---

## 2. Vấn đề N+1 query ở endpoint tìm kiếm khóa học

### Vấn đề

`GET /api/courses` trả về danh sách phân trang. Với mỗi khóa học trong trang, code cũ gọi thêm:

1. `course.getCategory().getName()` — kích hoạt LAZY loading
2. `course.getInstructor().getFullName()` — kích hoạt LAZY loading
3. `lessonRepository.countByCourseId(id)` — 1 truy vấn đếm
4. `enrollmentRepository.countByCourseIdAndStatus(id, ACTIVE)` — 1 truy vấn đếm

Số truy vấn **tăng tuyến tính theo số dòng** — đúng định nghĩa N+1.

### Cách sửa

| Kỹ thuật | Áp dụng ở đâu | Tác dụng |
|---|---|---|
| `@EntityGraph(attributePaths = {"category", "instructor"})` | ghi đè `CourseRepository.findAll(Specification, Pageable)` | Nạp sẵn danh mục + giảng viên trong cùng truy vấn danh sách |
| 2 truy vấn `GROUP BY` gộp | `CourseServiceImpl.CourseListMapper` | Đếm bài học và ghi danh cho **cả trang** bằng 2 truy vấn, tra cứu bằng `Map` |
| `hibernate.default_batch_fetch_size: 20` | `application.yml` | Lưới an toàn chung: gom tối đa 20 khóa thành một câu `IN (...)` |

### Kết quả đo

Test `CourseSearchQueryCountTest` dùng `Hibernate Statistics.getPrepareStatementCount()`
để đếm số câu SQL thực sự gửi xuống CSDL.

| Kích thước trang | Số truy vấn **trước** | Số truy vấn **sau** | Giảm |
|---|---|---|---|
| 5 dòng | 19 | **4** | −79% |
| 10 dòng | 29 | **4** | −86% |

Điểm quan trọng hơn con số tuyệt đối: **độ dốc**. Trước khi sửa, thêm 5 dòng → thêm 10 truy vấn.
Sau khi sửa, số truy vấn là **hằng số 4**, không phụ thuộc số dòng trong trang:

```
1  SELECT courses JOIN categories JOIN instructors  (@EntityGraph)
2  SELECT COUNT(*) FROM courses                     (phân trang)
3  SELECT course_id, COUNT(*) FROM lessons     GROUP BY course_id
4  SELECT course_id, COUNT(*) FROM enrollments GROUP BY course_id
```

Test này cũng là **chốt chặn hồi quy**: nó khẳng định số truy vấn của trang 10 dòng bằng
đúng số truy vấn của trang 5 dòng, nên nếu ai đó vô tình thêm lại một lời gọi LAZY trong
vòng lặp thì test sẽ đỏ ngay.

Cách chạy:

```bash
./mvnw test -Dtest=CourseSearchQueryCountTest
```

---

## 3. Thiếu quy tắc nghiệp vụ khi xóa

| Vấn đề | Trước | Sau |
|---|---|---|
| Xóa học viên còn ghi danh | Không kiểm tra → CSDL ném lỗi khóa ngoại → client nhận "Vi phạm ràng buộc dữ liệu" | `BusinessException`: "Học viên đã có lịch sử ghi danh, không thể xóa…" |
| Xóa khóa học chỉ còn ghi danh `CANCELLED` | Guard chỉ đếm `ACTIVE` nên cho qua → vỡ khóa ngoại | Chặn bằng `existsByCourseId`, gợi ý dùng `archive` |

Ba lời gọi `delete()` của Category / Instructor / Student / Course giờ **nhất quán**: đều
kiểm tra ràng buộc nghiệp vụ ở tầng Service trước khi chạm tới CSDL.

Test: `StudentServiceTest.delete_shouldFailWithBusinessMessage_whenStudentHasEnrollment`,
`CourseServiceTest.delete_shouldFail_whenCourseHasCancelledEnrollmentOnly`.

---

## 4. Đếm bằng `count()` thay vì `findAll().size()`

`CourseServiceImpl.getStatistics()` trước đây gọi `courseRepository.findAll(spec).size()` —
tải **toàn bộ** entity vào bộ nhớ chỉ để lấy một con số.

```java
// Trước
long published = courseRepository.findAll(CourseSpecifications.hasStatus(PUBLISHED)).size();
// Sau
long published = courseRepository.count(CourseSpecifications.hasStatus(PUBLISHED));
```

`JpaSpecificationExecutor.count(spec)` sinh `SELECT COUNT(*)` chạy ngay trên CSDL. Với 10 000
khóa học, cách cũ nạp 10 000 đối tượng Java vào heap; cách mới trả về một số nguyên.

---

## 5. Bảo mật & chất lượng

| Vấn đề | Xử lý |
|---|---|
| Mật khẩu MySQL nằm thẳng trong `docker-compose.yml` (đã commit lên GitHub) | Chuyển sang biến môi trường đọc từ `.env`; thêm `.env.example` làm mẫu; `.env` vào `.gitignore` |
| Hàm `esc()` ở frontend không xử lý dấu nháy đơn, chuỗi lại được nối vào `onclick="...('…')"` | Bỏ hẳn việc nối chuỗi vào thuộc tính `onclick` — lấy tiêu đề từ API. Escape HTML **không** cứu được trường hợp này vì trình duyệt giải mã HTML trước rồi mới phân tích JS |
| `existsByStudentIdAndCourseIdAndStatus` khai báo nhưng không nơi nào gọi | Xóa (dead code) |

---

## 6. Hạn chế đã biết — chưa xử lý (chủ ý)

| Hạn chế | Lý do giữ nguyên | Giải pháp đúng |
|---|---|---|
| `ddl-auto: update` ở profile `prod` | Phạm vi đồ án; thêm công cụ migration sẽ kéo theo chi phí bảo trì cho các bảng sẽ thêm ở giai đoạn sau | **Flyway** / Liquibase, đổi `ddl-auto` thành `validate` |
| Phân trang dùng `OFFSET` | Dữ liệu đồ án nhỏ, `OFFSET` đủ dùng và đơn giản | **Keyset pagination** khi bảng vượt vài triệu dòng |
| Chưa có index cho `courses.status`, `enrollments(course_id, status)` | Dữ liệu nhỏ, chưa đo được lợi ích | Thêm index khi có số liệu thực tế chứng minh |
| Chưa có xác thực / phân quyền | Ngoài phạm vi đề tài | Spring Security + JWT, phân vai ADMIN / INSTRUCTOR / STUDENT |

Các hạn chế này được nêu rõ để **hiểu và giải thích được**, không phải để giấu đi.
