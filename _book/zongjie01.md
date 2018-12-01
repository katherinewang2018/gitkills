## JFinal 框架中缓存的使用
1.j2cache缓存的使用
1.1 一级缓存使用Redis；二级缓存使用Ehcache
    二级缓存Ehcache是内存缓存；二级缓存是为了缓解一级缓存的压力；
设计原理：
    https://img-blog.csdn.net/20180827132737551?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0MTY5ODAy/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70
读取数据：
    https://img-blog.csdn.net/20180827132817303?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0MTY5ODAy/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70

更新数据：
    https://img-blog.csdn.net/20180827132856492?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0MTY5ODAy/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70

1.2 缓存的配置：ehcache.xml 
以下是参数说明：
name              - Sets the name of the cache. This is used to identify the cache. It must be unique.
maxInMemory       - Sets the maximum number of objects that will be created in memory
        eternal           - Sets whether elements are eternal. If eternal,  timeouts are ignored and the element
                            is never expired.
        timeToIdleSeconds - Sets the time to idle for an element before it expires. Is only used
                            if the element is not eternal. Idle time is now - last accessed time
        timeToLiveSeconds - Sets the time to live for an element before it expires. Is only used
                            if the element is not eternal. TTL is now - creation time
        overflowToDisk    - Sets whether elements can overflow to disk when the in-memory cache
                            has reached the maxInMemory limit.

```
 <defaultCache
          // 这里是默认缓存配置
    </defaultCache>

<ehcache updateCheck="false" dynamicConfig="false"  >
<cache name="session"
           maxElementsInMemory=
           eternal=
           timeToIdleSeconds=
           timeToLiveSeconds=
           overflowToDisk= />

</ehcache>
```
2.j2cache.properties 配置文件

```

# Cache Broadcast Method
# values:
# jgroups -> use jgroups's multicast
# redis -> use redis publish/subscribe mechanism

cache.broadcast=redis


# Level 1&2 provider
# values:
# none -> disable this level cache
# ehcache -> use ehcache as level 1 cache
# redis -> use redis as level 2 cache
# [classname] -> use custom provider

cache.L1.provider_class=ehcache
cache.L2.provider_class=redis

# Cache Serialization Provider
# values:
# fst -> fast-serialization
# java -> java standard
# [classname implements Serializer]

cache.serialization = fst

# Redis connection configuration

## connection
redis.host = localhost
redis.port = 6379
redis.timeout = 2000
redis.password = 123456
redis.database = 1

## properties
redis.maxActive = 100
redis.maxIdle = 2000
redis.maxWaitMillis = 100
redis.minEvictableIdleTimeMillis = 864000000
redis.minIdle = 1000
redis.numTestsPerEvictionRun = 10
redis.lifo = false
redis.softMinEvictableIdleTimeMillis = 10
redis.testOnBorrow = true
redis.testOnReturn = false
redis.testWhileIdle = false
redis.timeBetweenEvictionRunsMillis = 300000
redis.blockWhenExhausted = true

```
3.j2cache的使用J2CacheKit 类中包括set get remove 等方法
登陆时可以缓存用户 校验码的缓存等