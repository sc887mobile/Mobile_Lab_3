package project.sarah.mobile_lab_3;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Locale;
import com.google.cloud.translate.*;

public class MainActivity extends AppCompatActivity {

    private ListView obj;
    private Speaker speaker;
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DBHelper(this);
        if(mydb.numberOfRows()==0) {
            mydb.insertConversation("hello", "안녕하세요");
            mydb.insertConversation("Thank you", "감사합니다");
            mydb.insertConversation("bye", "안녕히가세요");
            mydb.insertConversation("how much is this?", "이것은 얼마인가요?");
        }

        ArrayList array_list = mydb.getAllConversations();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array_list);
        obj = (ListView) findViewById(R.id.listView1);
        obj.setAdapter(arrayAdapter);
        obj.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                int id_To_Search = arg2 + 1;
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);

                ////speak
                Intent intent = new Intent(getApplicationContext(), project.sarah.mobile_lab_3.Conversation.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);

        return true;
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId())

        {
            case R.id.Add:
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", 0);
                Intent intent = new Intent(getApplicationContext(), project.sarah.mobile_lab_3.Conversation.class);
                intent.putExtras(dataBundle);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public boolean onKeyDown(int keycode, KeyEvent event) {

        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }

        return super.onKeyDown(keycode, event);
    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent intentBack=new Intent(MainActivity.this, TransActivity.class);
        startActivity(intentBack);
    }

}
