package com.example.androidproject.schedule;

import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidproject.Constants;
import com.example.androidproject.R;
import com.example.androidproject.db.MMRSDBHelper;
import com.example.androidproject.participant.Participant;
import com.example.androidproject.participant.ParticipantListActivity;
import com.example.androidproject.user.User;
import com.google.android.gcm.GCMRegistrar;

public class ScheduleListFragment extends Fragment {

	private MMRSDBHelper mHelper;
	
	public ScheduleListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.layout_schedule_list, container, false);

		mHelper = new MMRSDBHelper(getActivity());
		
		// new GetScheduleRequestTask().execute(GCMRegistrar.getRegistrationId(getActivity()));

		return rootView;
	}

	public class GetScheduleRequestTask extends AsyncTask<String, Void, Schedule[]> {

		@Override
		protected Schedule[] doInBackground(String... params) {
			// TODO Auto-generated method stub
			String registrationId = params[0];
			
			String url = Constants.URL + "/getSchedules?registrationId=" + registrationId;

			RestTemplate restTemplate = new RestTemplate();

			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			Schedule[] schedules = restTemplate.getForObject(url, Schedule[].class);
			
			mHelper.deleteAllSchedule();

			for (Schedule schedule : schedules) {
				
				mHelper.insertSchedule(schedule);
			}
			
			return schedules;
		}

		@Override
		protected void onPostExecute(Schedule[] result) {
			// TODO Auto-generated method stub
			ListView friendListView = (ListView) getActivity().findViewById(R.id.scheduleListView);

			ScheduleListAdapter adapter = new ScheduleListAdapter(getActivity(), result);
			
			friendListView.setAdapter(adapter);

			friendListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	public class ScheduleListAdapter extends ArrayAdapter<Schedule> {

		private Context context;
		private Schedule[] schedules;

		public ScheduleListAdapter(Context context, Schedule[] objects) {
			super(context, R.layout.layout_schedule_view, objects);
			// TODO Auto-generated constructor stub
			this.context = context;
			this.schedules = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View v = inflater.inflate(R.layout.layout_schedule_view, parent, false);

			TextView scheduleTitleTextView = (TextView) v.findViewById(R.id.scheduleTitleTextView);
			TextView scheduleDateTextView = (TextView) v.findViewById(R.id.scheduleDateTextView);

			Button scheduleButton1 = (Button) v.findViewById(R.id.scheduleButton1);
			Button scheduleButton2 = (Button) v.findViewById(R.id.scheduleButton2);
			Button scheduleButton3 = (Button) v.findViewById(R.id.scheduleButton3);
			Button scheduleButton4 = (Button) v.findViewById(R.id.scheduleButton4);
			Button scheduleButton5 = (Button) v.findViewById(R.id.scheduleButton5);

			scheduleTitleTextView.setText(schedules[position].getTitle());
			scheduleDateTextView.setText(schedules[position].getFormattedDate("yyyy-MM-dd HH:mm:ss"));

			ScheduleRemoveListener l = new ScheduleRemoveListener(position);

			scheduleButton1.setOnClickListener(l);
			scheduleButton2.setOnClickListener(l);
			scheduleButton3.setOnClickListener(l);
			scheduleButton4.setOnClickListener(l);
			scheduleButton5.setOnClickListener(l);

			return v;
		}

		class ScheduleRemoveListener implements OnClickListener {

			private int position;

			public ScheduleRemoveListener(int position) {

				this.position = position;
			}

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				int scheduleId = schedules[position].getScheduleId();

				switch (v.getId()) {
				case R.id.scheduleButton1:

					new SetStateCodeTask().execute(scheduleId, GCMRegistrar.getRegistrationId(context), 1);

					break;
				case R.id.scheduleButton2:

					new SetStateCodeTask().execute(scheduleId, GCMRegistrar.getRegistrationId(context), 2);

					break;
				case R.id.scheduleButton3: // UPDATE

					Intent intent1 = new Intent(getActivity(), UpdateSchedule.class);
					
					intent1.putExtra("scheduleId", scheduleId);
					
					startActivity(intent1);

					break;
				case R.id.scheduleButton4:

					new RemoveScheduleTask().execute(scheduleId);

					break;
				case R.id.scheduleButton5:

					Intent intent = new Intent(getActivity(), ParticipantListActivity.class);

					intent.putExtra("scheduleId", scheduleId);

					startActivity(intent);

					break;

				default:
					break;
				}
			}
		}

		public class SetStateCodeTask extends AsyncTask<Object, Void, Void> {

			@Override
			protected Void doInBackground(Object... params) {
				// TODO Auto-generated method stub
				int scheduleId = (Integer) params[0];
				String registrationId = (String) params[1];
				int stateCode = (Integer) params[2];

				String url = Constants.URL + "/setStateCode";

				RestTemplate restTemplate = new RestTemplate();

				restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
				restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

				Participant participant = new Participant();

				User user = new User();

				user.setRegistrationId(registrationId);

				participant.setScheduleId(scheduleId);
				participant.setUser(user);
				participant.setStateCode(stateCode);

				HttpHeaders requestHeaders = new HttpHeaders();

				requestHeaders.setContentType(MediaType.APPLICATION_JSON);
				requestHeaders.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));

				HttpEntity<Participant> requestEntity = new HttpEntity<Participant>(participant, requestHeaders);

				ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

				String result = responseEntity.getBody();

				System.out.println("result : " + result);

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			}
		}

		public class RemoveScheduleTask extends AsyncTask<Integer, Void, Void> {

			@Override
			protected Void doInBackground(Integer... params) {
				// TODO Auto-generated method stub
				int scheduleId = params[0];

				String url = Constants.URL + "/removeSchedule?scheduleId=" + scheduleId;

				RestTemplate restTemplate = new RestTemplate();

				restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

				String result = restTemplate.getForObject(url, String.class);

				System.out.println("result : " + result);

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			}

		}
	}
}
