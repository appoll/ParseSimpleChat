package paul.antton.parsesimplechat;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends ActionBarActivity {

    private static final String TAG = ChatActivity.class.getName();
    private static String sUserId;

    private Handler handler = new Handler();

    private static final String USER_ID_KEY = "userId";
    private static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    private EditText messageInput;
    private Button sendButton;

    private ListView chatList;
    private ArrayList<Message> mMessages;
    private ChatListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (ParseUser.getCurrentUser() != null)
        {
            startWithCurrentUser();
        }
        else
        {
            login();
        }

        handler.postDelayed(runnable, 100);
    }

    private Runnable runnable = new Runnable()
    {
        @Override
        public void run() {
            refreshMessages();
            handler.postDelayed(this,100);
        }
    };

    private void refreshMessages() {
        receiveMessage();
    }

    private void startWithCurrentUser ()
    {
        sUserId = ParseUser.getCurrentUser().getObjectId();
        setupMessagePosting();
    }

    private void setupMessagePosting()
    {
        messageInput = (EditText) findViewById(R.id.etMessage);
        sendButton = (Button) findViewById(R.id.btSend);
        chatList = (ListView) findViewById(R.id.lvChat);
        mMessages = new ArrayList<Message>();
        mAdapter = new ChatListAdapter(ChatActivity.this, sUserId, mMessages);
        chatList.setAdapter(mAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = messageInput.getText().toString();
                Message message = new Message();
                message.put(USER_ID_KEY, sUserId);
                message.put("body", input);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        receiveMessage();
                    }
                });
                messageInput.setText("");
            }
        });
    }

    public void receiveMessage()
    {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if (e == null)
                {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged();
                    chatList.invalidate();
                }
                else
                {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });
    }


    private void login()
    {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e != null)
                {
                    Log.d(TAG, "Anonymus login failed");
                }
                else
                {
                    startWithCurrentUser();
                }
            }
        });
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
}
