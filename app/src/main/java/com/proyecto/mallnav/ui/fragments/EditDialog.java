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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.proyecto.mallnav.R;

import java.util.HashMap;
import java.util.Map;


public class EditDialog extends DialogFragment {

    EditText mNuevoNombre, mNuevoApellido, mNuevoCorreo;
    Button mUpdate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.dialog_edit, container, false);
       initViews(view);
       setViewsParams();
       setViewsListeners();
       return view;
    }

    private void setViewsListeners() {
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nuevoNombre = mNuevoNombre.getText().toString().trim();
                String nuevoApellido = mNuevoApellido.getText().toString().trim();
                String nuevoCorreo = mNuevoCorreo.getText().toString().trim();

                Map<String, Object> nuevosDatos = new HashMap<>();
                nuevosDatos.put("nombre", nuevoNombre);
                nuevosDatos.put("apellido", nuevoApellido);
                nuevosDatos.put("e_mail", nuevoCorreo);

                ProfileFragment.docRef.update(nuevosDatos).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(),"Datos actualizados correctamente",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Error al actualizar datos"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setViewsParams() {
        mNuevoNombre.setText(ProfileFragment.nombre);
        mNuevoApellido.setText(ProfileFragment.apellido);
        mNuevoCorreo.setText(ProfileFragment.correo);
    }

    private void initViews(View view) {
        mNuevoNombre = view.findViewById(R.id.edtTxtNombre);
        mNuevoApellido = view.findViewById(R.id.edtTxtApellido);
        mNuevoCorreo = view.findViewById(R.id.edtTxtEmail);
        mUpdate = view.findViewById(R.id.btnUpdate);
    }
}