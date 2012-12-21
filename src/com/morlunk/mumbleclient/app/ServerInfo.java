package com.morlunk.mumbleclient.app;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.morlunk.mumbleclient.R;
import com.morlunk.mumbleclient.app.db.DbAdapter;
import com.morlunk.mumbleclient.app.db.Server;

public class ServerInfo extends SherlockDialogFragment {
	private EditText nameEdit;
	private EditText hostEdit;
	private EditText portEdit;
	private EditText usernameEdit;

	private Server server;
	
	private ServerInfoListener serverListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		if(getArguments() != null) {
			long serverId = this.getArguments().getLong("serverId", -1);
			if(serverId != -1) {
				DbAdapter adapter = new DbAdapter(activity);
				adapter.open();
				server = adapter.fetchServer(serverId);
				adapter.close();
			}
		}
		
		try {
			serverListener = (ServerInfoListener)activity;
		} catch(ClassCastException exception) {
			throw new ClassCastException(activity.toString() + " must implement ServerInfoListener");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.server_add, null, false);
		
		nameEdit = (EditText) view.findViewById(R.id.serverNameEdit);
		hostEdit = (EditText) view.findViewById(R.id.serverHostEdit);
		portEdit = (EditText) view.findViewById(R.id.serverPortEdit);
		usernameEdit = (EditText) view.findViewById(R.id.serverUsernameEdit);
		if (server != null) {
			nameEdit.setText(server.getName());
			hostEdit.setText(server.getHost());
			portEdit.setText(String.valueOf(server.getPort()));
			usernameEdit.setText(server.getUsername());
		}
		
		Button saveButton = (Button) view.findViewById(R.id.serverAddButton);
		saveButton.setText(server == null ? R.string.add : R.string.save);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});
		
		Button cancelButton = (Button) view.findViewById(R.id.serverCancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		return view;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle(server != null ? R.string.serverChange : R.string.serverAdd);
		return dialog;
	}
	
	public void save() {

		String name = (nameEdit).getText().toString().trim();
		String host = (hostEdit).getText().toString().trim();

		int port;
		try {
			port = Integer.parseInt((portEdit).getText().toString());
		} catch (final NumberFormatException ex) {
			port = 64738;
		}

		String username = (usernameEdit).getText().toString().trim();

		DbAdapter db = new DbAdapter(getActivity());

		db.open();
		if (server != null) {
			db.updateServer(server.getId(), name, host, port, username, server.getPassword());
		} else {
			db.createServer(name, host, port, username, "");
		}
		db.close();

		dismiss();
		
		serverListener.serverInfoUpdated();
	}
}
