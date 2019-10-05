package jupiter.amirahmed.com.jupiterchat.Activites;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import jupiter.amirahmed.com.jupiterchat.Adapters.ChatAdapter;
import jupiter.amirahmed.com.jupiterchat.Models.ChatMessage;
import jupiter.amirahmed.com.jupiterchat.R;

public class ContinueChatActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ChatAdapter adapter;

    EditText editText;

    ImageView sendbutton;

    RecyclerView recyclerView;

    String username,recivername,userImage,reciverImage,conversationname;

    TextView seen;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue);

        editText = findViewById(R.id.edittext);
        sendbutton = findViewById(R.id.sendbutton);

        seen = findViewById(R.id.seen);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        conversationname = extras.getString("ConversationName");
        username = extras.getString("Sender");
        recivername = extras.getString("Receiver");
        userImage = extras.getString("UserImage");
        reciverImage = extras.getString("ReceiverImage");

        setUpRecyclerView();

        RealtimeSeen();

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!editText.getText().toString().trim().isEmpty())
                {
                    sendMessage(editText.getText().toString().trim());
                }
            }
        });
    }

    public void sendMessage(final String message)
    {
        if(!message.isEmpty())
        {

            List<String> seenArray = new ArrayList<>();
            seenArray.add(username);

            Map<String, Object> messages = new HashMap<>();
            messages.put("sender", username);
            messages.put("senderImage", userImage);
            messages.put("seenArray",seenArray);
            messages.put("message", message);
            messages.put("timeStamp", FieldValue.serverTimestamp());

            db.collection("Conversations").document(conversationname).collection("Messages").document().set(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                    editText.setText("");
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                    updateChat(message);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }

    }



    public void updateChat(String last)
    {

        Map<String, Object> conversation = new HashMap<>();
        conversation.put("name", username +"-"+recivername);
        conversation.put("sender", username);
        conversation.put("receiver", recivername);
        conversation.put("receiverImage", reciverImage);
        conversation.put("lastMessage", last);
        conversation.put("timeStamp", FieldValue.serverTimestamp());

        db.collection("Conversations").document(conversationname).set(conversation);

    }


    public void RealtimeSeen()
    {

        Query query = db.collection("Conversations").document(conversationname).collection("Messages").orderBy("timeStamp", Query.Direction.DESCENDING).limit(1);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    showMessage("Listen failed.");
                    return;
                }

                if (snapshot != null) {

                    @SuppressWarnings (value="unchecked")
                    List<String> group = (List<String>) snapshot.getDocuments().get(0).get("seenArray");
                    assert group != null;

                    if(!group.get(0).equals(username))
                    {

                        List<String> seenArray = new ArrayList<>();
                        seenArray.add(group.get(0));
                        seenArray.add(username);

                        Map<String, Object> users = new HashMap<>();
                        users.put("seenArray",seenArray);


                        db.collection("Conversations").document(conversationname).collection("Messages").document(snapshot.getDocuments().get(0).getId()).update(users);

                    }


                    if(group.size()==2)
                    {
                        seen.setVisibility(View.VISIBLE);
                    }else
                    {
                        seen.setVisibility(View.GONE);
                    }


                } else {
                    showMessage("Current data: null");
                }
            }
        });
    }


    private void setUpRecyclerView() {

        Query query3 = db.collection("Conversations").document(conversationname).collection("Messages").orderBy("timeStamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query3, ChatMessage.class)
                .build();

        adapter = new ChatAdapter(options);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1)))
                {
                    recyclerView.scrollToPosition(positionStart);
                }

            }
        });


    }


    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(ContinueChatActivity.this , ConversationsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
