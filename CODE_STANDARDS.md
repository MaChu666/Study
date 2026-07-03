# 代码生成与修改规范

> 所有代码生成、修改必须严格遵守本规范。违反任一规则即为不合格。

---

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

---

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

## 二、前端

| 规则 | 说明 |
|------|------|
| 缩进 | Tab |
| API 风格 | Composition API (`<script setup>`) |
| CSS | 只用 `var(--bil-xxx)` |
| 异步 | 必须 `await` |
| 文件上传 | 用 `FormData` + `request.post(url, formData)` |
| API 封装 | `src/api/modules/xxx.js` |

## 三、严禁事项

1. 不读文件就改
2. 调用不存在的函数
3. 异步不加 `await`
4. 可选参数加 `@NotEmpty`
5. PO 加字段不更新 XML
6. 改完不编译
7. 数据库存 base64/大文件内容
8. 注册和登录 MD5 次数不一致
9. Token 能取的 userId 还标 `@NotEmpty`

## 四、检查清单

- [ ] 读了文件最新内容？
- [ ] XML 所有段落都更新了？
- [ ] 可选参数没加 @NotEmpty？
- [ ] await 都加了？
- [ ] 函数名正确且存在？
- [ ] 前端参数名和后端一致？
- [ ] mvn compile 通过？
- [ ] npm run build 通过？
- [ ] DB 变更写了 SQL？
