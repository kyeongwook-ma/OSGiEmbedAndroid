package com.example.androidproject.user;

import java.util.ArrayList;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.androidproject.Constants;
import com.example.androidproject.R;

public class FriendListFragment extends Fragment {

	public FriendListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.layout_friend_list, container, false);
        
        new GetUsersRequestTask().execute();
        
		return rootView;
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
			ArrayList<String> list = new ArrayList<String>();
			
			for (User user : result) {
				
				list.add(user.getName() + " " + user.getPhone());
			}
			
	        ListView friendListView = (ListView) getActivity().findViewById(R.id.friendListView);
	        
	        String[] items = list.toArray(new String[0]);
	        
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
	        
	        friendListView.setAdapter(adapter);
	        friendListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}
}
