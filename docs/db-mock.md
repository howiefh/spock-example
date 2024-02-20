# 前言

在进行单元测试时，如果使用真实的数据库，可能会遇到多种挑战。首先是测试数据的不确定性，真实数据库的数据可能因为之前的测试操作而产生变化，这会影响到测试的一致性和可重复性。其次是依赖性问题，测试可能依赖于数据库的特定环境或配置，这可能导致在不同环境下测试失败。此外，真实数据库通常涉及I/O操作，这可能导致测试运行缓慢，特别是当有大量测试需要运行时，这会显著增加整体的测试时间。使用内存数据库可以有效解决这些问题：它能够保证每次测试开始时都有一个干净的数据库状态，避免了数据的不确定性；由于其独立性，减少了外部依赖和环境配置的问题；同时，内存数据库由于其高速的存取性能，可以显著提升测试的执行速度。因此，在单元测试中采用内存数据库是一种既高效又可靠的策略。

常见的内存数据库有 H2、HSQLDB 和 Apache Derby 等。在 Java 项目中，H2 是非常受欢迎的内存数据库，经常被用于单元测试。（H2、HSQLDB 和 Derby的性能比较可以参考<https://www.h2database.com/html/performance.html>）

# 使用

## 引入依赖

如果使用Spring Boot，只需要在pom中参考如下配置引入H2依赖。如未使用Spring Boot，还需要自行添加最新的版本号。

```xml
<dependency>
     <groupId>com.h2database</groupId>
     <artifactId>h2</artifactId>
</dependency>
```

## 配置数据库连接

可以通过以下方式配置单元测试的数据库连接。

```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb;MODE=MYSQL;DB_CLOSE_DELAY=-1;IGNORECASE=FALSE;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;
    username: sa
    password: sa
```

这个 H2 数据库的 JDBC URL 包含了多个参数，用于定义数据库的连接和行为。下面是参数的解释：

- `jdbc:h2:mem:testdb`: 这部分定义了使用 JDBC 连接到 H2 数据库的基本格式。其中：
    - `jdbc:h2` 表示使用 JDBC 连接到 H2 数据库。
    - `mem` 表示数据库在内存中运行，数据在应用程序运行时存在，应用停止时数据会丢失。
    - `testdb` 是数据库的名字。在使用内存数据库时，如果需要多个连接共享同一个数据库实例，必须给数据库一个名字，如 `jdbc:h2:mem:testdb`。这样，只要是在同一个虚拟机和类加载器环境中，所有使用这个 URL 的连接都能访问到相同的数据库。

- `MODE=MYSQL`: 这个参数会让 H2 在兼容模式下运行，尽量模仿 MySQL 数据库的行为。这样，一些特定于 MySQL 的语法和功能在 H2 中也可以使用。具体的兼容情况可以参考H2文档： <https://www.h2database.com/html/features.html#compatibility> 。

- `DB_CLOSE_DELAY=-1`: H2数据库在内存模式下，数据仅在内存中临时存储，不写入硬盘。默认情况下，当最后一个连接关闭时，内存数据库也会关闭，里面的数据会丢失。为了防止这种情况，可以在连接数据库的URL中添加`;DB_CLOSE_DELAY=-1`参数，这样即使关闭所有连接，内存数据库仍然保持打开状态，数据不会丢失。

- `IGNORECASE=FALSE`: 在H2数据库中，默认情况下，文本列（如VARCHAR类型）是区分大小写的。这与MySQL的默认行为不同，MySQL中的文本列默认是不区分大小写的（如果建表时指定COLLATE=utf8_bin或COLLATE=utf8mb4_bin，是区分大小写的，COLLATE以`_ci`结尾，如`utf8_unicode_ci`时，不区分大小写）。想要在H2数据库中创建不区分大小写的文本列，可以在连接数据库时在JDBC URL中添加`IGNORECASE=TRUE`参数。这会使得所有的文本比较都按照不区分大小写的方式进行。

- `DATABASE_TO_LOWER=TRUE`: 默认情况下，H2 的配置是`DATABASE_TO_UPPER=TRUE;DATABASE_TO_LOWER=FALSE`，会将标识符转换为大写。设置 `DATABASE_TO_LOWER=TRUE`时，H2会默认设置`DATABASE_TO_UPPER=FALSE`，会将标识符转为小写。如果设置`DATABASE_TO_UPPER=FALSE;DATABASE_TO_LOWER=FALSE`即数据库不会自动将标识符转换为大写或小写，从而允许保留标识符的原始大小写状态。
 
- `CASE_INSENSITIVE_IDENTIFIERS=TRUE`: 如果需要不区分大小写的标识符时，可以在 URL 中添加 `CASE_INSENSITIVE_IDENTIFIERS=TRUE`。

综上所述，配置 JDBC URL 时，应该根据实际应用场景选择合适的参数组合。

不同参数组合可以参考测试用例：[H2DbSettingsTest](https://github.com/howiefh/spock-example/blob/master/src/test/groovy/io/github/howiefh/spock/example/H2DbSettingsTest.groovy)、 [H2 MySQL 模式测试用例](https://github.com/h2database/h2database/blob/806cdc5203c1483f8a8744f73a51e6699467d175/h2/src/test/org/h2/test/db/TestCompatibility.java#L310)、[H2 标识符测试用例](https://github.com/h2database/h2database/blob/806cdc5203c1483f8a8744f73a51e6699467d175/h2/src/test/org/h2/test/db/TestCompatibility.java#L724)

## 初始化数据

有多种方式可以实现初始化数据库，下面介绍三种相对简单的方式：H2 数据库的 `RUNSCRIPT` 命令，Spring Boot 提供的`spring.sql.init` 配置和`@Sql` 注解方式。

1. **H2 RUNSCRIPT 命令**：
 
    `RUNSCRIPT` 是 H2 数据库的命令，允许用户执行 SQL 脚本文件，既可以在 H2 控制台中直接使用，也可以通过 JDBC URL 在内存数据库创建时进行初始化，非常适合在不使用 Spring Boot 环境或需要在数据库启动时立即准备数据的场景中使用。
 
    示例如下：
    ```
    jdbc:h2:mem:testdb;INIT=runscript from 'classpath:schema.sql'\;runscript from 'classpath:data.sql';
    ```

2. **spring.sql.init 配置**：

    `spring.sql.init` 是 Spring Boot 2.5 及以上版本中的一组配置属性，用于在应用启动时自动执行 SQL 脚本以初始化数据库schema和数据，通过在 `application.properties` 或 `application.yml` 文件中设置，且这些脚本仅在应用启动时执行一次。

    示例如下：
    ```properties
    spring.sql.init.mode=ALWAYS
    spring.sql.init.schema-locations=classpath:schema.sql
    spring.sql.init.data-locations=classpath:data.sql
    ```
    Spring Boot 2.5之前的配置方式：
    ```properties
    spring.datasource.initialization-mode=ALWAYS
    spring.datasource.schema=classpath:schema.sql
    spring.datasource.data=classpath:data.sql
    spring.datasource.platform=h2
    ```

3. **@Sql 注解**：

    `@Sql` 是 Spring Framework 提供的注解，用于在测试方法执行前后运行 SQL 脚本，适用于测试类或方法，以方便在测试场景中进行数据的准备和清理，允许每个测试方法指定自己的 SQL 脚本以实现更精细的测试数据管理。

    示例如下：
    ```java
    @SqlGroup({
        @Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    public class UserRepositoryTest {
    }
    ```

**使用场景对比**：

- `RUNSCRIPT` 命令适用于在不依赖 Spring Boot 的情况下初始化 H2 数据库，或者在使用内存数据库时，希望在数据库启动时立即加载 SQL 脚本。
- `spring.sql.init` 配置适合于在 Spring Boot 应用的启动过程中自动初始化数据库架构和数据，适用于生产、测试和开发环境。
- `@Sql` 注解主要用于测试，通过在测试类或方法上声明，可以精确控制测试用例执行前后的数据库状态。

# 常见问题

使用 H2 进行单元测试时，应当尽量保持与生产环境接近，需要注意 H2 的局限性，并适当调整测试策略和配置。

简单介绍一下在使用 H2 时遇到过的一些问题及解决方案。

1. H2 的 `KEY` 或 `UNIQUE KEY` 是数据库级别而不是表级别的，因此不同表的索引名不能相同，建议建索引时通过表名和字段名拼接命名索引名以避免索引名称重复。
2. H2 不支持`DATE_FORMAT`, `JSON_EXTRACT`等函数，可以参考文档自定义函数：<https://www.h2database.com/html/features.html#user_defined_functions>。示例代码：[H2CompatibilityTest](https://github.com/howiefh/spock-example/blob/fb5047392742a1e2b4db77b01429f6e9fed499ad/src/test/groovy/io/github/howiefh/spock/example/H2CompatibilityTest.groovy#L35)。
3. H2 不支持`IF`函数，可以使用 `CASE WHEN THEN` 语句代替。
4. 如果遇到一些特殊的 MySQL 语句，H2 不能通过上述方式支持时，可以考虑根据不同数据库执行不同SQL。使用 MyBatis 时，可以使用 [databaseIdProvider](https://mybatis.org/mybatis-3/zh_CN/configuration.html#%E6%95%B0%E6%8D%AE%E5%BA%93%E5%8E%82%E5%95%86%E6%A0%87%E8%AF%86%EF%BC%88databaseidprovider%EF%BC%89)。示例代码：[MyBatisConfiguration](https://github.com/howiefh/spock-example/blob/master/src/main/java/io/github/howiefh/spock/config/MyBatisConfiguration.java)，[UserMapper](https://github.com/howiefh/spock-example/blob/master/src/main/resources/mappers/user/UserMapper.xml)，[UserDaoTest](https://github.com/howiefh/spock-example/blob/master/src/test/groovy/io/github/howiefh/spock/dao/UserDaoTest.groovy)。
5. 需要以表格形式查看 H2 数据库的数据时，可以通过配置 `spring.h2.console.enabled: true`，然后启动应用，在浏览器中打开 <http://localhost:8080/h2-console>。输入应用中所配置的 JDBC URL、User Name及Password，连接后即可看到数据库内容。

# 总结

在单元测试中采用H2内存数据库是一种高效且可靠的方式。它为测试提供了一个轻量级、快速启动的环境，能够模拟不同的数据库系统，如MySQL，能够确保测试的独立性和可重复性。通过使用JDBC URL进行适当配置，H2数据库能够为每次测试提供一个干净、隔离的状态，并在测试完成后轻松清理，这使得开发者能够在不影响现有开发或测试数据库的前提下，有效地进行数据库交互测试。因此，H2数据库可以作为一个单元测试中理想的数据库选择。