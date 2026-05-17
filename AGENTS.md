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
│   └── utils/             # BeanUtils (copy / copyList)
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
- Login generates a UUID token (dashes stripped) and writes it to `sys_user.token`.
- Service throws raw `RuntimeException` on failure (no global handler yet).

## Database
- MySQL at `192.168.31.100:3306/attend`, user `root` / `root123`.
- Existing tables: `sys_user`, `rule`.
- `@TableName` explicitly set on every entity.

## Conventions
- **Lombok**: `@Data` on entities/DTOs, `@RequiredArgsConstructor` on controllers.
- **No tests, no CI, no linter, no typecheck** configured.
- `HelloController.java` at `GET /hello` is a legacy test endpoint.
- `Rule.desc` is a SQL reserved word — backtick-quoted (`\`desc\``) in XML.
- Controller paths use camelCase (e.g. `/sysUser`).
- DDL 建表脚本在 `src/main/resources/database/init.sql`。
- `sys_user.uuid` 是业务主键，API 增删改查优先用 uuid 而非 id 定位用户。
- Bean 复制统一用 `com.hailang.config.utils.BeanUtils`（基于 Jackson `convertValue`）。
