# AGENTS.md

## Run
```bash
mvn spring-boot:run
```
Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

## Architecture
Single-module Spring Boot 3.2 + MyBatis-Plus 3.5.5 + MySQL.

```
com.hailang
├── AttendApplication.java   # @SpringBootApplication main
├── config/
│   ├── swagger/
│   │   └── OpenApiConfig.java
│   ├── utils/             # BeanUtils (copy / copyList), Result, ResultUtils
│   ├── exception/
│   │   └── GlobalExceptionHandler.java
│   ├── interceptor/
│   │   └── TokenInterceptor.java
│   ├── AuthContext.java
│   ├── MyBatisPlusConfig.java
│   └── WebMvcConfig.java
├── controller/
│   ├── req/                 # @Schema 接口入参
│   └── resp/                # @Schema 接口出参
├── service/
│   ├── dto/                 # Service 层 DTO（可流入 DAO 层）
│   └── impl/                # extends ServiceImpl<Dao, Entity>
├── dao/                     # extends BaseMapper<Entity> + same-named .xml
├── entity/                  # @TableName, @Data, @Schema
```

## Data flow across layers
```
Controller: req (入参) → Service → resp (出参)
Service:    dto 接值 → 处理 → 返回 dto（由 Controller 转 resp）
DAO:        dto / entity 均可传入
```

## DAO / MyBatis-Plus
- XML mappers live in `src/main/java/com/hailang/dao/` (same dir as interfaces).
- pom.xml copies `**/*.xml` from `src/main/java` to classpath — do not move them to `resources/`.
- `application.yml` → `mapper-locations: classpath:com/hailang/dao/*.xml`.

## Login system
- Passwords stored as **MD5** (`DigestUtils.md5Hex` from `commons-codec`).
- Login accepts `account` (账号) as login identifier.
- Login generates a UUID token and stores user JSON in Redis with key `sysUser_{uuid}`, 30min TTL.
- Service throws raw `RuntimeException` on failure (caught by `GlobalExceptionHandler` → `ResultUtils.failed`).

## Database
- MySQL at `192.168.31.100:3306/attend`, user `root` / `root123`.
- Existing tables: `sys_user`, `rule`, `apply`, `approve`, `dept`, `leader`, `position`.
- `sys_user.account` is the login account field (note: spelling is `account`, not `account`).
- `@TableName` explicitly set on every entity.
- **开发新接口时请参考实际数据库表结构，而非 init.sql**（实际库表结构与 init.sql 可能存在差异）。

## Conventions
- **Lombok**: `@Data` on entities/DTOs, `@RequiredArgsConstructor` on controllers.
- **No tests, no CI, no linter, no typecheck** configured.
- `Rule.desc` is a SQL reserved word — backtick-quoted (`\`desc\``) in XML.
- Controller paths use camelCase (e.g. `/sysUser`).
- 所有表统一以 `uuid` 作为业务主键，API 增删改查优先用 uuid 而非自增 id 定位记录。
- Bean 复制统一用 `com.hailang.config.utils.BeanUtils`（基于 Jackson `convertValue`）。
- 接口统一返回 `Result<T>`，成功用 `ResultUtils.ok(data)`，异常由 `GlobalExceptionHandler` 统一拦截返回 `ResultUtils.failed(msg)`。
