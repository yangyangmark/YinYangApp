package com.example.yinyangapp.database;

import java.io.IOException;
import java.util.ArrayList;

import com.example.yinyangapp.databaseentities.Comment;
import com.example.yinyangapp.databaseentities.DatabaseType;
import com.example.yinyangapp.databaseentities.User;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseAdapter 
{
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private SoData mDbHelper;

    public DatabaseAdapter(Context context) 
    {
        this.mContext = context;
        mDbHelper = new SoData(mContext);
    }

    public DatabaseAdapter createDatabase() throws SQLException 
    {
        try 
        {
            mDbHelper.createDataBase();
        } 
        catch (IOException mIOException) 
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DatabaseAdapter open() throws SQLException 
    {
        try 
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } 
        catch (SQLException mSQLException) 
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() 
    {
        mDbHelper.close();
    }

    
    private ArrayList<User> cursorToUsers(Cursor cursor) {
    	 ArrayList<User> users = new ArrayList<User>();
    	 if ( cursor != null)
         { while (cursor.moveToNext()) {
        	 User user = new User(cursor);
	         users.add(user);	}
         }
    	 return users;
     }
    
    private ArrayList<DatabaseType> cursorToArrayList(Cursor cursor)
    {
    		ArrayList<DatabaseType> list = new ArrayList<DatabaseType>();
    		if ( cursor != null ) {

				// returns different classes depending on the number of 
    			// columns in the cursor
    			switch (cursor.getColumnCount()) {
    			
    			// users table has 13 fields
    			case 13:
    				while (cursor.moveToNext()) {
    					User user = new User(cursor);
    			        list.add(user);
    				}
    				break;
    				
				// comments table has 6 fields
    			case 6:
    				while (cursor.moveToNext()) {
    					Comment comment = new Comment(cursor);
    			        list.add(comment);
    				}
    				break;
    			
				// posts table has 20 fields
    			case 20:
    				break;
				
				// votes table has 4 fields
    			case 4:
    				break;
    				
    			}
     }
     return list;
    }
    
     private Cursor getCursor(String sqlString) {
    	 try
         {
             Cursor mCur = mDb.rawQuery(sqlString, null);
             return mCur;
         }
         catch (SQLException mSQLException) 
         {
             Log.e(TAG, "getTestData >>"+ mSQLException.toString());
             throw mSQLException;
         }
     }
     
     public ArrayList<DatabaseType> getUsers()
     {
         Cursor cursor = this.getCursor("SELECT * FROM users LIMIT 20");
         return cursorToArrayList(cursor);
     }
     
     public User getUser(int id)
     {
         Cursor cursor = this.getCursor("SELECT * FROM users WHERE (id='"+id+"')");
         return cursorToUsers(cursor).get(0);
     }
     
     public ArrayList<DatabaseType> getComments()
     {
         Cursor cursor = this.getCursor("SELECT * FROM comments LIMIT 20");
         return cursorToArrayList(cursor);
     }
     
     public User getComment(int id)
     {
         Cursor cursor = this.getCursor("SELECT * FROM comments WHERE (id='"+id+"')");
         return cursorToUsers(cursor).get(0);
     }
}