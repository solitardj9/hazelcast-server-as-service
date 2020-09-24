package com.example.demo.systemInterface.imdgInterface.model;

import com.example.demo.systemInterface.imdgInterface.service.InMemoryEventListener;

import lombok.Data;

@Data
public class InMemoryInstane {
	//
	private String name;               // Data 객체 이름
	
	private Integer backupCount;		// sync backup
	
	private Boolean readBackupData;
    
    private String lockName;
    
    private InMemoryEventListener eventListener;
}