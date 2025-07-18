package com.alphay.boot.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.alphay.boot.common.core.constant.CacheConstants;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.core.utils.StreamUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.redis.utils.RedisUtils;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.api.domain.SysUserOnline;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;

/**
 * 在线用户监控
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@RestController
@RequestMapping("/online")
public class SysUserOnlineController extends BaseController {

  /**
   * 获取在线用户监控列表
   *
   * @param ipaddr IP地址
   * @param userName 用户名
   */
  @SaCheckPermission("monitor:online:list")
  @GetMapping("/list")
  public PageResult<SysUserOnline> list(String ipaddr, String userName) {
    // 获取所有未过期的 token
    Collection<String> keys = RedisUtils.keys(CacheConstants.ONLINE_TOKEN_KEY + "*");
    List<SysUserOnline> userOnlineDTOList = new ArrayList<>();
    for (String key : keys) {
      String token = StringUtils.substringAfterLast(key, ":");
      // 如果已经过期则跳过
      if (StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) < -1) {
        continue;
      }
      userOnlineDTOList.add(RedisUtils.getCacheObject(CacheConstants.ONLINE_TOKEN_KEY + token));
    }
    if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
      userOnlineDTOList =
          StreamUtils.filter(
              userOnlineDTOList,
              userOnline ->
                  StringUtils.equals(ipaddr, userOnline.getIpaddr())
                      && StringUtils.equals(userName, userOnline.getUserName()));
    } else if (StringUtils.isNotEmpty(ipaddr)) {
      userOnlineDTOList =
          StreamUtils.filter(
              userOnlineDTOList, userOnline -> StringUtils.equals(ipaddr, userOnline.getIpaddr()));
    } else if (StringUtils.isNotEmpty(userName)) {
      userOnlineDTOList =
          StreamUtils.filter(
              userOnlineDTOList,
              userOnline -> StringUtils.equals(userName, userOnline.getUserName()));
    }
    Collections.reverse(userOnlineDTOList);
    userOnlineDTOList.removeAll(Collections.singleton(null));
    List<SysUserOnline> userOnlineList =
        BeanUtil.copyToList(userOnlineDTOList, SysUserOnline.class);
    return PageResult.build(userOnlineList);
  }

  /**
   * 强退用户
   *
   * @param tokenId token值
   */
  @SaCheckPermission("monitor:online:forceLogout")
  @Log(title = "在线用户", businessType = BusinessType.FORCE)
  @DeleteMapping("/{tokenId}")
  public R<Void> forceLogout(@PathVariable String tokenId) {
    try {
      StpUtil.kickoutByTokenValue(tokenId);
    } catch (NotLoginException ignored) {
    }
    return R.ok();
  }

  /** 获取当前用户登录在线设备 */
  @GetMapping()
  public PageResult<SysUserOnline> getInfo() {
    // 获取指定账号 id 的 token 集合
    List<String> tokenIds = StpUtil.getTokenValueListByLoginId(StpUtil.getLoginIdAsString());
    List<SysUserOnline> userOnlineDTOList =
        tokenIds.stream()
            .filter(token -> StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) >= -1)
            .map(
                token ->
                    (SysUserOnline)
                        RedisUtils.getCacheObject(CacheConstants.ONLINE_TOKEN_KEY + token))
            .collect(Collectors.toList());
    // 复制和处理 SysUserOnline 对象列表
    Collections.reverse(userOnlineDTOList);
    userOnlineDTOList.removeAll(Collections.singleton(null));
    List<SysUserOnline> userOnlineList =
        BeanUtil.copyToList(userOnlineDTOList, SysUserOnline.class);
    return PageResult.build(userOnlineList);
  }

  /**
   * 强退当前在线设备
   *
   * @param tokenId token值
   */
  @Log(title = "在线设备", businessType = BusinessType.FORCE)
  @DeleteMapping("/myself/{tokenId}")
  public R<Void> remove(@PathVariable("tokenId") String tokenId) {
    try {
      // 获取指定账号 id 的 token 集合
      List<String> keys = StpUtil.getTokenValueListByLoginId(StpUtil.getLoginIdAsString());
      keys.stream()
          .filter(key -> key.equals(tokenId))
          .findFirst()
          .ifPresent(key -> StpUtil.kickoutByTokenValue(tokenId));
    } catch (NotLoginException ignored) {
    }
    return R.ok();
  }
}
