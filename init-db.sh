#!/bin/bash
set -euo pipefail

DB_NAME="${POSTGRES_DB:-silverpancake_db}"

until pg_isready -q -U "$POSTGRES_USER" -d postgres; do
  sleep 2
done

DB_EXISTS=$(psql -U "$POSTGRES_USER" -t -c "SELECT 1 FROM pg_database WHERE datname = '$DB_NAME'" | xargs)

if [[ "$DB_EXISTS" != "1" ]]; then
  psql -U "$POSTGRES_USER" -c "CREATE DATABASE \"$DB_NAME\";"
fi
