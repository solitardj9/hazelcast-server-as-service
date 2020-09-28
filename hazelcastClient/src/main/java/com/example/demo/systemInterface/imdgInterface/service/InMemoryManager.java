package com.example.demo.systemInterface.imdgInterface.service;

import com.hazelcast.core.IMap;

public interface InMemoryManager {
    
    public Boolean isConnected();
    
    public IMap<Object, Object> addMap(String mapName);
    
    public IMap<Object, Object> getMap(String mapName);
}