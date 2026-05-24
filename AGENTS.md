# AGENTS.md

## TDD First (强制要求)

**所有后端逻辑开发必须 TDD 驱动** — 先写测试，再写实现。

```
1. 写测试（验证接口行为 + 边界条件 + 异常）
2. 运行测试 → 确认失败（红）
3. 写实现
4. 运行测试 → 全部通过（绿）
5. 重构（绿保持）
```

- **Dao / Service / Controller** 三层均需覆盖测试
- Service 层是重点：业务逻辑、边界条件、异常路径、事务回滚
- `calculate` 类核心逻辑必须覆盖：正常计算、无打卡、全天请假、跨天请假、午休扣减、精度取整、无规则兜底

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
- `sys_user.account` is the login account field.
- 年假和调休假合并在 `leave_balance` 一张表，同一行记录通过 `annual_*` / `comp_*` 字段区分。
- When creating leave applications of type 年假 (1) or 调休假 (4), the system validates and deducts the balance on final approval.
- `@TableName` explicitly set on every entity.
- **开发新接口时请参考实际数据库表结构，而非 init.sql**（实际库表结构与 init.sql 可能存在差异）。
- **DB schema 变更不写 SQL 文件**，由 Agent 直接操作：写 Python 脚本 + pymysql 连接数据库执行。参考 `rename_column.py`、`merge_leave_tables.py`。
- `init.sql` 仅用于新环境建表参考，不用于迁移现有库。

## Testing
- `@SpringBootTest @Transactional` on every test class.
- **每个 `@Test` 方法完全自包含，不依赖其他测试的数据**。`@BeforeEach` 清空相关表并插入通用数据，每条 `@Test` 再补充自己需要的业务数据。
- 数据准备三步走：
  1. 清空涉及的所有表（`dao.delete(null)`）
  2. 插入前置数据（规则、用户等）
  3. 插入当前 case 特有的业务数据
- 涉及 `AuthContext.getCurrentUser()` 的 Service 测试，用 `MockHttpServletRequest` + `RequestContextHolder.setRequestAttributes` 模拟登录上下文。
- 测试文件在 `src/test/java/.../service/` 目录下。
- Run: `mvn test -Dtest=TestClassName`

## Conventions
- **Lombok**: `@Data` on entities/DTOs, `@RequiredArgsConstructor` on controllers.
- **测试方法名必须用英文**，禁止使用中文（含拼音）。中文方法名在 IDE 和 CI 日志中显示乱码，违反行业惯例。
- **No CI, no linter, no typecheck** configured.
- `Rule.desc` is a SQL reserved word — backtick-quoted (`\`desc\``) in XML.
- Controller paths use camelCase (e.g. `/sysUser`).
- 所有表统一以 `uuid` 作为业务主键，API 增删改查优先用 uuid 而非自增 id 定位记录。
- Bean 复制统一用 `com.hailang.config.utils.BeanUtils`（基于 Jackson `convertValue`）。
- 接口统一返回 `Result<T>`，成功用 `ResultUtils.ok(data)`，异常由 `GlobalExceptionHandler` 统一拦截返回 `ResultUtils.failed(msg)`。
