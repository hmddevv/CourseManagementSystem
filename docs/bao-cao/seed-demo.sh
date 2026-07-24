#!/usr/bin/env bash
# Seed demo data into the running prod stack via the public REST API.
set -u
BASE=http://localhost:8080/api

post() { curl -s -X POST "$BASE/$1" -H "Content-Type: application/json" -d "$2"; }
patch() { curl -s -X PATCH "$BASE/$1" -H "Content-Type: application/json" ${2:+-d "$2"}; }
id_of() { grep -o '"id":[0-9]*' <<<"$1" | head -1 | cut -d: -f2; }

echo "--- Danh muc ---"
CAT_WEB=$(id_of "$(post categories '{"name":"Lap trinh Web","description":"HTML, CSS, JavaScript, Spring Boot"}')")
CAT_DATA=$(id_of "$(post categories '{"name":"Co so du lieu","description":"SQL, thiet ke luoc do, toi uu truy van"}')")
CAT_MOBILE=$(id_of "$(post categories '{"name":"Lap trinh di dong","description":"Android, iOS, Flutter"}')")
echo "categories: $CAT_WEB $CAT_DATA $CAT_MOBILE"

echo "--- Giang vien ---"
GV1=$(id_of "$(post instructors '{"fullName":"Ly Ngoc Hung","email":"hung.ly@gdu.edu.vn","expertise":"Java, Spring Boot","bio":"Giang vien mon Lap trinh ung dung voi Java."}')")
GV2=$(id_of "$(post instructors '{"fullName":"Tran Thi Mai","email":"mai.tran@gdu.edu.vn","expertise":"Co so du lieu","bio":"Chuyen ve thiet ke va toi uu CSDL quan he."}')")
GV3=$(id_of "$(post instructors '{"fullName":"Pham Van Nam","email":"nam.pham@gdu.edu.vn","expertise":"Mobile","bio":"Phat trien ung dung di dong da nen tang."}')")
echo "instructors: $GV1 $GV2 $GV3"

echo "--- Hoc vien ---"
HV1=$(id_of "$(post students '{"fullName":"Ho Minh Dao","email":"dao.ho@student.gdu.edu.vn","phone":"0901000001"}')")
HV2=$(id_of "$(post students '{"fullName":"Le Dinh Thanh","email":"thanh.le@student.gdu.edu.vn","phone":"0901000002"}')")
HV3=$(id_of "$(post students '{"fullName":"Nguyen Chi Cuong","email":"cuong.nguyen@student.gdu.edu.vn","phone":"0901000003"}')")
HV4=$(id_of "$(post students '{"fullName":"Vo Thi Lan","email":"lan.vo@student.gdu.edu.vn","phone":"0901000004"}')")
HV5=$(id_of "$(post students '{"fullName":"Dang Quoc Bao","email":"bao.dang@student.gdu.edu.vn","phone":"0901000005"}')")
echo "students: $HV1 $HV2 $HV3 $HV4 $HV5"

echo "--- Khoa hoc ---"
mkcourse() { # title desc level price capacity hours catId gvId
  id_of "$(post courses "{\"title\":\"$1\",\"description\":\"$2\",\"level\":\"$3\",\"price\":$4,\"capacity\":$5,\"durationHours\":$6,\"categoryId\":$7,\"instructorId\":$8}")"
}
C1=$(mkcourse "Spring Boot tu co ban den nang cao" "Xay dung REST API hoan chinh voi Spring Boot, JPA va MySQL." INTERMEDIATE 1200000 30 45 "$CAT_WEB" "$GV1")
C2=$(mkcourse "JavaScript hien dai ES6+" "Nam vung JavaScript hien dai va Fetch API de goi backend." BEGINNER 800000 40 30 "$CAT_WEB" "$GV1")
C3=$(mkcourse "Thiet ke co so du lieu quan he" "Chuan hoa, khoa ngoai, chi muc va toi uu truy van." BEGINNER 900000 35 28 "$CAT_DATA" "$GV2")
C4=$(mkcourse "Toi uu truy van SQL nang cao" "Phan tich ke hoach thuc thi, chi muc va xu ly N+1 query." ADVANCED 1500000 20 36 "$CAT_DATA" "$GV2")
C5=$(mkcourse "Lap trinh Flutter da nen tang" "Xay dung ung dung chay tren ca Android va iOS." INTERMEDIATE 1100000 25 40 "$CAT_MOBILE" "$GV3")
C6=$(mkcourse "Kien truc phan tang trong Java" "Controller - Service - Repository, DTO va Dependency Injection." ADVANCED 1400000 2 32 "$CAT_WEB" "$GV1")
echo "courses: $C1 $C2 $C3 $C4 $C5 $C6"

echo "--- Bai hoc + xuat ban ---"
addlesson() { post "courses/$1/lessons" "{\"title\":\"$2\",\"content\":\"$3\",\"orderIndex\":$4,\"durationMinutes\":$5}" >/dev/null; }
for C in $C1 $C2 $C3 $C4 $C5 $C6; do
  addlesson "$C" "Gioi thieu khoa hoc" "Tong quan muc tieu va lo trinh hoc." 1 30
  addlesson "$C" "Cai dat moi truong" "Chuan bi cong cu va chay vi du dau tien." 2 45
  addlesson "$C" "Thuc hanh du an" "Ap dung kien thuc vao mot du an nho." 3 60
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
post "courses/$C1/reviews" "{\"studentId\":$HV1,\"rating\":5,\"comment\":\"Khoa hoc rat chi tiet, vi du de hieu.\"}" >/dev/null
post "courses/$C1/reviews" "{\"studentId\":$HV2,\"rating\":4,\"comment\":\"Noi dung tot, mong co them bai tap.\"}" >/dev/null
post "courses/$C1/reviews" "{\"studentId\":$HV3,\"rating\":5,\"comment\":\"Giang vien giai thich ro rang.\"}" >/dev/null
post "courses/$C3/reviews" "{\"studentId\":$HV1,\"rating\":4,\"comment\":\"Phan chuan hoa du lieu rat huu ich.\"}" >/dev/null
post "courses/$C3/reviews" "{\"studentId\":$HV4,\"rating\":5,\"comment\":\"Hoc xong lam duoc ngay.\"}" >/dev/null
post "courses/$C2/reviews" "{\"studentId\":$HV5,\"rating\":3,\"comment\":\"Kha on nhung hoi nhanh.\"}" >/dev/null

echo
echo "=== KET QUA ==="
echo "Thong ke:"; curl -s "$BASE/courses/statistics"; echo
echo "Xep hang:"; curl -s "$BASE/courses/top-rated" | head -c 500; echo
echo
echo "GHI NHO CHO DEMO:"
echo "  Khoa 'Kien truc phan tang trong Java' (id=$C6) suc chua 2 - dung de demo khoa day"
echo "  Hoc vien 'Ho Minh Dao' (id=$HV1) da co 2 chung chi"
echo "  Ghi danh trung: studentId=$HV1 + courseId=$C1 -> se bao loi"
