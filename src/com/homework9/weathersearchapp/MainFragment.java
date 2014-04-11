package com.homework9.weathersearchapp;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainFragment extends Fragment{/*
	private UiLifecycleHelper uiHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    //Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        //onSessionStateChange(session, state, exception);
	    }
	};
	
	public void publishFacebookPost() {

    	final Dialog dialog = new Dialog(WeatherActivity.this);
        dialog.setContentView(R.layout.buttonlayout);
        dialog.setTitle(R.string.postString);
     
        		                
        LoginButton weatherButton = (LoginButton) dialog.findViewById(R.id.button2);
        Button cancelButton = (Button) dialog.findViewById(R.id.button3);
        weatherButton.setText(R.string.current_weather);
        // if button is clicked, close the custom dialog
        weatherButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	Session.openActiveSession(WeatherActivity.this, true, new Session.StatusCallback() {

            	      // callback when session changes state
            	      @SuppressWarnings("deprecation")
					@Override
            	      public void call(Session session, SessionState state, Exception exception) {
            	    	
            	        if (session.isOpened()) {

            	          // make request to the /me API
            	          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
            	            // callback after Graph API response with user object
            	            @Override
            	            public void onCompleted(GraphUser user, Response response) {
            	              if (user != null) { 
            	            	  isCurrentPost=true;
            	            	  publishFeedDialog();
            	               }
            	            }
            	          });
            	        }      	        
            	      }
            	    });
            	
            	
            }
        });
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    
	}

*/}
