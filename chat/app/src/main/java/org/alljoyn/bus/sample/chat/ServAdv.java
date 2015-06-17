// Author : Rohan Parikh
// CPS Lab, Rutgers University

package org.alljoyn.bus.sample.chat;

import java.io.Serializable;
import java.util.ArrayList;

public class ServAdv implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String varBatteryStatus;	// Variable to store battery status
	private Float varCPUUsage;			// Variable to store the current CPU usage
	private Long varMemoryUsage;		// Variable to store the current memory usage
	private Long varCurrentVoltage;		// Variable to store the current voltage of the battery
	private ArrayList<String> dictionaries = new ArrayList<String>();	// List of dictionaries to be compared present at the service provider
	private String modelName;
	
	public void setVarBatteryStatus(String bs){
		varBatteryStatus = bs;
	}
	
	public void setVarCPUUsage(Float cpuUsage){
		varCPUUsage = cpuUsage;
	}
	
	public void setVarMemoryUsage(Long memUsage){
		varMemoryUsage= memUsage;
	}
	
	public void setVarCurrentVoltage(Long currentVoltage){
		varCurrentVoltage = currentVoltage;
	}
	
	public void setModelName(String mName){
		modelName = mName;
	}
	
	public String getVarBatteryStatus(){
		return varBatteryStatus;
	}
	
	public Float getVarCPUUsage(){
		return varCPUUsage;
	}
	
	public Long getVarMemoryUsage(){
		return varMemoryUsage;
	}
	
	public Long getVarCurrentVoltage(){
		return varCurrentVoltage;
	}
	
	public String getModelName(){
		return modelName;
	}
	
	public void setListOfDictionaries(ArrayList<String> d){
		dictionaries = d;
	}
	
	public ArrayList<String> getListOfDictionaries(){
		return dictionaries;
	}
	
	
}
