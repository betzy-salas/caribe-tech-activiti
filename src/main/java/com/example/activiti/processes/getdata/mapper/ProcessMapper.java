package com.example.activiti.processes.getdata.mapper;

import com.example.activiti.model.ProcessRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

import java.util.HashMap;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface ProcessMapper {

    default Map<String, Object> mapToProcessVariables(ProcessRequest request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("DOCUMENT_TYPE", request.getDocumentType());
        variables.put("DOCUMENT_NUMBER", request.getDocumentNumber());
        return variables;
    }
}

