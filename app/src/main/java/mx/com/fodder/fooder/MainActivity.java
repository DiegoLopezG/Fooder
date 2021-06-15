package mx.com.fodder.fooder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import mx.com.fodder.fooder.Cards.arrayAdapter;
import mx.com.fodder.fooder.Cards.cards;

import static android.widget.Toast.makeText;

//VIDEO 8 NO SE NECESITO POR EL MOMENTO

public class MainActivity extends AppCompatActivity {

    private cards cards_data[];
    private mx.com.fodder.fooder.Cards.arrayAdapter arrayAdapter;
    private int i;
    private int min;

    private FirebaseAuth mAuth;
    private String currentUID;
    private DatabaseReference usersDB;
    private ArrayList<String> likedRestaurants;

    ListView listView;
    List<cards> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersDB = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();

        likedRestaurants = new ArrayList<>();

        checkUserMode();

        rowItems = new ArrayList<cards>();
        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        final SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                cards objCards = (cards) dataObject;
                String userID = objCards.getUserId();
                usersDB.child(currentUID).child("Conexiones").child("Rechazado").child(userID).setValue(true);
                usersDB.child(userID).child("Conexiones").child("Rechazado").child(currentUID).setValue(true);
                makeText(MainActivity.this, "Rechazado", Toast.LENGTH_SHORT).show();

                //estp es nuevo
                checkUserMode();
            }

            @Override
            public void onRightCardExit(final Object dataObject) {
                cards objCards = (cards) dataObject;
                final String userID = objCards.getUserId();
                //AQUI VAMOS A CREAR IDENTIFICADOR DEL CHILD CHAT
                String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                //current user
                DatabaseReference userDB = usersDB.child(objCards.getUserId());
                userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        likedRestaurants.add(dataSnapshot.child("Specs").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
            });
                //usersDB.child(currentUID).child("Conexiones").child("Aceptado").child(userID).setValue(true);
                usersDB.child(currentUID).child("Conexiones").child("Aceptado").child(userID).child("ChatID").setValue(key);

                //card user
                //usersDB.child(userID).child("Conexiones").child("Aceptado").child(currentUID).setValue(true);
                usersDB.child(userID).child("Conexiones").child("Aceptado").child(currentUID).child("ChatID").setValue(key);
                for (int i = 0; i<likedRestaurants.size(); i++){
                    makeText(MainActivity.this, likedRestaurants.get(i), Toast.LENGTH_SHORT).show();
                }
                makeText(MainActivity.this, "Aceptado", Toast.LENGTH_SHORT).show();

                //esto es nuevo
                //usersDB.r
                checkUserMode();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                makeText(MainActivity.this, "Click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getminimo(){
        int count = 0;
        String rest = "";
        final ArrayList<String> remLikedRestaurants = new ArrayList<>();
        min = likedRestaurants.size();
        if (!likedRestaurants.isEmpty()){
            remLikedRestaurants.addAll(likedRestaurants);
            rest = likedRestaurants.get(0);
            Collections.sort(likedRestaurants);
        }
        for(String restaurant : likedRestaurants) {
            if(restaurant.equals(rest)) {
                count++;
            } else {
                if(count < min) {
                    min = count;
                }
                count = 1;
                rest = restaurant;
            }
        }
        if(count < min) {
            min = count;
        }
    }

    private String userMode;
    private String oppositeUserMode;
    public void checkUserMode(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDB = usersDB.child(user.getUid());

/*        if (usersDB.child("Conexiones").equals(null)){
            userDB = usersDB.child("Nombre");
        }*/

        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                        if (dataSnapshot.child("UserMode").getValue() != null){
                            userMode = dataSnapshot.child("UserMode").getValue().toString();
                            Toast.makeText(MainActivity.this, userMode, Toast.LENGTH_SHORT).show();
                            switch (userMode){
                                case "Cliente":
                                    oppositeUserMode = "Restaurante";
                                    Toast.makeText(MainActivity.this, oppositeUserMode, Toast.LENGTH_SHORT).show();
                                    break;
                                case "Restaurante":
                                    oppositeUserMode = "Cliente";
                                    break;
                            }
                            //getOppositeModeUser();
                        }
                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //userDBaqui abajo
        usersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getminimo();
                ArrayList<String> remLikedRestaurants = new ArrayList<>();
                if (!likedRestaurants.isEmpty()){
                    remLikedRestaurants.addAll(likedRestaurants);
                }

                int i = 0;
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if (i < min){
                        if (!dataSnapshot1.hasChild("Conexiones")){
                            remLikedRestaurants.add(dataSnapshot1.child("Specs").getValue().toString());
                            makeText(MainActivity.this, "specs::"+dataSnapshot1.child("Specs").getValue().toString(), Toast.LENGTH_SHORT).show();
                            i++;
                        }
                    } else {
                        break;
                    }
                }
                Random random = new Random();
                String restaurantType;

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if (!remLikedRestaurants.isEmpty()){
                        restaurantType = remLikedRestaurants.get(random.nextInt(remLikedRestaurants.size()));
                        if (dataSnapshot1.child("Specs").getValue().equals(restaurantType) && !dataSnapshot1.hasChild("Conexiones")){
                            String profileImageURL = "default";
                            if (!dataSnapshot1.child("ImagenPerfilURL").getValue().toString().equals("default")) {
                                profileImageURL = dataSnapshot.child("ImagenPerfilURL").getValue().toString();
                            }
                            String instagram = "none";
                            if (!dataSnapshot1.child("Instagram").getValue().toString().equals("none")) {
                                instagram = dataSnapshot1.child("Instagram").getValue().toString();
                            }
                            for (int e = 0; e < remLikedRestaurants.size(); e++){
                                makeText(MainActivity.this, "forlike #"+e+" "+remLikedRestaurants.get(e), Toast.LENGTH_SHORT).show();
                            }
                            cards item = new cards(dataSnapshot1.getKey(), dataSnapshot1.child("Nombre").getValue().toString(), profileImageURL, instagram);
                            rowItems.clear();
                            rowItems.add(item);
                            arrayAdapter.notifyDataSetChanged();
                            break;
                        }
                    }else{
                        if (!dataSnapshot1.hasChild("Conexiones")){
                            String profileImageURL = "default";
                            if (!dataSnapshot1.child("ImagenPerfilURL").getValue().toString().equals("default")) {
                                profileImageURL = dataSnapshot.child("ImagenPerfilURL").getValue().toString();
                            }
                            String instagram = "none";
                            if (!dataSnapshot1.child("Instagram").getValue().toString().equals("none")) {
                                instagram = dataSnapshot1.child("Instagram").getValue().toString();
                            }
                            for (int e = 0; e < remLikedRestaurants.size(); e++){
                                makeText(MainActivity.this, "liked"+remLikedRestaurants.get(e), Toast.LENGTH_SHORT).show();
                            }
                            cards item = new cards(dataSnapshot1.getKey(), dataSnapshot1.child("Nombre").getValue().toString(), profileImageURL, instagram);
                            rowItems.clear();
                            rowItems.add(item);
                            arrayAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }

                /*for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    makeText(MainActivity.this, dataSnapshot1.getKey(), Toast.LENGTH_SHORT).show();
                    if (dataSnapshot1.exists()){
                        if (dataSnapshot1.child("UserMode").getValue() != null){

                            makeText(MainActivity.this, "ONCHUILDADDED", Toast.LENGTH_SHORT).show();
                            //AQUI SE TIENE QUE MODIFICAR DESPUES YA QUE CHECA RECHAZADO O ACEPTADO
                            if (dataSnapshot1.child("UserMode").getValue() != null) {
                                makeText(MainActivity.this, "ENTROUSERMODE", Toast.LENGTH_SHORT).show();
                                if (dataSnapshot1.exists() && !dataSnapshot1.child("Conexiones").child("Rechazado").hasChild(currentUID) && !dataSnapshot1.child("Conexiones").child("Aceptado").hasChild(currentUID) && dataSnapshot1.child("UserMode").getValue().toString().equals(oppositeUserMode)) {
                                    makeText(MainActivity.this, "ENTROCONEXIONESS", Toast.LENGTH_SHORT).show();
                                    if (!likedRestaurants.isEmpty()){
                                        makeText(MainActivity.this, "ENTROOOOOOO", Toast.LENGTH_SHORT).show();
                                        for (int a =0; a < min && dataSnapshot1.exists(); a++){
                                            remLikedRestaurants.add(dataSnapshot1.child("Specs").getValue().toString());
                                        }
                                        restaurantType = remLikedRestaurants.get(random.nextInt(remLikedRestaurants.size()));
                                        if (dataSnapshot1.child("Specs").getValue().equals(restaurantType)){
                                            String profileImageURL = "default";
                                            if (!dataSnapshot1.child("ImagenPerfilURL").getValue().toString().equals("default")) {
                                                profileImageURL = dataSnapshot.child("ImagenPerfilURL").getValue().toString();
                                            }
                                            String instagram = "none";
                                            if (!dataSnapshot1.child("Instagram").getValue().toString().equals("none")) {
                                                instagram = dataSnapshot1.child("Instagram").getValue().toString();
                                            }
                                            for (int e = 0; e < remLikedRestaurants.size(); e++){
                                                makeText(MainActivity.this, remLikedRestaurants.get(e), Toast.LENGTH_SHORT).show();
                                            }
                                            cards item = new cards(dataSnapshot1.getKey(), dataSnapshot1.child("Nombre").getValue().toString(), profileImageURL, instagram);
                                            rowItems.add(item);
                                            arrayAdapter.notifyDataSetChanged();

                                        }
                                    } else {
                                        String profileImageURL = "default";
                                        if (!dataSnapshot1.child("ImagenPerfilURL").getValue().toString().equals("default")) {
                                            profileImageURL = dataSnapshot1.child("ImagenPerfilURL").getValue().toString();
                                        }
                                        String instagram = "none";
                                        if (!dataSnapshot1.child("Instagram").getValue().toString().equals("none")) {
                                            instagram = dataSnapshot1.child("Instagram").getValue().toString();
                                        }
                                        cards item = new cards(dataSnapshot1.getKey(), dataSnapshot1.child("Nombre").getValue().toString(), profileImageURL, instagram);
                                        rowItems.add(item);
                                        arrayAdapter.notifyDataSetChanged();
                                        makeText(MainActivity.this, "ELSEEEEEEEE", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }
                }*/




                    /*if (dataSnapshot.exists()){
                        if (dataSnapshot.child("UserMode").getValue() != null){
                            userMode = dataSnapshot.child("UserMode").getValue().toString();
                            Toast.makeText(MainActivity.this, userMode, Toast.LENGTH_SHORT).show();
                            switch (userMode){
                                case "Cliente":
                                    oppositeUserMode = "Restaurante";
                                    Toast.makeText(MainActivity.this, oppositeUserMode, Toast.LENGTH_SHORT).show();
                                    break;
                                case "Restaurante":
                                    oppositeUserMode = "Cliente";
                                    break;
                            }
                            getOppositeModeUser();
                        }
                    }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getOppositeModeUser(){
        int count = 0;
        String rest = "";
        final ArrayList<String> remLikedRestaurants = new ArrayList<>();
        min = likedRestaurants.size();
        if (!likedRestaurants.isEmpty()){
            remLikedRestaurants.addAll(likedRestaurants);
            rest = likedRestaurants.get(0);
            Collections.sort(likedRestaurants);
        }
        for(String restaurant : likedRestaurants) {
            if(restaurant.equals(rest)) {
                count++;
            } else {
                if(count < min) {
                    min = count;
                }
                count = 1;
                rest = restaurant;
            }
        }
        if(count < min) {
            min = count;
        }

        usersDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                makeText(MainActivity.this, "ONCHUILDADDED", Toast.LENGTH_SHORT).show();
                Random random = new Random();
                String restaurantType;
                //AQUI SE TIENE QUE MODIFICAR DESPUES YA QUE CHECA RECHAZADO O ACEPTADO
                if (dataSnapshot.child("UserMode").getValue() != null) {
                    if (dataSnapshot.exists() && !dataSnapshot.child("Conexiones").child("Rechazado").hasChild(currentUID) && !dataSnapshot.child("Conexiones").child("Aceptado").hasChild(currentUID) && dataSnapshot.child("UserMode").getValue().toString().equals(oppositeUserMode)) {
                        if (!likedRestaurants.isEmpty()){
                            makeText(MainActivity.this, "ENTROOOOOOO", Toast.LENGTH_SHORT).show();
                            for (int i =0; i < min && dataSnapshot.exists(); i++){
                                remLikedRestaurants.add(dataSnapshot.child("Specs").getValue().toString());
                            }
                            restaurantType = remLikedRestaurants.get(random.nextInt(remLikedRestaurants.size()));
                            if (dataSnapshot.child("Specs").getValue().equals(restaurantType)){
                                String profileImageURL = "default";
                                if (!dataSnapshot.child("ImagenPerfilURL").getValue().toString().equals("default")) {
                                    profileImageURL = dataSnapshot.child("ImagenPerfilURL").getValue().toString();
                                }
                                String instagram = "none";
                                if (!dataSnapshot.child("Instagram").getValue().toString().equals("none")) {
                                    instagram = dataSnapshot.child("Instagram").getValue().toString();
                                }
                                for (int e = 0; e < remLikedRestaurants.size(); e++){
                                    makeText(MainActivity.this, remLikedRestaurants.get(e), Toast.LENGTH_SHORT).show();
                                }
                                cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("Nombre").getValue().toString(), profileImageURL, instagram);
                                rowItems.add(item);
                                arrayAdapter.notifyDataSetChanged();

                            }
                        } else {
                            String profileImageURL = "default";
                            if (!dataSnapshot.child("ImagenPerfilURL").getValue().toString().equals("default")) {
                                profileImageURL = dataSnapshot.child("ImagenPerfilURL").getValue().toString();
                            }
                            String instagram = "none";
                            if (!dataSnapshot.child("Instagram").getValue().toString().equals("none")) {
                                instagram = dataSnapshot.child("Instagram").getValue().toString();
                            }
                            cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("Nombre").getValue().toString(), profileImageURL, instagram);
                            rowItems.add(item);
                            arrayAdapter.notifyDataSetChanged();
                            makeText(MainActivity.this, "ELSEEEEEEEE", Toast.LENGTH_SHORT).show();
                        }
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
    };

    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, ChooseLoginRegistration.class);
        startActivity(intent);
        finish();
        return;
    }

    public void settings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        Toast.makeText(this, userMode, Toast.LENGTH_SHORT).show();
        startActivity(intent);
        return;
    }

    public void meGusta(View view) {
        Intent intent = new Intent(MainActivity.this, GustaActivity.class);
        Toast.makeText(this, userMode, Toast.LENGTH_SHORT).show();
        startActivity(intent);
        return;
    }

}
