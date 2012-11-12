package com.morlunk.mumbleclient.app;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.morlunk.mumbleclient.Globals;
import com.morlunk.mumbleclient.R;
import com.morlunk.mumbleclient.app.db.DbAdapter;
import com.morlunk.mumbleclient.service.MumbleService;

public class TokenDialogFragment extends DialogFragment {
	
	private TokenDialogFragmentListener tokenListener;
	
	private List<String> tokens;
	
	private ListView tokenList;
	private TokenAdapter tokenAdapter;
	
	private EditText tokenField;
	
	private DbAdapter dbAdapter;
	
	public static TokenDialogFragment newInstance() {
		TokenDialogFragment dialogFragment = new TokenDialogFragment();
		return dialogFragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			tokenListener = (TokenDialogFragmentListener)activity;
		} catch(ClassCastException exception) {
			throw new ClassCastException(activity.toString() + " must implement TokenDialogFragmentListener");
		}
		
		dbAdapter = new DbAdapter(activity);
		dbAdapter.open();
		tokens = dbAdapter.fetchAllTokens(MumbleService.getCurrentService().getServerId());
		dbAdapter.close();
		
		tokenAdapter = new TokenAdapter(activity, tokens);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if(getDialog() != null)
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
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		
		if(tokenListener != null) {
			tokenListener.updateAccessTokens(tokens);
		}
	}
	
	class TokenAdapter extends ArrayAdapter<String> {

		public TokenAdapter(Context context,
				List<String> objects) {
			super(context, android.R.layout.simple_list_item_1, objects);
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if(convertView == null) {
				view = getActivity().getLayoutInflater().inflate(R.layout.token_row, null, false);
			}
			
			final String token = getItem(position);
			
			TextView title = (TextView) view.findViewById(R.id.tokenItemTitle);
			title.setText(token);
			
			ImageButton deleteButton = (ImageButton) view.findViewById(R.id.tokenItemDelete);
			deleteButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dbAdapter.open();
					dbAdapter.deleteToken(token, MumbleService.getCurrentService().getServerId());
					dbAdapter.close();
					tokens.remove(position);
					notifyDataSetChanged();
				}
			});
			
			return view;
		}
		
	}

}
