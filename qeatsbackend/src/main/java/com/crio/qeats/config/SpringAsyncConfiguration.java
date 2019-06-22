package com.crio.qeats.config;

import java.util.concurrent.Executor;
import org.springframework.scheduling.annotation.AsyncConfigurer;

// TODO: CRIO_TASK_MODULE_MULTITHREADING
// Spring uses certain tags which help asynchronous execution.
// Use the class level tag for this module.
// Hint: This class implements AsyncConfigurer, so perhaps a tag in connection to that?
public class SpringAsyncConfiguration implements AsyncConfigurer {

  @Override
  public Executor getAsyncExecutor() {

    return null;
  }
}
