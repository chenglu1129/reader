# Reader API 测试指南

## 启动应用

```powershell
cd d:\workspace\reader\reader-java
.\start.ps1 run
```

应用将在 http://localhost:8080 启动

## API 端点

### 1. 系统信息

#### 健康检查
```bash
GET http://localhost:8080/reader3/health
```

#### 获取系统信息
```bash
GET http://localhost:8080/reader3/getInfo
```

### 2. 用户管理

#### 用户注册
```bash
POST http://localhost:8080/reader3/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}
```

#### 用户登录
```bash
POST http://localhost:8080/reader3/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}
```

#### 获取用户信息
```bash
GET http://localhost:8080/reader3/getUserInfo?username=testuser
```

#### 修改密码
```bash
POST http://localhost:8080/reader3/changePassword
Content-Type: application/json

{
  "username": "testuser",
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

### 3. 书架管理

#### 获取书架列表
```bash
GET http://localhost:8080/reader3/getShelfBooks?username=default
```

#### 保存书籍到书架
```bash
POST http://localhost:8080/reader3/saveBook?username=default
Content-Type: application/json

{
  "name": "测试书籍",
  "author": "测试作者",
  "bookUrl": "https://example.com/book/123",
  "origin": "https://example.com",
  "originName": "测试书源",
  "type": 0,
  "intro": "这是一本测试书籍"
}
```

#### 删除书籍
```bash
POST http://localhost:8080/reader3/deleteBook?username=default&bookUrl=https://example.com/book/123
```

#### 获取章节列表
```bash
GET http://localhost:8080/reader3/getChapterList?username=default&bookUrl=https://example.com/book/123
```

#### 保存阅读进度
```bash
POST http://localhost:8080/reader3/saveProgress?username=default&bookUrl=https://example.com/book/123&chapterIndex=5
```

### 4. 书源管理

#### 获取所有书源
```bash
GET http://localhost:8080/reader3/getBookSources?username=default
```

#### 获取启用的书源
```bash
GET http://localhost:8080/reader3/getEnabledBookSources?username=default
```

#### 保存书源
```bash
POST http://localhost:8080/reader3/saveBookSource?username=default
Content-Type: application/json

{
  "bookSourceName": "测试书源",
  "bookSourceUrl": "https://example.com",
  "bookSourceType": 0,
  "enabled": true,
  "searchUrl": "https://example.com/search?key={{key}}",
  "ruleSearch": {
    "bookList": ".book-list > .item",
    "name": ".title",
    "author": ".author"
  }
}
```

#### 删除书源
```bash
POST http://localhost:8080/reader3/deleteBookSource?username=default&sourceUrl=https://example.com
```

## 使用 curl 测试

### 用户注册
```bash
curl -X POST http://localhost:8080/reader3/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

### 用户登录
```bash
curl -X POST http://localhost:8080/reader3/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

### 获取书架
```bash
curl http://localhost:8080/reader3/getShelfBooks?username=default
```

## 使用 Postman 测试

1. 导入以下环境变量：
   - `baseUrl`: http://localhost:8080
   - `username`: default

2. 创建请求集合，按照上述API端点配置请求

## 数据存储位置

- 用户数据：`storage/data/users.json`
- 书架数据：`storage/data/{username}/shelf/{bookMd5}/book.json`
- 章节数据：`storage/data/{username}/shelf/{bookMd5}/chapters.json`
- 书源数据：`storage/data/{username}/bookSource.json`

## 注意事项

1. 默认用户名为 `default`，无需登录即可使用
2. 如果启用了 `secure` 模式（在 application.yml 中配置），则需要先注册和登录
3. 所有数据以JSON格式存储在文件系统中
4. 书籍URL的MD5值用作书籍目录名
