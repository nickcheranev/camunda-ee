package com.example.workflow.delegate;

import com.example.workflow.utils.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.delegate.VariableScope;

import java.util.Optional;

/**
 * @author Kornilov Oleg
 */
@Slf4j
public abstract class AbstractJavaDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String activityName = Optional.ofNullable(execution.getCurrentActivityName()).orElse("No name").replaceAll("\n", " ");
        try {
            log.debug("Start execute delegate '{}'", activityName);
            executeDelegate(execution);
            log.debug("End execute delegate '{}'", activityName);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    protected abstract void executeDelegate(DelegateExecution execution);

    protected <T> T getVariable(VariableScope scope, String name, Class<T> type) {
        return ConvertUtils.convert(scope.getVariableLocal(name), type);
    }

    protected <T> T getProcessVariable(VariableScope scope, String name, Class<T> type) {
        return ConvertUtils.convert(scope.getVariable(name), type);
    }

    protected void setVariable(VariableScope scope, String name, Object value) {
        if (log.isDebugEnabled()) {
            log.debug("Set local variable '{}' = {}", name, value);
        }
        scope.setVariableLocal(name, value);
    }
}
