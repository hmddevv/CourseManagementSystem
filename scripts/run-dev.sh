#!/usr/bin/env bash
# ==========================================================================
# Chay ung dung o profile dev (MySQL 8 - cung engine voi prod).
#
# Cach dung:
#   docker compose up -d mysql     # bat CSDL truoc
#   bash scripts/run-dev.sh
#
# Script doc thong tin dang nhap tu .env (file khong duoc commit) nen khong
# co mat khau nao nam trong ma nguon. Cong 3307 la cong host cua container
# MySQL, xem docker-compose.yml.
# ==========================================================================
set -euo pipefail

cd "$(dirname "$0")/.."

if [ ! -f .env ]; then
    echo "Khong tim thay .env. Chay:  cp .env.example .env  roi doi mat khau." >&2
    exit 1
fi

# Nap .env nhung KHONG lay SPRING_PROFILES_ACTIVE tu do - file .env dat gia tri
# prod cho Docker Compose, con script nay chay o dev.
set -a
# shellcheck disable=SC1091
. ./.env
set +a

export SPRING_PROFILES_ACTIVE=dev
export DB_URL="jdbc:mysql://localhost:3307/${MYSQL_DATABASE}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USERNAME="${MYSQL_USER}"
export DB_PASSWORD="${MYSQL_PASSWORD}"

echo "Profile: dev | CSDL: MySQL 8 tai localhost:3307/${MYSQL_DATABASE}"
exec ./mvnw spring-boot:run
