package com.pichangetheworld.tantacuore;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final int MAID_CHIEFS_PER_SET = 2;
	private static final int GENERAL_MAIDS_PER_SET[] = {16, 16, 18};
	private static final int MAX_SETS = 3;
	private static int TOTAL_GENERAL_MAIDS = 0;
	
	private static GridLayout _grid;
	private static TextView _chief1;
	private static TextView _chief2;
	
	private boolean[] flags;
	private String[] names;
	private int chiefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TOTAL_GENERAL_MAIDS = 0;
		for (int sum : GENERAL_MAIDS_PER_SET) {
			TOTAL_GENERAL_MAIDS += sum;
		}
		
		names = new String[MAX_SETS * MAID_CHIEFS_PER_SET + TOTAL_GENERAL_MAIDS];
		loadNames();
		
		flags = new boolean[TOTAL_GENERAL_MAIDS];
		clearFlags();

		// set default
		for (int i = 0; i < 10; ++i)
			flags[i] = true;
		chiefs = 0;
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			
			_grid = (GridLayout) rootView.findViewById(R.id.gridLayout);
			_chief1 = (TextView) rootView.findViewById(R.id.chief1);
			_chief2 = (TextView) rootView.findViewById(R.id.chief2);
			((MainActivity)getActivity()).generateRandom(null);
			
			return rootView;
		}
	}
	
	private void loadNames() {
		InputStream inputStream;
		try {
			inputStream = getAssets().open("maids.dat");
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			
			String name = bufferedReader.readLine();
			int i = 0;
			while (name != null) {
				System.out.println("name is " + name);
				names[i] = name;
				name = bufferedReader.readLine();
				++i;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void clearFlags() {
		for (int i = 0; i < TOTAL_GENERAL_MAIDS; ++i)
			flags[i] = false;
	}
	
	public void generateRandom(View v) {
		clearFlags();
		
		Random r = new Random();
		chiefs = r.nextInt(MAX_SETS);
		for (int i = 0; i < 10; ) {
			int rand = r.nextInt(TOTAL_GENERAL_MAIDS);
			if (!flags[rand]) {
				flags[rand] = true;
				++i;
			}
		}
		
		update();
	}
	
	private void update() {
		updateChiefs();
		updateGrid();
	}
	
	private void updateChiefs() {
		int baseMaidChief = 0;
		for (int i = 0; i < chiefs; ++i) {
			baseMaidChief += GENERAL_MAIDS_PER_SET[i] + MAID_CHIEFS_PER_SET;
		}
		_chief1.setText(String.format(getResources().getString(
				R.string.chiefs, chiefs+1, names[baseMaidChief])));
		_chief2.setText(String.format(getResources().getString(
				R.string.chiefs, chiefs+1, names[baseMaidChief+1])));
	}
	
	private void updateGrid() {
		int i = 0, setno = 1, maidno = 0;
		for (int val = 0; val < TOTAL_GENERAL_MAIDS; ++val, ++maidno) {
			if (maidno == GENERAL_MAIDS_PER_SET[setno-1]) {
				setno++;
				maidno = 0;
			} 
			if (flags[val]) {
				TextView t = (TextView) _grid.getChildAt(i);
				String maid_name = names[val + 2*setno];
				t.setText(String.format(getResources().getString(
						R.string.output, setno, maidno + 3, maid_name)));
				++i;
			}
		}
	}

}
