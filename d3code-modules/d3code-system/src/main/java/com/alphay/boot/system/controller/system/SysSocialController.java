package com.alphay.boot.system.controller.system;

import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.domain.vo.SysSocialVo;
import com.alphay.boot.system.service.ISysSocialService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 社会化关系
 *
 * @author thiszhc
 * @date 2023-06-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/social")
public class SysSocialController extends BaseController {

  private final ISysSocialService socialUserService;

  /** 查询社会化关系列表 */
  @GetMapping("/list")
  public R<List<SysSocialVo>> list() {
    return R.ok(socialUserService.queryListByUserId(LoginHelper.getUserId()));
  }
}
