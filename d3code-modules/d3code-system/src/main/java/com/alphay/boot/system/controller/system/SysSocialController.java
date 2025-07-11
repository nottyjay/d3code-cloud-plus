package com.alphay.boot.system.controller.system;

import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.domain.vo.SysSocialVo;
import com.alphay.boot.system.service.ISysSocialService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 社会化关系
 *
 * @author Nottyjay
 * @since 1.0.0
 * @date 2023-06-16
 */
@Validated
@RestController
@RequestMapping("/social")
public class SysSocialController extends BaseController {

  @Resource private ISysSocialService socialUserService;

  /** 查询社会化关系列表 */
  @GetMapping("/list")
  public R<List<SysSocialVo>> list() {
    return R.ok(socialUserService.queryListByUserId(LoginHelper.getUserId()));
  }
}
