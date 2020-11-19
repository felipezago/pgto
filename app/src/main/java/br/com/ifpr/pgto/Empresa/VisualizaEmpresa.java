package br.com.ifpr.pgto.Empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.ifpr.pgto.Firebase.UsuarioFirebase;
import br.com.ifpr.pgto.Model.Funcionario;
import br.com.ifpr.pgto.Util.MaskEditUtil;
import br.com.ifpr.pgto.Model.Empresa;
import br.com.ifpr.pgto.R;

public class VisualizaEmpresa extends AppCompatActivity {

    EditText cnpj, email,  endereco, razao;
    Empresa emp = UsuarioFirebase.getDadosEmpresaLogada();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    List<Empresa> lista;
    FirebaseUser user;
    Button btnVoltar, btnEditar, btnSalvar;
    String userAtual, senhaAtual;
    Empresa e  = new Empresa();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualiza_empresa);

        cnpj = findViewById(R.id.cnpj);
        email= findViewById(R.id.email);
        endereco = findViewById(R.id.end);
        razao = findViewById(R.id.razao);
        lista= new ArrayList<Empresa>();
        btnVoltar = findViewById(R.id.btnVoltar);
        btnEditar = findViewById(R.id.btnEditar);
        btnSalvar = findViewById(R.id.btnSalvar);

        user = FirebaseAuth.getInstance().getCurrentUser();

        setEnableFalse();

        userAtual = emp.getEmail();


        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference().child("Empresas").child(emp.getId());
        ref.keepSynced(true);

        lista.add(emp);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Empresa emp = dataSnapshot.getValue(Empresa.class);

                cnpj.setText(emp.getCnpj());
                email.setText(emp.getEmail());
                endereco.setText(emp.getEndereço());
                razao.setText(emp.getRazaoSocial());

                e.setRazaoSocial(emp.getRazaoSocial());
                e.setSenha(emp.getSenha());
                e.setEndereço(emp.getEndereço());
                e.setCnpj(emp.getCnpj());
                e.setEmail(emp.getEmail());
                e.setId(emp.getId());

                senhaAtual = emp.getSenha();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d("senha", "onCreate: "+senhaAtual);

        cnpj.addTextChangedListener(MaskEditUtil.maskText(cnpj, MaskEditUtil.FORMAT_CNPJ));

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VisualizaEmpresa.this, PrincipalEmpresa.class);
                startActivity(i);
                finish();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEnableTrue();
                btnSalvar.setVisibility(View.VISIBLE);
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvar(e);
            }
        });

    }

    public void setEnableFalse(){
        cnpj.setEnabled(false);
        email.setEnabled(false);
        endereco.setEnabled(false);
        razao.setEnabled(false);
    }

    public void setEnableTrue(){
        cnpj.setEnabled(true);
        email.setEnabled(true);
        endereco.setEnabled(true);
        razao.setEnabled(true);
    }

    public void carregarValoresEdt(Empresa e){
        String cnpjEdt = MaskEditUtil.unmask(cnpj.getText().toString());
        String enderecoEdt = endereco.getText().toString();
        String razaoEdt = razao.getText().toString();
        String emailEdt = email.getText().toString();

        e.setEmail(emailEdt);
        e.setCnpj(cnpjEdt);
        e.setEndereço(enderecoEdt);
        e.setRazaoSocial(razaoEdt);

    }

    public void salvar(final Empresa a){

        carregarValoresEdt(a);

        Log.d("EMAIL", "salvar: "+a.getEmail());

        AuthCredential credential = EmailAuthProvider
                .getCredential(userAtual, senhaAtual); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("TAG", "User re-authenticated.");
                        //Now change your email address \\
                        //----------------Code for Changing Email Address----------\\
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail(a.getEmail())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("TAG", "User email address updated.");
                                        }
                                    }
                                });
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(a.getRazaoSocial()).build();

                        user.updateProfile(profileUpdates);
                        //----------------------------------------------------------\\
                    }
                });

        ref.setValue(e).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mensagem("Registro alterado com Sucesso!");
                redirecionar();
            }
        });
    }

    public void mensagem(String mensagem) {
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_LONG).show();

    }

    public void redirecionar(){
        Intent i = new Intent(VisualizaEmpresa.this, VisualizaEmpresa.class);
        startActivity(i);
    }
}
