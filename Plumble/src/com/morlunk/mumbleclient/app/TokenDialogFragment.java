package com.morlunk.mumbleclient.app;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.morlunk.mumbleclient.Globals;
import com.morlunk.mumbleclient.R;
import com.morlunk.mumbleclient.app.db.DbAdapter;
import com.morlunk.mumbleclient.service.MumbleService;

public class TokenDialogFragment extends DialogFragment {
	
	private List<String> tokens;
	
	private ListView tokenList;
	private ArrayAdapter<String> tokenAdapter;
	
	private EditText tokenField;
	
	private DbAdapter dbAdapter;
	
	public static TokenDialogFragment newInstance() {
		TokenDialogFragment dialogFragment = new TokenDialogFragment();
		return dialogFragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		dbAdapter = new DbAdapter(activity);
		dbAdapter.open();
		tokens = dbAdapter.fetchAllTokens(MumbleService.getCurrentService().getServerId());
		dbAdapter.close();
		
		tokenAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, tokens);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getDialog().setTitle(R.string.accessTokens);
		
		View view = inflater.inflate(R.layout.fragment_tokens, null, false);
		
		tokenList = (ListView) view.findViewById(R.id.tokenList);
		tokenList.setAdapter(tokenAdapter);
		
		tokenField = (EditText) view.findViewById(R.id.tokenField);
		
		Button addButton = (Button) view.findViewById(R.id.tokenAddButton);
		addButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String tokenText = tokenField.getText().toString().trim();
				
				if(tokenText.equals("")) {
					return;
				}
				
				tokenField.setText("");
				
				Log.i(Globals.LOG_TAG, "Adding token: "+tokenText);
				
				tokens.add(tokenText);
				tokenAdapter.notifyDataSetChanged();
				
				dbAdapter.open();
				dbAdapter.createToken(MumbleService.getCurrentService().getServerId(), tokenText);
				dbAdapter.close();
			}
		});
		
		Button cancelButton = (Button) view.findViewById(R.id.tokenCloseButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		return view;
	}

}
