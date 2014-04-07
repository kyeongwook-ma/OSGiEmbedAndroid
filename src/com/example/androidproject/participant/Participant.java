package com.example.androidproject.participant;

import com.example.androidproject.user.User;


public class Participant {

	private int scheduleId;
	private User user;
	private int stateCode;
	
	public int getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getStateCode() {
		return stateCode;
	}
	public void setStateCode(int stateCode) {
		this.stateCode = stateCode;
	}
	@Override
	public String toString() {
		return "Participant [scheduleId=" + scheduleId + ", user=" + user
				+ ", stateCode=" + stateCode + "]";
	}
}
