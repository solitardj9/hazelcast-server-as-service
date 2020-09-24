package com.example.demo.systemInterface.imdgInterface.model;

public enum InMemoryStatus {
    //
    COONECTED(true),
    DISCOONECTED(false)
    ;
    
    private Boolean status;
    
    private InMemoryStatus(Boolean status) {
        this.status = status;
    }
    
    public Boolean getStatus() {
        return status;
    }
    
    @Override
    public String toString() {
        return status.toString();
    }
}