package com.alphay.boot.common.dubbo.config;

import com.alphay.boot.common.core.utils.StringUtils;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import lombok.SneakyThrows;
import org.apache.dubbo.common.constants.CommonConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

/**
 * dubbo自定义IP注入(避免IP不正确问题)
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

  /**
   * 获取该 BeanFactoryPostProcessor 的顺序，确保它在容器初始化过程中具有最高优先级
   *
   * @return 优先级顺序值，越小优先级越高
   */
  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  /**
   * 在 Spring 容器初始化过程中对 Bean 工厂进行后置处理
   *
   * @param beanFactory 可配置的 Bean 工厂
   * @throws BeansException 如果在处理过程中发生错误
   */
  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    String property = System.getProperty(CommonConstants.DubboProperty.DUBBO_IP_TO_REGISTRY);
    if (StringUtils.isNotBlank(property)) {
      return;
    }
    String ip = "127.0.0.1";
    // 获取第一个非虚拟网卡地址
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
    while (interfaces.hasMoreElements()) {
      NetworkInterface ni = interfaces.nextElement();
      // 排除回环、虚拟和禁用的接口
      if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) continue;

      // 排除 WSL 网卡（名称通常包含 "vEthernet" 或 "WSL"）
      if (ni.getName().contains("vEthernet") || ni.getDisplayName().contains("WSL")) continue;

      Enumeration<InetAddress> addresses = ni.getInetAddresses();
      while (addresses.hasMoreElements()) {
        InetAddress addr = addresses.nextElement();
        if (addr instanceof Inet4Address && !addr.isLinkLocalAddress()) {
          ip = addr.getHostAddress();
        }
      }
    }
    // 设置系统属性 DUBBO_IP_TO_REGISTRY 为获取到的 IP 地址
    System.setProperty(CommonConstants.DubboProperty.DUBBO_IP_TO_REGISTRY, ip);
  }
}
