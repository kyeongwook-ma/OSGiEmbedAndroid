package com.example.androidproject.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.androidproject.participant.Participant;


public class Schedule {

	private int scheduleId;
	private String title;
	private String content;
	private String mainAddress;
	private String detailAddress;
	private double longitude;
	private double latitude;
	private Date date;
	private int visible;
	private List<Participant> participants;
	
	public int getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMainAddress() {
		return mainAddress;
	}
	public void setMainAddress(String mainAddress) {
		this.mainAddress = mainAddress;
	}
	public String getDetailAddress() {
		return detailAddress;
	}
	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public Date getDate() {
		return date;
	}
	public String getFormattedDate(String pattern) {
		
		SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.KOREA);
		
		return format.format(date);
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void setFormattedDate(String pattern, String date) {
		
		SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.KOREA);
		
		try {
			
			this.date = format.parse(date);
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			this.date = Calendar.getInstance(Locale.KOREA).getTime();
			
			e.printStackTrace();
		}
	}
	public int getVisible() {
		return visible;
	}
	public void setVisible(int visible) {
		this.visible = visible;
	}
	public List<Participant> getParticipants() {
		return participants;
	}
	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}
	
	@Override
	public String toString() {
		return "Schedule [scheduleId=" + scheduleId + ", title=" + title
				+ ", content=" + content + ", mainAddress=" + mainAddress
				+ ", detailAddress=" + detailAddress + ", longitude="
				+ longitude + ", latitude=" + latitude + ", date=" + date
				+ ", visible=" + visible + ", participants=" + participants
				+ "]";
	}
}
