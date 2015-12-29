package com.smartdev.pusher.chatsimple;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends CustomActivity {

    private static final String TAG = "MainActivity";

    private Pusher pusher = new Pusher("a23a160d93f93ade6019");
    private Channel channel = pusher.subscribe("test_channel");

    private ArrayList<Conversation> convList;
    private ChatAdapter adp;

    private EditText txt;
    private String buddy = FakeUser.username;
    private Date lastMsgDate;
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        convList = new ArrayList<Conversation>();
        ListView list = (ListView) findViewById(R.id.list);
        adp = new ChatAdapter();
        list.setAdapter(adp);
        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setStackFromBottom(true);

        txt = (EditText) findViewById(R.id.txt);
        txt.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        setTouchNClick(R.id.btnSend);

        getActionBar().setTitle("Chat Room");

        channel.bind("my_event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(data);

                            Conversation c = new Conversation(jsonObject
                                    .getString("username"), jsonObject.getString("message"), jsonObject.getString("date"));
                            convList.add(c);
                            adp.notifyDataSetChanged();
                        } catch (JSONException ex) {
                            Log.e(TAG, ex.getMessage());
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        pusher.connect();
    }

    /* (non-Javadoc)
         * @see com.socialshare.custom.CustomFragment#onClick(android.view.View)
         */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btnSend) {
            sendMessage();
        }

    }

    private void sendMessage()
    {
        if (txt.length() == 0)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);

        String s = txt.getText().toString();
        txt.setText(null);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("username", FakeUser.username);
        params.put("message", s);
        params.put("date", String.valueOf(new Date().getTime()));
        client.post(this, "https://pusherchatsimple.herokuapp.com/chat/add", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "------=----");
                Log.d(TAG, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, "------=----");
                Log.d(TAG, responseString);
            }
        });
    }

    /**
     * The Class ChatAdapter is the adapter class for Chat ListView. This
     * adapter shows the Sent or Receieved Chat message in each list item.
     */
    private class ChatAdapter extends BaseAdapter {

        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return convList.size();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Conversation getItem(int arg0) {
            return convList.get(arg0);
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int pos, View v, ViewGroup arg2) {
            Conversation c = getItem(pos);
            if (c.isSent())
                v = getLayoutInflater().inflate(R.layout.chat_item_sent, null);
            else
                v = getLayoutInflater().inflate(R.layout.chat_item_rcv, null);

            TextView lbl = (TextView) v.findViewById(R.id.lbl1);
            lbl.setText(DateUtils.getRelativeDateTimeString(MainActivity.this, Long.parseLong(c
                            .getDate()), DateUtils.SECOND_IN_MILLIS,
                    DateUtils.DAY_IN_MILLIS, 0));

            lbl = (TextView) v.findViewById(R.id.lbl2);
            lbl.setText(c.getMessage());

            lbl = (TextView) v.findViewById(R.id.lbl3);
            if (c.isSent()) {
                lbl.setText("Delivered");
            } else
                lbl.setText("");

            return v;
        }

    }

}
