package com.alphay.boot.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.constant.SystemConstants;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.excel.utils.ExcelUtil;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.api.domain.param.SysPostQueryParam;
import com.alphay.boot.system.domain.bo.SysPostBo;
import com.alphay.boot.system.domain.vo.SysPostVo;
import com.alphay.boot.system.service.ISysPostService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 岗位信息操作处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/post")
public class SysPostController extends BaseController {

  @Resource private ISysPostService postService;

  /** 获取岗位列表 */
  @SaCheckPermission("system:post:list")
  @GetMapping("/list")
  public PageResult<SysPostVo> list(SysPostQueryParam param) {
    return postService.queryPageList(param);
  }

  /** 导出岗位列表 */
  @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
  @SaCheckPermission("system:post:export")
  @PostMapping("/export")
  public void export(SysPostQueryParam param, HttpServletResponse response) {
    List<SysPostVo> list = postService.queryList(param);
    ExcelUtil.exportExcel(list, "岗位数据", SysPostVo.class, response);
  }

  /**
   * 根据岗位编号获取详细信息
   *
   * @param postId 岗位ID
   */
  @SaCheckPermission("system:post:query")
  @GetMapping(value = "/{postId}")
  public R<SysPostVo> getInfo(@PathVariable Long postId) {
    return R.ok(postService.getVoById(postId));
  }

  /** 新增岗位 */
  @SaCheckPermission("system:post:add")
  @Log(title = "岗位管理", businessType = BusinessType.INSERT)
  @PostMapping
  public R<Void> add(@Validated @RequestBody SysPostBo post) {
    if (!postService.checkPostNameUnique(post)) {
      return R.fail("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
    } else if (!postService.checkPostCodeUnique(post)) {
      return R.fail("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
    }
    return toAjax(postService.insertPost(post));
  }

  /** 修改岗位 */
  @SaCheckPermission("system:post:edit")
  @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
  @PutMapping
  public R<Void> edit(@Validated @RequestBody SysPostBo post) {
    if (!postService.checkPostNameUnique(post)) {
      return R.fail("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
    } else if (!postService.checkPostCodeUnique(post)) {
      return R.fail("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
    } else if (SystemConstants.DISABLE.equals(post.getStatus())
        && postService.countUserPostById(post.getPostId()) > 0) {
      return R.fail("该岗位下存在已分配用户，不能禁用!");
    }
    return toAjax(postService.updatePost(post));
  }

  /**
   * 删除岗位
   *
   * @param postIds 岗位ID串
   */
  @SaCheckPermission("system:post:remove")
  @Log(title = "岗位管理", businessType = BusinessType.DELETE)
  @DeleteMapping("/{postIds}")
  public R<Void> remove(@PathVariable Long[] postIds) {
    return toAjax(postService.deletePostByIds(postIds));
  }

  /**
   * 获取岗位选择框列表
   *
   * @param postIds 岗位ID串
   * @param deptId 部门id
   */
  @SaCheckPermission("system:post:query")
  @GetMapping("/optionselect")
  public R<List<SysPostVo>> optionselect(
      @RequestParam(required = false) Long[] postIds, @RequestParam(required = false) Long deptId) {
    List<SysPostVo> list = new ArrayList<>();
    if (ObjectUtil.isNotNull(deptId)) {
      SysPostQueryParam param = new SysPostQueryParam();
      param.setDeptId(deptId);
      list = postService.queryList(param);
    } else if (postIds != null) {
      list = postService.selectPostByIds(List.of(postIds));
    }
    return R.ok(list);
  }
}
