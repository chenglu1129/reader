# Reader - 纯Java版本

基于Spring Boot 3.2.2的阅读器后端服务，从Kotlin + Vert.x迁移而来。

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.9+

### 编译运行

```bash
# 设置Java 17环境（Windows PowerShell）
$env:JAVA_HOME="C:\Users\92872\.jdks\corretto-17.0.17"

# 编译
mvn clean compile

# 运行
mvn spring-boot:run

# 打包
mvn clean package
```

### 访问应用

- 健康检查：http://localhost:8080/reader3/health
- 系统信息：http://localhost:8080/reader3/getInfo

## 项目结构

```
src/main/java/com/htmake/reader/
├── ReaderApplication.java      # 主启动类
├── config/                     # 配置类
│   ├── ReaderConfig.java
│   ├── WebConfig.java
│   └── AsyncConfig.java
├── entity/                     # 实体类
│   ├── Book.java
│   ├── BookSource.java
│   ├── BookChapter.java
│   ├── SearchBook.java
│   └── User.java
├── utils/                      # 工具类
│   ├── StorageHelper.java
│   └── MD5Utils.java
├── service/                    # 服务层
└── controller/                 # 控制器
    └── SystemController.java
```

## 配置说明

配置文件：`src/main/resources/application.yml`

主要配置项：
- `server.port`: 服务端口（默认8080）
- `reader.app.storage-path`: 数据存储路径（默认storage）
- `reader.app.secure`: 是否启用多用户模式（默认false）

更多配置请参考 [迁移指南](../../../.gemini/antigravity/brain/1e57a3dd-9df6-40ea-a843-a7d0eca19f28/migration_guide.md)

## 技术栈

- Java 17
- Spring Boot 3.2.2
- Maven
- Lombok
- OkHttp3
- Jsoup
- Gson

## 开发状态

当前已完成：
- ✅ 项目基础结构
- ✅ 核心实体类
- ✅ 基础工具类
- ✅ 配置类

待完成：
- ⏳ 服务层实现
- ⏳ 控制器迁移
- ⏳ 规则解析引擎
- ⏳ WebDAV集成

## 相关文档

- [迁移指南](../../../.gemini/antigravity/brain/1e57a3dd-9df6-40ea-a843-a7d0eca19f28/migration_guide.md)
- [实施计划](../../../.gemini/antigravity/brain/1e57a3dd-9df6-40ea-a843-a7d0eca19f28/implementation_plan.md)
- [原项目文档](../doc.md)

## 许可证

GPL-3.0
