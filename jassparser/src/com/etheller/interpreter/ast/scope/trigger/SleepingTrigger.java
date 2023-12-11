package com.etheller.interpreter.ast.scope.trigger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;

public class SleepingTrigger {
  private ScheduledExecutorService scheduler;

  public SleepingTrigger() {
    scheduler = Executors.newScheduledThreadPool(1);
  }

  public void scheduleWakeup(double time, GlobalScope globalScope, TriggerExecutionScope triggerExecutionScope, 
    BiConsumer<GlobalScope, TriggerExecutionScope> triggerWakeupCallback) {
    final long sleepTime = (long) time;
    Runnable task = new Runnable() {
        public void run() {
          triggerWakeupCallback.accept(globalScope, triggerExecutionScope);
        }
    };
    scheduler.schedule(task, sleepTime, TimeUnit.MILLISECONDS);
  }

  public void shutdown() {
    scheduler.shutdown();
  }
}
