package project.sarah.mobile_lab_3;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by sarah on 12/8/17.
 */

public class Conversation extends AppCompatActivity {

    int from_Where_I_Am_Coming = 0;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private final int CHECK_CODE = 0x1;

    private DBHelper mydb;
    TextView english;
    TextView korean;
    private Speaker speaker;

    int id_To_Update = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        english = (TextView) findViewById(R.id.ed_kor);
        korean = (TextView) findViewById(R.id.ed_kor);
        mydb = new DBHelper(this);

        checkTTS();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            final int Value = extras.getInt("id");
            if (Value > 0) {
                Cursor rs = mydb.getData(Value);
                id_To_Update = Value;
                rs.moveToFirst();
                String eng = rs.getString(rs.getColumnIndex(DBHelper.CONV_COLUMN_ENG));
                String kor = rs.getString(rs.getColumnIndex(DBHelper.CONV_COLUMN_KOR));

                if (!rs.isClosed()) {
                    rs.close();
                }

                Button b = (Button) findViewById(R.id.btn_save);
                b.setVisibility(View.INVISIBLE);

                english.setText((CharSequence) eng);
                english.setFocusable(false);
                english.setClickable(false);

                korean.setText((CharSequence) kor);
                korean.setFocusable(false);
                korean.setClickable(false);

                Button btn_speak=(Button)findViewById(R.id.btn_speak);
                btn_speak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Cursor cursor = mydb.getData(Value);
                        cursor.moveToFirst();
                        String temp = cursor.getString(2);
                        speaker.allow(true);
                        speaker.speak(temp);
                    }
                });
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int Value = extras.getInt("id");
            if (Value > 0) {
                getMenuInflater().inflate(R.menu.display_contact, menu);
            } else {
                getMenuInflater().inflate(R.menu.mainmenu, menu);
            }
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.Edit_Conversation:
                Button b = (Button)findViewById(R.id.btn_save);
                b.setVisibility(View.VISIBLE);
                english.setEnabled(true);
                english.setFocusableInTouchMode(true);
                english.setClickable(true);
                korean.setEnabled(true);
                korean.setFocusableInTouchMode(true);
                korean.setClickable(true);

                return true;

            case R.id.Delete_Conversation:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteConversation)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mydb.deleteConversation(id_To_Update);
                                Toast.makeText(getApplicationContext(), "Deleted	Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), project.sarah.mobile_lab_3.MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new
                                DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });

                AlertDialog d = builder.create();
                d.setTitle("Are you sure");
                d.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void run(View view) {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            int Value = extras.getInt("id");

            if (Value > 0) {
                if (mydb.updateConversation(id_To_Update, english.getText().toString(), korean.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), project.sarah.mobile_lab_3.MainActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (mydb.insertConversation(english.getText().toString(), korean.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "not done", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getApplicationContext(), project.sarah.mobile_lab_3.MainActivity.class);
                startActivity(intent);

            }

        }

    }

    private void startVoiceRead(String sentence) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, sentence);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }


    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHECK_CODE){
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                speaker = new Speaker(this);
            }else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }

}