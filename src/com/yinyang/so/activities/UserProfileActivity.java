package com.yinyang.so.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yinyang.so.R;
import com.yinyang.so.controllers.UserProfileController;

public class UserProfileActivity extends ShowMenuActivity {

	public final static String EXTRA_USERID = "com.example.YingYangApp.USERID";
	public final static String EXTRA_DB_HELPER = "com.example.YingYangApp.DBHELPER";
	private UserProfileController controller;
	private int userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		//Set the userId to -1 if EXTRA_USERID is not included in the intent
		userId = intent.getIntExtra(EXTRA_USERID, -1);
		//Get the user, if the user does not exist show a toast to the user and close the activity
		try {
			controller = new UserProfileController(this, userId);
		} catch (NullPointerException e) {
			String text = "No user with id: " + userId;
			Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
			toast.show();
			finish();
		}
		if (controller != null) {
			setContentView(R.layout.activity_user_profile);
			addDrawer();
			updateView();
		}	

	}
	
	private void updateView() {
		setReputationText();
		setUserNameText();
		setCreationDateText();
		setLastAccessDate();
		setLocation();
		setWebsite();
		setDescription();
		setProfileViews();
		setAge();

	}

	private void setReputationText() {
		TextView textView = (TextView) findViewById(R.id.reputationScore);
		textView.setText("" + controller.getReputationText());
	}

	private void setUserNameText() {
		TextView textView = (TextView) findViewById(R.id.userName);
		textView.setText(controller.getUserName());
	}

	private void setCreationDateText() {
		TextView textView = (TextView) findViewById(R.id.membershipDuration);
		textView.setText(controller.getTimeSinceCreation());

	}

	private void setLastAccessDate() {
		TextView textView = (TextView) findViewById(R.id.lastSeen);
		textView.setText(controller.getTimeSinceAccess());
	}

	private void setWebsite() {
		TextView textView = (TextView) findViewById(R.id.website);
		textView.setText(controller.getWebsite());
	}

	private void setLocation() {
		TextView textView = (TextView) findViewById(R.id.location);
		textView.setText(controller.getLocation());
	}

	private void setAge() {
		TextView textView = (TextView) findViewById(R.id.age);
		textView.setText(controller.getAge());
	}

	private void setDescription() {
		TextView textView = (TextView) findViewById(R.id.userDescription);
		textView.setText(Html.fromHtml(controller.getDescriptionInHtml()));
	}

	private void setProfileViews() {
		TextView textView = (TextView) findViewById(R.id.profileViews);
		textView.setText(Integer.toString(controller.getProfileViews()));
	}

	public void questionsView(View view) {
		controller.gotoUserQuestionView(this);
	}

}