package com.example.activiti.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRequest {
    private String documentType;
    private String documentNumber;
    private String accountType;
    private String accountNumber;
}

