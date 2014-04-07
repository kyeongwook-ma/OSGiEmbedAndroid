package com.example.androidproject.message;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androidproject.Constants;
import com.example.androidproject.R;
import com.example.androidproject.schedule.ScheduleRegisterActivity;

public class ChattingFragment extends Fragment implements OnClickListener {

	private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			TextView textView = (TextView) getActivity().findViewById(R.id.messageTextView);
			
			try {
				
				String message = URLDecoder.decode(intent.getExtras().getString("message"), "UTF-8");
				
				textView.append(message);
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	public ChattingFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.layout_chatting, container, false);
		
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("com.google.android.c2dm.intent.RECEIVE"));
        
        rootView.findViewById(R.id.scheduleAddButton).setOnClickListener(this);
        rootView.findViewById(R.id.messageSendButton).setOnClickListener(this);
        
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
			case R.id.scheduleAddButton:
				
				Intent intent = new Intent(getActivity(), ScheduleRegisterActivity.class);
				
				startActivity(intent);
				
				break;
				
			case R.id.messageSendButton:
				
				String registrationId = getArguments().getString("registrationId");
				
				new SendToAllRequestTask().execute(registrationId);
				
				break;
	
			default:
				break;
		}
	}
	
	public class SendToAllRequestTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			String registrationId = params[0];
			
			RestTemplate restTemplate = new RestTemplate();
			
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
			
			String url = Constants.URL + "/sendToAll";
			
			String text = "";
			
			try {
				
				EditText editText = (EditText) getActivity().findViewById(R.id.messageEditText);
				
				text = URLEncoder.encode(editText.getText().toString(), "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Message message = new Message();
			
			message.setRegistrationId(registrationId);
			message.setText(text);
			
			HttpHeaders requestHeaders = new HttpHeaders();
			
			requestHeaders.setContentType(MediaType.APPLICATION_JSON);
			requestHeaders.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
			
			HttpEntity<Message> requestEntity = new HttpEntity<Message>(message, requestHeaders);
			
			restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			
			return null;
		}
	}
}
