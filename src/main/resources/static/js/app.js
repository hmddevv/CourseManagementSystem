/* ============================================================
   Course Management System - Frontend (vanilla JS + Fetch API)
   Goi REST API cua Spring Boot. Khong dung framework ngoai.
   ============================================================ */

const API = "/api";

/* ---------- HTTP helper: bao quanh ApiResponse/ErrorResponse ---------- */
async function http(method, url, body) {
    const opt = { method, headers: { "Content-Type": "application/json" } };
    if (body !== undefined) opt.body = JSON.stringify(body);
    const res = await fetch(API + url, opt);
    if (res.status === 204) return null;
    const json = await res.json().catch(() => ({}));
    if (!res.ok) {
        let msg = json.message || ("Loi " + res.status);
        if (json.fieldErrors && json.fieldErrors.length) {
            msg += ": " + json.fieldErrors.map(f => f.field + " - " + f.message).join("; ");
        }
        throw new Error(msg);
    }
    return json.data !== undefined ? json.data : json;
}
const api = {
    get: (u) => http("GET", u),
    post: (u, b) => http("POST", u, b),
    put: (u, b) => http("PUT", u, b),
    patch: (u, b) => http("PATCH", u, b),
    del: (u) => http("DELETE", u),
};

/* ---------- Toast ---------- */
function toast(msg, type = "success") {
    const t = document.getElementById("toast");
    t.textContent = msg;
    t.className = "toast show " + type;
    setTimeout(() => (t.className = "toast"), 2600);
}

/* ---------- Utils ---------- */
const money = (n) => (n == null ? "0" : Number(n).toLocaleString("vi-VN")) + "đ";
const esc = (s) => (s == null ? "" : String(s).replace(/[&<>"']/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c])));
const badge = (v, extra = "") => `<span class="badge ${extra} ${esc(v)}">${esc(v)}</span>`;
/** Hien thi diem danh gia dang sao, vi du 4.5 -> "★★★★☆ 4.5 (12)". */
const stars = (avg, count) => {
    if (!count) return `<span class="muted">chưa có</span>`;
    const filled = Math.round(avg);
    return `<span class="stars" title="${avg}/5 từ ${count} đánh giá">${"★".repeat(filled)}${"☆".repeat(5 - filled)}</span> ${avg} <span class="muted">(${count})</span>`;
};

/* ---------- Modal ---------- */
const Modal = {
    _submit: null,
    open(title, fieldsHtml, onSubmit) {
        document.getElementById("modal-title").textContent = title;
        document.getElementById("modal-form").innerHTML = fieldsHtml;
        this._submit = onSubmit;
        document.getElementById("modal-overlay").classList.add("open");
    },
    close(e) {
        if (e && e.target && e.target.id !== "modal-overlay" && e.type === "click" && e.currentTarget.id === "modal-overlay") return;
        document.getElementById("modal-overlay").classList.remove("open");
        this._submit = null;
    },
    /** Modal chi de xem, khong co form gui di (vi du: xem chung chi). */
    info(title, html) {
        this.open(title, html, null);
    },
    val(name) { const el = document.querySelector(`#modal-form [name="${name}"]`); return el ? el.value : null; },
    async submit() {
        if (!this._submit) return;
        try {
            await this._submit();
            this.close();
        } catch (err) {
            toast(err.message, "error");
        }
    }
};
function field(label, name, type = "text", value = "", opts) {
    if (type === "select") {
        const options = opts.map(o => `<option value="${esc(o.value)}" ${String(o.value) === String(value) ? "selected" : ""}>${esc(o.label)}</option>`).join("");
        return `<div class="field"><label>${label}</label><select name="${name}">${options}</select></div>`;
    }
    if (type === "textarea") {
        return `<div class="field"><label>${label}</label><textarea name="${name}">${esc(value)}</textarea></div>`;
    }
    return `<div class="field"><label>${label}</label><input name="${name}" type="${type}" value="${esc(value)}"></div>`;
}

/* ---------- Tab navigation ---------- */
document.querySelectorAll(".tab").forEach(tab => {
    tab.addEventListener("click", () => {
        document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"));
        document.querySelectorAll(".view").forEach(v => v.classList.remove("active"));
        tab.classList.add("active");
        const view = tab.dataset.view;
        document.getElementById("view-" + view).classList.add("active");
        Router[view] && Router[view]();
    });
});

/* ============================================================
   DASHBOARD
   ============================================================ */
const Dashboard = {
    async load() {
        try {
            const s = await api.get("/courses/statistics");
            document.getElementById("stat-cards").innerHTML = [
                ["Tổng khóa học", s.totalCourses],
                ["Đã xuất bản", s.publishedCourses],
                ["Bản nháp", s.draftCourses],
                ["Học viên", s.totalStudents],
                ["Lượt đang học", s.totalActiveEnrollments],
            ].map(([label, num]) => `<div class="stat-card"><div class="num">${num}</div><div class="label">${label}</div></div>`).join("");
            const pop = s.popularCourses || [];
            document.getElementById("popular-courses").innerHTML = pop.length
                ? pop.map(p => `<div class="popular-item"><span>${esc(p.title)}</span><span class="count">${p.activeEnrollments} học viên</span></div>`).join("")
                : `<div class="empty">Chưa có dữ liệu ghi danh</div>`;
        } catch (e) { toast(e.message, "error"); }
    }
};

/* ============================================================
   COURSES
   ============================================================ */
const CourseUI = {
    page: 0,
    async reload() { this.page = 0; await this.load(); },
    async load() {
        const kw = document.getElementById("course-keyword").value;
        const status = document.getElementById("course-status").value;
        const level = document.getElementById("course-level").value;
        const sort = document.getElementById("course-sort").value;
        const params = new URLSearchParams({ page: this.page, size: 8, sort });
        if (kw) params.set("keyword", kw);
        if (status) params.set("status", status);
        if (level) params.set("level", level);
        try {
            const pg = await api.get("/courses?" + params.toString());
            this.render(pg);
        } catch (e) { toast(e.message, "error"); }
    },
    render(pg) {
        const rows = pg.content.map(c => `
            <tr>
                <td><strong>${esc(c.title)}</strong><br><span class="muted">${esc(c.categoryName)} · ${esc(c.instructorName)}</span></td>
                <td>${badge(c.level, "level")}</td>
                <td>${badge(c.status)}</td>
                <td>${money(c.price)}</td>
                <td>${c.activeEnrollments}/${c.capacity}<br><span class="muted">${c.lessonCount} bài</span></td>
                <td>${stars(c.averageRating, c.reviewCount)}</td>
                <td><div class="actions">
                    ${c.status === "DRAFT" ? `<button class="btn sm primary" onclick="CourseUI.publish(${c.id})">Xuất bản</button>` : ""}
                    ${c.status === "PUBLISHED" ? `<button class="btn sm" onclick="CourseUI.archive(${c.id})">Lưu trữ</button>` : ""}
                    <button class="btn sm" onclick="CourseUI.lessons(${c.id})">Bài học</button>
                    <button class="btn sm" onclick="ReviewUI.open(${c.id})">Đánh giá</button>
                    <button class="btn sm" onclick="CourseUI.openForm(${c.id})">Sửa</button>
                    <button class="btn sm danger" onclick="CourseUI.remove(${c.id})">Xóa</button>
                </div></td>
            </tr>`).join("");
        document.getElementById("courses-table").innerHTML = pg.content.length ? `
            <table><thead><tr><th>Khóa học</th><th>Trình độ</th><th>Trạng thái</th><th>Giá</th><th>Ghi danh</th><th>Đánh giá</th><th>Thao tác</th></tr></thead>
            <tbody>${rows}</tbody></table>` : `<div class="empty">Không có khóa học nào</div>`;
        this.renderPager(pg);
    },
    renderPager(pg) {
        document.getElementById("courses-pager").innerHTML = `
            <button class="btn sm" ${pg.first ? "disabled" : ""} onclick="CourseUI.go(${pg.page - 1})">‹ Trước</button>
            <span>Trang ${pg.page + 1}/${Math.max(1, pg.totalPages)} · ${pg.totalElements} khóa học</span>
            <button class="btn sm" ${pg.last ? "disabled" : ""} onclick="CourseUI.go(${pg.page + 1})">Sau ›</button>`;
    },
    go(p) { this.page = p; this.load(); },

    async openForm(id) {
        const cats = await api.get("/categories/all");
        const ins = await api.get("/instructors/all");
        let c = { title: "", description: "", level: "BEGINNER", price: 0, capacity: 20, durationHours: 10, categoryId: cats[0]?.id, instructorId: ins[0]?.id };
        if (id) c = await api.get("/courses/" + id);
        const levels = ["BEGINNER", "INTERMEDIATE", "ADVANCED"].map(v => ({ value: v, label: v }));
        Modal.open(id ? "Sửa khóa học" : "Thêm khóa học",
            field("Tiêu đề", "title", "text", c.title) +
            field("Mô tả", "description", "textarea", c.description) +
            field("Trình độ", "level", "select", c.level, levels) +
            field("Giá (đ)", "price", "number", c.price) +
            field("Sức chứa", "capacity", "number", c.capacity) +
            field("Thời lượng (giờ)", "durationHours", "number", c.durationHours) +
            field("Danh mục", "categoryId", "select", c.categoryId, cats.map(x => ({ value: x.id, label: x.name }))) +
            field("Giảng viên", "instructorId", "select", c.instructorId, ins.map(x => ({ value: x.id, label: x.fullName }))),
            async () => {
                const body = {
                    title: Modal.val("title"), description: Modal.val("description"),
                    level: Modal.val("level"), price: Number(Modal.val("price")),
                    capacity: Number(Modal.val("capacity")), durationHours: Number(Modal.val("durationHours")),
                    categoryId: Number(Modal.val("categoryId")), instructorId: Number(Modal.val("instructorId"))
                };
                if (id) await api.put("/courses/" + id, body); else await api.post("/courses", body);
                toast("Đã lưu khóa học"); this.load(); Dashboard.load();
            });
    },
    async publish(id) { try { await api.patch(`/courses/${id}/publish`); toast("Đã xuất bản"); this.load(); } catch (e) { toast(e.message, "error"); } },
    async archive(id) { try { await api.patch(`/courses/${id}/archive`); toast("Đã lưu trữ"); this.load(); } catch (e) { toast(e.message, "error"); } },
    async remove(id) { if (!confirm("Xóa khóa học này?")) return; try { await api.del("/courses/" + id); toast("Đã xóa"); this.load(); Dashboard.load(); } catch (e) { toast(e.message, "error"); } },

    // Tieu de duoc lay lai tu API thay vi noi chuoi vao thuoc tinh onclick:
    // tieu de chua dau nhay don se lam vo cu phap JS trong onclick (escape HTML
    // khong cuu duoc vi trinh duyet giai ma HTML truoc roi moi phan tich JS).
    async lessons(courseId) {
        const [course, list] = await Promise.all([
            api.get("/courses/" + courseId),
            api.get(`/courses/${courseId}/lessons`)
        ]);
        const title = course.title;
        const rows = list.map(l => `<div class="popular-item"><span><strong>${l.orderIndex}.</strong> ${esc(l.title)} <span class="muted">(${l.durationMinutes || 0}')</span></span>
            <button class="btn sm danger" onclick="CourseUI.delLesson(${l.id}, ${courseId})">Xóa</button></div>`).join("");
        Modal.open("Bài học: " + title,
            `<div style="display:flex;flex-direction:column;gap:8px">${rows || '<div class="empty">Chưa có bài học</div>'}</div>
             <hr style="margin:14px 0;border:none;border-top:1px solid #eee">
             ${field("Tên bài học mới", "ltitle")}
             ${field("Thứ tự", "lorder", "number", (list.length + 1))}
             ${field("Thời lượng (phút)", "lmin", "number", 30)}`,
            async () => {
                await api.post(`/courses/${courseId}/lessons`, { title: Modal.val("ltitle"), orderIndex: Number(Modal.val("lorder")), durationMinutes: Number(Modal.val("lmin")) });
                toast("Đã thêm bài học"); this.lessons(courseId); this.load();
            });
    },
    async delLesson(lessonId, courseId) { try { await api.del("/lessons/" + lessonId); toast("Đã xóa bài học"); this.lessons(courseId); } catch (e) { toast(e.message, "error"); } }
};

/* ============================================================
   ENROLLMENTS
   ============================================================ */
const EnrollUI = {
    async load() {
        // nap dropdown khoa hoc
        const sel = document.getElementById("enroll-course");
        if (sel.options.length <= 1) {
            const pg = await api.get("/courses?size=100");
            pg.content.forEach(c => { const o = document.createElement("option"); o.value = c.id; o.textContent = c.title; sel.appendChild(o); });
        }
        this.reload();
    },
    async reload() {
        const courseId = document.getElementById("enroll-course").value;
        if (!courseId) { document.getElementById("enroll-table").innerHTML = `<div class="empty">Chọn một khóa học để xem danh sách ghi danh</div>`; return; }
        try {
            const pg = await api.get(`/enrollments?courseId=${courseId}&size=50`);
            const rows = pg.content.map(e => `
                <tr>
                    <td>${esc(e.studentName)}</td>
                    <td>${badge(e.status)}</td>
                    <td><div class="progress"><div style="width:${e.progressPercent}%"></div></div> ${e.progressPercent}%</td>
                    <td>${e.enrolledAt ? e.enrolledAt.substring(0, 10) : ""}</td>
                    <td><div class="actions">
                        <button class="btn sm" onclick="EnrollUI.progress(${e.id}, ${e.progressPercent})">Tiến độ</button>
                        ${e.status === "COMPLETED" ? `<button class="btn sm" onclick="EnrollUI.certificate(${e.id})">Chứng chỉ</button>` : ""}
                        ${e.status !== "CANCELLED" ? `<button class="btn sm danger" onclick="EnrollUI.cancel(${e.id})">Hủy</button>` : ""}
                    </div></td>
                </tr>`).join("");
            document.getElementById("enroll-table").innerHTML = pg.content.length ? `
                <table><thead><tr><th>Học viên</th><th>Trạng thái</th><th>Tiến độ</th><th>Ngày ghi danh</th><th>Thao tác</th></tr></thead>
                <tbody>${rows}</tbody></table>` : `<div class="empty">Chưa có học viên ghi danh</div>`;
        } catch (e) { toast(e.message, "error"); }
    },
    async openForm() {
        const students = await api.get("/students/all");
        const pg = await api.get("/courses?status=PUBLISHED&size=100");
        if (!pg.content.length) { toast("Chưa có khóa học PUBLISHED để ghi danh", "error"); return; }
        Modal.open("Ghi danh mới",
            field("Học viên", "studentId", "select", "", students.map(s => ({ value: s.id, label: s.fullName + " (" + s.email + ")" }))) +
            field("Khóa học", "courseId", "select", "", pg.content.map(c => ({ value: c.id, label: c.title }))),
            async () => {
                await api.post("/enrollments", { studentId: Number(Modal.val("studentId")), courseId: Number(Modal.val("courseId")) });
                toast("Ghi danh thành công");
                document.getElementById("enroll-course").value = Modal.val("courseId");
                this.reload(); Dashboard.load();
            });
    },
    async progress(id, cur) {
        Modal.open("Cập nhật tiến độ", field("Tiến độ (%)", "p", "number", cur), async () => {
            await api.patch(`/enrollments/${id}/progress`, { progressPercent: Number(Modal.val("p")) });
            toast("Đã cập nhật"); this.reload();
        });
    },
    async cancel(id) { if (!confirm("Hủy ghi danh này?")) return; try { await api.patch(`/enrollments/${id}/cancel`); toast("Đã hủy"); this.reload(); } catch (e) { toast(e.message, "error"); } },

    /** Chung chi duoc cap tu dong khi tien do dat 100%, khong cap thu cong. */
    async certificate(enrollmentId) {
        try {
            const c = await api.get(`/certificates/enrollments/${enrollmentId}`);
            Modal.info("Chứng chỉ hoàn thành", `
                <div class="certificate">
                    <div class="cert-code">${esc(c.code)}</div>
                    <p>Chứng nhận <strong>${esc(c.studentName)}</strong></p>
                    <p>đã hoàn thành khóa học <strong>${esc(c.courseTitle)}</strong></p>
                    <p class="muted">Cấp ngày ${c.issuedAt ? c.issuedAt.substring(0, 10) : ""}</p>
                </div>`);
        } catch (e) { toast("Ghi danh này chưa có chứng chỉ", "error"); }
    }
};

/* ============================================================
   REVIEWS - danh gia khoa hoc va bang xep hang
   ============================================================ */
const ReviewUI = {
    /** Xem danh sach danh gia cua mot khoa hoc va gui danh gia moi. */
    async open(courseId) {
        try {
            const [course, page, students] = await Promise.all([
                api.get("/courses/" + courseId),
                api.get(`/courses/${courseId}/reviews?size=50`),
                api.get("/students/all")
            ]);
            const list = page.content.length
                ? page.content.map(r => `<div class="popular-item">
                        <span><strong>${esc(r.studentName)}</strong> ${stars(r.rating, 1)}<br>
                        <span class="muted">${esc(r.comment) || "(không có nhận xét)"}</span></span></div>`).join("")
                : `<div class="empty">Chưa có đánh giá nào</div>`;

            Modal.open("Đánh giá: " + course.title,
                `<div style="display:flex;flex-direction:column;gap:8px">${list}</div>
                 <hr style="margin:14px 0;border:none;border-top:1px solid #eee">
                 ${field("Học viên", "rvStudent", "select", "", students.map(s => ({ value: s.id, label: s.fullName })))}
                 ${field("Điểm", "rvRating", "select", 5, [5, 4, 3, 2, 1].map(v => ({ value: v, label: "★".repeat(v) })))}
                 ${field("Nhận xét", "rvComment", "textarea", "")}`,
                async () => {
                    await api.post(`/courses/${courseId}/reviews`, {
                        studentId: Number(Modal.val("rvStudent")),
                        rating: Number(Modal.val("rvRating")),
                        comment: Modal.val("rvComment")
                    });
                    toast("Đã gửi đánh giá");
                    CourseUI.load();
                });
        } catch (e) { toast(e.message, "error"); }
    }
};

const RankingUI = {
    async load() {
        try {
            const pg = await api.get("/courses/top-rated?size=20");
            const rows = pg.content.map((c, i) => `<tr>
                <td><strong>#${i + 1}</strong></td>
                <td><strong>${esc(c.title)}</strong><br><span class="muted">${esc(c.categoryName)} · ${esc(c.instructorName)}</span></td>
                <td>${stars(c.averageRating, c.reviewCount)}</td>
            </tr>`).join("");
            document.getElementById("ranking-table").innerHTML = pg.content.length
                ? `<table><thead><tr><th>Hạng</th><th>Khóa học</th><th>Điểm trung bình</th></tr></thead><tbody>${rows}</tbody></table>`
                : `<div class="empty">Chưa có khóa học nào được đánh giá</div>`;
        } catch (e) { toast(e.message, "error"); }
    }
};

/* ============================================================
   AUDIT LOG - nhat ky thao tac (chi doc)
   ============================================================ */
const AuditUI = {
    load() { this.reload(); },
    async reload() {
        const entity = document.getElementById("audit-entity").value;
        const action = document.getElementById("audit-action").value;
        const params = new URLSearchParams({ size: 50, sort: "createdAt,desc" });
        if (entity) params.set("entityName", entity);
        if (action) params.set("action", action);
        try {
            const pg = await api.get("/audit-logs?" + params.toString());
            const rows = pg.content.map(l => `<tr>
                <td class="muted">${l.createdAt ? l.createdAt.substring(0, 19).replace("T", " ") : ""}</td>
                <td>${badge(l.action)}</td>
                <td><strong>${esc(l.entityName)}</strong>${l.entityId ? " #" + l.entityId : ""}</td>
                <td>${esc(l.actor)}</td>
                <td class="muted">${esc(l.detail)}</td>
            </tr>`).join("");
            document.getElementById("audit-table").innerHTML = pg.content.length
                ? `<table><thead><tr><th>Thời điểm</th><th>Hành động</th><th>Đối tượng</th><th>Người thực hiện</th><th>Chi tiết</th></tr></thead><tbody>${rows}</tbody></table>`
                : `<div class="empty">Chưa có nhật ký nào</div>`;
        } catch (e) { toast(e.message, "error"); }
    }
};

/* ============================================================
   Generic CRUD cho Category / Instructor / Student
   ============================================================ */
function makeCrud(cfg) {
    return {
        async load() {
            try {
                const pg = await api.get(`/${cfg.path}?size=100`);
                const rows = pg.content.map(cfg.row).join("");
                document.getElementById(cfg.tableId).innerHTML = pg.content.length
                    ? `<table><thead><tr>${cfg.headers}</tr></thead><tbody>${rows}</tbody></table>`
                    : `<div class="empty">Chưa có dữ liệu</div>`;
            } catch (e) { toast(e.message, "error"); }
        },
        async openForm(id) {
            let obj = cfg.empty();
            if (id) obj = await api.get(`/${cfg.path}/${id}`);
            Modal.open(id ? cfg.editTitle : cfg.addTitle, cfg.form(obj), async () => {
                const body = cfg.collect();
                if (id) await api.put(`/${cfg.path}/${id}`, body); else await api.post(`/${cfg.path}`, body);
                toast("Đã lưu"); this.load();
            });
        },
        async remove(id) {
            if (!confirm("Xóa mục này?")) return;
            try { await api.del(`/${cfg.path}/${id}`); toast("Đã xóa"); this.load(); }
            catch (e) { toast(e.message, "error"); }
        }
    };
}

const CategoryUI = makeCrud({
    path: "categories", tableId: "categories-table",
    addTitle: "Thêm danh mục", editTitle: "Sửa danh mục",
    headers: "<th>Tên</th><th>Mô tả</th><th>Số khóa học</th><th>Thao tác</th>",
    empty: () => ({ name: "", description: "" }),
    row: (c) => `<tr><td><strong>${esc(c.name)}</strong></td><td class="muted">${esc(c.description)}</td><td>${c.courseCount}</td>
        <td><div class="actions"><button class="btn sm" onclick="CategoryUI.openForm(${c.id})">Sửa</button>
        <button class="btn sm danger" onclick="CategoryUI.remove(${c.id})">Xóa</button></div></td></tr>`,
    form: (c) => field("Tên danh mục", "name", "text", c.name) + field("Mô tả", "description", "textarea", c.description),
    collect: () => ({ name: Modal.val("name"), description: Modal.val("description") })
});

const InstructorUI = makeCrud({
    path: "instructors", tableId: "instructors-table",
    addTitle: "Thêm giảng viên", editTitle: "Sửa giảng viên",
    headers: "<th>Họ tên</th><th>Email</th><th>Chuyên môn</th><th>Số khóa</th><th>Thao tác</th>",
    empty: () => ({ fullName: "", email: "", expertise: "", bio: "" }),
    row: (i) => `<tr><td><strong>${esc(i.fullName)}</strong></td><td class="muted">${esc(i.email)}</td><td>${esc(i.expertise)}</td><td>${i.courseCount}</td>
        <td><div class="actions"><button class="btn sm" onclick="InstructorUI.openForm(${i.id})">Sửa</button>
        <button class="btn sm danger" onclick="InstructorUI.remove(${i.id})">Xóa</button></div></td></tr>`,
    form: (i) => field("Họ tên", "fullName", "text", i.fullName) + field("Email", "email", "email", i.email) +
        field("Chuyên môn", "expertise", "text", i.expertise) + field("Giới thiệu", "bio", "textarea", i.bio),
    collect: () => ({ fullName: Modal.val("fullName"), email: Modal.val("email"), expertise: Modal.val("expertise"), bio: Modal.val("bio") })
});

const StudentUI = makeCrud({
    path: "students", tableId: "students-table",
    addTitle: "Thêm học viên", editTitle: "Sửa học viên",
    headers: "<th>Họ tên</th><th>Email</th><th>SĐT</th><th>Số ghi danh</th><th>Thao tác</th>",
    empty: () => ({ fullName: "", email: "", phone: "" }),
    row: (s) => `<tr><td><strong>${esc(s.fullName)}</strong></td><td class="muted">${esc(s.email)}</td><td>${esc(s.phone)}</td><td>${s.enrollmentCount}</td>
        <td><div class="actions"><button class="btn sm" onclick="StudentUI.openForm(${s.id})">Sửa</button>
        <button class="btn sm danger" onclick="StudentUI.remove(${s.id})">Xóa</button></div></td></tr>`,
    form: (s) => field("Họ tên", "fullName", "text", s.fullName) + field("Email", "email", "email", s.email) + field("Số điện thoại", "phone", "text", s.phone),
    collect: () => ({ fullName: Modal.val("fullName"), email: Modal.val("email"), phone: Modal.val("phone") })
});

/* ---------- Router ---------- */
const Router = {
    dashboard: () => Dashboard.load(),
    courses: () => CourseUI.load(),
    enrollments: () => EnrollUI.load(),
    ranking: () => RankingUI.load(),
    audit: () => AuditUI.load(),
    categories: () => CategoryUI.load(),
    instructors: () => InstructorUI.load(),
    students: () => StudentUI.load(),
};

/* ---------- Boot ---------- */
Dashboard.load();
