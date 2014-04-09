package com.example.androidproject;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.androidproject.R;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	
	public GCMIntentService() {
		super(Constants.PROJECT_ID);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		// TODO Auto-generated method stub
		context.sendBroadcast(intent);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		// new AddUserRequestTask().execute(registrationId);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub

	}

    public class AddUserRequestTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String registrationId = params[0];
			
			String url = Constants.URL + "/addUser?registrationId=" + registrationId;
			
			RestTemplate restTemplate = new RestTemplate();
			
			try {
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
									
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			return restTemplate.getForObject(url, String.class);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			System.out.println("GCMIntentService");
			
			if (result.equals("success")) {
				
				Toast.makeText(getApplicationContext(), R.string.add_user, Toast.LENGTH_LONG).show();
			}
		}
    }
}
