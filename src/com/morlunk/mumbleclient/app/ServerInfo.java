package com.morlunk.mumbleclient.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.morlunk.mumbleclient.R;
import com.morlunk.mumbleclient.app.db.DbAdapter;
import com.morlunk.mumbleclient.app.db.Server;

public class ServerInfo extends SherlockActivity {
	
	public void save() {
		final EditText nameEdit = (EditText) findViewById(R.id.serverNameEdit);
		final EditText hostEdit = (EditText) findViewById(R.id.serverHostEdit);
		final EditText portEdit = (EditText) findViewById(R.id.serverPortEdit);
		final EditText usernameEdit = (EditText) findViewById(R.id.serverUsernameEdit);
		final EditText passwordEdit = (EditText) findViewById(R.id.serverPasswordEdit);

		final String name = (nameEdit).getText().toString().trim();
		final String host = (hostEdit).getText().toString().trim();

		int port;
		try {
			port = Integer.parseInt((portEdit).getText().toString());
		} catch (final NumberFormatException ex) {
			port = 64738;
		}

		final String username = (usernameEdit).getText().toString().trim();
		final String password = (passwordEdit).getText().toString();

		final DbAdapter db = new DbAdapter(this);

		db.open();
		final long serverId = ServerInfo.this.getIntent().getLongExtra(
				"serverId", -1);
		if (serverId != -1) {
			db.updateServer(serverId, name, host, port, username, password);
		} else {
			db.createServer(name, host, port, username, password);
		}
		db.close();

		finish();
	}

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_add);
		

		final long serverId = this.getIntent().getLongExtra("serverId", -1);
		if (serverId != -1) {
			final EditText nameEdit = (EditText) findViewById(R.id.serverNameEdit);
			final EditText hostEdit = (EditText) findViewById(R.id.serverHostEdit);
			final EditText portEdit = (EditText) findViewById(R.id.serverPortEdit);
			final EditText usernameEdit = (EditText) findViewById(R.id.serverUsernameEdit);
			final EditText passwordEdit = (EditText) findViewById(R.id.serverPasswordEdit);

			final DbAdapter db = new DbAdapter(this);
			db.open();
			
			Server server = db.fetchServer(serverId);
			nameEdit.setText(server.getName());
			hostEdit.setText(server.getHost());
			portEdit.setText(String.valueOf(server.getPort()));
			usernameEdit.setText(server.getUsername());
			passwordEdit.setText(server.getPassword());
			
			db.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_save_button:
			save();
			return true;
		case R.id.menu_delete_button:
			createDeleteServerDialog().show();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_server_info, menu);
		
		MenuItem deleteButton = menu.findItem(R.id.menu_delete_button);
		if(getIntent().getLongExtra("serverId", -1) == -1) {
			deleteButton.setVisible(false);
		}
		
		return true;
	}
	
	private Dialog createDeleteServerDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.sureDeleteServer).setCancelable(
			false).setPositiveButton(
			"Yes",
			new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					DbAdapter dbAdapter = new DbAdapter(ServerInfo.this);
					long serverId = getIntent().getLongExtra("serverId", -1);
					dbAdapter.open();
					dbAdapter.deleteServer(serverId);
					dbAdapter.close();
					Toast.makeText(
						ServerInfo.this,
						R.string.server_deleted,
						Toast.LENGTH_SHORT).show();
					finish();
				}
			}).setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});

		return builder.create();
	}
}
