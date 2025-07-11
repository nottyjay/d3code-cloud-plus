package com.alphay.boot.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.core.utils.StringUtils;
import jakarta.annotation.Resource;
import java.util.*;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 缓存监控
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@RestController
@RequestMapping("/cache")
public class CacheController {

  @Resource private RedissonConnectionFactory connectionFactory;

  /** 获取缓存监控列表 */
  @SaCheckPermission("monitor:cache:list")
  @GetMapping()
  public R<CacheListInfoVo> getInfo() throws Exception {
    RedisConnection connection = connectionFactory.getConnection();
    try {
      Properties commandStats = connection.commands().info("commandstats");
      List<Map<String, String>> pieList = new ArrayList<>();
      if (commandStats != null) {
        commandStats
            .stringPropertyNames()
            .forEach(
                key -> {
                  Map<String, String> data = new HashMap<>(2);
                  String property = commandStats.getProperty(key);
                  data.put("name", StringUtils.removeStart(key, "cmdstat_"));
                  data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
                  pieList.add(data);
                });
      }
      return R.ok(
          new CacheListInfoVo(
              connection.commands().info(), connection.commands().dbSize(), pieList));
    } finally {
      // 归还连接给连接池
      RedisConnectionUtils.releaseConnection(connection, connectionFactory);
    }
  }

  public record CacheListInfoVo(
      Properties info, Long dbSize, List<Map<String, String>> commandStats) {}
}
