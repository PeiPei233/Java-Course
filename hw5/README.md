# Chat-Java

## 运行指南

本项目使用 `Java` 编写，使用 `Maven` 管理依赖。需要安装 `JDK 17+` 以及 `Maven`。

### 服务端

进入 `server` 目录，使用 `Maven` 打包后即可运行：

```shell
mvn clean package assembly:single
java -jar target/server-1.0-SNAPSHOT-jar-with-dependencies.jar
```

可以通过设置环境变量或修改 `server/src/main/resources/application.properties` 并重新编译来修改部分配置，程序将会按照以下顺序读取配置：环境变量 > `application.properties` > 默认值。

可修改的配置项如下：

| 环境变量 | 配置项 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `SERVER_PORT` | `server.port` | `8080` | 服务端口 |
| `DB_URL` | `db.url` | null | 数据库地址 |
| `DB_USER` | `db.user` | null | 数据库用户名 |
| `DB_PASSWORD` | `db.password` | null | 数据库密码 |
| `DB_POOL_SIZE` | `db.poolSize` | `10` | 数据库连接池大小 |

### 客户端

进入 `client` 目录，使用 `Maven` 打包后即可运行：

```shell
mvn clean package assembly:single
java -jar target/client-1.0-SNAPSHOT-jar-with-dependencies.jar
```