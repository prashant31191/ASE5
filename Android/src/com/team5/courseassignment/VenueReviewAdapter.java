package com.team5.courseassignment;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.team5.courseassignment.ImageLoader.ImageLoadedListener;

public class VenueReviewAdapter extends ArrayAdapter<VenueReview> {
	private int resourceId = 0;
	private LayoutInflater inflater;
	private ImageLoader imageLoader = new ImageLoader();
	private Context context;
	
	private String kKey;
	private String KEY_JSON = SharedPreferencesEditor.KEY_JSON;
	private final static String SUCCESS_JSON = "success";
	
	// variables for the POST call
	private static String VOTE_URL;
	private final static String VOTE_URL_EXT = "vote";
	
	private final String REVIEW_ID = "review_id";
	private String reviewId;
	private final String VOTE = "vote";
	private String voteValue;

	public VenueReviewAdapter(Context context, int resourceId,
			List<VenueReview> mediaItems) {
		super(context, 0, mediaItems);
		this.resourceId = resourceId;
		this.context = context;
		this.kKey = SharedPreferencesEditor.getKey();
		;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view;
		TextView firstName;
		TextView lastName;
		TextView rating;
		TextView review;
		TextView voteNumber;

		final ImageView image;

		view = inflater.inflate(resourceId, parent, false);

		try {
			firstName = (TextView) view.findViewById(R.id.firstName);
			lastName = (TextView) view.findViewById(R.id.lastName);
			rating = (TextView) view.findViewById(R.id.rating);
			review = (TextView) view.findViewById(R.id.review);
			voteNumber = (TextView) view.findViewById(R.id.voteNumber);
			reviewId = VenueReview.getReviewId();
			
			String baseUrl = context.getResources().getString(R.string.base_url);
			VOTE_URL = baseUrl + VOTE_URL_EXT;

			Button ib = (Button) view.findViewById(R.id.followerCommentButton);
			ib.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showCommentPopup();
				}
			});

			image = (ImageView) view.findViewById(R.id.profile_image);
			image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					openFollowerProfile();
				}
			});

			final CheckBox voteUp = (CheckBox) view.findViewById(R.id.voteUp);
			final CheckBox voteDown = (CheckBox) view.findViewById(R.id.voteDown);

			voteUp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {

					if (voteUp.isChecked()) {
						voteUp.setEnabled(true);
						// voteUp.setText(1);
						voteDown.setEnabled(false);
						voteValue = "1";
						voteUp();

					} else {
						voteUp.setEnabled(false);
						// voteUp.setText(1);
						voteDown.setEnabled(true);
						voteValue = "0";
						voteDown();
					}

				}

			});

			voteDown.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {

					if (voteDown.isChecked()) {
						voteDown.setEnabled(true);
						// voteUp.setText(1);
						voteUp.setEnabled(false);
						// TODO
						/**
						 * String selectedText = voteUp.getText().toString();
						 * Intent i = new Intent(this, VenueReview.class);
						 * i.putExtra("cakedata", selectedText);
						 * startActivity(i);
						 */

					} else {
						voteUp.setEnabled(true);
						// voteUp.setText(1);
						voteDown.setEnabled(false);
						// TODO
					}

				}

			});

		} catch (ClassCastException e) {
			// Log.e(TAG,
			// "Your layout must provide an image and a text view with ID's icon and text.",
			// e);
			throw e;
		}

		VenueReview item = getItem(position);

		Bitmap cachedImage = null;
		try {
			cachedImage = imageLoader.loadImage(item.getProfileImage(),
					new ImageLoadedListener() {
						public void imageLoaded(Bitmap imageBitmap) {
							image.setImageBitmap(imageBitmap);
							notifyDataSetChanged();
						}
					});
		} catch (MalformedURLException e) {
			// Log.e(TAG, "Bad remote image URL: " + item.getProfileImage(), e);
		}

		firstName.setText(item.getFirstName());
		lastName.setText(item.getLastName());
		rating.setText("Rating: " + item.getRating() + " stars");
		review.setText(item.getReview());
		voteNumber.setText(item.getVotes()); // Vote number need to get from
												// server.

		if (cachedImage != null) {
			image.setImageBitmap(cachedImage);
		}

		return view;
	}

	private void showCommentPopup() {

		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(context);
		helpBuilder.setTitle("Enter your comment here");
		helpBuilder.setMessage(" ");
		final EditText input = new EditText(context);
		input.setSingleLine();
		input.setText("");
		helpBuilder.setView(input);

		helpBuilder.setPositiveButton("Continue",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Send the comment to the server...
					}
				});

		helpBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		AlertDialog helpDialog = helpBuilder.create();
		helpDialog.show();
	}

	private void openFollowerProfile() {
		// launch ProfilePageActivity
		Intent openProfile = new Intent(this.context, FollowerProfileActivity.class);
		openProfile.putExtra(SharedPreferencesEditor.KEY_JSON, kKey);
		// openProfile.putExtra(user_NAME, user_id);

		context.startActivity(openProfile);
	}
	
	@SuppressWarnings("unchecked")
	private void voteUp() {

		List<NameValuePair> data = new ArrayList<NameValuePair>(3);
		data.add(new BasicNameValuePair(KEY_JSON, kKey));
		data.add(new BasicNameValuePair(REVIEW_ID, reviewId));
		data.add(new BasicNameValuePair(VOTE, voteValue));

		// make POST call
		ProgressDialog progress = ProgressDialog.show(context, "Please wait", "Loading ...");
		new VoteAsyncTask(progress).execute(data);
	}
	
	@SuppressWarnings("unchecked")
	private void voteDown() {

		List<NameValuePair> data = new ArrayList<NameValuePair>(3);
		data.add(new BasicNameValuePair(KEY_JSON, kKey));
		data.add(new BasicNameValuePair(REVIEW_ID, reviewId));
		data.add(new BasicNameValuePair(VOTE, voteValue));

		// make POST call
		ProgressDialog progress = ProgressDialog.show(context, "Please wait", "Loading ...");
		new VoteAsyncTask(progress).execute(data);
	}

	private class VoteAsyncTask extends AsyncTask<List<NameValuePair>, Void, JSONObject> {
		private ProgressDialog progress;

		public VoteAsyncTask(ProgressDialog progress) {
			this.progress = progress;
		}

		public void onPreExecute() {
			progress.show();
		}

		@SuppressWarnings("unused")
		protected void onProgressUpdate(Integer... progress) {
			((Activity) context).setProgress(progress[0]);
		}

		@Override
		protected JSONObject doInBackground(List<NameValuePair>... params) {

			List<NameValuePair> data = params[0];

			JSONObject resultJson = HttpRequest.makePostRequest(VOTE_URL,
					data);

			return resultJson;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			super.onPostExecute(result);
			progress.dismiss();
			if (result != null) {

				try {

					String success = result.getString(SUCCESS_JSON);

					if (success.equals("true")) {
						// do something here
					} else {
						// TODO: if the server responds with error, display
						// message
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}
	}
}
