package com.example.androidproject.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.androidproject.Constants;
import com.example.androidproject.R;
import com.example.androidproject.participant.Participant;

public class UpdateSchedule extends FragmentActivity implements OnClickListener {

	private EditText updateTitleEditText;
	private EditText updateContentEditText;
	private TextView updateDateTextView;
	private TextView updateTimeTextView;
	
	private int scheduleId ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_schedule);
		
		updateTitleEditText = (EditText) findViewById(R.id.updateTitleEditText);
		updateContentEditText = (EditText) findViewById(R.id.updateContentEditText);
		updateDateTextView = (TextView) findViewById(R.id.updateDateTextView);
		updateTimeTextView = (TextView) findViewById(R.id.updateTimeTextView);
		
		findViewById(R.id.updatedateChangeButton).setOnClickListener(this);
		findViewById(R.id.updatetimeChangeButton).setOnClickListener(this);
		findViewById(R.id.updateButton).setOnClickListener(this);
		
		Intent intent = getIntent();
		
		scheduleId = intent.getExtras().getInt("scheduleId");
		
		new GetScheduleTask().execute(scheduleId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_schedule, menu);
		return true;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.updatedateChangeButton:
		
			new DatePickerFragment().show(getSupportFragmentManager(), "datePicker");
		
		    break;
	
		case R.id.updatetimeChangeButton:
			
			new TimePickerFragment().show(getSupportFragmentManager(), "timePicker");
			
			break;
			
		case R.id.updateButton:
			
			new UpdateScheduleTask().execute(scheduleId);
			
			
			break;

		default:
			break;
	}
	}

	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			// TODO Auto-generated method stub
			TextView dateTextView = (TextView) getActivity().findViewById(R.id.updateDateTextView);
			
			dateTextView.setText(year + "-" + (month + 1) + "-" + day);
		}
	}

	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			TextView timeTextView = (TextView) getActivity().findViewById(R.id.updateTimeTextView);
			
			timeTextView.setText(pad(hourOfDay) + ":" + pad(minute) + ":00");
		}
		
		private String pad(int c) {
			// TODO Auto-generated method stub
			return (c >= 10) ? String.valueOf(c) : "0" + String.valueOf(c);
		}
	}
	
	public class GetScheduleTask extends AsyncTask<Integer, Void, Schedule> {

		@Override
		protected Schedule doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int scheduleId = params[0];
			
			String url = Constants.URL + "/getSchedule?scheduleId=" + scheduleId;

			RestTemplate restTemplate = new RestTemplate();

			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			Schedule schedule = restTemplate.getForObject(url, Schedule.class);
			
			return schedule;
		}

		@Override
		protected void onPostExecute(Schedule result) {
			// TODO Auto-generated method stub
			updateTitleEditText.setText(result.getTitle());
			updateContentEditText.setText(result.getContent());
			
			updateDateTextView.setText(result.getFormattedDate("yyyy-MM-dd"));
			updateTimeTextView.setText(result.getFormattedDate("HH:mm:ss"));
			
			super.onPostExecute(result);
		}
	}

	public class UpdateScheduleTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int scheduleId = params[0];

			String url = Constants.URL + "/updateSchedule";

			RestTemplate restTemplate = new RestTemplate();
			
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

			String title = updateTitleEditText.getText().toString();
			String content = updateContentEditText.getText().toString();
			String date = updateDateTextView.getText() + " " + updateTimeTextView.getText();
			
			Schedule schedule = new Schedule();
			
			schedule.setScheduleId(scheduleId);
			schedule.setTitle(title);
			schedule.setContent(content);
			schedule.setFormattedDate("yyyy-MM-dd HH:mm:ss", date);
			
			ArrayList<Participant> items = new ArrayList<Participant>();
			
			schedule.setParticipants(items);
			
			HttpHeaders requestHeaders = new HttpHeaders();
			
			requestHeaders.setContentType(MediaType.APPLICATION_JSON);
			requestHeaders.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
			
			HttpEntity<Schedule> requestEntity = new HttpEntity<Schedule>(schedule, requestHeaders);
			
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			
			String result = responseEntity.getBody();

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (result.equals("success")) {

				Toast.makeText(UpdateSchedule.this, R.string.schedule_update_success, Toast.LENGTH_LONG).show();
				
				finish();
			}
		}
	}

}
