package com.alphay.boot.system.controller.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.core.utils.file.MimeTypeUtils;
import com.alphay.boot.common.encrypt.annotation.ApiEncrypt;
import com.alphay.boot.common.idempotent.annotation.RepeatSubmit;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.helper.DataPermissionHelper;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.resource.api.RemoteFileService;
import com.alphay.boot.resource.api.domain.RemoteFile;
import com.alphay.boot.system.api.domain.param.SysUserQueryParam;
import com.alphay.boot.system.domain.bo.SysUserBo;
import com.alphay.boot.system.domain.bo.SysUserPasswordBo;
import com.alphay.boot.system.domain.bo.SysUserProfileBo;
import com.alphay.boot.system.domain.vo.SysUserVo;
import com.alphay.boot.system.service.ISysUserService;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 个人信息 业务处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/user/profile")
public class SysProfileController extends BaseController {

  @Resource private ISysUserService userService;

  @DubboReference private RemoteFileService remoteFileService;

  /** 个人信息 */
  @GetMapping
  public R<ProfileVo> profile() {
    SysUserVo user = userService.getVoById(LoginHelper.getUserId());
    String roleGroup = userService.selectUserRoleGroup(user.getUserId());
    String postGroup = userService.selectUserPostGroup(user.getUserId());
    ProfileVo profileVo = new ProfileVo(user, roleGroup, postGroup);
    return R.ok(profileVo);
  }

  /** 修改用户信息 */
  @RepeatSubmit
  @Log(title = "个人信息", businessType = BusinessType.UPDATE)
  @PutMapping
  public R<Void> updateProfile(@Validated @RequestBody SysUserProfileBo profile) {
    SysUserQueryParam param = BeanUtil.toBean(profile, SysUserQueryParam.class);
    param.setUserId(LoginHelper.getUserId());
    String username = LoginHelper.getUsername();
    if (StringUtils.isNotEmpty(param.getPhonenumber()) && !userService.checkPhoneUnique(param)) {
      return R.fail("修改用户'" + username + "'失败，手机号码已存在");
    }
    if (StringUtils.isNotEmpty(param.getEmail()) && !userService.checkEmailUnique(param)) {
      return R.fail("修改用户'" + username + "'失败，邮箱账号已存在");
    }
    SysUserBo userBo = BeanUtil.toBean(param, SysUserBo.class);
    int rows = DataPermissionHelper.ignore(() -> userService.updateUserProfile(userBo));
    if (rows > 0) {
      return R.ok();
    }
    return R.fail("修改个人信息异常，请联系管理员");
  }

  /**
   * 重置密码
   *
   * @param bo 新旧密码
   */
  @RepeatSubmit
  @ApiEncrypt
  @Log(title = "个人信息", businessType = BusinessType.UPDATE)
  @PutMapping("/updatePwd")
  public R<Void> updatePwd(@Validated @RequestBody SysUserPasswordBo bo) {
    SysUserVo user = userService.getVoById(LoginHelper.getUserId());
    String password = user.getPassword();
    if (!BCrypt.checkpw(bo.getOldPassword(), password)) {
      return R.fail("修改密码失败，旧密码错误");
    }
    if (BCrypt.checkpw(bo.getNewPassword(), password)) {
      return R.fail("新密码不能与旧密码相同");
    }
    int rows =
        DataPermissionHelper.ignore(
            () -> userService.resetUserPwd(user.getUserId(), BCrypt.hashpw(bo.getNewPassword())));
    if (rows > 0) {
      return R.ok();
    }
    return R.fail("修改密码异常，请联系管理员");
  }

  /**
   * 头像上传
   *
   * @param avatarfile 用户头像
   */
  @RepeatSubmit
  @GlobalTransactional(rollbackFor = Exception.class)
  @Log(title = "用户头像", businessType = BusinessType.UPDATE)
  @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public R<AvatarVo> avatar(@RequestPart("avatarfile") MultipartFile avatarfile)
      throws IOException {
    if (!avatarfile.isEmpty()) {
      String extension = FileUtil.extName(avatarfile.getOriginalFilename());
      if (!StringUtils.equalsAnyIgnoreCase(extension, MimeTypeUtils.IMAGE_EXTENSION)) {
        return R.fail("文件格式不正确，请上传" + Arrays.toString(MimeTypeUtils.IMAGE_EXTENSION) + "格式");
      }
      RemoteFile oss =
          remoteFileService.upload(
              avatarfile.getName(),
              avatarfile.getOriginalFilename(),
              avatarfile.getContentType(),
              avatarfile.getBytes());
      String avatar = oss.getUrl();
      boolean updateSuccess =
          DataPermissionHelper.ignore(
              () -> userService.updateUserAvatar(LoginHelper.getUserId(), oss.getOssId()));
      if (updateSuccess) {
        return R.ok(new AvatarVo(avatar));
      }
    }
    return R.fail("上传图片异常，请联系管理员");
  }

  public record AvatarVo(String imgUrl) {}

  public record ProfileVo(SysUserVo user, String roleGroup, String postGroup) {}
}
