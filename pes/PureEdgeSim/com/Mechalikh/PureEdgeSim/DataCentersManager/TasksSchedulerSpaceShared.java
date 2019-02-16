package com.Mechalikh.PureEdgeSim.DataCentersManager;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;

import com.Mechalikh.PureEdgeSim.ScenarioManager.SimulationParameters;
import com.Mechalikh.PureEdgeSim.TasksGenerator.Task;
import com.Mechalikh.PureEdgeSim.TasksGenerator.Task.Status;

public class TasksSchedulerSpaceShared extends CloudletSchedulerSpaceShared {


	private static final long serialVersionUID = 1L;

	public TasksSchedulerSpaceShared() {
	}
 
	@Override
	public void cloudletFinish(final CloudletExecution ce) {
		Task task = ((Task) ce.getCloudlet());
		EdgeDataCenter edc = (EdgeDataCenter) task.getVm().getHost().getDatacenter();
		// task failed long delay
		if ((task.getSimulation().clock() - task.getTime()) + task.getUploadLanNetworkUsage() > task.getMaxLatency()) {
			task.setFailureReason(Task.Status.FAILED_DUE_TO_LATENCY);
			task.setStatus(Cloudlet.Status.FAILED);		  
		} 
		else
		if (edc.isDead() || task.getEdgeDevice().isDead()) { 
			//the destination (where the task is executed) 
			//or the origin of the task(the device which offloaded the task)
           // if one of them is dead
			task.setFailureReason(Status.FAILED_BECAUSE_DEVICE_DEAD);
			ce.setCloudletStatus(Cloudlet.Status.FAILED);
		} 	 
		else
		// a simple representation of task failure due to device mobility, if there is
		// no vm migration
		// if vm location doesn't equal the edge device location (that generated this
		// task)
		if (edc.getType()!=SimulationParameters.TYPES.CLOUD && !edc.getLocation().equals(task.getEdgeDevice().getLocation())) {
			task.setFailureReason(Task.Status.FAILED_DUE_TO_DEVICE_MOBILITY);
			ce.setCloudletStatus(Cloudlet.Status.FAILED);
		} else {
		  ce.setCloudletStatus(Cloudlet.Status.SUCCESS);
		}
		ce.finalizeCloudlet();
		addCloudletToFinishedList(ce);
	}




}
