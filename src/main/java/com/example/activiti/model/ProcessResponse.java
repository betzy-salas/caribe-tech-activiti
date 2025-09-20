package com.example.activiti.model;

import com.example.activiti.model.response.SourceResponse;
import com.example.activiti.model.response.TargetResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessResponse {
    private SourceResponse sourceResponse;
    private TargetResponse targetResponse;
}

