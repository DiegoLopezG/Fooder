package mx.com.fodder.fooder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mx.com.fodder.fooder.R;

public class GustaActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mGustaAdapter;
    private RecyclerView.LayoutManager mGustaLayoutManager;

    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gusta);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mGustaLayoutManager = new LinearLayoutManager(GustaActivity.this);
        mRecyclerView.setLayoutManager(mGustaLayoutManager);
        mGustaAdapter = new GustaAdapter(getDataSetGusta(), GustaActivity.this);
        mRecyclerView.setAdapter(mGustaAdapter);

        getUserGustaID();

    }

    private void getUserGustaID() {
        DatabaseReference gustaDB = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Conexiones").child("Aceptado");
        gustaDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot gusta : dataSnapshot.getChildren()){
                        fetchGustaInformation(gusta.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchGustaInformation(String key) {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String userID = dataSnapshot.getKey();
                    String name = "";
                    String ImagenPerfilURL = "";

                    if (dataSnapshot.child("Nombre").getValue() != null){
                        name = dataSnapshot.child("Nombre").getValue().toString();
                    }

                    if (dataSnapshot.child("ImagenPerfilURL").getValue() != null){
                        ImagenPerfilURL = dataSnapshot.child("ImagenPerfilURL").getValue().toString();
                    }

                    GustaObject object = new GustaObject(userID, name, ImagenPerfilURL);
                    resultGusta.add(object);
                    mGustaAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<GustaObject> resultGusta = new ArrayList<GustaObject>();
    private List<GustaObject> getDataSetGusta() {
        return resultGusta;
    }
}