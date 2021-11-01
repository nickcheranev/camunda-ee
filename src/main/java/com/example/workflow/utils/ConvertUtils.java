package com.example.workflow.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;

/**
 * @author Kornilov Oleg
 */
@UtilityClass
public class ConvertUtils {

    private final ConvertUtilsBean CONVERT_UTILS_BEAN = new ConvertUtilsBean();

    public <T> T convert(Object value, Class<T> type) {
        return convert(value, type, null);
    }

    public <T> T convert(Object value, Class<T> type, Object defaultValue) {
        Converter converter = CONVERT_UTILS_BEAN.lookup(type);
        if (value != null && !String.valueOf(value).trim().equals("")) {
            return converter.convert(type, value);
        } else {
            return defaultValue != null ? converter.convert(type, defaultValue) : null;
        }
    }
}
