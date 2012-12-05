package com.morlunk.mumbleclient.app;

import java.util.List;

import com.morlunk.mumbleclient.Globals;
import com.morlunk.mumbleclient.service.MumbleService;
import com.morlunk.mumbleclient.service.model.Channel;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

public class ChannelSearchProvider extends ContentProvider {

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if(MumbleService.getCurrentService() == null)
			return null;
		
		String query = "";
		for(int x=0;x<selectionArgs.length;x++) {
			query += selectionArgs[x];
			if(x != selectionArgs.length-1)
				query += " ";
		}
		
		query = query.toLowerCase();
		
		MatrixCursor cursor = new MatrixCursor(new String[] { "_ID", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_DATA });
		
		List<Channel> channels = MumbleService.getCurrentService().getChannelList();
		
		for(int x=0;x<channels.size();x++) {
			Channel channel = channels.get(x);
			String channelNameLower = channel.name.toLowerCase();
			if(channelNameLower.contains(query)) {
				cursor.addRow(new Object[] { x, channel.name, channel.id });
			}
		}
		
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
