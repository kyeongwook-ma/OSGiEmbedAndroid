package com.example.androidproject.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
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
import com.example.androidproject.navermap.NaverMapActivity;
import com.example.androidproject.participant.Participant;
import com.example.androidproject.user.User;

public class ScheduleRegisterActivity extends FragmentActivity implements OnClickListener {

	private final int NAVER_MAP_ACTIVITY = 2;
	
	private EditText titleEditText;
	private EditText contentEditText;
	private TextView dateTextView;
	private TextView timeTextView;
	private TextView addressTextView;
	private TextView participantTextView;
	
	private List<Participant> selectedItems;
	
	private double longitude;
	private double latitude;
	
	private String mainAddress;
	private String detailAddress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_register);

		titleEditText = (EditText) findViewById(R.id.scheduleRegisterTitleEditText);
		contentEditText = (EditText) findViewById(R.id.scheduleRegisterContentEditText);
		dateTextView = (TextView) findViewById(R.id.scheduleRegisterDateTextView);
		timeTextView = (TextView) findViewById(R.id.scheduleRegisterTimeTextView);
		addressTextView = (TextView) findViewById(R.id.addressTextView);
		participantTextView = (TextView) findViewById(R.id.selectedParticipantTextView);
		
		findViewById(R.id.dateChangeButton).setOnClickListener(this);
		findViewById(R.id.timeChangeButton).setOnClickListener(this);
		findViewById(R.id.locationChoiceButton).setOnClickListener(this);
		findViewById(R.id.participantChoiceButton).setOnClickListener(this);
		findViewById(R.id.scheduleRegisterButton).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.schedule_register, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.dateChangeButton:

			new DatePickerFragment().show(getSupportFragmentManager(), "datePicker");

			break;

		case R.id.timeChangeButton:

			new TimePickerFragment().show(getSupportFragmentManager(), "timePicker");

			break;

		case R.id.participantChoiceButton:

			new GetUsersRequestTask().execute();

			break;

		case R.id.locationChoiceButton:

			Intent intent = new Intent(this, NaverMapActivity.class);

			startActivityForResult(intent, NAVER_MAP_ACTIVITY);

			break;

		case R.id.scheduleRegisterButton:

			new AddScheduleRequestTask().execute();

			break;

		default:
			break;
		}
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == NAVER_MAP_ACTIVITY) {
			
			if (resultCode == RESULT_OK) {
				
				Bundle bundle = data.getExtras();
				
				longitude = bundle.getDouble("longitude");
				latitude = bundle.getDouble("latitude");
				mainAddress = bundle.getString("mainAddress");
				detailAddress = bundle.getString("detailAddress");
				
				System.out.println("longitude" + longitude);
				System.out.println("latitude" + latitude);
				System.out.println("mainAddress" + mainAddress);
				System.out.println("detailAddress" + detailAddress);
				
				addressTextView.setText(mainAddress + ", " + detailAddress);
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	public EditText getTitleEditText() {
		return titleEditText;
	}

	public EditText getContentEditText() {
		return contentEditText;
	}

	public TextView getDateTextView() {
		return dateTextView;
	}

	public TextView getTimeTextView() {
		return timeTextView;
	}

	public TextView getParticipantTextView() {
		return participantTextView;
	}

	public List<Participant> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<Participant> selectedItems) {
		this.selectedItems = selectedItems;
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
			TextView dateTextView = ((ScheduleRegisterActivity) getActivity()).getDateTextView();

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
			TextView timeTextView = ((ScheduleRegisterActivity) getActivity()).getTimeTextView();

			timeTextView.setText(pad(hourOfDay) + ":" + pad(minute) + ":00");
		}

		private String pad(int c) {
			// TODO Auto-generated method stub
			return (c >= 10) ? String.valueOf(c) : "0" + String.valueOf(c);
		}
	}

	public static class ParticipantPickerFragment extends DialogFragment implements OnMultiChoiceClickListener, DialogInterface.OnClickListener {

		private ArrayList<Integer> ids;
		private ArrayList<String> items;
		private boolean[] checkedItems;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			ids = getArguments().getIntegerArrayList("ids");
			items = getArguments().getStringArrayList("items");
			checkedItems = getArguments().getBooleanArray("checkedItems");

			Builder ab = new Builder(getActivity());

			ab.setTitle(R.string.participant_choice_text);

			ab.setMultiChoiceItems(items.toArray(new String[0]), checkedItems, this);

			ab.setPositiveButton(R.string.positive_button_text, this);
			ab.setNegativeButton(R.string.negative_button_text, this);

			return ab.create();
		}

		@Override
		public void onClick(DialogInterface dialog, int which, boolean isChecked) {
			// TODO Auto-generated method stub
			checkedItems[which] = isChecked;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			if (which == DialogInterface.BUTTON_POSITIVE) {

				TextView participantTextView = ((ScheduleRegisterActivity) getActivity()).getParticipantTextView();
				
				List<Participant> items = getSelectedItems();
				
				((ScheduleRegisterActivity) getActivity()).setSelectedItems(items);
				
				String s = convertListToString(items);
				
				participantTextView.setText(s);
			}
		}

		private String convertListToString(List<Participant> items) {
			
			String s = "";
			
			for (int i = 0; i < items.size(); i++) {
				
				if (i == 0) {
					
					s = items.get(i).getUser().getName();
				}
				else {
					
					s = s + ", " + items.get(i).getUser().getName();
				}
			}
			
			return s;
		}

		private List<Participant> getSelectedItems() {

			List<Participant> list = new ArrayList<Participant>();

			for (int i = 0; i < checkedItems.length; i++) {

				if (checkedItems[i]) {

					User user = new User();

					user.setUserId(ids.get(i));
					user.setName(items.get(i));

					Participant participant = new Participant();
					
					participant.setUser(user);
					
					list.add(participant);
				}
			}

			return list;
		}
	}

	public class GetUsersRequestTask extends AsyncTask<Void, Void, User[]> {

		@Override
		protected User[] doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String url = Constants.URL + "/getUsers";

			RestTemplate restTemplate = new RestTemplate();

			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			User[] users = restTemplate.getForObject(url, User[].class);

			return users;
		}

		@Override
		protected void onPostExecute(User[] result) {
			// TODO Auto-generated method stub
			ArrayList<Integer> intList = new ArrayList<Integer>();
			ArrayList<String> strList = new ArrayList<String>();

			for (User user : result) {

				intList.add(user.getUserId());
				strList.add(user.getName());
			}

			boolean[] checkedItems = new boolean[strList.size()];

			for (int i = 0; i < checkedItems.length; i++) {

				checkedItems[i] = false;
			}

			ParticipantPickerFragment participantPickerFragment = new ParticipantPickerFragment();

			Bundle bundle = new Bundle();

			bundle.putIntegerArrayList("ids", intList);
			bundle.putStringArrayList("items", strList);
			bundle.putBooleanArray("checkedItems", checkedItems);

			participantPickerFragment.setArguments(bundle);

			participantPickerFragment.show(getSupportFragmentManager(), "participantPicker");
		}
	}
	

	public class AddScheduleRequestTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			RestTemplate restTemplate = new RestTemplate();

			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

			String url = Constants.URL + "/addSchedule";

			String title = titleEditText.getText().toString();
			String content = contentEditText.getText().toString();
			String date = dateTextView.getText() + " " + timeTextView.getText();

			Schedule schedule = new Schedule();

			schedule.setTitle(title);
			schedule.setContent(content);
			schedule.setMainAddress(mainAddress);
			schedule.setDetailAddress(detailAddress);
			schedule.setLongitude(longitude);
			schedule.setLatitude(latitude);
			schedule.setFormattedDate("yyyy-MM-dd HH:mm:ss", date);

			schedule.setParticipants(selectedItems);

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

				Toast.makeText(ScheduleRegisterActivity.this, R.string.schedule_register_success, Toast.LENGTH_LONG).show();
				
				finish();
			}
		}
	}
}
