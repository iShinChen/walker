package com.springmvc.walker.entity;

public class ProcessBean {
	
	private String taskId;
	private Process process;
	private ProcessState state;
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public Process getProcess() {
		return process;
	}
	public void setProcess(Process process) {
		this.process = process;
	}
	public ProcessState getState() {
		return state;
	}
	public void setState(ProcessState state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "ProcessBean [taskId=" + taskId + ", process=" + process
				+ ", state=" + state + "]";
	}
	
}
