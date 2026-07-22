-- =====================================================================
-- HE THONG QUAN LY KHOA HOC - LUOC DO CO SO DU LIEU (DDL)
-- =====================================================================
-- File nay KHONG viet tay. DDL duoc sinh truc tiep tu metadata cua
-- Hibernate (dialect MySQL 8) nen luon khop 100% voi cac entity JPA
-- trong `src/main/java/com/university/coursemanagement/entity/`.
--
-- Lenh sinh lai file:
--   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev \
--     -Dspring-boot.run.arguments="\
--        --spring.main.web-application-type=none \
--        --spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create \
--        --spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=docs/schema.sql \
--        --spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect"
--
-- Luu y: ten constraint dang `UKxxxx` / `FKxxxx` la ten Hibernate tu sinh
-- khi entity khong dat ten tuong minh. Cac rang buoc nghiep vu quan trong
-- (`uk_enrollment_student_course`, `uk_review_student_course`,
-- `uk_certificate_enrollment`, `uk_certificate_code`) deu duoc dat ten
-- tuong minh trong entity de doc log loi de hieu.
--
-- Moi bang deu ke thua BaseEntity nen co chung 6 cot ky thuat:
--   id, created_at, updated_at, created_by, updated_by, version
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. categories - Danh muc khoa hoc
-- ---------------------------------------------------------------------
create table categories (
    id          bigint       not null auto_increment,
    name        varchar(100) not null,
    description varchar(500),
    created_at  datetime(6)  not null,
    updated_at  datetime(6),
    created_by  varchar(100),
    updated_by  varchar(100),
    version     bigint,
    primary key (id)
) engine=InnoDB;

-- ---------------------------------------------------------------------
-- 2. instructors - Giang vien
-- ---------------------------------------------------------------------
create table instructors (
    id         bigint        not null auto_increment,
    full_name  varchar(150)  not null,
    email      varchar(150)  not null,
    expertise  varchar(100),
    bio        varchar(1000),
    created_at datetime(6)   not null,
    updated_at datetime(6),
    created_by varchar(100),
    updated_by varchar(100),
    version    bigint,
    primary key (id)
) engine=InnoDB;

-- ---------------------------------------------------------------------
-- 3. students - Hoc vien
-- ---------------------------------------------------------------------
create table students (
    id         bigint       not null auto_increment,
    full_name  varchar(150) not null,
    email      varchar(150) not null,
    phone      varchar(20),
    created_at datetime(6)  not null,
    updated_at datetime(6),
    created_by varchar(100),
    updated_by varchar(100),
    version    bigint,
    primary key (id)
) engine=InnoDB;

-- ---------------------------------------------------------------------
-- 4. courses - Khoa hoc (entity trung tam)
-- ---------------------------------------------------------------------
create table courses (
    id             bigint        not null auto_increment,
    title          varchar(200)  not null,
    description    varchar(2000),
    level          enum ('BEGINNER','INTERMEDIATE','ADVANCED')  not null,
    status         enum ('DRAFT','PUBLISHED','ARCHIVED')        not null,
    price          decimal(12,2) not null,
    capacity       integer       not null,
    duration_hours integer,
    category_id    bigint        not null,
    instructor_id  bigint        not null,
    created_at     datetime(6)   not null,
    updated_at     datetime(6),
    created_by     varchar(100),
    updated_by     varchar(100),
    version        bigint,
    primary key (id)
) engine=InnoDB;

-- ---------------------------------------------------------------------
-- 5. lessons - Bai hoc thuoc khoa hoc
-- ---------------------------------------------------------------------
create table lessons (
    id               bigint        not null auto_increment,
    title            varchar(200)  not null,
    content          varchar(4000),
    order_index      integer       not null,
    duration_minutes integer,
    course_id        bigint        not null,
    created_at       datetime(6)   not null,
    updated_at       datetime(6),
    created_by       varchar(100),
    updated_by       varchar(100),
    version          bigint,
    primary key (id)
) engine=InnoDB;

-- ---------------------------------------------------------------------
-- 6. enrollments - Ghi danh (bang trung gian mang du lieu nghiep vu)
-- ---------------------------------------------------------------------
create table enrollments (
    id               bigint      not null auto_increment,
    student_id       bigint      not null,
    course_id        bigint      not null,
    status           enum ('ACTIVE','COMPLETED','CANCELLED') not null,
    progress_percent integer     not null,
    enrolled_at      datetime(6) not null,
    completed_at     datetime(6),
    created_at       datetime(6) not null,
    updated_at       datetime(6),
    created_by       varchar(100),
    updated_by       varchar(100),
    version          bigint,
    primary key (id)
) engine=InnoDB;

-- ---------------------------------------------------------------------
-- 7. reviews - Danh gia khoa hoc (1-5 sao)
-- ---------------------------------------------------------------------
create table reviews (
    id         bigint        not null auto_increment,
    student_id bigint        not null,
    course_id  bigint        not null,
    rating     integer       not null,
    comment    varchar(1000),
    created_at datetime(6)   not null,
    updated_at datetime(6),
    created_by varchar(100),
    updated_by varchar(100),
    version    bigint,
    primary key (id)
) engine=InnoDB;

-- ---------------------------------------------------------------------
-- 8. certificates - Chung chi hoan thanh khoa hoc
-- ---------------------------------------------------------------------
create table certificates (
    id            bigint       not null auto_increment,
    enrollment_id bigint       not null,
    code          varchar(60)  not null,
    issued_at     datetime(6)  not null,
    student_name  varchar(150) not null,
    course_title  varchar(200) not null,
    created_at    datetime(6)  not null,
    updated_at    datetime(6),
    created_by    varchar(100),
    updated_by    varchar(100),
    version       bigint,
    primary key (id)
) engine=InnoDB;

-- ---------------------------------------------------------------------
-- 9. audit_logs - Nhat ky thao tac (chi ghi va doc, khong sua)
-- ---------------------------------------------------------------------
create table audit_logs (
    id          bigint       not null auto_increment,
    entity_name varchar(60)  not null,
    entity_id   bigint,
    action      enum ('CREATE','UPDATE','DELETE','PUBLISH','ARCHIVE','ENROLL','CANCEL') not null,
    actor       varchar(100) not null,
    detail      varchar(500),
    created_at  datetime(6)  not null,
    updated_at  datetime(6),
    created_by  varchar(100),
    updated_by  varchar(100),
    version     bigint,
    primary key (id)
) engine=InnoDB;

-- =====================================================================
-- CHI MUC (INDEX)
-- =====================================================================
create index idx_audit_entity on audit_logs (entity_name, entity_id);
create index idx_audit_action on audit_logs (action);

-- =====================================================================
-- RANG BUOC DUY NHAT (UNIQUE)
-- =====================================================================
alter table categories
    add constraint UKt8o6pivur7nn124jehx7cygw5 unique (name);

alter table instructors
    add constraint UK1p61qho6k9oewkyd5uv1aniv7 unique (email);

alter table students
    add constraint UKe2rndfrsx22acpq2ty1caeuyw unique (email);

-- Mot hoc vien chi co dung MOT ban ghi ghi danh cho moi khoa hoc
alter table enrollments
    add constraint uk_enrollment_student_course unique (student_id, course_id);

-- Mot hoc vien chi danh gia MOT lan cho moi khoa hoc
alter table reviews
    add constraint uk_review_student_course unique (student_id, course_id);

-- Mot ghi danh chi duoc cap MOT chung chi; ma chung chi khong trung
alter table certificates
    add constraint uk_certificate_enrollment unique (enrollment_id);

alter table certificates
    add constraint uk_certificate_code unique (code);

-- =====================================================================
-- KHOA NGOAI (FOREIGN KEY)
-- =====================================================================
alter table courses
    add constraint FK72l5dj585nq7i6xxv1vj51lyn
    foreign key (category_id) references categories (id);

alter table courses
    add constraint FK1kswo6qqebbdy2kq0kx6udof7
    foreign key (instructor_id) references instructors (id);

alter table lessons
    add constraint FK17ucc7gjfjddsyi0gvstkqeat
    foreign key (course_id) references courses (id);

alter table enrollments
    add constraint FK8kf1u1857xgo56xbfmnif2c51
    foreign key (student_id) references students (id);

alter table enrollments
    add constraint FKho8mcicp4196ebpltdn9wl6co
    foreign key (course_id) references courses (id);

alter table reviews
    add constraint FKfm8jxphx9i3qes8demb91pa7e
    foreign key (student_id) references students (id);

alter table reviews
    add constraint FKccbfc9u1qimejr5ll7yuxbtqs
    foreign key (course_id) references courses (id);

alter table certificates
    add constraint FKjy46ubyh2tf64mgos6jgpcx4u
    foreign key (enrollment_id) references enrollments (id);
