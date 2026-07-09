
# 项目介绍

MybatisPlus Solon Plugin

- [solon](https://solon.noear.org/) 类似 `spring-boot` 国产 web 开发框架。

## 模块说明（3.5.17 起）

| 模块 | 适用环境 | 说明 |
| --- | --- | --- |
| `mybatis-plus-solon-plugin` | Solon 4.x（基线 4.0.x，主线） | 核心插件，源码基线 |
| `mybatis-plus-solon3-plugin` | Solon 3.x LTS（基线 3.7.x） | 与核心插件同源码，按 Solon 3 LTS 依赖编译发布 |
| `mybatis-plus-solon-native-image` | Solon 4.x + GraalVM Native | AOT 元数据注册（对标 `mybatis-plus-spring-boot-native-image`），构建 native 二进制时添加 |

### Maven 坐标

Solon 4.x 项目（主线）：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-solon-plugin</artifactId>
    <version>3.5.17</version>
</dependency>
```

Solon 3.x LTS 项目：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-solon3-plugin</artifactId>
    <version>3.5.17</version>
</dependency>
```

Solon 4.x + GraalVM Native 项目，额外添加：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-solon-native-image</artifactId>
    <version>3.5.17</version>
</dependency>
```

native 构建示例见 `examples/native-demo`（`mvn -Pnative native:compile`）。

注意：native 构建时 classpath 需要 `kotlin-stdlib-jdk8` 与 `kotlin-reflect`（mybatis-plus 的
`IRepository` 含 kotlin 链式方法签名，solon-aot 处理期需要可加载这些类型）。

## 3.5.17 升级说明（破坏性变更）

跟随 mybatis-plus 3.5.17 消除 JDK9+ split-package 的方向，本插件提供的扩展类由
`com.baomidou.mybatisplus.extension.*` 迁移至 `com.baomidou.mybatisplus.solon.*`：

| 旧包名 | 新包名 |
| --- | --- |
| `com.baomidou.mybatisplus.extension.service.IService` | `com.baomidou.mybatisplus.solon.service.IService` |
| `com.baomidou.mybatisplus.extension.service.impl.ServiceImpl` | `com.baomidou.mybatisplus.solon.service.impl.ServiceImpl` |
| `com.baomidou.mybatisplus.extension.repository.CrudRepository` | `com.baomidou.mybatisplus.solon.repository.CrudRepository` |
| `com.baomidou.mybatisplus.extension.activerecord.Model` | `com.baomidou.mybatisplus.solon.activerecord.Model` |
| `com.baomidou.mybatisplus.extension.toolkit.SqlRunner` | `com.baomidou.mybatisplus.solon.toolkit.SqlRunner` |
| `com.baomidou.mybatisplus.extension.ddl.SimpleDdl` | `com.baomidou.mybatisplus.solon.ddl.SimpleDdl` |
| `com.baomidou.mybatisplus.extension.spi.SolonCompatibleSet` | `com.baomidou.mybatisplus.solon.spi.SolonCompatibleSet` |
| `com.baomidou.mybatisplus.core.override.SolonMybatisMapperProxy` | `com.baomidou.mybatisplus.solon.override.SolonMybatisMapperProxy` |

# 特别赞助

<p>
  <a href="https://apipig.aizuda.com?from=mp" target="_blank">
   <img alt="apipig-Logo" src="https://foruda.gitee.com/images/1780817190825464732/410b4c54_12260.png" width="160px" height="50px">
  </a>
</p>
