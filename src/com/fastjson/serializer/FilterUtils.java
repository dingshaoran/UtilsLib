package com.fastjson.serializer;

import java.lang.reflect.Type;
import java.util.List;

import com.fastjson.parser.DefaultJSONParser;
import com.fastjson.parser.deserializer.ExtraProcessor;
import com.fastjson.parser.deserializer.ExtraTypeProvider;

public class FilterUtils {

    public static Type getExtratype(DefaultJSONParser parser, Object object, String key) {
        List<ExtraTypeProvider> extraTypeProviders = parser.getExtraTypeProvidersDirect();
        if (extraTypeProviders == null) {
            return null;
        }

        Type type = null;
        for (ExtraTypeProvider extraProvider : extraTypeProviders) {
            type = extraProvider.getExtraType(object, key);
        }
        return type;
    }

    public static void processExtra(DefaultJSONParser parser, Object object, String key, Object value) {
        List<ExtraProcessor> extraProcessors = parser.getExtraProcessorsDirect();
        if (extraProcessors == null) {
            return;
        }
        for (ExtraProcessor process : extraProcessors) {
            process.processExtra(object, key, value);
        }
    }

    public static char writeBefore(JSONSerializer serializer, Object object, char seperator) {
        List<BeforeFilter> beforeFilters = serializer.getBeforeFiltersDirect();
        if (beforeFilters != null) {
            for (BeforeFilter beforeFilter : beforeFilters) {
                seperator = beforeFilter.writeBefore(serializer, object, seperator);
            }
        }
        return seperator;
    }

    public static char writeAfter(JSONSerializer serializer, Object object, char seperator) {
        List<AfterFilter> afterFilters = serializer.getAfterFiltersDirect();
        if (afterFilters != null) {
            for (AfterFilter afterFilter : afterFilters) {
                seperator = afterFilter.writeAfter(serializer, object, seperator);
            }
        }
        return seperator;
    }

    public static Object processValue(JSONSerializer serializer, Object object, String key, Object propertyValue) {
        List<ValueFilter> valueFilters = serializer.getValueFiltersDirect();
        if (valueFilters != null) {
            for (ValueFilter valueFilter : valueFilters) {
                propertyValue = valueFilter.process(object, key, propertyValue);
            }
        }

        return propertyValue;
    }

    public static String processKey(JSONSerializer serializer, Object object, String key, Object propertyValue) {
        List<NameFilter> nameFilters = serializer.getNameFiltersDirect();
        if (nameFilters != null) {
            for (NameFilter nameFilter : nameFilters) {
                key = nameFilter.process(object, key, propertyValue);
            }
        }

        return key;
    }

    public static boolean applyName(JSONSerializer serializer, Object object, String key) {
        List<PropertyPreFilter> filters = serializer.getPropertyPreFiltersDirect();

        if (filters == null) {
            return true;
        }

        for (PropertyPreFilter filter : filters) {
            if (!filter.apply(serializer, object, key)) {
                return false;
            }
        }

        return true;
    }

    public static boolean apply(JSONSerializer serializer, Object object, String key, Object propertyValue) {
        List<PropertyFilter> propertyFilters = serializer.getPropertyFiltersDirect();

        if (propertyFilters == null) {
            return true;
        }

        for (PropertyFilter propertyFilter : propertyFilters) {
            if (!propertyFilter.apply(object, key, propertyValue)) {
                return false;
            }
        }

        return true;
    }
}
