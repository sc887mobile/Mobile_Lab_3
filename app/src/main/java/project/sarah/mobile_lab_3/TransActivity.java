package project.sarah.mobile_lab_3;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sarah on 12/13/17.
 */

public class TransActivity extends AppCompatActivity {

    private static final String API_KEY = "AIzaSyB_6TCNNFtI6k14Hruun2_exSNl8KcGirk";

    private final int CHECK_CODE = 0x1;
    private Speaker speaker;

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv, mTranseOutputTv;
    private Button mSpeakBtn;
    private static final String NAME = "name";
    private String transed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);

        mVoiceInputTv = (TextView)findViewById(R.id.tv_input);
        mTranseOutputTv = (TextView)findViewById(R.id.tv_output);
        mSpeakBtn = (Button)findViewById(R.id.btn_speak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceInput();
            }
        });

        Button btn_movetosample = (Button)findViewById(R.id.btn_gotosample);
        btn_movetosample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveIntent=new Intent(TransActivity.this, MainActivity.class);
                startActivity(moveIntent);
            }
        });

        checkTTS();


    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, What sentance do you want to translate to KOREAN?");
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
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CHECK_CODE: {
                if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                    speaker = new Speaker(this);
                }else {
                    Intent install = new Intent();
                    install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(install);
                }
                break;
            }
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mVoiceInputTv.setText(result.get(0));
                    final String temp=result.get(0);

                    try{
                        transed=new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... voids) {
                                TranslateOptions options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
                                Translate translate = options.getService();
                                final Translation translation = translate.translate(temp,Translate.TranslateOption.targetLanguage("ko"));
                                transed=translation.getTranslatedText();
                                return transed;
                            }
                        }.execute().get();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    //System.out.println("AAAAA  "+transed);
                    mTranseOutputTv.setText(transed);
                    speaker.allow(true);
                    speaker.speak(transed);

                }
                break;
            }
        }
    }
}
