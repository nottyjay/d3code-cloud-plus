package com.alphay.boot.system.domain.vo;

import com.alphay.boot.common.core.utils.StringUtils;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 路由显示信息
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class MetaVo implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 设置该路由在侧边栏和面包屑中展示的名字 */
  private String title;

  /** 设置该路由的图标，对应路径src/assets/icons/svg */
  private String icon;

  /** 设置为true，则不会被 <keep-alive>缓存 */
  private Boolean noCache;

  /** 内链地址（http(s)://开头） */
  private String link;

  public MetaVo(String title, String icon) {
    this.title = title;
    this.icon = icon;
  }

  public MetaVo(String title, String icon, Boolean noCache) {
    this.title = title;
    this.icon = icon;
    this.noCache = noCache;
  }

  public MetaVo(String title, String icon, String link) {
    this.title = title;
    this.icon = icon;
    this.link = link;
  }

  public MetaVo(String title, String icon, Boolean noCache, String link) {
    this.title = title;
    this.icon = icon;
    this.noCache = noCache;
    if (StringUtils.ishttp(link)) {
      this.link = link;
    }
  }
}
