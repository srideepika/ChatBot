package com.bradleybossard.androidprogramabdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ChatActivity extends ActionBarActivity {

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private static final String TAG = "MainActivity";

    String botname = "super",request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initControls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText("INTBOT");
        File fileExt = new File(getExternalFilesDir(null).getAbsolutePath() + "/bots");
        if (!fileExt.exists()) {
            ZipFileExtraction extract = new ZipFileExtraction();

            try {
                extract.unZipIt(getAssets().open("bots.zip"), getExternalFilesDir(null).getAbsolutePath() + "/");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        load();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);
                request=messageET.getText().toString();
                messageET.setText("");

                displayMessage(chatMessage);
                new ask().execute();


            }
        });


    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void load(){






        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        request="Who are you?";
        new ask().execute();



    }


    public class ask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            final String path = getExternalFilesDir(null).getAbsolutePath();

            Bot bot = new Bot(botname, path);
            Chat chatSession = new Chat(bot);

            //String request = "What is your name?";
            String response = chatSession.multisentenceRespond(request);

            Log.v(TAG, "response = " + response);
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response.isEmpty()) {
                response = "There is no response";
            }
            ChatMessage msg1 = new ChatMessage();
            msg1.setId(2);
            msg1.setMe(false);
            msg1.setMessage(response);
            msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
            displayMessage(msg1);

        }
    }



}
