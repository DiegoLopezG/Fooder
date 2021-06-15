package mx.com.fodder.fooder.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.com.fodder.fooder.R;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private EditText mSendEditText;
    private ImageButton mSendButton;

    private String currentUserID, gustaID, chatID;
    private DatabaseReference databaseReferenceUser, databaseReferenceChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        gustaID = getIntent().getExtras().getString("gustaID");

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Conexiones").child("Aceptado").child(gustaID).child("ChatID");
        databaseReferenceChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        getChatID();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendButton = (ImageButton) findViewById(R.id.send);
        mSendEditText = (EditText) findViewById(R.id.message);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    private void sendMessage() {
        String textMessage = mSendEditText.getText().toString();
        if (!textMessage.isEmpty()){
            DatabaseReference newMessageDB = databaseReferenceChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserID);
            newMessage.put("text", textMessage);

            newMessageDB.setValue(newMessage);
        }
        mSendEditText.setText(null);
    }

    private void getChatID(){
        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    chatID = dataSnapshot.getValue().toString();
                    databaseReferenceChat = databaseReferenceChat.child(chatID);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getChatMessages() {
        databaseReferenceChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    String message = null;
                    String createdByUser = null;

                    if (dataSnapshot.child("text").getValue() != null){
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if (dataSnapshot.child("createdByUser").getValue() != null){
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }
                    if (message != null && createdByUser != null){
                        Boolean currentUserB = false;
                        if (createdByUser.equals(currentUserID)){
                            currentUserB = true;
                        }
                        ChatObject newMessage = new ChatObject(message, currentUserB);
                        resultChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<ChatObject> resultChat = new ArrayList<ChatObject>();
    private List<ChatObject> getDataSetChat() {
        return resultChat;
    }
}