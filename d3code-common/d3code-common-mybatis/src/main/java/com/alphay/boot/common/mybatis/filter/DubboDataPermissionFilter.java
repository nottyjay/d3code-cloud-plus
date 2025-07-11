package com.alphay.boot.common.mybatis.filter;

import com.alphay.boot.common.mybatis.helper.DataPermissionHelper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * dubbo 数据权限参数传递
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Activate(group = {CommonConstants.CONSUMER})
public class DubboDataPermissionFilter implements Filter {

  @Override
  public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
    RpcServiceContext context = RpcContext.getServiceContext();
    Map<String, Object> dataPermissionContext = DataPermissionHelper.getContext();
    context.setObjectAttachment(DataPermissionHelper.DATA_PERMISSION_KEY, dataPermissionContext);
    return invoker.invoke(invocation);
  }
}
