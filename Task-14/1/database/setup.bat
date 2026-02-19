@echo off
chcp 65001 >nul 2>nul

echo ===============================================
echo Настройка базы данных автосервиса
echo ===============================================

where psql >nul 2>nul
if %errorlevel% neq 0 (
	echo ОШИБКА: PostgreSQL не установлен или psql не в PATH
	echo Установите PostgreSQL и добавьте в PATH
	pause
	exit /b 1
)

echo PostgreSQL найден

echo Создание базы данных и таблиц...

psql -U postgres -W -f "ddl\create_tables.sql"

if errorlevel 1 (
	echo ОШИБКА: создание таблиц неудачно
	pause
	exit /b 1
)

echo Таблицы успешно созданы

echo Вставка тестовых данных...

psql -U postgres -d auto_service -W -f "dml\insert_test_data.sql"

if errorlevel 1 (
	echo ОШИБКА: вставка тестовых данных неудачна
	pause
	exit /b 1
)

echo Тестовые данные вставлены удачно
echo ===============================================
echo Установка базы данных завершена!
set PGPASSWORD=
set POSTGRES_PASSWORD=
pause
