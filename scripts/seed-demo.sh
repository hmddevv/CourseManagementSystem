#!/usr/bin/env bash
# Seed demo data into the running prod stack via the public REST API.
set -u
BASE=http://localhost:8080/api

# Bodies go through a temp file, never through -d '...'. On Windows/Git Bash the
# Win32 ANSI code page corrupts multi-byte UTF-8 passed as a command-line argument,
# which makes the server reject the JSON with 400 "Failed to read request".
BODY=$(mktemp)
trap 'rm -f "$BODY"' EXIT

send() { # method path [json]
  if [ -n "${3:-}" ]; then
    printf '%s' "$3" >"$BODY"
    curl -s -X "$1" "$BASE/$2" -H "Content-Type: application/json; charset=utf-8" --data-binary @"$BODY"
  else
    curl -s -X "$1" "$BASE/$2"
  fi
}
post() { send POST "$1" "${2:-}"; }
patch() { send PATCH "$1" "${2:-}"; }
id_of() { grep -o '"id":[0-9]*' <<<"$1" | head -1 | cut -d: -f2; }

echo "--- Danh muc ---"
CAT_WEB=$(id_of "$(post categories '{"name":"Lập trình Web","description":"HTML, CSS, JavaScript, Spring Boot"}')")
CAT_DATA=$(id_of "$(post categories '{"name":"Cơ sở dữ liệu","description":"SQL, thiết kế lược đồ, tối ưu truy vấn"}')")
CAT_MOBILE=$(id_of "$(post categories '{"name":"Lập trình di động","description":"Android, iOS, Flutter"}')")
echo "categories: $CAT_WEB $CAT_DATA $CAT_MOBILE"

echo "--- Giang vien ---"
GV1=$(id_of "$(post instructors '{"fullName":"Lý Ngọc Hưng","email":"hung.ly@gdu.edu.vn","expertise":"Java, Spring Boot","bio":"Giảng viên môn Lập trình ứng dụng với Java."}')")
GV2=$(id_of "$(post instructors '{"fullName":"Trần Thị Mai","email":"mai.tran@gdu.edu.vn","expertise":"Cơ sở dữ liệu","bio":"Chuyên về thiết kế và tối ưu CSDL quan hệ."}')")
GV3=$(id_of "$(post instructors '{"fullName":"Phạm Văn Nam","email":"nam.pham@gdu.edu.vn","expertise":"Mobile","bio":"Phát triển ứng dụng di động đa nền tảng."}')")
echo "instructors: $GV1 $GV2 $GV3"

echo "--- Hoc vien ---"
HV1=$(id_of "$(post students '{"fullName":"Hồ Minh Đảo","email":"dao.ho@student.gdu.edu.vn","phone":"0901000001"}')")
HV2=$(id_of "$(post students '{"fullName":"Lê Đình Thành","email":"thanh.le@student.gdu.edu.vn","phone":"0901000002"}')")
HV3=$(id_of "$(post students '{"fullName":"Nguyễn Chí Cường","email":"cuong.nguyen@student.gdu.edu.vn","phone":"0901000003"}')")
HV4=$(id_of "$(post students '{"fullName":"Võ Thị Lan","email":"lan.vo@student.gdu.edu.vn","phone":"0901000004"}')")
HV5=$(id_of "$(post students '{"fullName":"Đặng Quốc Bảo","email":"bao.dang@student.gdu.edu.vn","phone":"0901000005"}')")
echo "students: $HV1 $HV2 $HV3 $HV4 $HV5"

echo "--- Khoa hoc ---"
mkcourse() { # title desc level price capacity hours catId gvId
  id_of "$(post courses "{\"title\":\"$1\",\"description\":\"$2\",\"level\":\"$3\",\"price\":$4,\"capacity\":$5,\"durationHours\":$6,\"categoryId\":$7,\"instructorId\":$8}")"
}
C1=$(mkcourse "Spring Boot từ cơ bản đến nâng cao" "Xây dựng REST API hoàn chỉnh với Spring Boot, JPA và MySQL." INTERMEDIATE 1200000 30 45 "$CAT_WEB" "$GV1")
C2=$(mkcourse "JavaScript hiện đại ES6+" "Nắm vững JavaScript hiện đại và Fetch API để gọi backend." BEGINNER 800000 40 30 "$CAT_WEB" "$GV1")
C3=$(mkcourse "Thiết kế cơ sở dữ liệu quan hệ" "Chuẩn hóa, khóa ngoại, chỉ mục và tối ưu truy vấn." BEGINNER 900000 35 28 "$CAT_DATA" "$GV2")
C4=$(mkcourse "Tối ưu truy vấn SQL nâng cao" "Phân tích kế hoạch thực thi, chỉ mục và xử lý N+1 query." ADVANCED 1500000 20 36 "$CAT_DATA" "$GV2")
C5=$(mkcourse "Lập trình Flutter đa nền tảng" "Xây dựng ứng dụng chạy trên cả Android và iOS." INTERMEDIATE 1100000 25 40 "$CAT_MOBILE" "$GV3")
C6=$(mkcourse "Kiến trúc phân tầng trong Java" "Controller - Service - Repository, DTO và Dependency Injection." ADVANCED 1400000 2 32 "$CAT_WEB" "$GV1")
echo "courses: $C1 $C2 $C3 $C4 $C5 $C6"

echo "--- Bai hoc + xuat ban ---"
addlesson() { post "courses/$1/lessons" "{\"title\":\"$2\",\"content\":\"$3\",\"orderIndex\":$4,\"durationMinutes\":$5}" >/dev/null; }
for C in $C1 $C2 $C3 $C4 $C5 $C6; do
  addlesson "$C" "Giới thiệu khóa học" "Tổng quan mục tiêu và lộ trình học." 1 30
  addlesson "$C" "Cài đặt môi trường" "Chuẩn bị công cụ và chạy ví dụ đầu tiên." 2 45
  addlesson "$C" "Thực hành dự án" "Áp dụng kiến thức vào một dự án nhỏ." 3 60
  patch "courses/$C/publish" >/dev/null
done
echo "da them 3 bai hoc va xuat ban 6 khoa hoc"

echo "--- Ghi danh ---"
enroll() { post enrollments "{\"studentId\":$1,\"courseId\":$2}"; }
E1=$(id_of "$(enroll "$HV1" "$C1")")
E2=$(id_of "$(enroll "$HV2" "$C1")")
E3=$(id_of "$(enroll "$HV3" "$C1")")
E4=$(id_of "$(enroll "$HV1" "$C3")")
E5=$(id_of "$(enroll "$HV4" "$C3")")
E6=$(id_of "$(enroll "$HV5" "$C2")")
E7=$(id_of "$(enroll "$HV2" "$C4")")
echo "enrollments: $E1 $E2 $E3 $E4 $E5 $E6 $E7"

echo "--- Tien do (E1 va E4 hoan thanh -> cap chung chi) ---"
patch "enrollments/$E1/progress" '{"progressPercent":100}' >/dev/null
patch "enrollments/$E4/progress" '{"progressPercent":100}' >/dev/null
patch "enrollments/$E2/progress" '{"progressPercent":60}'  >/dev/null
patch "enrollments/$E3/progress" '{"progressPercent":25}'  >/dev/null
patch "enrollments/$E5/progress" '{"progressPercent":80}'  >/dev/null

echo "--- Danh gia ---"
post "courses/$C1/reviews" "{\"studentId\":$HV1,\"rating\":5,\"comment\":\"Khóa học rất chi tiết, ví dụ dễ hiểu.\"}" >/dev/null
post "courses/$C1/reviews" "{\"studentId\":$HV2,\"rating\":4,\"comment\":\"Nội dung tốt, mong có thêm bài tập.\"}" >/dev/null
post "courses/$C1/reviews" "{\"studentId\":$HV3,\"rating\":5,\"comment\":\"Giảng viên giải thích rõ ràng.\"}" >/dev/null
post "courses/$C3/reviews" "{\"studentId\":$HV1,\"rating\":4,\"comment\":\"Phần chuẩn hóa dữ liệu rất hữu ích.\"}" >/dev/null
post "courses/$C3/reviews" "{\"studentId\":$HV4,\"rating\":5,\"comment\":\"Học xong làm được ngay.\"}" >/dev/null
post "courses/$C2/reviews" "{\"studentId\":$HV5,\"rating\":3,\"comment\":\"Khá ổn nhưng hơi nhanh.\"}" >/dev/null

echo
echo "=== KET QUA ==="
echo "Thong ke:"; curl -s "$BASE/courses/statistics"; echo
echo "Xep hang:"; curl -s "$BASE/courses/top-rated" | head -c 500; echo
echo
echo "GHI NHO CHO DEMO:"
echo "  Khoa 'Kiến trúc phân tầng trong Java' (id=$C6) suc chua 2 - dung de demo khoa day"
echo "  Hoc vien 'Hồ Minh Đảo' (id=$HV1) da co 2 chung chi"
echo "  Ghi danh trung: studentId=$HV1 + courseId=$C1 -> se bao loi"
