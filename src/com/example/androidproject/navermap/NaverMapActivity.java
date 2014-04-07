package com.example.androidproject.navermap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidproject.R;
import com.example.androidproject.naversearch.Item;
import com.example.androidproject.naversearch.Rss;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.NMapView.OnMapStateChangeListener;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

public class NaverMapActivity extends NMapActivity {

	private final double _LATITUDE = 126.94105636850571;
	private final double LATITUDE = 37.55170991851449;

	private final String API_KEY = "1536f5bb20e95dd0f40f32707dc62717 ";
	
	private EditText searchEditText;
	private Button searchButton;
	private EditText detailAddresEditText;
	private ListView listView;
	private Button okButton;
	
	private NMapView mMapView;
	
	private NMapController mMapController;
	
	private NMapViewerResourceProvider mMapViewerResourceProvider;

	private NMapOverlayManager mOverlayManager;
	
	private Bundle extras;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_naver_map);
		
		searchEditText = (EditText) findViewById(R.id.searchEditText);
		searchButton = (Button) findViewById(R.id.searchButton);
		detailAddresEditText = (EditText) findViewById(R.id.detailAddressEditText);
		listView = (ListView) findViewById(R.id.searchListView);
		okButton = (Button) findViewById(R.id.naverMapOkButton);
		
		searchButton.setOnClickListener(onClickListener);
		okButton.setOnClickListener(onClickListener);
		
		extras = new Bundle();
		
		listView.setOnItemClickListener(onItemClickListener);
		
		mMapView = (NMapView)findViewById(R.id.mapView);
		
		mMapView.setApiKey(API_KEY);

		mMapView.setClickable(true);
		
		mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);

		mMapController = mMapView.getMapController();
		
		mMapView.setBuiltInZoomControls(true, null);

		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.naver_map, menu);
		return true;
	}
	
	private class NaverSearchAdapter extends ArrayAdapter<Item> {

		private Context context;
		private List<Item> items;
		
		public NaverSearchAdapter(Context context, List<Item> objects) {
			super(context, R.layout.layout_custom_list_view, objects);
			// TODO Auto-generated constructor stub
			this.context = context;
			this.items = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View v = inflater.inflate(R.layout.layout_custom_list_view, parent, false);
			
			TextView titleTextView = (TextView) v.findViewById(R.id.titleTextView);
			TextView addresstextView = (TextView) v.findViewById(R.id.addressTextView);
			
			titleTextView.setText(items.get(position).getTitle());
			addresstextView.setText(items.get(position).getAddress());
			
			return v;
		}
	}

	private class NaverSearchTask extends AsyncTask<String, Void, Rss> {

		@Override
		protected Rss doInBackground(String... params) {
			// TODO Auto-generated method stub
			String word = params[0];
			
			word = (word.equals("")) ? "서강대학교" : word;
			
			String url = "http://openapi.naver.com/search?key=ae724df24adf1fd5c5af90227839aef4&query=" + word + "&target=local&start=1&display=10";
			
			RestTemplate restTemplate = new RestTemplate();
			
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
			restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());
			
			Rss rss = restTemplate.getForObject(url, Rss.class);
			
			return rss;
		}

		@Override
		protected void onPostExecute(Rss rss) {
			// TODO Auto-generated method stub
			List<Item> items = rss.getChannel().getItems();
			
			if (items == null) {
				
				items = new ArrayList<Item>();
			}
			
			NaverSearchAdapter adapter = new NaverSearchAdapter(NaverMapActivity.this, items);
			
			listView.setAdapter(adapter);
		}
	}
	
	private final OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.searchButton:
			
					String word = searchEditText.getText().toString();
					
					new NaverSearchTask().execute(word);
					
					break;
				case R.id.naverMapOkButton:
					
					String detailAddress = detailAddresEditText.getText().toString();
					
					extras.putString("detailAddress", detailAddress);
					
					Intent intent = new Intent();
					
					intent.putExtras(extras);
					
					setResult(RESULT_OK, intent);
					
					finish();
					
					break;
	
				default:
					break;
			}
		}
	};
	
	private final OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parentView, View clickedView, int position, long id) {
			// TODO Auto-generated method stub
			NaverSearchAdapter adapter = (NaverSearchAdapter) listView.getAdapter();
			
			Item item = adapter.getItem(position);
			
			mOverlayManager.clearOverlays();
			
			NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
			
			poiData.beginPOIdata(1);
			
			NMapPOIitem poiItem = poiData.addPOIitem(null, null, NMapPOIflagType.PIN, 0);
			
			if (poiItem != null) {
			
				poiItem.setPoint(new NGeoPoint(item.getLongitude(), item.getLatitude()));
				
				poiItem.setTitle(item.getTitle());
			}
			
			poiData.endPOIdata();

			NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
			
			if (poiDataOverlay != null) {
				
				poiDataOverlay.deselectFocusedPOIitem();
				
				poiDataOverlay.selectPOIitem(0, true);
			}
			
			extras.putDouble("longitude", item.getLongitude());
			extras.putDouble("latitude", item.getLatitude());
			extras.putString("mainAddress", item.getTitle());
		}
	};
	
	private final OnMapStateChangeListener onMapViewStateChangeListener = new OnMapStateChangeListener() {

		@Override
		public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onMapCenterChange(NMapView arg0, NGeoPoint arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMapCenterChangeFine(NMapView arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMapInitHandler(NMapView arg0, NMapError errorInfo) {
			// TODO Auto-generated method stub
			if (errorInfo == null) { // success
				
				mMapController.setMapCenter(new NGeoPoint(_LATITUDE, LATITUDE), 11);
			} 
			else { // fail
				
				Log.e("error", "onMapInitHandler: error=" + errorInfo.toString());
			}
		}

		@Override
		public void onZoomLevelChange(NMapView arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
	};
}
