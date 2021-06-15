package mx.com.fodder.fooder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private EditText campoNombre, campoInstagram;
    private ImageButton botonGuardar, botonRegresar;
    private ImageView imagenPerfil;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsuarioBD;

    private String userID, name, instagram, imageURL, userMode;

    private Uri resultURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        campoNombre = (EditText) findViewById(R.id.nombre);
        campoInstagram = (EditText) findViewById(R.id.instagram);
        imagenPerfil = (ImageView) findViewById(R.id.imagenPerfil);
        botonGuardar = (ImageButton) findViewById(R.id.guardarDatos);
        botonRegresar = (ImageButton) findViewById(R.id.regresar);


        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mUsuarioBD = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        getUserInformation();
        imagenPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");//CAMBIAR POR ASTERISCO EN VEA DE JPG
                startActivityForResult(intent, 1);
            }
        });

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });
        
    }

    private void getUserInformation(){
        mUsuarioBD.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Nombre") != null){
                        name = map.get("Nombre").toString();
                        campoNombre.setText(name);
                    }
                    if (map.get("Instagram") != null){
                        instagram = map.get("Instagram").toString();
                        if (instagram.equals("none")){
                            campoInstagram.setText("");
                        }else {
                            campoInstagram.setText(instagram);
                        }
                    }
                    if (map.get("UserMode") != null){
                        userMode = map.get("UserMode").toString();
                    }
                    Glide.clear(imagenPerfil);
                    if (map.get("ImagenPerfilURL") != null){
                        imageURL = map.get("ImagenPerfilURL").toString();
                        switch (imageURL){
                            case "default":
                                Glide.with(getApplication()).load(R.mipmap.profilepicsmall).into(imagenPerfil);
                                break;
                            default:
                                Glide.with(getApplication()).load(imageURL).into(imagenPerfil);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void saveUserInformation() {
        name = campoNombre.getText().toString();
        instagram = campoInstagram.getText().toString();

        Map userInformation = new HashMap();
        userInformation.put("Nombre", name);
        userInformation.put("Instagram", instagram);

        mUsuarioBD.updateChildren(userInformation);

        if (resultURI != null){
            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("ImagenesPerfil").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultURI);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("ImagenPerfilURL", uri.toString());
                            mUsuarioBD.updateChildren(newImage);

                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            finish();
                            return;
                        }
                    });
                }
            });

        }
        //ImagenPerfilURL
        else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, data.getDataString(), Toast.LENGTH_LONG).show();
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            Toast.makeText(this, "if", Toast.LENGTH_SHORT).show();
            //final Uri imageURI = data.getData();
            resultURI = Uri.parse(data.getDataString());
            imagenPerfil.setImageURI(resultURI);

        }
    }
}