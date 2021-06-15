package mx.com.fodder.fooder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ChooseLoginRegistration extends AppCompatActivity {

    private ImageButton mlogin;
    private ImageButton mregistro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_registration);

        mlogin = (ImageButton) findViewById(R.id.login);
        mregistro = (ImageButton) findViewById(R.id.registro);

        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseLoginRegistration.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseLoginRegistration.this, RegistroActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

    }
}