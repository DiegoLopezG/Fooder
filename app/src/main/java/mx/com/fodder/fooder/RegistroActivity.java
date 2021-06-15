package mx.com.fodder.fooder;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import mx.com.fodder.fooder.entidades.ConexionSQLiteHelper;
import mx.com.fodder.fooder.utilidades.Utilidades;

public class RegistroActivity extends AppCompatActivity {

    private ImageButton botonRegistrar;
    private EditText campoCorreo, campoContra, campoNombre, campoSpecs;
    private RadioGroup mradioGroup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private String userid;

    private ConexionSQLiteHelper conn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        conn = new ConexionSQLiteHelper(this, "db_usuariofooder", null, 1);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        botonRegistrar = (ImageButton) findViewById(R.id.registrar);
        campoCorreo = (EditText) findViewById(R.id.correo);
        campoContra = (EditText) findViewById(R.id.contra);
        mradioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        campoNombre = (EditText) findViewById(R.id.nombre);
        campoSpecs = (EditText) findViewById(R.id.specs);

        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idbutton = mradioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = (RadioButton) findViewById(idbutton);
                if (radioButton.getText() == null)
                {
                    return;
                }

                final String correo = campoCorreo.getText().toString();
                final String contra = campoContra.getText().toString();
                final String nombre = campoNombre.getText().toString();
                final String specs = campoSpecs.getText().toString();
                final String useridbd;

                mAuth.createUserWithEmailAndPassword(correo, contra).addOnCompleteListener(RegistroActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(RegistroActivity.this, "Error al completar el registro", Toast.LENGTH_SHORT).show();
                            Toast.makeText(RegistroActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            userid = mAuth.getCurrentUser().getUid();
                            Toast.makeText(RegistroActivity.this, userid, Toast.LENGTH_LONG).show();
                            DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                            //useridbd = currentUserDB.get;

                            Map userInfo = new HashMap<>();
                            userInfo.put("Nombre", nombre);
                            userInfo.put("UserMode", radioButton.getText().toString());
                            userInfo.put("ImagenPerfilURL", "default");
                            userInfo.put("Instagram", "none");
                            userInfo.put("Specs", specs);
                            currentUserDB.updateChildren(userInfo);

                            Toast.makeText(RegistroActivity.this, "aqui: " + userid, Toast.LENGTH_LONG).show();
                            SQLiteDatabase db = conn.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(Utilidades.CAMPO_ID, userid);
                            values.put(Utilidades.CAMPO_NOMBRE, nombre);
                            values.put(Utilidades.CAMPO_CORREO, correo);

                            Long idResultante = db.insert(Utilidades.TABLA_USUARIO, Utilidades.CAMPO_ID, values);
                            Toast.makeText(RegistroActivity.this, "id registro: " + idResultante, Toast.LENGTH_SHORT).show();
                            db.close();
                        }


                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}