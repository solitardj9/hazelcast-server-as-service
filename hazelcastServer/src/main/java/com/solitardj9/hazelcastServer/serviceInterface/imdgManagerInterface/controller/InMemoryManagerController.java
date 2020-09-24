package com.solitardj9.hazelcastServer.serviceInterface.imdgManagerInterface.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hazelcast.core.IMap;
import com.solitardj9.hazelcastServer.serviceInterface.imdgManagerInterface.model.ResponseDefualt;
import com.solitardj9.hazelcastServer.serviceInterface.imdgManagerInterface.model.ResponseIMap;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastDistributedObjectNameConflict;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastIMapNotFound;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastServerAlreadyClosed;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastServerAlreadyOpened;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.model.exception.ExceptionHazelcastServerConfigError;
import com.solitardj9.hazelcastServer.systemInterface.imdgInterface.service.InMemoryManager;

@RestController
@RequestMapping(value="/management")
public class InMemoryManagerController {
	
	private static final Logger logger = LoggerFactory.getLogger(InMemoryManagerController.class);
	
	@Autowired
	InMemoryManager inMemoryManager;

	@SuppressWarnings("rawtypes")
	@PutMapping(value="/imdg/start")
	public ResponseEntity start(@RequestBody(required=false) String requestBody) {
		//
		logger.info("[InMemoryManagerController].start is called.");
		
		try {
			inMemoryManager.startServer();
		} catch (ExceptionHazelcastServerAlreadyOpened e) {
			logger.error("[InMemoryManagerController].start : error = " + e);
			return new ResponseEntity<>(new ResponseDefualt(e.getErrCode(), e.getMessage()), e.getHttpStatus());
		} catch (ExceptionHazelcastServerConfigError e) {
			logger.error("[InMemoryManagerController].start : error = " + e);
			return new ResponseEntity<>(new ResponseDefualt(e.getErrCode(), e.getMessage()), e.getHttpStatus());
		}
		
		return new ResponseEntity<>(new ResponseDefualt(HttpStatus.OK.value(), "success"), HttpStatus.OK);
    }
	
	@SuppressWarnings("rawtypes")
	@PutMapping(value="/imdg/stop")
	public ResponseEntity stop(@RequestBody(required=false) String requestBody) {
		//
		logger.info("[InMemoryManagerController].stop is called.");
		
		try {
			inMemoryManager.stopServer();
		} catch (ExceptionHazelcastServerAlreadyClosed e) {
			logger.error("[InMemoryManagerController].stop : error = " + e);
			return new ResponseEntity<>(new ResponseDefualt(e.getErrCode(), e.getMessage()), e.getHttpStatus());
		}
		
		return new ResponseEntity<>(new ResponseDefualt(HttpStatus.OK.value(), "success"), HttpStatus.OK);
    }
	
	@SuppressWarnings("rawtypes")
	@PostMapping(value="/imdg/map/{map}")
	public ResponseEntity createMap(@PathVariable("map") String map) {
		//
		logger.info("[TimelineSyncController].createMap is called.");
		
		IMap<Object, Object> mapObj = null;
		try {
			mapObj = inMemoryManager.createMap(map);
		} catch (ExceptionHazelcastServerAlreadyClosed e) {
			logger.error("[InMemoryManagerController].createMap : error = " + e);
			return new ResponseEntity<>(new ResponseDefualt(e.getErrCode(), e.getMessage()), e.getHttpStatus());
		} catch (ExceptionHazelcastDistributedObjectNameConflict e) {
			logger.error("[InMemoryManagerController].createMap : error = " + e);
			return new ResponseEntity<>(new ResponseDefualt(e.getErrCode(), e.getMessage()), e.getHttpStatus());
		}
		
		return new ResponseEntity<>(new ResponseIMap(HttpStatus.OK.value(), "success", mapObj), HttpStatus.OK);
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping(value="/imdg/map/{map}")
	public ResponseEntity getMap(@PathVariable("map") String map) {
		//
		logger.info("[TimelineSyncController].getMap is called.");
		
		IMap<Object, Object> mapObj = null;
		try {
			mapObj = inMemoryManager.getMap(map);
		} catch (ExceptionHazelcastServerAlreadyClosed e) {
			logger.error("[InMemoryManagerController].getMap : error = " + e);
			return new ResponseEntity<>(new ResponseDefualt(e.getErrCode(), e.getMessage()), e.getHttpStatus());
		} catch (ExceptionHazelcastIMapNotFound e) {
			logger.error("[InMemoryManagerController].getMap : error = " + e);
			return new ResponseEntity<>(new ResponseDefualt(e.getErrCode(), e.getMessage()), e.getHttpStatus());
		}
		
		if (mapObj == null) {
			ExceptionHazelcastIMapNotFound e = new ExceptionHazelcastIMapNotFound();
			logger.error("[InMemoryManagerController].getMap : error = " + e);
			return new ResponseEntity<>(new ResponseDefualt(e.getErrCode(), e.getMessage()), e.getHttpStatus());
		}
		
		return new ResponseEntity<>(new ResponseIMap(HttpStatus.OK.value(), "success", mapObj), HttpStatus.OK);
	}
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping(value="/imdg/map/{map}")
	public ResponseEntity clearMap(@PathVariable("map") String map) {
		//
		logger.info("[TimelineSyncController].clearMap is called.");
		
		try {
			inMemoryManager.clearMap(map);
		} catch (ExceptionHazelcastServerAlreadyClosed e) {
			logger.error("[InMemoryManagerController].getMap : error = " + e);
			return new ResponseEntity<>(new ResponseDefualt(e.getErrCode(), e.getMessage()), e.getHttpStatus());
		} catch (ExceptionHazelcastIMapNotFound e) {
			logger.error("[InMemoryManagerController].getMap : error = " + e);
			return new ResponseEntity<>(new ResponseDefualt(e.getErrCode(), e.getMessage()), e.getHttpStatus());
		}
		
		return new ResponseEntity<>(new ResponseDefualt(HttpStatus.OK.value(), "success"), HttpStatus.OK);
	}
}