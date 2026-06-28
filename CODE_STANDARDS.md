CODE_STANDARDS.md# 代码规范

> 基于项目技术栈 Spring Boot + MyBatis + Redis + Elasticsearch，从现有代码提炼。

---

## 一、项目结构

```
bil-common/     # 公共模块：entity、service、mapper、component、utils、redis、exception
bil-web/        # 前台接口模块：controller
bil-admin/      # 后台接口模块：controller
```

- `bil-common` 不放任何 Web 层依赖（如 `HttpServletRequest`、`HttpServletResponse`）
- Controller 所在模块依赖 `bil-common`

## 二、包结构

```
com.machugit
├── component      # RedisComponent 等公共组件
├── entity
│   ├── constants  # Constants 静态常量
│   ├── dto        # TokenUserInfoDto 等传输对象
│   ├── enums      # ResponseCodeEnum、UserSexEnum 等枚举
│   ├── po         # UserInfo 等持久化对象（对应数据库表）
│   ├── query      # BaseParam、UserInfoQuery 等查询参数
│   └── vo         # ResponseVO、PaginationResultVO 等返回对象
├── exception      # BusinessException
├── mappers        # MyBatis Mapper 接口
├── redis          # RedisConfig、RedisUtils
├── service        # 业务接口
│   └── impl       # 业务实现
└── utils          # StringTools、CopyTools 等工具类
```

## 三、命名规范

| 类型 | 规则 | 示例 |
|------|------|------|
| 实体类 (PO) | 表名驼峰，无后缀 | `UserInfo` |
| 传输对象 (DTO) | `XxxDto` | `TokenUserInfoDto` |
| 返回对象 (VO) | `XxxVO` | `ResponseVO`、`PaginationResultVO` |
| 查询对象 | `XxxQuery` | `UserInfoQuery` |
| 枚举 | `XxxEnum` | `ResponseCodeEnum`、`UserSexEnum` |
| Service 接口 | `XxxService` | `UserInfoService` |
| Service 实现 | `XxxServiceImpl` | `UserInfoServiceImpl` |
| Mapper | `XxxMapper` | `UserInfoMapper` |
| 常量 | 全大写 + 下划线 | `REDIS_KEY_CHECK_CODE`、`REGEX_PASSWORD` |

- 方法名：camelCase，动词开头（`getCheckCode`、`saveTokenInfo`、`findListByParam`）
- 布尔方法：`isXxx` 或 `hasXxx`

## 四、Controller 层

### 4.1 类注解

```java
@RestController
@RequestMapping("/account")
@Validated   // 开启方法参数校验
public class AccountController extends ABaseController {
```

- 必须继承 `ABaseController`
- 必须加 `@Validated` 以激活 `@NotEmpty` 等参数校验
- 使用 `@RequestMapping` 声明路径（不指定 method，同时支持 GET/POST）

### 4.2 依赖注入

```java
@Resource
private UserInfoServiceImpl userInfoService;

@Resource
private RedisComponent redisComponent;
```

- 统一使用 `@Resource`，不用 `@Autowired`
- 注具体实现类而非接口（`UserInfoServiceImpl` 而非 `UserInfoService`）

### 4.3 参数校验

```java
public ResponseVO login(HttpServletRequest request,
                        HttpServletResponse response,
                        @NotEmpty @Email @Size(max = 150) String email,
                        @NotEmpty String password,
                        @NotEmpty String checkCodeKey,
                        @NotEmpty String checkCode) {
```

- `@Validated` + JSR-303 注解（`@NotEmpty`、`@Email`、`@Size`、`@Pattern`）
- 不需要 `@RequestParam`，Spring 自动按名绑定

### 4.4 返回响应

```java
return getSuccessResponseVO(data);         // 成功
throw new BusinessException("验证码错误");   // 业务错误
```

- 成功统一用 `getSuccessResponseVO(T data)`
- 业务错误统一 `throw new BusinessException(message)`
- 不在 Controller 中手动构造 `ResponseVO`

### 4.5 异常处理

- 全局异常拦截器 `AGlobalExceptionHandlerController` 统一处理
- Controller 层只抛 `BusinessException`，不写 try-catch（验证码校验等 finally 场景除外）

## 五、Service 层

### 5.1 接口与实现

```java
// 接口
public interface UserInfoService {
    void register(String email, String useName, String registerPassword);
    TokenUserInfoDto login(String email, String password, String ip);
}

// 实现
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
}
```

- `@Service` 必须指定名称
- 实现类以 `Impl` 结尾
- 注入使用 `@Resource`，非 `@Autowired`

### 5.2 业务异常

```java
if (null != userInfo) {
    throw new BusinessException("邮箱已注册");
}
```

- 业务校验不通过直接 `throw new BusinessException(message)`
- `BusinessException` 已重写 `fillInStackTrace()` 返回 `this`，不产生堆栈，性能友好

### 5.3 日志

```java
private static final Logger logger = LoggerFactory.getLogger(Xxx.class);
logger.info("login captcha - user: [{}], redis: [{}]", checkCode, codeFromRedis);
logger.error("设置redisKey:{},value:{}失败", key, value);
```

- 使用 SLF4J（`org.slf4j.Logger`）
- 占位符用 `{}`，不拼接字符串

## 六、数据访问层

### 6.1 Mapper

```java
public interface UserInfoMapper<T, P> extends BaseMapper<T, P> {
    UserInfo selectByEmail(@Param("email") String email);
    UserInfo selectByUseName(@Param("useName") String useName);
    Integer updateByUserId(@Param("bean") UserInfo bean, @Param("userId") String userId);
}
```

- 继承 `BaseMapper<T, P>`
- Mapper XML 使用参数名映射

## 七、Redis 操作

### 7.1 组件分层

```
Controller → RedisComponent → RedisUtils → RedisTemplate
```

- `RedisComponent`：业务语义方法（`saveChackCode`、`cleanTokenInfo`），不暴露 Redis key 细节
- `RedisUtils`：通用 Redis 操作封装，使用范型 `<V>`

### 7.2 Key 命名

```java
// Constants.java
public static final String REDIS_KEY_PREFIX = "machugit";
public static final String REDIS_KEY_CHECK_CODE = REDIS_KEY_PREFIX + "checkCode:";
public static final String REDIS_KEY_TOKEN_WEB = REDIS_KEY_PREFIX + "token:web:";
```

- 所有 key 前缀集中定义在 `Constants`
- 格式：`{项目前缀}:{业务标识}:{唯一ID}`

### 7.3 过期时间

```java
public static final Integer REDIS_KEY_EXPIRE_TIME_ONE_MIN = 60000;           // 1分钟（毫秒）
public static final Integer REDIS_KEY_EXPIRE_TIME_ONE_DAY = REDIS_KEY_EXPIRE_TIME_ONE_MIN * 60 * 24; // 1天
```

- 统一毫秒单位
- 由基础值乘算得出，避免魔法数字

## 八、响应规范

### 8.1 统一响应体

```java
public class ResponseVO<T> {
    private String status;   // "success" / "error"
    private Integer code;    // 200 / 404 / 500 / 600 / 601
    private String info;     // 提示信息
    private T data;          // 业务数据
}
```

### 8.2 状态码

| code | 含义 |
|------|------|
| 200  | 请求成功 |
| 404  | 请求地址不存在 |
| 500  | 服务器错误 |
| 600  | 请求参数错误 |
| 601  | 信息已经存在 |

## 九、异常体系

```java
// 业务异常：不生成堆栈，提高性能
throw new BusinessException("验证码错误");

// 全局处理器自动映射
@ExceptionHandler(Exception.class)
Object handleException(Exception e, HttpServletRequest request) {
    // BusinessException    → code 600, info = e.getMessage()
    // ConstraintViolation  → code 600, info = "请求参数错误"
    // DuplicateKeyException → code 601
    // 其他                 → code 500
}
```

## 十、代码格式

- **缩进**：4 空格
- **编码**：UTF-8
- **换行符**：LF（Unix 风格）
- **包导入**：显式导入，不用通配符（`import java.util.Map;`，不写 `import java.util.*`）
- **类成员顺序**：Logger → @Resource 字段 → 方法
- **零 Lombok**：不使用 Lombok，getter/setter/toString/构造器全部手写
- **注解风格**：
  - 类注解一行一个，紧贴类声明上方
  - 参数注解内联：`@NotEmpty @Email @Size(max = 150) String email`
  - 方法注解（`@RequestMapping`、`@Override`）单独一行
- **HTTP 映射**：只用 `@RequestMapping`，不用 `@GetMapping`/`@PostMapping`

## 十一、PO 实体类

```java
public class UserInfo implements Serializable {
    /** 用户id */
    private String userId;
    /** 邮箱 */
    private String email;
    /** 最后登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;
    // ... getter/setter
}
```

- 实现 `Serializable`
- 字段中文注释用 `/** */` Javadoc
- Date 字段必须配 `@JsonFormat` + `@DateTimeFormat`，时区 `GMT+8`
- getter/setter 手写，**setter 在 getter 之前**

## 十二、通用约定

1. **抽象基类**：前缀 `A`（`ABaseController`、`AGlobalExceptionHandlerController`）
2. **判空**：`null == obj` 风格（Yoda 式，null 在前）
3. **字符串判空**：使用 `StringTools.isEmpty()`
4. **常量**：所有魔法数字/字符串提取到 `Constants`
5. **分层隔离**：`bil-common` 不引入 Servlet API，web 层（cookie、session、request）的处理留在 Controller
6. **finally 清理**：验证码等临时数据在 finally 块中清理，保证异常时也能释放
7. **TODO 标记**：未完成功能用 `//TODO` 标注
8. **Bean 复制**：使用 `CopyTools.copy()`，不用 `BeanUtils.copyProperties()`
9. **密码安全**：存储前必须 MD5 加密（`StringTools.encodeByMd5()`）
10. **中文注释**：Javadoc、TODO、行注释均使用中文
11. **Maven 模块**：公共代码放 `bil-common`，接口层分别放 `bil-web` / `bil-admin`
