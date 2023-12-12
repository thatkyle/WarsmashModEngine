package com.etheller.interpreter.ast.statement;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.LocalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.value.JassValue;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public interface JassStatement {
	// When a value is returned, this indicates a RETURN statement,
	// and will end outer execution
	JassValue execute(GlobalScope globalScope, LocalScope localScope, TriggerExecutionScope triggerScope);
	
	default boolean isSleepStatement(JassStatement statement) {
		
	};

	default JassValue delayedExecute(JassStatement statement, GlobalScope globalScope, 
			LocalScope localScope, TriggerExecutionScope triggerScope, long delay) {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.schedule(() -> {
		    	statement.execute(globalScope, localScope, triggerScope);
		}, delay, TimeUnit.MILLISECONDS);
		executor.shutdown();
		return null;
	}
}
