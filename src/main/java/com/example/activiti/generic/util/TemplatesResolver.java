package com.example.activiti.generic.util;

import java.util.Map;

public class TemplatesResolver {

    public static String resolve(String template, Map<String, Object> variables) {
        if (template == null) return null;

        String resolved = template;

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map<?, ?> nestedMap) {
                for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
                    String nestedPlaceholder = "${" + key + "." + nestedEntry.getKey() + "}";
                    Object nestedValue = nestedEntry.getValue();
                    resolved = resolved.replace(
                            nestedPlaceholder,
                            nestedValue != null ? nestedValue.toString() : ""
                    );
                }
            }

            String placeholder = "${" + key + "}";
            //System.out.println(placeholder);
            //System.out.println(value);
            resolved = resolved.replace(
                    placeholder,
                    value != null ? value.toString() : ""
            );
            //System.out.println(resolved);
        }

        return resolved;
    }
}


