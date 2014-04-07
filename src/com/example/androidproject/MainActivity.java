package com.example.androidproject;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.example.androidproject.R;
import com.example.androidproject.adaptation.AdaptationService;
import com.example.androidproject.message.ChattingFragment;
import com.example.androidproject.schedule.ScheduleListFragment;
import com.example.androidproject.user.FriendListFragment;
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		Context context = getApplicationContext();

		System.setProperty("http.keepAlive", "false");

		try {

			checkDevice(context);

			if (!GCMRegistrar.isRegistered(context)) {

				GCMRegistrar.register(context, Constants.PROJECT_ID);
			}
			else {

				String registrationId = GCMRegistrar.getRegistrationId(context);

				new AddUserRequestTask().execute(registrationId);
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.mainPager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {

						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			Tab tab = actionBar.newTab();

			tab.setText(mSectionsPagerAdapter.getPageTitle(i));
			tab.setTabListener(this);

			actionBar.addTab(tab);
		}
	}

//	占쏙옙占�menubar 占쏙옙占�//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MainActivity.this, AdaptationService.class);
		
		stopService(intent);
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MainActivity.this, AdaptationService.class);
		
		startService(intent);
		
		super.onResume();
	}

	private void checkDevice(Context context) throws Exception {

		try {

			GCMRegistrar.checkDevice(context);
		} catch (Exception e) {
			// TODO: handle exception
			throw new Exception("This device can't use GCM");
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			switch (position) {

			case 0:

				return new FriendListFragment();

			case 1:

				String registrationId = GCMRegistrar.getRegistrationId(MainActivity.this);
				
				ChattingFragment chattingFragment = new ChattingFragment();
				
				Bundle bundle = new Bundle();

				bundle.putString("registrationId", registrationId);

				chattingFragment.setArguments(bundle);

				return chattingFragment;

			case 2:

				return new ScheduleListFragment();
			}

			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {

			switch (position) {

			case 0:

				return getString(R.string.title_section1);

			case 1:

				return getString(R.string.title_section2);

			case 2:

				return getString(R.string.title_section3);
			}
			return null;
		}
	}

	public class AddUserRequestTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String registrationId = params[0];

			String url = Constants.URL + "/addUser?registrationId=" + registrationId;

			RestTemplate restTemplate = new RestTemplate();

			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

			String result = restTemplate.getForObject(url, String.class);

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			System.out.println("MainActivity");

			if (result.equals("success")) {

				Toast.makeText(MainActivity.this, R.string.add_user, Toast.LENGTH_LONG).show();
			}
		}
	}
}
