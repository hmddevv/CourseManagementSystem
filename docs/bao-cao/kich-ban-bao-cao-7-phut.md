# Kịch bản báo cáo 7 phút — Nhóm 1

> Đề tài: **Hệ thống quản lý khóa học** — Lập trình ứng dụng với Java (14113014)
> Repo: <https://github.com/hmddevv/CourseManagementSystem>

---

## 0. CHUẨN BỊ TRƯỚC KHI LÊN (làm trước 5 phút)

Mở sẵn **5 tab** trên trình duyệt, theo đúng thứ tự này:

| Tab | Nội dung | Đường dẫn |
|---|---|---|
| 1 | ERD (sơ đồ CSDL) | mở file `docs/database-schema.png` |
| 2 | Sơ đồ kiến trúc | mở file `docs/architecture.png` |
| 3 | Web app đang chạy | <http://localhost:8080> |
| 4 | Swagger UI | <http://localhost:8080/swagger-ui.html> |
| 5 | GitHub repo | <https://github.com/hmddevv/CourseManagementSystem> |

Chạy app **trước khi lên bục** (đừng chạy trên bục, mất 40 giây khởi động):

```bash
cd TieuLuan
mvnw.cmd spring-boot:run
```

Chờ tới khi log hiện `Started CourseManagementApplication`. Bấm F5 tab 3 kiểm tra
dashboard có số liệu.

**Nếu app không chạy được** → vẫn báo cáo bình thường bằng tab 1, 2, 5. Đừng
loay hoay sửa trên bục.

---

## 1. PHÂN CHIA 7 PHÚT

| Thời gian | Ai nói | Nội dung |
|---|---|---|
| 0:00 – 0:40 | **Người 1** | Mở đầu + đề tài + chức năng |
| 0:40 – 2:10 | **Người 1** | Thiết kế CSDL (tab 1) |
| 2:10 – 3:30 | **Người 2** | Kiến trúc hệ thống (tab 2) |
| 3:30 – 5:30 | **Người 2** | Demo chạy thật (tab 3, 4) |
| 5:30 – 6:40 | **Người 3** | Điểm kỹ thuật nổi bật + số liệu đo |
| 6:40 – 7:00 | **Người 3** | Kết + quy trình làm việc nhóm (tab 5) |

> Ai nói phần nào **thì phải hiểu phần đó**. Thầy thường hỏi ngay người vừa nói.

---

## 2. LỜI THOẠI

### 0:00 – 0:40 — Mở đầu (Người 1)

> "Em chào thầy và các bạn. Nhóm 1 báo cáo đề tài **Hệ thống quản lý khóa học**,
> xây dựng bằng **Spring Boot 3.4 và Java 17**.
>
> Hệ thống gồm 3 phần: **REST API backend**, **cơ sở dữ liệu quan hệ**, và
> **giao diện web** gọi API.
>
> Nghiệp vụ chính: quản lý danh mục, giảng viên, khóa học và bài học; học viên
> **ghi danh** vào khóa học, cập nhật **tiến độ học**, đạt 100% thì được **cấp
> chứng chỉ**, và có thể **đánh giá khóa học** để xếp hạng.
>
> Sau đây em xin trình bày thiết kế cơ sở dữ liệu."

### 0:40 – 2:10 — Thiết kế CSDL (Người 1) — **chiếu tab 1**

> "Đây là sơ đồ ERD gồm **9 bảng**.
>
> Trục chính là: **Category** 1–N **Course**, **Instructor** 1–N **Course**, và
> **Course** 1–N **Lesson**.
>
> Điểm quan trọng nhất là quan hệ giữa **Student** và **Course**. Về mặt lý
> thuyết đây là **nhiều–nhiều**, nhưng nhóm em **không dùng `@ManyToMany`**, mà
> tách thành một bảng trung gian riêng là **`enrollments`**. Lý do: mỗi lần ghi
> danh còn phải mang thêm dữ liệu — **trạng thái**, **phần trăm tiến độ**, **ngày
> ghi danh**. `@ManyToMany` chỉ nối được hai khóa ngoại, không chứa được các cột
> này.
>
> Trên bảng `enrollments` em đặt ràng buộc **`UNIQUE(student_id, course_id)`** để
> một học viên không ghi danh trùng một khóa hai lần — chặn ngay ở tầng cơ sở dữ
> liệu, không phụ thuộc vào code.
>
> Ba bảng còn lại là tính năng nâng cao: **`reviews`** (đánh giá 1–5 sao),
> **`certificates`** (chứng chỉ, quan hệ 1–1 với ghi danh, có mã tra cứu duy
> nhất), và **`audit_logs`** ghi nhật ký mọi thao tác ghi.
>
> Toàn bộ khóa ngoại đều để **NOT NULL**, và file DDL trong `docs/schema.sql`
> được **sinh trực tiếp từ metadata của Hibernate**, không viết tay, nên chắc
> chắn khớp với code."

### 2:10 – 3:30 — Kiến trúc (Người 2) — **chiếu tab 2**

> "Về kiến trúc, nhóm em dùng **Layered Architecture** 4 tầng:
>
> **Controller** nhận request và validate dữ liệu đầu vào — **Service** chứa
> nghiệp vụ và quản lý transaction — **Repository** truy vấn database qua Spring
> Data JPA — và **Entity** ánh xạ xuống bảng.
>
> Giữa tầng API và tầng CSDL em **tách DTO khỏi Entity**, chuyển đổi qua lớp
> **Mapper**. Làm vậy để Entity không bị lộ ra ngoài API — đổi cấu trúc bảng
> không làm vỡ hợp đồng API, và tránh lỗi vòng lặp vô hạn khi serialize JSON các
> quan hệ hai chiều.
>
> Các tầng nối với nhau bằng **Dependency Injection qua constructor**, nên
> Service không tự tạo Repository mà Spring tiêm vào — dễ thay thế và dễ viết
> test.
>
> Toàn bộ lỗi được xử lý tập trung ở một chỗ bằng
> **`@RestControllerAdvice`** — Controller không có một khối `try-catch` nào, mọi
> lỗi trả về cùng một định dạng JSON."

### 3:30 – 5:30 — Demo (Người 2) — **chiếu tab 3, rồi tab 4**

Làm đúng 4 bước này, không lan man:

1. **Dashboard** — "Đây là thống kê tổng quan: số khóa học, số học viên, số ghi danh."
2. **Danh sách khóa học** — gõ từ khóa vào ô tìm kiếm, chọn 1 bộ lọc.
   > "Phần tìm kiếm này hỗ trợ **lọc động nhiều tiêu chí** cùng lúc và có
   > **phân trang, sắp xếp**. Em không viết mỗi tổ hợp một hàm, mà dùng
   > **Specification API** để ghép điều kiện lúc chạy."
3. **Ghi danh** — chọn học viên + khóa học → Ghi danh → kéo tiến độ lên **100%**.
   > "Khi tiến độ đạt 100%, hệ thống **tự động cấp chứng chỉ** trong cùng một
   > transaction, và trạng thái chuyển sang COMPLETED."
   → mở tab chứng chỉ cho thấy mã.
4. **Bấm ghi danh lại lần nữa cùng học viên đó** → hiện lỗi.
   > "Đây là **validate nghiệp vụ**: hệ thống chặn ghi danh trùng và trả về JSON
   > lỗi có cấu trúc rõ ràng."

Chuyển **tab 4 (Swagger)** 10 giây:
> "Toàn bộ API được sinh tài liệu tự động bằng **Swagger**, hiện có **42
> endpoint**."

### 5:30 – 6:40 — Điểm kỹ thuật nổi bật (Người 3)

Đây là phần **ăn điểm nhất**. Nói chậm, rõ:

> "Ngoài chức năng, nhóm em có xử lý **hai vấn đề kỹ thuật thật** và **có đo số
> liệu**.
>
> **Thứ nhất — lỗi tranh chấp khi ghi danh.** Một khóa học có giới hạn sức chứa.
> Code ban đầu **đếm số người đã ghi danh rồi mới chèn bản ghi mới**. Nếu nhiều
> người bấm ghi danh cùng lúc, tất cả cùng đọc ra số cũ và cùng chèn — khóa học
> bị **vượt sức chứa**.
>
> Nhóm em viết test cho **8 luồng chạy đồng thời** vào một khóa chỉ còn **1 chỗ**.
> Kết quả trước khi sửa: **8 người vào được**. Sau khi sửa bằng **khóa bi quan**
> — tức `SELECT ... FOR UPDATE` để giữ dòng khóa học lại — kết quả đúng
> **1 trên 8**, 7 luồng còn lại bị từ chối.
>
> Chỗ này em xin nói thêm: khóa lạc quan bằng `@Version` **không cứu được** ca
> này, vì luồng nghiệp vụ chỉ **INSERT** vào bảng ghi danh chứ **không UPDATE**
> dòng khóa học, nên `@Version` không có gì để so sánh.
>
> **Thứ hai — lỗi N+1 query.** Màn hình danh sách khóa học phải hiện số bài học,
> số học viên và điểm đánh giá. Ban đầu mỗi khóa học sinh thêm truy vấn riêng.
> Em đo bằng **Hibernate Statistics**: một trang 10 khóa học tốn **29 câu truy
> vấn**. Sau khi dùng **`@EntityGraph`** và **truy vấn gộp GROUP BY**, còn **4
> câu**.
>
> Quan trọng hơn con số: trước đây **trang càng nhiều dòng thì càng nhiều truy
> vấn**, giờ **luôn là 4 câu** bất kể bao nhiêu dòng. Em có viết một test khóa
> chặt điều này lại để sau này ai sửa code làm hỏng thì test sẽ báo đỏ."

### 6:40 – 7:00 — Kết (Người 3) — **chiếu tab 5 (GitHub)**

> "Về quy trình: nhóm em làm trên **GitHub**, hiện có **18 commit**, dùng
> **conventional commits**. Mỗi lần push đều chạy **GitHub Actions** tự động
> build và chạy test — hiện **30 test đều pass**.
>
> Hệ thống đóng gói bằng **Docker Compose** gồm ứng dụng và MySQL, đã chạy thử
> thật và kiểm chứng dữ liệu vẫn còn sau khi restart container.
>
> Nhóm em xin hết. Em cảm ơn thầy ạ."

---

## 3. CÂU HỎI THẦY HAY HỎI — TRẢ LỜI NGẮN

| Câu hỏi | Trả lời |
|---|---|
| **Sao không dùng `@ManyToMany`?** | Vì mỗi lần ghi danh còn mang thêm trạng thái, tiến độ, ngày ghi danh. `@ManyToMany` chỉ nối 2 khóa ngoại, không chứa được các cột này. |
| **DTO để làm gì, sao không trả Entity thẳng?** | Để Entity không lộ ra API. Đổi cấu trúc bảng không làm vỡ hợp đồng API. Và tránh lỗi vòng lặp vô hạn khi serialize quan hệ 2 chiều. |
| **`@Transactional` hoạt động thế nào?** | Spring tạo proxy quanh bean Service. Vào phương thức thì mở transaction, chạy xong không lỗi thì commit, gặp RuntimeException thì rollback toàn bộ. |
| **LAZY và EAGER khác gì?** | LAZY chỉ nạp khi thật sự dùng tới, EAGER nạp ngay. Nhóm em để **LAZY toàn bộ** để tránh nạp thừa, chỗ nào cần thì chỉ định rõ bằng `@EntityGraph`. |
| **N+1 query là gì?** | Lấy danh sách N dòng tốn 1 truy vấn, nhưng mỗi dòng lại sinh thêm 1 truy vấn con → tổng N+1. Em đo được 29 câu cho 1 trang, sửa xuống còn 4. |
| **Sao lại là khóa bi quan mà không phải lạc quan?** | Vì luồng này chỉ INSERT bản ghi ghi danh, **không UPDATE** dòng khóa học, nên `@Version` không có gì để so sánh. Phải giữ dòng khóa học lại bằng `SELECT ... FOR UPDATE`. |
| **Validate ở đâu?** | Hai lớp: **Bean Validation** (`@NotNull`, `@Min`…) trên DTO chặn dữ liệu sai định dạng; **quy tắc nghiệp vụ** ở tầng Service (khóa đầy, publish khi chưa có bài học…). |
| **Xử lý lỗi thế nào?** | Tập trung ở `@RestControllerAdvice`. Controller không có `try-catch`, mọi lỗi trả về cùng một định dạng JSON. |
| **Có đăng nhập / phân quyền chưa?** | **Chưa có, và nhóm em ghi rõ đây là hạn chế đã biết trong báo cáo.** Hiện `studentId` do client gửi lên nên chưa chống được giả mạo. Hướng khắc phục là thêm **Spring Security + JWT**, lấy danh tính từ token thay vì từ body request. |
| **Ai làm phần nào?** | *(Xem mục 4 bên dưới — trả lời trung thực.)* |

> **Nguyên tắc vàng:** không biết thì nói **"Dạ chỗ này em chưa nắm chắc, em xin
> phép tìm hiểu thêm ạ."** — Đừng bịa. Thầy hỏi tiếp một câu là lộ ngay.

---

## 4. CÂU HỎI KHÓ NHẤT: "Ai làm phần nào?"

**Thực tế repo:** 17 commit của Cường, 1 commit của Đảo, **Thành chưa có commit
nào** (chưa được mời vào repo).

Đừng nói dối con số này — thầy mở GitHub ra xem được ngay trong 5 giây.

**Cách trả lời an toàn:** nói theo **mảng phụ trách**, không nói theo số commit.
Và quan trọng nhất: **mỗi người phải trả lời được câu hỏi kỹ thuật về mảng của
mình** — đó mới là thứ thầy thật sự chấm.

> "Dạ nhóm em chia theo mảng: bạn A phụ trách thiết kế CSDL và các quan hệ JPA,
> bạn B phụ trách tầng Service, transaction và xử lý lỗi, bạn C phụ trách các
> tính năng nâng cao và phần đóng gói Docker/CI. Phần commit đang tập trung ở
> một bạn do nhóm em gộp lại khi đẩy code, chỗ này nhóm em đang khắc phục ạ."

**⚠️ VIỆC CẦN LÀM NGAY SAU BUỔI BÁO CÁO:** Đảo vào GitHub → repo →
`Settings` → `Collaborators` → mời **`thanhhdev`** quyền **Write**. Rồi để Thành
tự commit ít nhất vài lần trước buổi bảo vệ cuối. Không có commit thì tiêu chí
làm việc nhóm sẽ mất điểm.

---

## 5. CHECKLIST 60 GIÂY TRƯỚC KHI LÊN

- [ ] App đã chạy, dashboard hiện số liệu
- [ ] 5 tab mở sẵn đúng thứ tự
- [ ] Đã thử ghi danh 1 lần cho chắc là chạy được
- [ ] Máy cắm sạc, tắt thông báo
- [ ] Cỡ chữ trình duyệt phóng lên **125–150%** (Ctrl và dấu +) cho thầy nhìn rõ
- [ ] Biết chắc ai nói phần nào
