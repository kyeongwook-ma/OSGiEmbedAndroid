package com.example.androidproject.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.androidproject.schedule.Schedule;

public class MMRSDBHelper extends SQLiteOpenHelper {

	public MMRSDBHelper(Context context) {
		super(context, "MMRSDB.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE schedule (	 schedule_id 		INTEGER PRIMARY KEY," +
											"title 				TEXT NOT NULL," +
											"content 			TEXT NOT NULL," +
											"main_address 		TEXT NOT NULL," +
											"detail_address 	TEXT NOT NULL," +
											"longitude 			REAL NOT NULL," +
											"latitude 			REAL NOT NULL," +
											"date 				TEXT NOT NULL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS schedule;");
		onCreate(db);
	}
	
	public void deleteAllSchedule() {
		
		SQLiteDatabase db = getWritableDatabase();
		
		db.execSQL("DELETE FROM schedule;");
		
		close();
	}
	
	public void insertSchedule(Schedule schedule) {
		
		SQLiteDatabase db = getWritableDatabase();
		
		db.execSQL("INSERT INTO schedule VALUES ('" + schedule.getScheduleId() + "', " +
												"'" + schedule.getTitle() + "', " +
												"'" + schedule.getContent() + "', " +
												"'" + schedule.getMainAddress() + "', " +
												"'" + schedule.getDetailAddress() + "', " +
												"'" + schedule.getLongitude() + "', " +
												"'" + schedule.getLatitude() + "', " +
												"'" + schedule.getFormattedDate("yyyy-MM-dd HH:mm:ss") + "');");
		
		close();
	}

	public List<Schedule> selectSchedules() {
		// TODO Auto-generated method stub
		List<Schedule> schedules = new ArrayList<Schedule>();
		
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM schedule;", null);
		
		while (cursor.moveToNext()) {
			
			Schedule schedule = new Schedule();
			
			schedule.setScheduleId(cursor.getInt(0));
			schedule.setTitle(cursor.getString(1));
			schedule.setContent(cursor.getString(2));
			schedule.setMainAddress(cursor.getString(3));
			schedule.setDetailAddress(cursor.getString(4));
			schedule.setLongitude(cursor.getDouble(5));
			schedule.setLatitude(cursor.getDouble(6));
			schedule.setFormattedDate("yyyy-MM-dd HH:mm:ss", cursor.getString(7));
			
			schedules.add(schedule);
		}
		
		return schedules;
	}
}
