package com.example.androidproject.adaptation.bundle.gps;

import android.location.Location;

import com.example.androidproject.adaptation.bundle.Bundle;

public class GpsBundle implements Bundle {

	@Override
	public Object analyzeContext(Object o1, Object o2) {
		// TODO Auto-generated method stub
		double distance = 0.0;
		
		try {
		
			Location src = (Location) o1;
			Location dest = (Location) o2;
			
			distance = src.distanceTo(dest);
		}
		catch (NullPointerException e) {
			// TODO: handle exception
			e.printStackTrace();
			
			throw new IllegalArgumentException(e);
		}
		
		return distance;
	}
}
