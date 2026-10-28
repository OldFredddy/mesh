# Mesh

Mesh — сервис управления пользователями, балансами и переводами на Java 21 / Spring Boot 3.2.
Сохранены веб-страницы входа и кабинета, JWT (cookie или Bearer), PostgreSQL + Flyway, Redis Cache, начисление 10% каждые 30 секунд с потолком 207% от начального баланса, аудит и тесты.

## Модули и зависимости

```text
               +------------------+
               | Boot (application)|
               +--------+---------+
                        |
                        v
               +------------------+        +-------------------+
               | Impl (business,  |------->| API (DTO, services)|
               | JPA, REST, auth) |        +-------------------+
               +--------+---------+                 ^
                        |                           |
                        v                           |
               +------------------+        +--------+---------+
               | Transport-Common |<-------| Client (REST)    |
               +------------------+        +------------------+
```

- **API** — DTO, запросы и интерфейсы бизнес-сервисов, без реализации и контроллеров.
- **Transport-Common** — константы HTTP-путей и заголовков для клиента и сервера.
- **Impl** — сервисы `*ServiceImpl`, транзакции, контроллеры, JPA, Redis, JWT, обработка ошибок, аудит.
- **Client** — отдельно используемый Java HTTP-клиент `MeshRestClient` (авторизация через Bearer).
- **Boot** — точка входа `MeshApplication`, конфигурация и миграции Flyway, HTML и CSS.

## Локальный запуск

Требуются **JDK 21**, **Docker Compose** и интернет для первоначальной загрузки Gradle / Maven Central.

1. `docker compose up -d`
2. `cp .env.example .env`
3. Установите длинный случайный `JWT_SECRET` (не оставляйте пример из файла).
4. Загрузите переменные окружения: `set -a; . ./.env; set +a`
5. `./gradlew :Boot:bootRun`

Для Windows: скопируйте `.env.example` в `.env`, задайте переменные окружения в PowerShell или в настройках запуска IDE, выполните `gradlew.bat :Boot:bootRun`.

`gradlew` и `gradlew.bat` используют локальный Gradle, а при его отсутствии загружают официальную Gradle 8.10.2. В архиве нет бинарного Gradle wrapper JAR: его не было в исходном проекте и в среде преобразования он недоступен.

Приложение доступно по `http://localhost:8080/`, OpenAPI — `http://localhost:8080/swagger-ui.html`.
При включении TLS используйте `SERVER_SSL_ENABLED`, `SERVER_SSL_KEYSTORE`, `SERVER_SSL_KEYSTORE_PASSWORD`.

### Демо-аккаунты

Миграция `V2__seed_users.sql` создаёт семь демонстрационных профилей. Для локальных тестов пароль у всех — `mesh-demo-123`.
Пример входа: `ivan-megaman@gmail.com` / `mesh-demo-123`.

## Build и тесты

- `./gradlew clean build` — сборка всех 5 модулей и unit-тесты.
- `./gradlew :Boot:bootJar` — исполняемый JAR в `Boot/build/libs/mesh-service.jar`.
- `./gradlew :Impl:test` — unit-тесты, а также интеграционные тесты PostgreSQL (требуется доступный Docker; без него они пропускаются).
- `java -jar Boot/build/libs/mesh-service.jar` — запуск с переменными окружения.

## HTTP API

| Метод | Endpoint | Описание |
| --- | --- | --- |
| POST | `/auth/login` | JWT в HttpOnly cookie |
| GET | `/auth/logout` | Завершение сеанса (cookie) |
| GET | `/users` | Поиск с фильтрами и пагинацией |
| GET | `/users/{id}` | Данные пользователя |
| GET | `/users/me` | Текущий пользователь |
| POST | `/users/{id}/update-emails` | Заменить emails владельца |
| POST | `/users/{id}/emails` | Добавить email владельца |
| DELETE | `/users/{id}/emails/{email}` | Удалить email владельца |
| POST | `/users/{id}/emails/{previousEmail}` | Изменить email владельца |
| POST | `/users/{id}/update-phones` | Заменить телефоны владельца |
| POST | `/users/{id}/phones` | Добавить телефон владельца |
| DELETE | `/users/{id}/phones/{phone}` | Удалить телефон владельца |
| POST | `/users/{id}/phones/{previousPhone}` | Изменить телефон владельца |
| POST | `/transfers` | Перевод по ID или номеру телефона |

Бизнес-ошибки возвращают структурированный JSON `{"code": "...", "message": "..."}` со статусом 400/401/403/404.

