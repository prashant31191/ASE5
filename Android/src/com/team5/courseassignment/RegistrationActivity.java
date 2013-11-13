package com.team5.courseassignment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.team5.courseassignment.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;


/*
 * IMPORTANT NOTICE: We are only using DEBUG certificates as signature for the Google Maps API !!!
 */


public class RegistrationActivity extends Activity{
	
	//variables for the POST call
	private final static String REGISTER_URL = "http://switchcodes.in/sandbox/projectpackets/t5/user/register";
	private final static String EMAIL_KEY = "email";
	private final static String PASSWORD_KEY = "passwd";
	private final static String FIRSTNAME_KEY = "first_name";
	private final static String LASTNAME_KEY = "last_name";
	
	
	//variables for the POST answer
	private final static String SUCCESS_JSON = "success";
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //set layout
        setContentView(R.layout.registration);	 
        
        //set button actions
        Button continueButton = (Button) findViewById(R.id.register_button_registration);
        continueButton.setOnClickListener(new OnClickListener() {
			
        	 @SuppressWarnings("unchecked")
			@Override
        		public void onClick(View v) {
        		
        	//TODO: show the user that progress is happening and set text fields to be unchangeable!
			
			//get Strings from the EditText fields
			String firstName = ((EditText) findViewById(R.id.first_name_registration)).getEditableText().toString();
			String lastName = ((EditText) findViewById(R.id.last_name_registration)).getEditableText().toString();
			String email = ((EditText) findViewById(R.id.email_box_registration)).getEditableText().toString();
			String password = ((EditText) findViewById(R.id.password_box_registration)).getEditableText().toString();
			String retypePassword = ((EditText) findViewById(R.id.retype_password_box_registration)).getEditableText().toString();
			
			//ensure that the password Strings are equal
			if(! password.equals(retypePassword)) {
				
				showAlertMessage((getResources().getString(R.string.errorMessage)),getResources().getString(R.string.passwords_not_equal));
			}
			
			//TODO: maybe more user input checking
			
			String encryptedPassword = Utilities.encryptString(password);
			
			List<NameValuePair> data = new ArrayList<NameValuePair>(4);
			data.add(new BasicNameValuePair(FIRSTNAME_KEY, firstName));
			data.add(new BasicNameValuePair(LASTNAME_KEY, lastName));
			data.add(new BasicNameValuePair(EMAIL_KEY, email));
			data.add(new BasicNameValuePair(PASSWORD_KEY, encryptedPassword));
			

			//make POST call
			new RegisterAsyncTask().execute(data);
			
	
        	 }
			
		});
        
    } 
    
    
    
    private void showAlertMessage(final String title,final String message) {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(title);
		
		alert.setMessage(message);
		alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
		
		
		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO: maybe delete content of some fields
				if((getResources().getString(R.string.successMessageRegistration)).equals(message)){
					Intent i = new Intent(getApplicationContext(),  LoginActivity.class);
					startActivity(i);
				}else{
					//TODO 
				}
			}
		});
		
		alert.show();
	}
    
    
    
    private class RegisterAsyncTask extends AsyncTask<List<NameValuePair>, Void, JSONObject> {

    	@Override
		protected JSONObject doInBackground(List<NameValuePair>... params) {
			
			List<NameValuePair> data = params[0];
			
			JSONObject resultJson = HttpRequest.makePostRequest(REGISTER_URL, data);
			
			return resultJson;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			
			super.onPostExecute(result);
			
			if(result != null) {
				
				try {
					
					String success = result.getString(SUCCESS_JSON);
					
					if(success.equals("true")) {
						
						
						showAlertMessage((getResources().getString(R.string.congratulations)),getResources().getString(R.string.successMessageRegistration));
						
						
					} else {
						
						showAlertMessage((getResources().getString(R.string.errorMessage)),getResources().getString(R.string.invalid_input_generic));

						
					} //TODO: do more error checking stuff when Sandeep has extended his API
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			} 
		}

    	
    }



    
   

    



}