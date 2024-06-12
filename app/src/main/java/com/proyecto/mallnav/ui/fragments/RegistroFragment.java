package com.proyecto.mallnav.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.proyecto.mallnav.R;

import java.util.HashMap;
import java.util.Map;


public class RegistroFragment extends DialogFragment {

    EditText nombre, apellido, e_mail, password;
    Button btnRegistrar;
    private FirebaseFirestore mfirestore;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registro, container, false);
        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        nombre = v.findViewById(R.id.edtTxtNombre);
        apellido = v.findViewById(R.id.edtTxtApellido);
        e_mail = v.findViewById(R.id.edtTxtEmail);
        password = v.findViewById(R.id.edtTxtPassword);
        btnRegistrar = v.findViewById(R.id.btnReg);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreReg = nombre.getText().toString().trim();
                String apellidoReg = apellido.getText().toString().trim();
                String e_mailReg = e_mail.getText().toString().trim();
                String passwordReg = password.getText().toString().trim();

                if (nombreReg.isEmpty()||apellidoReg.isEmpty()||e_mailReg.isEmpty()||passwordReg.isEmpty()){
                    Toast.makeText(getContext(),"Debe ingresar todos los datos", Toast.LENGTH_LONG).show();
                }
                else {
                    registrarUser(nombreReg, apellidoReg, e_mailReg, passwordReg);
                }
            }
        });

        return v;
    }

    public void registrarUser(String nombreReg, String apellidoReg, String e_mailReg, String passwordReg){
        mAuth.createUserWithEmailAndPassword(e_mailReg,passwordReg).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String id = mAuth.getCurrentUser().getUid();
                Map<String, Object> map = new HashMap<>();
                map.put("id",id);
                map.put("nombre",nombreReg);
                map.put("apellido",apellidoReg);
                map.put("e_mail",e_mailReg);
                map.put("password",passwordReg);

                mfirestore.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(),"Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                        getDialog().dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Error al guardar", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Error al registrar usuario", Toast.LENGTH_SHORT).show();
            }
        });

    }
}