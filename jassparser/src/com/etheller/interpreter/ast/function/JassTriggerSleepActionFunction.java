package com.etheller.interpreter.ast.function;

import java.util.List;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;

public class JassTriggerSleepActionFunction implements JassFunction{
  private double sleepTime;

  public void setSleepTime(double sleepTime) {
    this.sleepTime = sleepTime;
  }

  public double getSleepTime() {
    return this.sleepTime;
  }

  @Override
  public JassValue call(final List<JassValue> arguments, final GlobalScope globalScope,
      final TriggerExecutionScope triggerScope) {
    // triggerScope.setSleepTime(this.sleepTime);
    return null;
  }
}
