@echo off
set PGPASSWORD=postgres
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -f create_database.sql
