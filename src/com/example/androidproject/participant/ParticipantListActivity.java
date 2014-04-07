package com.example.androidproject.participant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.example.androidproject.Constants;
import com.example.androidproject.R;

public class ParticipantListActivity extends Activity {

	private ExpandableListView mList;
	
	String[] arProv = new String[] {
		"nonresponse", "attendance", "absence"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_participant_list);
		
		mList = (ExpandableListView) findViewById(R.id.participantExpandableListView);

		Intent intent = getIntent();
		
		int scheduleId = intent.getExtras().getInt("scheduleId");
		
		new GetParticipantsTask().execute(scheduleId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.participant_list, menu);
		return true;
	}

	public class GetParticipantsTask extends AsyncTask<Integer, Void, ParticipantList> {

		@Override
		protected ParticipantList doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int scheduleId = params[0];

			String url = Constants.URL + "/getParticipantList?scheduleId=" + scheduleId;

			RestTemplate restTemplate = new RestTemplate();

			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			ParticipantList participantList = restTemplate.getForObject(url, ParticipantList.class);
			
			return participantList;
		}

		@Override
		protected void onPostExecute(ParticipantList participantList) {
			// TODO Auto-generated method stub
			List<Map<String, String>> provData = new ArrayList<Map<String,String>>();
			List<List<Map<String, String>>> cityData = new ArrayList<List<Map<String,String>>>();
			
			for (int i = 0; i < arProv.length; i++) {
				
				Map<String, String> prov = new HashMap<String, String>();
				
				prov.put("prov", arProv[i]);
				
				provData.add(prov);
			}
			
			;
			
			cityData.add(getChildItem("city", participantList.getList1()));
			cityData.add(getChildItem("city", participantList.getList2()));
			cityData.add(getChildItem("city", participantList.getList3()));
			
			ExpandableListAdapter adapter = new SimpleExpandableListAdapter(ParticipantListActivity.this,
																			provData,
																			android.R.layout.simple_expandable_list_item_1,
																			new String[] {"prov"},
																			new int[] {android.R.id.text1},
																			cityData,
																			android.R.layout.simple_expandable_list_item_1,
																			new String[] {"city"},
																			new int[] {android.R.id.text1});
			
			mList.setAdapter(adapter);
			
			super.onPostExecute(participantList);
		}

		private List<Map<String, String>> getChildItem(String key, List<Participant> list) {
			
			List<Map<String, String>> children = new ArrayList<Map<String,String>>();
			
			for (Participant participant : list) {
				
				Map<String, String> city = new HashMap<String, String>();
				
				city.put(key, participant.getUser().getName());
				
				children.add(city);
			}
			
			return children;
		}
	}
}
