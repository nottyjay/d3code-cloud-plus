package com.alphay.boot.job.snailjob;

import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.job.core.executor.AbstractJobExecutor;
import com.aizuda.snailjob.client.model.ExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author Nottyjay
 * @since 1.0.0
 * @date 2024-05-17
 */
@Component
public class TestClassJobExecutor extends AbstractJobExecutor {

  @Override
  protected ExecuteResult doJobExecute(JobArgs jobArgs) {
    return ExecuteResult.success("TestJobExecutor测试成功");
  }
}
