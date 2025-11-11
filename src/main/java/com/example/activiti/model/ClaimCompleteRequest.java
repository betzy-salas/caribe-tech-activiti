package com.example.activiti.model;

import java.util.Map;

public record ClaimCompleteRequest(String userId, String candidateGroup, Map<String, Object> variables) {}
