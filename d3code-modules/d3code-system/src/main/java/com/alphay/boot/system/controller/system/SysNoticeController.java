package com.alphay.boot.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.core.service.DictService;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.resource.api.RemoteMessageService;
import com.alphay.boot.system.api.domain.param.SysNoticeQueryParam;
import com.alphay.boot.system.domain.bo.SysNoticeBo;
import com.alphay.boot.system.domain.vo.SysNoticeVo;
import com.alphay.boot.system.service.ISysNoticeService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 公告 信息操作处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/notice")
public class SysNoticeController extends BaseController {

  private final ISysNoticeService noticeService;
  private final DictService dictService;

  @DubboReference private RemoteMessageService remoteMessageService;

  /** 获取通知公告列表 */
  @SaCheckPermission("system:notice:list")
  @GetMapping("/list")
  public PageResult<SysNoticeVo> list(SysNoticeQueryParam param) {
    return noticeService.queryPageList(param);
  }

  /**
   * 根据通知公告编号获取详细信息
   *
   * @param noticeId 公告ID
   */
  @SaCheckPermission("system:notice:query")
  @GetMapping(value = "/{noticeId}")
  public R<SysNoticeVo> getInfo(@PathVariable Long noticeId) {
    return R.ok(noticeService.getVoById(noticeId));
  }

  /** 新增通知公告 */
  @SaCheckPermission("system:notice:add")
  @Log(title = "通知公告", businessType = BusinessType.INSERT)
  @PostMapping
  public R<Void> add(@Validated @RequestBody SysNoticeBo notice) {
    int rows = noticeService.insertNotice(notice);
    if (rows <= 0) {
      return R.fail();
    }
    String type = dictService.getDictLabel("sys_notice_type", notice.getNoticeType());
    remoteMessageService.publishAll("[" + type + "] " + notice.getNoticeTitle());
    return R.ok();
  }

  /** 修改通知公告 */
  @SaCheckPermission("system:notice:edit")
  @Log(title = "通知公告", businessType = BusinessType.UPDATE)
  @PutMapping
  public R<Void> edit(@Validated @RequestBody SysNoticeBo notice) {
    return toAjax(noticeService.updateNotice(notice));
  }

  /**
   * 删除通知公告
   *
   * @param noticeIds 公告ID串
   */
  @SaCheckPermission("system:notice:remove")
  @Log(title = "通知公告", businessType = BusinessType.DELETE)
  @DeleteMapping("/{noticeIds}")
  public R<Void> remove(@PathVariable Long[] noticeIds) {
    return toAjax(noticeService.deleteNoticeByIds(noticeIds));
  }
}
