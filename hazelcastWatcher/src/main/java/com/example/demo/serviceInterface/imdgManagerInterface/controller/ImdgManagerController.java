package com.example.demo.serviceInterface.imdgManagerInterface.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.systemInterface.imdgInterface.service.InMemoryWatcher;

@RestController
@RequestMapping(value="/management")
public class ImdgManagerController {
	
	private static final Logger logger = LoggerFactory.getLogger(ImdgManagerController.class);
	
	@Autowired
	InMemoryWatcher inMemoryWatcher;

	@SuppressWarnings("rawtypes")
	@PutMapping(value="/imdg/start")
	public ResponseEntity start(@RequestBody(required=false) String requestBody) {
		//
		logger.info("[ImdgManagerController].start is called.");
		
		inMemoryWatcher.start();
		
		return new ResponseEntity<>(HttpStatus.OK);
    }
	
	@SuppressWarnings("rawtypes")
	@PutMapping(value="/imdg/stop")
	public ResponseEntity stop(@RequestBody(required=false) String requestBody) {
		//
		logger.info("[ImdgManagerController].stop is called.");
		
		inMemoryWatcher.stop();
		
		return new ResponseEntity<>(HttpStatus.OK);
    }
	
	@SuppressWarnings("rawtypes")
	@GetMapping(value="/imdg/status")
	public ResponseEntity status(@RequestBody(required=false) String requestBody) {
		//
		logger.info("[ImdgManagerController].status is called.");
		
		return new ResponseEntity<>(inMemoryWatcher.isConnected(), HttpStatus.OK);
    }
}