package br.com.ifpr.pgto.Empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.ifpr.pgto.Firebase.ConfiguracoesFirebase;
import br.com.ifpr.pgto.Firebase.UsuarioFirebase;
import br.com.ifpr.pgto.Model.Empresa;
import br.com.ifpr.pgto.Principal;
import br.com.ifpr.pgto.R;

public class CadEmp extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth autenticacao = FirebaseAuth.getInstance();
    boolean achou = true, cadastrou = false;

    Button btnSalvar, btnVoltar;
    EditText edtCnpj, edtRz, edtSenha, edtEnd, edtEmail;
    List<Empresa> lista = new ArrayList<Empresa>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebase();

        edtCnpj = findViewById(R.id.edtCNPJ);
        edtEnd = findViewById(R.id.edtEnd);
        edtRz = findViewById(R.id.edtRazao);
        edtSenha = findViewById(R.id.edtSenha);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnVoltar = findViewById(R.id.btnVoltar);
        edtEmail = findViewById(R.id.edtEmail);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastra(view);
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CadEmp.this, Principal.class);
                startActivity(i);
            }
        });

    }


    public void initFirebase(){
        FirebaseApp.initializeApp(CadEmp.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Empresas");
    }

    public void cadastra(View v) {
        final Empresa empr = new Empresa();
        final String razao = edtRz.getText().toString();
        final String cnpj = edtCnpj.getText().toString();
        final String senha = edtSenha.getText().toString();
        final String endereco = edtEnd.getText().toString();
        final String email = edtEmail.getText().toString();

        empr.setRazaoSocial(razao);

        empr.setEndereço(endereco);
        empr.setSenha(senha);
        empr.setEmail(email);

        if(!razao.isEmpty()){
            if(!cnpj.isEmpty() && cnpj.length() == 14){
                if(!senha.isEmpty()){

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                Empresa a = dataSnapshot1.getValue(Empresa.class);
                                lista.add(a);
                            }

                            int i = 0;

                            if(lista.size() == 0){
                                Empresa empre = new Empresa();

                                empre.setRazaoSocial(razao);
                                empre.setCnpj(cnpj);
                                empre.setEndereço(endereco);
                                empre.setSenha(senha);
                                empre.setEmail(email);

                                cadastrarEmpresa(empre);
                            }else{
                                for(Empresa e: lista){
                                    i++;
                                    if(e.getCnpj().equals(cnpj) && cadastrou){
                                        Toast.makeText(CadEmp.this,
                                                "Empresa cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                                        achou = false;
                                        break;
                                    }else if(e.getCnpj().equals(cnpj)){
                                        Toast.makeText(CadEmp.this,
                                                "CNPJ já cadastrado", Toast.LENGTH_SHORT).show();
                                        achou = false;
                                        break;
                                    }

                                    if(i == lista.size()){
                                        Empresa empre = new Empresa();

                                        empre.setRazaoSocial(razao);
                                        empre.setCnpj(cnpj);
                                        empre.setEndereço(endereco);
                                        empre.setSenha(senha);
                                        empre.setEmail(email);

                                        cadastrarEmpresa(empre);
                                        cadastrou = true;
                                        break;
                                    }


                                }



                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }else{
                    Toast.makeText(this,
                            "Preencha a senha!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this,
                        "Preencha o CNPJ completo!", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this,
                    "Preencha a razão social!", Toast.LENGTH_SHORT).show();
        }

    }


    public void cadastrarEmpresa(final Empresa emp) {

        autenticacao = ConfiguracoesFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                emp.getEmail(),
                emp.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {


            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    try {
                        String idUsuario = task.getResult().getUser().getUid();
                        emp.setId(idUsuario);
                        emp.salvar();
                        Log.i("tag", "SUCESSO");
                        UsuarioFirebase.AtualizaNome(emp.getRazaoSocial());
                        redirecionaLogin();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    String excecao = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {

                        excecao = "Digite uma senha mais forte!";
                    } catch (FirebaseAuthUserCollisionException e) {

                        excecao = "Esta conta já está cadastrada";
                        e.printStackTrace();
                    } catch (Exception e) {

                        excecao = "Erro ao cadastrar usuario: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadEmp.this,
                            excecao, Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void redirecionaLogin(){

        Intent intent = new Intent(this, LoginEmp.class);
        startActivity(intent);

    }


}
