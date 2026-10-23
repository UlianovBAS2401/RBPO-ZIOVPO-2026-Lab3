@echo off
psql -U postgres -c "CREATE DATABASE utility_accounting_db;"
psql -U postgres -d utility_accounting_db -f init-db.sql
