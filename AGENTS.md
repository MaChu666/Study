
# 代码生成与修改规范

> 所有代码生成、修改必须严格遵守本规范。违反任一规则即为不合格。


## 零、铁律（最高优先级）

### 0.1 修改前必须读文件
每次修改任何文件前，必须先 Read 最新内容。禁止凭空猜测行号、缩进、变量名。

### 0.2 修改后必须编译/构建
- 后端：`mvn compile` 必须 SUCCESS
- 前端：`npm run build` 必须 built

### 0.3 函数名必须匹配
调用前确认函数存在。文件名、导入路径、拼写 100% 精确。

### 0.4 前后端字段名一致
前端参数名 ↔ 后端方法参数名 ↔ DB 列名必须完全相同。

### 0.5 启动前必须检查端口
生成任何启动命令前，先提供端口占用检查命令（Windows: `netstat -ano | findstr <port>`；Linux/Mac: `ss -tlnp | grep <port>`）。多服务同时运行时确保端口不冲突，并提供替代端口方案。


## 一、后端

| 规则 | 说明 |
|------|------|
| Controller 继承 | `ABaseController`(web) / `ABaseAdminController`(admin) |
| 注解 | `@RestController` + `@RequestMapping` + `@Validated` |
| DI | `@Resource` 注实现类 |
| 可选参数 | **不加 `@NotEmpty`** |
| 返回 | `getSuccessResponseVO(data)` |
| 错误 | `throw new BusinessException("msg")` |
| 新增 PO 字段 | **XML 所有 12 段必须同步更新** |
| DB 变更 | 必须写 SQL 脚本给用户 |
| String 条件 | `query.field != null and query.field!=''` |
| Integer/Long 条件 | `query.field != null` |
| 配置项（@Value） | **必须有默认值**：`@Value("${my.key:/default}")`，或在 `application.yml` 中给出完整示例；涉及 Nacos 时必须提醒用户确认配置中心已启动且配置已发布 |
| Bean 命名 | **禁止与 Spring Boot 自动配置同名**（如 `sentinelGatewayFilter`、`dataSource`、`restTemplate`），若需自定义，使用 `@Bean("uniqueName")` 指定别名；如需覆盖，显式设置 `spring.main.allow-bean-definition-overriding=true` 并说明风险 |
| 依赖版本 | Maven/Gradle 依赖必须指定具体版本号，禁止 `LATEST` 或 `RELEASE`；推荐的组件版本必须相互兼容（Spring Boot / Cloud / Cloud Alibaba / RocketMQ / Seata / Sentinel） |
| JVM 内存参数 | `-Xmx` 和 `-Xms` 不超过物理内存 70%；RocketMQ Broker 默认 8G/512G 必须强制调低至 2G 以下（建议 `-Xmx2g -Xms2g`）；若机器内存小，进一步降至 1G，并指导调整系统页面文件 |
| 启动方式 | 对于 RocketMQ、Seata Server 等，优先建议使用官方 `bin/` 目录下的 `.sh`/`.cmd` 启动脚本，而非直接 `java -jar`；若必须 `java -jar`，需附带 `-Dloader.path` 指向 `lib` 目录 |


## 二、前端

| 规则 | 说明 |
|------|------|
| 缩进 | Tab |
| API 风格 | Composition API (`<script setup>`) |
| CSS | 只用 `var(--bil-xxx)` |
| 异步 | 必须 `await` |
| 文件上传 | 用 `FormData` + `request.post(url, formData)` |
| API 封装 | `src/api/modules/xxx.js` |
| Node.js 版本 | 若使用 ES2021+ 语法（如 `??=`），必须要求 Node.js ≥ 15，推荐 LTS v18+；提供 `node -v` 检查命令和 `nvm` 切换指导 |


## 三、SQL 与数据库

| 规则 | 说明 |
|------|------|
| SQL 脚本执行方式 | 图形化工具（DataGrip/DBeaver/Navicat）不支持 `SOURCE` 命令，需使用"运行 SQL 文件"功能；命令行可使用 `mysql -u root -p -D dbname < script.sql` |
| SQL 文件编码与路径 | 文件使用 UTF-8 编码；Windows 路径建议用正斜杠 `/` 或双反斜杠 `\\`，避免转义问题 |


## 四、文件与路径

| 规则 | 说明 |
|------|------|
| 路径规范 | 涉及文件上传、日志、存储时使用正斜杠 `/` 或双反斜杠 `\\`；优先放在用户目录（如 `~/temp`）或系统临时目录，避免根目录权限问题 |
| 目录创建 | 使用 `Files.createDirectories(path)` 确保目录存在 |
| 权限检查 | 出现 `Permission denied` 时，提示用户以管理员身份运行或修改目录权限 |


## 五、严禁事项

1. 不读文件就改
2. 调用不存在的函数
3. 异步不加 `await`
4. 可选参数加 `@NotEmpty`
5. PO 加字段不更新 XML
6. 改完不编译
7. 数据库存 base64/大文件内容
8. 注册和登录 MD5 次数不一致
9. Token 能取的 userId 还标 `@NotEmpty`
10. 端口冲突不检查直接启动
11. 配置项缺失默认值
12. 自定义 Bean 与自动配置同名
13. 依赖版本不指定或使用 LATEST/RELEASE
14. 图形化工具中用 `SOURCE` 执行 SQL 脚本
15. JVM 内存参数超出物理内存 70%
16. Node.js 版本低于 15 却使用 ES2021+ 语法


## 六、检查清单

- [ ] 读了文件最新内容？
- [ ] XML 所有段落都更新了？
- [ ] 可选参数没加 @NotEmpty？
- [ ] await 都加了？
- [ ] 函数名正确且存在？
- [ ] 前端参数名和后端一致？
- [ ] mvn compile 通过？
- [ ] npm run build 通过？
- [ ] DB 变更写了 SQL？
- [ ] 端口占用已检查？
- [ ] @Value 配置项有默认值或配置示例？
- [ ] 自定义 Bean 名称不与自动配置冲突？
- [ ] 所有依赖指定了具体版本？
- [ ] JVM 内存参数合理？
- [ ] SQL 脚本执行方式正确？
- [ ] Node.js 版本 ≥ 15？