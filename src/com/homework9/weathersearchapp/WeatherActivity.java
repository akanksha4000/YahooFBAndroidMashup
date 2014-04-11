package com.homework9.weathersearchapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class WeatherActivity extends FragmentActivity  implements OnClickListener{
	private TextView textView1,textView2,textView3,textView4,textView5,textView6,textView7;
	private EditText editText;
	private ImageView imageView;
	private RadioButton rdb1,rdb2;
	private String city, region, country, imageLink, feed, link, text, temp, temperatureUnit;
	private JSONArray jsonForecastArray;
	private boolean isCurrentPost;
	private String errorMessage, type;
	//private MainFragment mainFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*if (savedInstanceState == null) {
	        // Add the fragment on initial activity setup
	        mainFragment = new MainFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(android.R.id.content, mainFragment)
	        .commit();
	    } else {
	        // Or set the fragment from restored state info
	        mainFragment = (MainFragment) getSupportFragmentManager()
	        .findFragmentById(android.R.id.content);
	    }*/
		setContentView(R.layout.activity_weather);
		editText = (EditText) findViewById(R.id.editText1);
		rdb1 = (RadioButton) findViewById(R.id.radioButton1);
		rdb2 = (RadioButton) findViewById(R.id.radioButton2);
		textView1 = (TextView) findViewById(R.id.textView1);
		textView2 = (TextView) findViewById(R.id.textView2);
		textView3 = (TextView) findViewById(R.id.textView3);
		textView4 = (TextView) findViewById(R.id.textView4);
		textView5 = (TextView) findViewById(R.id.textView5);
		textView6 = (TextView) findViewById(R.id.textView6);
		textView7 = (TextView) findViewById(R.id.textView7);
		imageView = (ImageView) findViewById(R.id.imageView1);
		Button button = (Button)findViewById(R.id.button1);
        button.setOnClickListener(this);
	}
	
	public boolean validateEditText(String location) {
		boolean isError=false;
		
		if(location!="") {
			if(location.matches("^\\d*$")) {
				if(location.matches("\\b\\d{5}\\b")) {
					isError=false;
					type="zip";	
				}
				else {
					isError=true;
					errorMessage="Invalid zipcode: must be five digits\nExample: 90089";
				}
			}
			else {
				if(location.matches("^[a-zA-Z0-9\\s]+(?:[.'\\-,][a-zA-Z0-9\\s]+)*$")) {
					int countComma = location.split(",").length - 1;
					String[] dataArray = location.split(",");
					for (int i=0; i < dataArray.length; i++) {
						if ("^\\s*$".matches(dataArray[i]) || dataArray[i]=="") {
							isError=true;
							errorMessage="Invalid location: must include state or country separated by comma\nExample: Los Angeles, CA";
							break;
						}
					}
					if(isError==false) {
						if(countComma!=1 && countComma!=2){
							isError=true;
							errorMessage="Invalid location: must include state or country separated by comma\nExample: Los Angeles, CA";
						}
						else {
							isError=false;
							type="city";
						}
					}	
				}
				
			}
		}
		else {
			isError=true;
			errorMessage="Please enter a zip code or city name";
		}
		return isError;
	}
	public void onClick(View view) {  
		TableLayout table = (TableLayout)findViewById(R.id.tableLayout1);
		table.removeAllViews();
		String location = editText.getText().toString();
		boolean result = validateEditText(location);
		if(!result) {
			String tempUnit="f";
			if(rdb1.isChecked())
				tempUnit="f";
			if(rdb2.isChecked())
				tempUnit="c";
	        String url = "http://cs-server.usc.edu:24934/examples/weathersearch?location="+(location.replace(' ', '+'))+"&type="+type+"&tempUnit="+tempUnit;
	        Log.v("url",url);
	        DownloadTask task = new DownloadTask();
	        task.execute(url);
		}
		else {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WeatherActivity.this);
			alertDialogBuilder
			.setTitle("Error!")
			.setMessage(errorMessage)
			.setCancelable(false)
			.setNeutralButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.dismiss();
				}
			});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.weather, menu);
		return true;
	}
	@Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	  }
	
	
	private class DownloadTask extends AsyncTask<String, Void, String> {
		
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
			 protected void onPostExecute(Bitmap img) {
				 imageView.setImageBitmap(img);				 
			 }
			 protected Bitmap doInBackground(final String... params) {
				 try {
					 if(params == null) return null;
					 	String url = params[0];
		        	 HttpURLConnection connection =
		        	 (HttpURLConnection)new URL(url).openConnection();
		        	 connection.setDoInput(true);
		        	 connection.connect();
		        	 InputStream input = connection.getInputStream();
		        	 Bitmap bitmap = BitmapFactory.decodeStream(input);
		        	 input.close();
		        	 return bitmap;
		        	 } catch (IOException ioe) { return null; }
			 }
		}
     
				
        @Override
        protected void onPostExecute(String sJson) {
        	Log.v("json",sJson);
        	JSONParser parser = new JSONParser();
        	try {
        		
				Object obj = parser.parse(sJson);
				JSONObject jsonObject = (JSONObject) obj;
				JSONObject jsonWeatherObject = (JSONObject)jsonObject.get("weather");
				if((String)jsonWeatherObject.get("error") == "" || (String)jsonWeatherObject.get("error") == null) {
				JSONObject jsonLocationObject = (JSONObject)jsonWeatherObject.get("location");
				JSONObject jsonUnitsObject = (JSONObject)jsonWeatherObject.get("units");
				JSONObject jsonConditionObject = (JSONObject)jsonWeatherObject.get("condition");
				jsonForecastArray = (JSONArray)jsonWeatherObject.get("forecast");
				city = (String)jsonLocationObject.get("city");
				region = (String)jsonLocationObject.get("region");
				country = (String)jsonLocationObject.get("country");
				imageLink = (String)jsonWeatherObject.get("img");
				feed = (String)jsonWeatherObject.get("feed");
				link = (String)jsonWeatherObject.get("link");
				text = (String)jsonConditionObject.get("text");
				temp = (String)jsonConditionObject.get("temp");
				temperatureUnit = (String)jsonUnitsObject.get("temperature");
				
				textView1.setText(city);
				if(region.equals("N/A"))
					textView2.setText(country);
				else
					textView2.setText(region + ", " + country);
				DownloadImageTask imgtask = new DownloadImageTask();
				imgtask.execute(imageLink);
				textView3.setText(text);
				textView4.setText(temp + Html.fromHtml("&#176;") + temperatureUnit);
				textView5.setText("Forecast");
				/*textView6.setMovementMethod(LinkMovementMethod.getInstance());
				textView6.setText("Share Current Weather", BufferType.SPANNABLE);*/
				textView6.setText(R.string.share_current_weather);
				textView6.setClickable(true);
				textView6.setOnClickListener(new OnClickListener() {
			            @Override
			            public void onClick(View v) {
			            	final Dialog dialog = new Dialog(WeatherActivity.this);
			                dialog.setContentView(R.layout.buttonlayout);
			                dialog.setTitle(R.string.postString);
			             
			                		                
			                Button weatherButton = (Button) dialog.findViewById(R.id.button2);
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
			    });
				/*textView7.setMovementMethod(LinkMovementMethod.getInstance());
				textView7.setText("Share Weather Forecast", BufferType.SPANNABLE);*/
				
				textView7.setText(R.string.share_weather_forecast);
				textView7.setClickable(true);
				textView7.setOnClickListener(new OnClickListener() {
			            @Override
			            public void onClick(View v) {
			            	final Dialog dialog = new Dialog(WeatherActivity.this);
			                dialog.setContentView(R.layout.buttonlayout);
			                dialog.setTitle(R.string.postString);
			             
			                Button weatherButton = (Button) dialog.findViewById(R.id.button2);
			                Button cancelButton = (Button) dialog.findViewById(R.id.button3);
			                weatherButton.setText(R.string.weather_forecast);
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
			                    	            	  isCurrentPost=false;
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
			    });
				
				TableLayout table = (TableLayout)findViewById(R.id.tableLayout1);
				table.removeAllViews();
				
				for(int i=-1;i<jsonForecastArray.size();i++) {
					
					TableRow row = new TableRow(WeatherActivity.this);
					TextView tv1 = new TextView(WeatherActivity.this);
					TextView tv2 = new TextView(WeatherActivity.this);
					TextView tv3 = new TextView(WeatherActivity.this);
					TextView tv4 = new TextView(WeatherActivity.this);
					
					tv1.setTextSize(17);
					tv2.setTextSize(17);
					tv3.setTextSize(17);
					tv4.setTextSize(17);
					tv1.setGravity(Gravity.CENTER);
					tv2.setGravity(Gravity.CENTER);
					tv3.setGravity(Gravity.CENTER);
					tv4.setGravity(Gravity.CENTER);
					
					if(i==-1) {
						tv1.setText("Day");
						tv2.setText("Weather");
						tv3.setText("High");	
						tv4.setText("Low");
						tv1.setBackgroundResource(R.drawable.back);
						tv2.setBackgroundResource(R.drawable.back);
						tv3.setBackgroundResource(R.drawable.back);
						tv4.setBackgroundResource(R.drawable.back);
					}
					else {
						tv3.setTextColor(0xFFF06D2F);
						tv4.setTextColor(Color.BLUE);
						if(i%2==0){
							tv1.setBackgroundResource(R.drawable.back_even);
							tv2.setBackgroundResource(R.drawable.back_even);
							tv3.setBackgroundResource(R.drawable.back_even);
							tv4.setBackgroundResource(R.drawable.back_even);
						}
						else {
							tv1.setBackgroundResource(R.drawable.back_odd);
							tv2.setBackgroundResource(R.drawable.back_odd);
							tv3.setBackgroundResource(R.drawable.back_odd);
							tv4.setBackgroundResource(R.drawable.back_odd);
						}
						JSONObject forecastObj  = (JSONObject)jsonForecastArray.get(i);
						tv1.setText((String)forecastObj.get("day"));
						tv2.setText((String)forecastObj.get("text"));
						tv3.setText((String)forecastObj.get("high")+ Html.fromHtml("&#176;") + temperatureUnit);	
						tv4.setText((String)forecastObj.get("low")+ Html.fromHtml("&#176;") + temperatureUnit);
					}
					row.addView(tv1);
					row.addView(tv2);
					row.addView(tv3);
					row.addView(tv4);
					table.addView(row, i+1);
				}
				}
				else {
					textView2.setText((String)jsonWeatherObject.get("error"));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	//List<?> apps = new ArrayList<>();
            
        }
 
      

		protected String doInBackground(final String... params) {
        	   if(params == null) return null;
               
               // get url from params
               String url = params[0];
               try {
                   // create http connection
                   HttpClient client = new DefaultHttpClient();
                   HttpGet httpget = new HttpGet(url);
                    
                   // connect
                   HttpResponse response = client.execute(httpget);
                    
                   // get response
                   HttpEntity entity = response.getEntity();
                    
                   if(entity == null) {
                	   //String msg = "No response from server";
                       return null;       
                   }
                 
                   // get response content and convert it to json string
                   InputStream is = entity.getContent();
                   return convertStreamToString(is);
               }
               catch(IOException e){
                   //String msg = "No Network Connection";
               }
                
               return null;	
        }
        
         String convertStreamToString(InputStream is) throws IOException {
        	 BufferedReader reader = new BufferedReader(new InputStreamReader(is));
             StringBuilder sb = new StringBuilder();
             String line = null;
              
             try {
                 while ((line = reader.readLine()) != null) {
                     sb.append(line + "\n");
                 }
             }
             catch (IOException e) {
                 throw e;
             }
             finally {          
                 try {
                     is.close();
                 }
                 catch (IOException e) {
                     throw e;
                 }
             }
              
             return sb.toString();
        }
         
         private void publishFeedDialog() {
        	 
        	 final List<String> PERMISSIONS = Arrays.asList("publish_actions");
        	 final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
        	 boolean pendingPublishReauthorization = false;
        	 
        	 Session session = Session.getActiveSession();

        	    if (session != null){

        	        // Check for publish permissions    
        	       /* List<String> permissions = session.getPermissions();
        	        if (!isSubsetOf(PERMISSIONS, permissions)) {
        	            pendingPublishReauthorization = true;
        	            Session.NewPermissionsRequest newPermissionsRequest = new Session
        	                    .NewPermissionsRequest(this, PERMISSIONS);
        	        session.requestNewPublishPermissions(newPermissionsRequest);
        	            return;
        	    }*/
        	        
        	    Bundle params = new Bundle();
        	    JSONObject properties = new JSONObject();
	        	JSONObject prop1 = new JSONObject();
	        	prop1.put("text", "here");
	            prop1.put("href", link);
	            properties.put("Look at details", prop1);
	            if(region.equals("N/A"))
	            	params.putString("name", city + ", "  + country);
				else
					params.putString("name", city + ", " + region + ", " + country);
        	    params.putString("link", feed);
        	    params.putString("properties", properties.toString());
        	    if(isCurrentPost) {
	        	    params.putString("caption", "The current condition for " + city + " is " + text + ".");
	        	    params.putString("description", "Temperature is " + temp + "&deg;" + temperatureUnit + ".");
	        	    params.putString("picture", imageLink);
        	    }
        	    else {
        	    	String description="";
        	    	for(int i=0;i<jsonForecastArray.size();i++)  {
        	    		JSONObject forecastObj  = (JSONObject)jsonForecastArray.get(i);
						  if(i!=jsonForecastArray.size()-1)
						  	description+=(String)forecastObj.get("day")  + ": " +(String)forecastObj.get("text")+ ", " + (String)forecastObj.get("high")+ "/" + (String)forecastObj.get("low")+ Html.fromHtml("&#176;") + temperatureUnit + "; ";
						  else
						  	description+=(String)forecastObj.get("day")  + ": " +(String)forecastObj.get("text")+ ", " + (String)forecastObj.get("high")+ "/" + (String)forecastObj.get("low")+ Html.fromHtml("&#176;") + temperatureUnit;
					  }
 	        	    params.putString("caption", "Weather forecast for " + city);
 	        	    params.putString("description", description  +  ".");
 	        	    params.putString("picture", "http://www-scf.usc.edu/~csci571/2013Fall/hw8/weather.jpg");  
        	    }
        	    WebDialog feedDialog = (
        	        new WebDialog.FeedDialogBuilder(WeatherActivity.this,
        	            Session.getActiveSession(),
        	            params))
        	        .setOnCompleteListener(new OnCompleteListener() {

        	            @Override
        	            public void onComplete(Bundle values,
        	                FacebookException error) {
        	                if (error == null) {
        	                    // When the story is posted, echo the success
        	                    // and the post Id.
        	                    final String postId = values.getString("post_id");
        	                    if (postId != null) {
        	                        Toast.makeText(WeatherActivity.this,
        	                            "Posted story, id: "+postId,
        	                            Toast.LENGTH_SHORT).show();
        	                    } else {
        	                        // User clicked the Cancel button
        	                        Toast.makeText(WeatherActivity.this.getApplicationContext(), 
        	                            "Publish cancelled", 
        	                            Toast.LENGTH_SHORT).show();
        	                    }
        	                } else if (error instanceof FacebookOperationCanceledException) {
        	                    // User clicked the "x" button
        	                    Toast.makeText(WeatherActivity.this.getApplicationContext(), 
        	                        "Publish cancelled", 
        	                        Toast.LENGTH_SHORT).show();
        	                } else {
        	                    // Generic, ex: network error
        	                    Toast.makeText(WeatherActivity.this.getApplicationContext(), 
        	                        "Error posting story", 
        	                        Toast.LENGTH_SHORT).show();
        	                }
        	            }

        	        })
        	        .build();
        	    feedDialog.show();
        	}
         }
  
    }


}
