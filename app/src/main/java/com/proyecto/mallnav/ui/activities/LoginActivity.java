package com.proyecto.mallnav.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.proyecto.mallnav.R;
import com.proyecto.mallnav.ui.fragments.RegistroFragment;


import java.util.Map;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private int version = Build.VERSION.SDK_INT;
    private FirebaseFirestore mfirestore;
    FirebasePerformance performance = FirebasePerformance.getInstance();
    Trace loginTrace = performance.newTrace("login_trace");
    EditText mCorreo, mPassword;
    Button mLogin;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mCorreo = findViewById(R.id.editTextEmail);
        mPassword = findViewById(R.id.editTextPassword);
        mLogin = findViewById(R.id.buttonLogin);
        mAuth = FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();


        //Inicializar el launcher para la solicitud de permisos
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allPermissionsGranted = true;
                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        if (!entry.getValue()) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }
                    if (!allPermissionsGranted) {
                        Toast.makeText(this,"Para acceder a la geolocalización debe aceptar todos los permisos",Toast.LENGTH_LONG).show();
                    }
                }
        );

        initPermissionLauncher();

        //Definir listener del boton de inicio de sesión
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = mCorreo.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if(correo.isEmpty()||password.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Ingrese sus credenciales",Toast.LENGTH_SHORT).show();
                }else {
                    loginTrace.start();
                    iniciarSesion(correo, password);
                }
            }
        });
    }


    public void iniciarSesion(String correo, String password){
        mAuth.signInWithEmailAndPassword(correo, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    loginTrace.stop();
                    openMainScreen();
                    Toast.makeText(LoginActivity.this,"Bienvenido",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loginTrace.incrementMetric("error_count", 1);
                loginTrace.stop();
                Toast.makeText(LoginActivity.this,"Credenciales incorrectas",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initPermissionLauncher(){
        if (version>= Build.VERSION_CODES.S){
            // Verificar si los permisos necesarios están concedidos
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED){
                // Solicitar permisos
                requestPermissionLauncher.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED){
                // Solicitar permisos
                requestPermissionLauncher.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }
        }
    }


    private void openMainScreen() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(Objects.equals(Objects.requireNonNull(user).getEmail(), "johndoe@gmail.com")){
            startActivity(new Intent(this, AdminActivity.class));
            finishAffinity();
        }
        else {
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        }
    }

    public void registrar(View v){
        RegistroFragment fragment = new RegistroFragment();
        fragment.show(getSupportFragmentManager(), "Navegar a fragment");
    }

}