package com.mintcode.area_patient.area_task;

//import com.fasterxml.jackson.annotation.JsonTypeName;
import com.mintcode.base.BasePOJO;

//@JsonTypeName("get-reward")
public class TaskRewardPOJO extends BasePOJO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int coin;

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}


	/**
	 *   以下只是为了统一糖币奖励需要的额外参数,并不是接口返回
	 *
	 */

	private String taskDescOnce;
	private String taskDescDaily;
	private String taskNameOnce;
	private String taskNameDaily;

	public String getTaskDescOnce() {
		return taskDescOnce;
	}

	public void setTaskDescOnce(String taskDescOnce) {
		this.taskDescOnce = taskDescOnce;
	}

	public String getTaskDescDaily() {
		return taskDescDaily;
	}

	public void setTaskDescDaily(String taskDescDaily) {
		this.taskDescDaily = taskDescDaily;
	}

	public String getTaskNameOnce() {
		return taskNameOnce;
	}

	public void setTaskNameOnce(String taskNameOnce) {
		this.taskNameOnce = taskNameOnce;
	}

	public String getTaskNameDaily() {
		return taskNameDaily;
	}

	public void setTaskNameDaily(String taskNameDaily) {
		this.taskNameDaily = taskNameDaily;
	}


}
