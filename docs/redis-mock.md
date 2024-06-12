## 前言

在软件开发中，集成 Redis 是非常常见的。然而，在进行单元测试时，直接连接到实际的 Redis 服务可能会引入不必要的复杂性。例如，在持续集成流水线中，由于环境隔离问题可能导致无法连接到 Redis，从而导致单元测试失败。此外，真实缓存的数据可能因为之前的测试操作导致不符合预期的结果，从而影响单元测试的一致性和可重复性。为了避免这些情况，采用 Redis Mock 工具变得非常必要。这些工具允许我们在不依赖真实环境的 Redis 服务的情况下对代码进行有效的测试，就像内存数据库 H2 能够在单元测试中模拟其他关系型数据库一样。这类工具为单元测试提供了简便且可靠的模拟选项，确保测试环境的一致性和可重复性。

## 有哪些可选的 Redis Mock 工具

我们可以发现以下开源项目可以实现 Redis Mock：

* [microwww/redis-mock](https://github.com/microwww/redis-mock)
* [zxl0714/redis-mock](https://github.com/zxl0714/redis-mock)
* [fppt/jedis-mock](https://github.com/fppt/jedis-mock)（fork自[zxl0714/redis-mock](https://github.com/zxl0714/redis-mock)）
* [kstyrc/embedded-redis](https://github.com/kstyrc/embedded-redis)
* [ozimov/embedded-redis](https://github.com/ozimov/embedded-redis) （fork自[kstyrc/embedded-redis](https://github.com/kstyrc/embedded-redis)）
 
这些开源项目按实现方式可以分为两类：

* 模拟 Redis 服务响应

    以 [fppt/jedis-mock](https://github.com/fppt/jedis-mock) 为代表，这类项目通过模拟 Redis 服务的响应并提供 Redis API 方法，允许在不依赖真实 Redis 服务的情况下进行单元测试。
 
* 嵌入真正 Redis 服务

    以 [ozimov/embedded-redis](https://github.com/ozimov/embedded-redis) 为代表，这类项目通过嵌入真正的 Redis 服务，启动一个实际的 Redis 服务实例来处理客户端请求。

下面是 jedis-mock 和 embedded-redis 在几个方面的比较：

|             | jedis-mock                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               | embedded-redis                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|-------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 实现方式        | 	在网络协议级别模拟Redis服务的响应                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | 	启动真正的Redis服务实例处理客户端请求                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| 运行开销        | 	启动和关闭开销小，因为只模拟部分Redis服务的行为	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | 启动和关闭开销相对较大，因为启动真正的Redis服务                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| 配置方式        | 配置简单，可以快速上手	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | 支持的配置方式更多，可以指定Redis配置文件                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| 是否支持Lua脚本	  | 是（从2023.5.22发布的1.0.8开始通过 luaj 支持）                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        | 	是                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| 是否支持Redis集群 | 是（从2023.8.12发布的1.0.10开始支持）		                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | 是                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| 支持的命令       | [支持大多数常用命令](https://github.com/fppt/jedis-mock/blob/master/supported_operations.md)，可以通过拦截器直接返回命令响应的方式mock不支持的命令                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | 以嵌入的redis版本为准，默认使用redis-server-2.8.19 可以指定redis路径嵌入                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| 社区支持情况      | ![GitHub Repo stars](https://img.shields.io/github/stars/fppt/jedis-mock) ![GitHub watchers](https://img.shields.io/github/watchers/fppt/jedis-mock) ![GitHub forks](https://img.shields.io/github/forks/fppt/jedis-mock) ![GitHub issues](https://img.shields.io/github/issues/fppt/jedis-mock) ![GitHub closed issues](https://img.shields.io/github/issues-closed/fppt/jedis-mock) ![GitHub pull requests](https://img.shields.io/github/issues-pr/fppt/jedis-mock) ![GitHub closed pull requests](https://img.shields.io/github/issues-pr-closed/fppt/jedis-mock) ![GitHub contributors](https://img.shields.io/github/contributors/fppt/jedis-mock) ![GitHub last commit (by committer)](https://img.shields.io/github/last-commit/fppt/jedis-mock) | ![GitHub Repo stars](https://img.shields.io/github/stars/ozimov/embedded-redis) ![GitHub watchers](https://img.shields.io/github/watchers/ozimov/embedded-redis) ![GitHub forks](https://img.shields.io/github/forks/ozimov/embedded-redis) ![GitHub issues](https://img.shields.io/github/issues/ozimov/embedded-redis) ![GitHub closed issues](https://img.shields.io/github/issues-closed/ozimov/embedded-redis) ![GitHub pull requests](https://img.shields.io/github/issues-pr/ozimov/embedded-redis) ![GitHub closed pull requests](https://img.shields.io/github/issues-pr-closed/ozimov/embedded-redis) ![GitHub contributors](https://img.shields.io/github/contributors/ozimov/embedded-redis) ![GitHub last commit (by committer)](https://img.shields.io/github/last-commit/ozimov/embedded-redis) |

总的来说，jedis-mock和embedded-redis都有其适用的场景和优缺点。我们可以根据需求选择最适合的工具。如果需要在本地模拟完整的Redis服务，可以选择embedded-redis。如果需要更快地模拟Redis服务的响应或希望干预某些响应，那么可以选择jedis-mock。

### 如何使用jedis-mock

添加maven依赖

```xml

<dependency>
    <groupId>com.github.fppt</groupId>
    <artifactId>jedis-mock</artifactId>
    <version>1.1.1</version>
</dependency>
```

创建一个 Redis 服务，并通过客户端连接。

```java
// 将redis mock服务绑定到一个随机端口
RedisServer server = RedisServer
        .newRedisServer()
        .start();
 
// Jedis 连接:
Jedis jedis = new Jedis(server.getHost(), server.getBindPort());
jedis.set("key", "value");
server.stop();
```

模拟 Redis 集群。

```java
RedisServer server = RedisServer
        .newRedisServer()
        .setOptions(ServiceOptions.defaultOptions().withClusterModeEnabled())
        .start();

// Jedis 连接:
Set<HostAndPort> jedisClusterNodes = new HashSet<>();
jedisClusterNodes.add(new HostAndPort(server.getHost(), server.getBindPort()));
JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);
```

通过拦截器拦截Redis命令并给出对应响应。

```java
RedisServer server = RedisServer
    .newRedisServer()
    .setOptions(ServiceOptions.withInterceptor((state, roName, params) -> {
        if ("get".equalsIgnoreCase(roName)) {
            // 可以模仿 Redis 的任何响应
            return Response.bulkString(Slice.create("MOCK_VALUE"));
        } else if ("echo".equalsIgnoreCase(roName)) {
            // 你可以在这里编写任何验证
            assertEquals("hello", params.get(0).toString());
            // 模仿连接中断
            return MockExecutor.breakConnection(state);
        } else {
            // 委托给 JedisMock 执行，它将在可能的情况下模拟真实的 Redis 行为。
            return MockExecutor.proceed(state, roName, params);
        }
    }))
    .start();
try (Jedis jedis = new Jedis(server.getHost(), server.getBindPort())) {
    assertEquals("MOCK_VALUE", jedis.get("foo"));
    assertEquals("OK", jedis.set("bar", "baz"));
    assertThrows(JedisConnectionException.class, () -> jedis.echo("hello"));
}
server.stop();
```

### 如何使用embedded-redis

添加maven依赖

```xml
<dependency>
  <groupId>it.ozimov</groupId>
  <artifactId>embedded-redis</artifactId>
  <version>0.7.3</version>
</dependency>
```

创建一个Redis服务，并通过客户端连接

```java
// 将redis mock服务绑定到一个随机端口
RedisServer redisServer = new RedisServer(6379);
redisServer.start();
 
 
// Jedis 连接:
Jedis jedis = new Jedis("localhost", 6379);
jedis.set("key", "value");
redisServer.stop();
```

也可以指定redis的可执行文件，否则默认使用redis-server-2.8.19

```java
// 1) 指定显式文件
RedisServer redisServer = new RedisServer("/path/to/your/redis", 6379);
 
// 2) 指定不同操作系统执行文件
RedisExecProvider customProvider = RedisExecProvider.defaultProvider()
  .override(OS.UNIX, "/path/to/unix/redis")
  .override(OS.WINDOWS, Architecture.x86, "/path/to/windows/redis")
  .override(OS.Windows, Architecture.x86_64, "/path/to/windows/redis")
  .override(OS.MAC_OS_X, Architecture.x86, "/path/to/macosx/redis")
  .override(OS.MAC_OS_X, Architecture.x86_64, "/path/to/macosx/redis")
   
RedisServer redisServer = new RedisServer(customProvider, 6379);
```

用流式API指定更多配置

```java
RedisServer redisServer = RedisServer.builder()
  .redisExecProvider(customRedisProvider)
  .port(6379)
  .slaveOf("locahost", 6378)
  .configFile("/path/to/your/redis.conf")
  .build();
```

```java
RedisServer redisServer = RedisServer.builder()
  .redisExecProvider(customRedisProvider)
  .port(6379)
  .setting("bind 127.0.0.1")
  .slaveOf("locahost", 6378)
  .setting("daemonize no")
  .setting("appendonly no")
  .setting("maxmemory 128M")
  .build();
```

## 在 Spock 中使用 Redis Mock 工具

Spock 支持全局扩展。只需创建一个实现了接口 `IGlobalExtension` 的类，并将其全称类名放在classpath的 `META-INF/services/org.spockframework.runtime.extension.IGlobalExtension` 文件中，满足这两个条件，Spock 运行时就会自动加载并使用该扩展。

IGlobalExtension 有以下三种方法：

* `start()` 在 Spock 执行开始时调用一次。
* `visitSpec(SpecInfo spec)` 每个测试类都会被调用一次。
* `stop()` 在 Spock 执行结束时至少调用一次。

我们可以写一个类实现 `IGlobalExtension` 接口，在 `start` 方法中启动 Redis Mock 服务，在 `stop` 方法中停止 Redis Mock 服务。以jedis-mock为例，示例如下：

```groovy
class SpockGlobalExtension implements IGlobalExtension {
    static RedisServer server = RedisServer.newRedisServer(6379)
    static started = false

    @Override
    void start() {
        server.start()
        started = true
    }

    @Override
    void visitSpec(SpecInfo specInfo) {
    }

    @Override
    void stop() {
        if (started) {
            server.stop()
            started = false
        }
    }
}
```

## 公司内部分布式缓存服务使用 Redis Mock 工具

公司内部使用R2M（即将停用）和JIMDB分布式缓存服务并不直接兼容这些开源的Redis Mock工具，这要求我们进行一定程度的定制化开发。

我之前在项目中使用R2M时，针对这一问题开发过一个专门的工具包，它为R2M提供了Mock支持，但由于R2M即将下线，所以不得不迁往JIMDB。

对于JIMDB也可以做一些定制开发，使其支持Redis Mock工具。最简单的方式就是新写一个实现了`ConfigClient`接口的类，方便起见也可以直接继承`ConfigLongPollingSyncClient`类，这个类中的`ping`方法会请求接口返回分布式缓存节点信息，我们可以在一个配置文件中维护Redis Mock服务的节点配置信息。

```java
public class MockConfigClient extends ConfigLongPollingSyncClient {
    MockConfigClient(HttpSyncClient httpClient, ThreadPoolExecutor longPollingExecutor) {
        super(httpClient, longPollingExecutor);
    }

    CfsDataDto ping(PingContext pingCtx) throws AccessCfsException {
        try (InputStream inputStream = Files.newInputStream(ResourceUtils.getFile("classpath:jim.config.json").toPath())) {
            CfsData cfsData = JSON.parseObject(inputStream, CfsData.class);
            return cfsData.toCfsDataDto(pingCtx.token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

## 总结

引入Redis Mock工具可以提升单元测试的便捷性，它能够全局模拟真实Redis服务的行为，类似于H2内存数据库在数据库测试中的作用。