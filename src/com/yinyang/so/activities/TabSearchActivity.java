package com.yinyang.so.activities;

import java.util.ArrayList;

import com.yinyang.so.R;
import com.yinyang.so.controllers.SearchController;
import com.yinyang.so.database.DatabaseAdapter.SearchResultSortingAlgorithm;
import com.yinyang.so.databaseentities.Post;
import com.yinyang.so.extras.PredicateLayout;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TabSearchActivity extends Activity {

	// necessary for putting tags from the outside
	public final static String EXTRA_TAGSTRING = "com.example.YingYangApp.TAGSTRING";

	/**
	 * List of tags that are currently selected for search
	 */
	private ArrayList<String> selectedTags = new ArrayList<String>();
	
	/**
	 * Maximum number of tags that can be selected for search
	 */
	private final int MAXIMUM_NO_SELECTED_TAGS = 5;
	
	/**
	 * Search controller
	 */
	private SearchController oSearchController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_search);
		
		String inputTag = this.getIntent().getStringExtra(EXTRA_TAGSTRING);
		oSearchController = new SearchController(this.getBaseContext());
		ArrayList<String> oTags = new ArrayList<String>();
		
		// if there is a value passed to this class with intent, use
		// that as the center tag, otherwise get the three first tags alphabetically
		if (inputTag != null) {
			oTags.addAll(oSearchController.getNextAndPreviousTags(inputTag));
			oTags.add(1, inputTag);
		} else
		{
			oTags = oSearchController.getThreeTagsInAlphabeticalOrder();
		}
		
		// get center tag button and replace its text
		Button oCenterButton = (Button)this.findViewById(R.id.button_center);
		oCenterButton.getBackground().setColorFilter(0xA0149EE3, PorterDuff.Mode.MULTIPLY);
		this.replaceTagButtonText(oCenterButton, oTags.get(1));
		
		// populate buttons around center button
		this.populateCenterSurroundingButtons(oTags.get(1));
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tab_search, menu);
		return true;
	}
	
	/**
	 * Handles event when any of the related tag buttons is pressed
	 * - Text on button button_center will be replaced by text on button that has been pressed
	 * - Text of buttons left and right to button button_center will be replaced by text of 'previous' and 'next' tag
	 * - Text of the related tag buttons will be replaced by the text of top-related tags
	 * @param view the button that invoked the onClick method
	 */
	public void moveToCenter(View view){
		// get text of pressed button
		Button oButton = (Button)view;
		String sTag = (String) oButton.getText();
		
		// get center tag button
		Button oCenterButton = (Button)this.findViewById(R.id.button_center);
		
		// replace text of center tag button
		this.replaceTagButtonText(oCenterButton, sTag);
		
		// populate tag buttons around the tag button in the middle
		this.populateCenterSurroundingButtons(sTag);
	}
	
	/**
	 * Replaces the text of the given button by the provided text
	 * @param oButton button to replace the text for
	 * @param sNewText string to replace the text a button with
	 */
	private void replaceTagButtonText(Button oButton, String sNewText)
	{
		oButton.setText(sNewText);
		oButton.setTag(sNewText);
	}
	
	/**
	 * Handles event when button button_center is pressed
	 * - If tag has not been selected for search yet and 
	 *   the maximum number of tags selected for search has not been exceeded,
	 *   the tag will be added to the tags selected for search
	 * - If tag has already been selected for search,
	 *   the tag will be removed from the tags selected for search
	 * - If the maximum number of tags selected for search has been exceeded,
	 *   a toast will be displayed
	 * @param view the button that invoked the onClick method
	 */
	public void selectForSearch(View view){
		Button centerButton = (Button)view;		
		addTagToSelected(centerButton.getText().toString());
	}
	
	/**
	 * Helper method that adds tag to tags selected for search
	 * - If tag has not been selected for search yet and 
	 *   the maximum number of tags selected for search has not been exceeded,
	 *   the tag will be added to the tags selected for search
	 * - If tag has already been selected for search,
	 *   the tag will be removed from the tags selected for search
	 * - If the maximum number of tags selected for search has been exceeded,
	 *   a toast will be displayed
	 * @param tag item to add to tags selected for search
	 */
	private void addTagToSelected(String tag){
		// If the maximum number of tags selected for serach has been exceeded,
	    // a toast will be displayed
		if(!selectedTags.contains(tag)){
			if(selectedTags.size() >= MAXIMUM_NO_SELECTED_TAGS)
			{
				Toast toast = Toast.makeText(this.getApplicationContext(), "Too many tags!", Toast.LENGTH_LONG);
				toast.show();	
			}
			// If tag has not been selected for search yet and 
			// the maximum number of tags selected for search has not been exceeded,
			// the tag will be added to the tags selected for search
			else {
				// create tag button to add to tags selected for search
				Button button = (Button) getLayoutInflater().inflate(R.layout.selected_tag_button, null);
				//Button button = new Button(this.getApplicationContext());
				button.setText(tag);
				button.setTag(tag);
				button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Button tagButton = (Button)view;
					removeTagFromSelected(tagButton.getText().toString());					
				}
				});
			
				// add created tag button to layout
				PredicateLayout layout = (PredicateLayout)this.findViewById(R.id.layout_selectedtags);
				layout.addView(button);
			
				selectedTags.add(tag);		
			}
		}
		else{
			// If tag has already been selected for search,
			// the tag will be removed from the tags selected for search
			removeTagFromSelected(tag);
		}
	}
	
	/**
	 * Helper method that removes tag from tags selected for search
	 * @param tag item to remove from tags selected for search
	 */
	private void removeTagFromSelected(String tag){
		PredicateLayout layout = (PredicateLayout)this.findViewById(R.id.layout_selectedtags);	
		layout.removeView(layout.findViewWithTag(tag));
		
		selectedTags.remove(tag);
	}

	/**
	 * Handles event when button button_search is pressed
	 * @param view the button that invoked the onClick method
	 */
	public void performSearch(View view){
		// get search free text
		EditText editView = (EditText)this.findViewById(R.id.edit_search);
		
		// invoke search result activity
		Intent oIntent = new Intent(this, SearchResultActivity.class);
				
		// pass posts sorted by question score to search result activity
		ArrayList<Post> oPosts = oSearchController.freeTextAndTagSearch(editView.getText().toString(), selectedTags, SearchResultSortingAlgorithm.QuestionScoreAlgorithm);
		oIntent.putParcelableArrayListExtra("POSTS_QUESTION_SCORE", (ArrayList<? extends Parcelable>) oPosts);
		
		// pass posts sorted by creation date to search result activity
		oPosts = oSearchController.freeTextAndTagSearch(editView.getText().toString(), selectedTags, SearchResultSortingAlgorithm.CreationDateAlgorithm);
		oIntent.putParcelableArrayListExtra("POSTS_CREATION_DATE", (ArrayList<? extends Parcelable>) oPosts);
		
		startActivity(oIntent);
	}
	
	/**
	 * Handles event when search tag button is pressed
	 * @param view the button that invoked the onClick method
	 */
	public void performTagSearch(View view){
		// get tag search text
		EditText editView = (EditText)this.findViewById(R.id.edit_tag_search);
		
		// get tag by name
		String sNewCenterTag = oSearchController.getTagByName(editView.getText().toString());
		
		// get center tag button and replace its text
		Button oCenterButton = (Button)this.findViewById(R.id.button_center);
		this.replaceTagButtonText(oCenterButton, sNewCenterTag);
		
		// populate buttons around center button
		this.populateCenterSurroundingButtons(sNewCenterTag);
	}
	
	/**
	 * Populates tag buttons around the center tag button
	 * @param sNewCenterTag the new tag button in the middle
	 */
	private void populateCenterSurroundingButtons(String sNewCenterTag){
		Button oNorthWestButton = (Button)this.findViewById(R.id.button_northwest);
		this.replaceTagButtonText(oNorthWestButton, "");
		Button oNorthEastButton = (Button)this.findViewById(R.id.button_northeast);
		this.replaceTagButtonText(oNorthEastButton, "");
		Button oSouthWestButton = (Button)this.findViewById(R.id.button_southwest);
		this.replaceTagButtonText(oSouthWestButton, "");
		Button oSouthEastButton = (Button)this.findViewById(R.id.button_southeast);
		this.replaceTagButtonText(oSouthEastButton, "");
		
		Button oWestButton = (Button)this.findViewById(R.id.button_west);
		this.replaceTagButtonText(oWestButton, "");
		Button oEastButton = (Button)this.findViewById(R.id.button_east);
		this.replaceTagButtonText(oEastButton, "");
		
		if(!"".equals(sNewCenterTag)){
			// populate top related tags
			ArrayList<String> oTopRelatedTags = oSearchController.getTopRelatedTags(sNewCenterTag);	
			if(oTopRelatedTags.size() > 0){
				this.replaceTagButtonText(oNorthWestButton, oTopRelatedTags.get(0));
			}
			
			if(oTopRelatedTags.size() > 1){
				this.replaceTagButtonText(oNorthEastButton, oTopRelatedTags.get(1));
			}
			
			if(oTopRelatedTags.size() > 2){
				this.replaceTagButtonText(oSouthWestButton, oTopRelatedTags.get(2));
			}
			
			if(oTopRelatedTags.size() > 3){
				this.replaceTagButtonText(oSouthEastButton, oTopRelatedTags.get(3));
			}	
			
			// populate neighbour tags
			ArrayList<String> oNeighbourTags = oSearchController.getNextAndPreviousTags(sNewCenterTag);
			this.replaceTagButtonText(oEastButton, oNeighbourTags.get(0));
			this.replaceTagButtonText(oWestButton, oNeighbourTags.get(1));	
		}
	}
}
