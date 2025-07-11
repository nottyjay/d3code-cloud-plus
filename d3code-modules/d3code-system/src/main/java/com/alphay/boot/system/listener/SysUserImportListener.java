package com.alphay.boot.system.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.http.HtmlUtil;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.SpringUtils;
import com.alphay.boot.common.core.utils.StreamUtils;
import com.alphay.boot.common.core.utils.ValidatorUtils;
import com.alphay.boot.common.excel.core.ExcelListener;
import com.alphay.boot.common.excel.core.ExcelResult;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.system.domain.bo.SysUserBo;
import com.alphay.boot.system.domain.vo.SysUserImportVo;
import com.alphay.boot.system.domain.vo.SysUserVo;
import com.alphay.boot.system.service.ISysConfigService;
import com.alphay.boot.system.service.ISysUserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统用户自定义导入
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
public class SysUserImportListener extends AnalysisEventListener<SysUserImportVo>
    implements ExcelListener<SysUserImportVo> {

  private final ISysUserService userService;

  private final String password;

  private final Boolean isUpdateSupport;

  private final Long operUserId;
  private final StringBuilder successMsg = new StringBuilder();
  private final StringBuilder failureMsg = new StringBuilder();
  private int successNum = 0;
  private int failureNum = 0;

  public SysUserImportListener(Boolean isUpdateSupport) {
    String initPassword =
        SpringUtils.getBean(ISysConfigService.class).selectConfigByKey("sys.user.initPassword");
    this.userService = SpringUtils.getBean(ISysUserService.class);
    this.password = BCrypt.hashpw(initPassword);
    this.isUpdateSupport = isUpdateSupport;
    this.operUserId = LoginHelper.getUserId();
  }

  @Override
  public void invoke(SysUserImportVo userVo, AnalysisContext context) {
    SysUserVo sysUser = this.userService.selectUserByUserName(userVo.getUserName());
    try {
      // 验证是否存在这个用户
      if (ObjectUtil.isNull(sysUser)) {
        SysUserBo user = BeanUtil.toBean(userVo, SysUserBo.class);
        ValidatorUtils.validate(user);
        user.setPassword(password);
        user.setCreateBy(operUserId);
        userService.insertUser(user);
        successNum++;
        successMsg
            .append("<br/>")
            .append(successNum)
            .append("、账号 ")
            .append(user.getUserName())
            .append(" 导入成功");
      } else if (isUpdateSupport) {
        Long userId = sysUser.getUserId();
        SysUserBo user = BeanUtil.toBean(userVo, SysUserBo.class);
        user.setUserId(userId);
        ValidatorUtils.validate(user);
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(operUserId);
        userService.updateUser(user);
        successNum++;
        successMsg
            .append("<br/>")
            .append(successNum)
            .append("、账号 ")
            .append(user.getUserName())
            .append(" 更新成功");
      } else {
        failureNum++;
        failureMsg
            .append("<br/>")
            .append(failureNum)
            .append("、账号 ")
            .append(sysUser.getUserName())
            .append(" 已存在");
      }
    } catch (Exception e) {
      failureNum++;
      String msg =
          "<br/>" + failureNum + "、账号 " + HtmlUtil.cleanHtmlTag(userVo.getUserName()) + " 导入失败：";
      String message = e.getMessage();
      if (e instanceof ConstraintViolationException cvException) {
        message =
            StreamUtils.join(
                cvException.getConstraintViolations(), ConstraintViolation::getMessage, ", ");
      }
      failureMsg.append(msg).append(message);
      log.error(msg, e);
    }
  }

  @Override
  public void doAfterAllAnalysed(AnalysisContext context) {}

  @Override
  public ExcelResult<SysUserImportVo> getExcelResult() {
    return new ExcelResult<SysUserImportVo>() {

      @Override
      public String getAnalysis() {
        if (failureNum > 0) {
          failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
          throw new ServiceException(failureMsg.toString());
        } else {
          successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
      }

      @Override
      public List<SysUserImportVo> getList() {
        return null;
      }

      @Override
      public List<String> getErrorList() {
        return null;
      }
    };
  }
}
