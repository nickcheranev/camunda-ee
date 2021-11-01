package com.example.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.plugin.variable.SpinValues;
import org.camunda.spin.plugin.variable.value.JsonValue;
import org.springframework.stereotype.Component;

@Component
public class ServiceTaskAdapter implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String json = "{\"newsid\" : 1, \"category\" : \"category\" }";
        JsonValue jsonValue = SpinValues.jsonValue(json).create();
        execution.setVariable("news", jsonValue);

        News news = new News();
        news.setNewsid(1);
        news.setValue("value");
        execution.setVariable("news1", news);
    }
}
