package com.etheller.interpreter.ast.scope.trigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.etheller.interpreter.ast.debug.JassException;
import com.etheller.interpreter.ast.function.JassFunction;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.util.CHandle;

public class Trigger implements CHandle {
	private static int STUPID_STATIC_TRIGGER_COUNT_DELETE_THIS_LATER = 452354453;
	private final int handleId = STUPID_STATIC_TRIGGER_COUNT_DELETE_THIS_LATER++;
	private final List<TriggerBooleanExpression> conditions = new ArrayList<>();
	private final List<JassFunction> actions = new ArrayList<>();
	private int evalCount;
	private int execCount;
	private boolean enabled = true;
	// used for eval
	private transient final TriggerExecutionScope triggerExecutionScope = new TriggerExecutionScope(this);
	private boolean waitOnSleeps = true;
  private int currentActionIndex = 0;
  private boolean isActionPaused = false;

	public int addAction(final JassFunction function) {
		final int index = this.actions.size();
		this.actions.add(function);
		return index;
	}

	public int addCondition(final TriggerBooleanExpression boolexpr) {
		final int index = this.conditions.size();
		this.conditions.add(boolexpr);
		return index;
	}

	public void removeCondition(final TriggerBooleanExpression boolexpr) {
		this.conditions.remove(boolexpr);
	}

	public void removeConditionAtIndex(final int conditionIndex) {
		this.conditions.remove(conditionIndex);
	}

	public void clearConditions() {
		this.conditions.clear();
	}

	public int getEvalCount() {
		return this.evalCount;
	}

	public int getExecCount() {
		return this.execCount;
	}

  public boolean getIsActionPaused() {
    return this.isActionPaused;
  }

  public void setIsActionPaused(boolean isActionPaused) {
    System.err.println("Setting isActionPaused to " + isActionPaused);
    this.isActionPaused = isActionPaused;
  }

	public boolean evaluate(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
		for (final TriggerBooleanExpression condition : this.conditions) {
			if (!condition.evaluate(globalScope, triggerScope)) {
				return false;
			}
		}
		return true;
	}

  // private void replayTrigger(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
  //   Trigger trigger = this;
  //   Timer timer = new Timer();
  //   long delay = 3000L;
  //   timer.schedule(new TimerTask() {
  //     @Override
  //     public void run() {
  //       System.err.println("Execute replaying trigger");
  //       trigger.isActionPaused = false;
  //       trigger.execute(globalScope, triggerScope);
  //     }
  //   }, delay);
  // }

	public void execute(final GlobalScope globalScope, final TriggerExecutionScope triggerScope) {
		if (!this.enabled) {
      // it may not actually be safe to reset currentActionIndex if this if a trigger with a sleepAction that is disabled and then re-enabled
      this.currentActionIndex = 0;
			return;
		}
		for (; this.currentActionIndex < this.actions.size(); this.currentActionIndex++) {
      final JassFunction action = this.actions.get(this.currentActionIndex);
			try {
				action.call(Collections.emptyList(), globalScope, triggerScope);
        if (this.isActionPaused) {
          System.err.println("Trigger: Action paused, returning");
          // replayTrigger(globalScope, triggerScope);
          return;
        }
			}
			catch (final Exception e) {
				if ((e.getMessage() != null) && e.getMessage().startsWith("Needs to sleep")) {
					// TODO not good design
					e.printStackTrace();
				}
				else {
					throw new JassException(globalScope, "Exception during Trigger action execute", e);
				}
			}
		}
    this.currentActionIndex = 0;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public void destroy() {

	}

	public void reset() {
		this.actions.clear();
		this.conditions.clear();
		this.evalCount = 0;
		this.execCount = 0;
		this.enabled = true;
		this.waitOnSleeps = true;
	}

	public void setWaitOnSleeps(final boolean waitOnSleeps) {
		this.waitOnSleeps = waitOnSleeps;
	}

	public boolean isWaitOnSleeps() {
		return this.waitOnSleeps;
	}

	@Override
	public int getHandleId() {
		return this.handleId;
	}

}
