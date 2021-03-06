package com.yinyang.so.controllers;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.Toast;

import com.yinyang.so.activities.SearchResultActivity;
import com.yinyang.so.database.DatabaseAdapter;
import com.yinyang.so.database.DatabaseAdapter.SearchResultSortingAlgorithm;
import com.yinyang.so.database.KeyValuePair;
import com.yinyang.so.databaseentities.Post;
import com.yinyang.so.databaseentities.User;

public class SearchController {
	private DatabaseAdapter dbAdapter;
	private Context con;

	/**
	 * Communicates with the database through a DatabaseAdapter Fetches
	 * information for the question model
	 */
	public SearchController(Context con) {
		dbAdapter = new DatabaseAdapter(con);
		dbAdapter.createDatabase();

		this.con = con;
	}

	/**
	 * Searches for posts where the title or body contains the given free text
	 * 
	 * @param sFreeText
	 *            free text to search for in posts
	 * @param eSearchResultSortingAlgorithm
	 *            chosen search result algorithm
	 * @return an array list of posts
	 */
	public ArrayList<Post> freeTextSearch(String sFreeText,
			SearchResultSortingAlgorithm eSearchResultSortingAlgorithm) {
		dbAdapter.open();
		return dbAdapter.getQuestionsByFreeText(sFreeText.split(" "),
				eSearchResultSortingAlgorithm);
	}

	public ArrayList<String> getNextAndPreviousTags(String searchTag) {
		dbAdapter.open();
		return dbAdapter.getNextAndPreviousTags(searchTag);
	}

	/**
	 * Returns tag that's name match the given string
	 * 
	 * @param sName
	 *            string the tag name should match
	 * @return tag that's name match the given string
	 */
	public String getTagByName(String sName) {
		dbAdapter.open();
		return dbAdapter.getTagByName(sName);
	}

	/**
	 * Returns the four top related tags and number of occurences
	 * 
	 * @param sName
	 *            tag the returned tags are top related to
	 * @return the four top related tags
	 */
	public ArrayList<KeyValuePair> getTopRelatedTags(String sName) {
		dbAdapter.open();
		return dbAdapter.getTopRelatedTags(sName);
	}

	/**
	 * Returns three tags in alphabetical order
	 * 
	 * @param iLimit
	 *            limits number of returned tags
	 * @return three tags in alphabetical order
	 */
	public ArrayList<String> getThreeTagsInAlphabeticalOrder() {
		dbAdapter.open();
		return dbAdapter.getTagsInAlphabeticalOrder(3);
	}

	/**
	 * Get questions where - the given words are contained in either the title
	 * or the body and - there is a relation to all of the provided tags
	 * 
	 * @param oWords
	 *            the words that have to be contained in a question's title or
	 *            body to be returned by this method
	 * @param oTags
	 *            tags the returned questions should be related to
	 * @param eSearchResultSortingAlgorithm
	 *            chosen search result algorithm
	 * @return questions
	 */
	public ArrayList<Post> freeTextAndTagSearch(String sFreeText,
			ArrayList<String> oTags,
			SearchResultSortingAlgorithm eSearchResultSortingAlgorithm) {
		dbAdapter.open();

		if (!"".equals(sFreeText) || oTags.size() > 0) {
			return dbAdapter.getQuestionsByFreeTextAndTags(
					sFreeText.split(" "), oTags, eSearchResultSortingAlgorithm);
		} else {
			return new ArrayList<Post>();
		}
	}

	/**
	 * Searches posts that contain the given text in either its title or body
	 * sorted by - question score - creation date Invokes the search result
	 * activity
	 * 
	 * @param textSearch
	 *            text that has been search for
	 */
	public void performFreeTextSearch(String textSearch) {

		// invoke search result activity
		Intent oIntent = new Intent(con, SearchResultActivity.class);

		// pass posts sorted by question score to search result activity
		ArrayList<Post> postsFound = freeTextSearch(
				textSearch, SearchResultSortingAlgorithm.QuestionScoreAlgorithm);
		oIntent.putParcelableArrayListExtra(SearchResultActivity.KEY_POSTS_QUESTION_SCORE,
				(ArrayList<? extends Parcelable>) postsFound);

		// pass posts sorted by creation date to search result activity
		postsFound = freeTextSearch(textSearch,
				SearchResultSortingAlgorithm.CreationDateAlgorithm);
		oIntent.putParcelableArrayListExtra(SearchResultActivity.KEY_POSTS_CREATION_DATE,
				(ArrayList<? extends Parcelable>) postsFound);

		// pass posts sorted by answer count to search result activity
		postsFound = freeTextSearch(textSearch,
				SearchResultSortingAlgorithm.AnswerCountAlgotithm);
		oIntent.putParcelableArrayListExtra(SearchResultActivity.KEY_POSTS_ANSWER_COUNT,
				(ArrayList<? extends Parcelable>) postsFound);

		// pass posts sorted by answer count to search result activity
		postsFound = freeTextSearch(textSearch,
				SearchResultSortingAlgorithm.UserReputationAlgorithm);
		oIntent.putParcelableArrayListExtra(SearchResultActivity.KEY_POSTS_USER_REPUTATION,
				(ArrayList<? extends Parcelable>) postsFound);
		
		// pass free text
		oIntent.putExtra(SearchResultActivity.KEY_FREE_TEXT, textSearch);

		con.startActivity(oIntent);
	}

	/**
	 * Searches posts that are related to the given tags and contain the given
	 * text in either its title or body sorted by - question score - creation
	 * date
	 * 
	 * @param textSearch
	 *            text that has been search for
	 * @param selectedTags
	 *            tags that the posts should be related to
	 */
	public void performFreeTextAndTagSearch(String textSearch,
			ArrayList<String> selectedTags) {

		// invoke search result activity
		Intent oIntent = new Intent(con, SearchResultActivity.class);

		// pass posts sorted by question score to search result activity
		ArrayList<Post> oPosts = freeTextAndTagSearch(
				textSearch, selectedTags,
				SearchResultSortingAlgorithm.QuestionScoreAlgorithm);
		oIntent.putParcelableArrayListExtra(SearchResultActivity.KEY_POSTS_QUESTION_SCORE,
				(ArrayList<? extends Parcelable>) oPosts);

		// pass posts sorted by creation date to search result activity
		oPosts = freeTextAndTagSearch(textSearch,
				selectedTags,
				SearchResultSortingAlgorithm.CreationDateAlgorithm);
		oIntent.putParcelableArrayListExtra(SearchResultActivity.KEY_POSTS_CREATION_DATE,
				(ArrayList<? extends Parcelable>) oPosts);

		// pass posts sorted by answer count to search result activity
		oPosts = freeTextAndTagSearch(textSearch,
				selectedTags, SearchResultSortingAlgorithm.AnswerCountAlgotithm);
		oIntent.putParcelableArrayListExtra(SearchResultActivity.KEY_POSTS_ANSWER_COUNT,
				(ArrayList<? extends Parcelable>) oPosts);

		// pass posts sorted by user reputation to search result activity
		oPosts = freeTextAndTagSearch(textSearch,
				selectedTags,
				SearchResultSortingAlgorithm.UserReputationAlgorithm);
		oIntent.putParcelableArrayListExtra(SearchResultActivity.KEY_POSTS_USER_REPUTATION,
				(ArrayList<? extends Parcelable>) oPosts);
		
		// pass free text
		oIntent.putExtra(SearchResultActivity.KEY_FREE_TEXT, textSearch);
		
		// pass tags
		oIntent.putStringArrayListExtra(SearchResultActivity.KEY_TAGS, selectedTags);

		con.startActivity(oIntent);
	}

	/**
	 * Gets a user by id
	 * 
	 * @param id
	 *            user id
	 * @return found user
	 */
	public User getUser(int id) {
		dbAdapter.open();
		return dbAdapter.getUser(id);
	}
}
