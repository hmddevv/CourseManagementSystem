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
--        --spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create \
--        --spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=docs/schema.sql \
--        --spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect"
--
-- Luu y: ten cac constraint dang `UKxxxx` / `FKxxxx` la ten Hibernate tu
-- sinh khi entity khong dat ten tuong minh. Rieng rang buoc nghiep vu quan
-- trong nhat - `uk_enrollment_student_course` - duoc dat ten tuong minh
-- trong `Enrollment.java` de doc log loi de hieu.
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
    version          bigint,
    primary key (id)
) engine=InnoDB;

-- =====================================================================
-- RANG BUOC DUY NHAT (UNIQUE)
-- =====================================================================
alter table categories
    add constraint UKt8o6pivur7nn124jehx7cygw5 unique (name);

alter table instructors
    add constraint UK1p61qho6k9oewkyd5uv1aniv7 unique (email);

alter table students
    add constraint UKe2rndfrsx22acpq2ty1caeuyw unique (email);

-- Rang buoc nghiep vu: mot hoc vien chi co dung MOT ban ghi ghi danh
-- cho moi khoa hoc. Day la chot chan cuoi cung o tang CSDL, ben canh
-- kiem tra o tang Service.
alter table enrollments
    add constraint uk_enrollment_student_course unique (student_id, course_id);

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
