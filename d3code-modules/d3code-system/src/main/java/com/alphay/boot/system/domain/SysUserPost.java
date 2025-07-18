package com.alphay.boot.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户和岗位关联 sys_user_post
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@TableName("sys_user_post")
public class SysUserPost {

  /** 用户ID */
  @TableId(type = IdType.INPUT)
  private Long userId;

  /** 岗位ID */
  private Long postId;
}
