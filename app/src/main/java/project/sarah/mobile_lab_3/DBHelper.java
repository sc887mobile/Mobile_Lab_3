package project.sarah.mobile_lab_3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sarah on 12/8/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "conversation.db";
    public static final String CONV_TABLE_NAME = "conversation";
    public static final String CONV_COLUMN_ID = "id";
    public static final String CONV_COLUMN_ENG = "english";
    public static final String CONV_COLUMN_KOR = "korean";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table conversation " + "(id integer primary key, english text,korean text)"
        );
        System.out.println("db created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS conversation");

        onCreate(db);

    }

    public boolean insertConversation(String english, String korean) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("english", english);
        contentValues.put("korean", korean);
        db.insert("conversation", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from conversation where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONV_TABLE_NAME);

        return numRows;
    }

    public boolean updateConversation(Integer id, String english, String korean) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values  = new ContentValues();
        values.put("english"  , english);
        values.put("korean", korean);
        db.update("conversation" ,  values ,"id=?",new String[] {id.toString()});
        return true;
    }

    public Integer deleteConversation(Integer id) {
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete("conversation", "id=?",new String[] {id.toString()});
        return 0;
    }

    public ArrayList getAllConversations()
    {
        ArrayList array_list = new ArrayList();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from conversation", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(CONV_COLUMN_ENG)));
            res.moveToNext();
        }
        return array_list;
    }
}