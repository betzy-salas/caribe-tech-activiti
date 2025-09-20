package com.example.activiti.generic.factory;

import com.example.activiti.generic.config.ExternalServicesConfig;
import com.example.activiti.generic.mapping.JsonPathResponseMapper;
import com.example.activiti.generic.mapping.ResponseMapper;

public class MapperFactory {
    public static ResponseMapper getMapper(ExternalServicesConfig.ServicesDefinition config) {
        if (config.getResponseMapping() != null) {
            return new JsonPathResponseMapper(config.getResponseMapping());
        }
        return null;
    }
}

