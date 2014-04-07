package com.example.androidproject.adaptation.bundle.time;

import java.util.Date;

import com.example.androidproject.adaptation.bundle.Bundle;

public class TimeBundle implements Bundle {

	@Override
	public Object analyzeContext(Object o1, Object o2) {
		// TODO Auto-generated method stub
		Date now = (Date) o1;
		Date date = (Date) o2;
		
		long diff = date.getTime() - now.getTime();
	    
		
		return diff / (60 * 1000);
	}
}
