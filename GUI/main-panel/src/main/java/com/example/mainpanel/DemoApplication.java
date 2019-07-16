package com.example.mainpanel;

import java.util.List;

import com.example.mainpanel.osmowsis_source.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DemoApplication {
	// Spring API design
	// good example https://howtodoinjava.com/spring-boot2/rest-api-example/
	// https://www.tutorialspoint.com/spring_boot/spring_boot_building_restful_web_services.htm
	// https://github.com/spring-guides/tut-react-and-spring-data-rest/tree/master/basic
	// https://spring.io/guides/tutorials/react-and-spring-data-rest/


	// open browser: 0.read file 1.initialize map 2.report 3.Mowers States
	@PostMapping(value = "/simulation")
	public String startApplication(@RequestBody FilePath filePath) throws JsonProcessingException {
		// 0. read file -- the filePath need to be added to SimulationRun.java
		//SimulationRun monitorSim = new SimulationRun(filePath);
		SimulationRun monitorSim = new SimulationRun();

		// 1.initialize map
		InfoMap lawnMap = monitorSim.getLawnMap();

		// 2.report
		Report report = monitorSim.generateReport();

		// 3.mowers states
		List<MowerStates> mowerStates = monitorSim.getMowerStates();

		// object --> JSON
		// Spring uses Jackson ObjectMapper class to do Json Serialization and Deserialization.
		ObjectMapper objectMapper = new ObjectMapper();
		String mapAsString = objectMapper.writeValueAsString(lawnMap);
		String reportAsString = objectMapper.writeValueAsString(report);
		String stateAsString = objectMapper.writeValueAsString(mowerStates);
		return "{\"map\":" + mapAsString + ", \"report\":" + reportAsString + ", \"mowerStates\":" + stateAsString + "}";
	}

	// stop: 1. terminate application 2. map 3. report 4. Mowers States
	@DeleteMapping(value = "/stop")
	public String stopRun() throws JsonProcessingException {
		SimulationRun monitorSim = new SimulationRun();

		// 1. terminate application
		monitorSim.stopRun();

		// 2. map
		InfoMap lawnMap = monitorSim.getLawnMap();

		// 3.report
		Report report = monitorSim.generateReport();

		// 4.mowers states
		List<MowerStates> mowerStates = monitorSim.getMowerStates();

		// object --> JSON
		ObjectMapper objectMapper = new ObjectMapper();
		String mapAsString = objectMapper.writeValueAsString(lawnMap);
		String reportAsString = objectMapper.writeValueAsString(report);
		String stateAsString = objectMapper.writeValueAsString(mowerStates);
		return "{\"map\":" + mapAsString + ", \"report\":" + reportAsString + ", \"mowerStates\":" + stateAsString + "}";
	}

	// next: 1. move mower to next 2. map 3. report 4. Mowers States
	@PatchMapping(value = "/next")
	public String nextRun() throws JsonProcessingException {
		SimulationRun monitorSim = new SimulationRun();
		// 1. move mower to next - return current move Mower ID
		int mowerID = monitorSim.moveNext();

		// 2. map
		InfoMap lawnMap = monitorSim.getLawnMap();

		// 3.report
		Report report = monitorSim.generateReport();

		// 4.mowers states
		List<MowerStates> mowerStates = monitorSim.getMowerStates();

		// object --> JSON
		ObjectMapper objectMapper = new ObjectMapper();
		String mapAsString = objectMapper.writeValueAsString(lawnMap);
		String reportAsString = objectMapper.writeValueAsString(report);
		String stateAsString = objectMapper.writeValueAsString(mowerStates);
		return "{\"map\":" + mapAsString + ", \"report\":" + reportAsString + ", \"mowerStates\":" + stateAsString + "}";
	}

	// fast-forward: 1. move mower fast-forward 2. update map 3. report 4.Mowers States
	@PatchMapping(value = "/fast-forward")
	public String fastForwardRun() throws JsonProcessingException {
		SimulationRun monitorSim = new SimulationRun();
		// 1. move mower fast-forward
		monitorSim.act();

		// 2. update map
		InfoMap lawnMap = monitorSim.getLawnMap();

		// 3. report
		Report report = monitorSim.generateReport();

		// 4.Mowers States
		List<MowerStates> mowerStates = monitorSim.getMowerStates();

		// object --> JSON
		ObjectMapper objectMapper = new ObjectMapper();
		String mapAsString = objectMapper.writeValueAsString(lawnMap);
		String reportAsString = objectMapper.writeValueAsString(report);
		String stateAsString = objectMapper.writeValueAsString(mowerStates);
		return "{\"map\":" + mapAsString + ", \"report\":" + reportAsString + ", \"mowerStates\":" + stateAsString + "}";
	}
}
