package br.com.ifpr.pgto.Empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;

import br.com.ifpr.pgto.Firebase.ConfiguracoesFirebase;
import br.com.ifpr.pgto.Firebase.UsuarioFirebase;
import br.com.ifpr.pgto.Model.Empresa;
import br.com.ifpr.pgto.Model.Funcionario;
import br.com.ifpr.pgto.R;
import br.com.ifpr.pgto.Util.MaskEditUtil;

public class EditaFunc extends AppCompatActivity {

    EditText nome, cpf, cargo, data, email, nomeEmp, qtDep;
    Empresa e = UsuarioFirebase.getDadosEmpresaLogada();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref, ref2;
    FirebaseUser user;
    Button btnVoltar, btnSalvar, btnExcluir;
    String usuarioAtual, senhaAtual;
    private FirebaseAuth autenticacao = FirebaseAuth.getInstance();

    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edita_func);

        nome = findViewById(R.id.nome);
        cpf = findViewById(R.id.cpf);
        cargo = findViewById(R.id.cargo);
        data = findViewById(R.id.dataAdm);
        email = findViewById(R.id.email);
        qtDep = findViewById(R.id.dep);
        nomeEmp = findViewById(R.id.empresa);
        btnVoltar = findViewById(R.id.btnVoltar);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnExcluir = findViewById(R.id.btnExcluir);

        usuarioAtual = e.getEmail();

        data.addTextChangedListener(MaskEditUtil.mask(data, MaskEditUtil.FORMAT_DATE));
        cpf.addTextChangedListener(MaskEditUtil.mask(cpf, MaskEditUtil.FORMAT_CPF));

        gson = new Gson();

        final Funcionario func = gson.fromJson(getIntent().getStringExtra("funcEdit"), Funcionario.class);

        Log.d("SENHA", "onCreate: "+func.getSenha());
        Log.d("EMAIL", "onCreate: "+func.getEmail());
        initFirebase(func.getId());

        ref2= firebaseDatabase.getReference("Empresas").child(e.getId());

        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Empresa e = dataSnapshot.getValue(Empresa.class);
                senhaAtual = e.getSenha();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cargo.setText(func.getCargo());
        nome.setText(func.getNome());
        cpf.setText(func.getCpf());

        data.setText(func.getDataAdmissao());
        qtDep.setText(Integer.toString(func.getQtDependentes()));
        nomeEmp.setText(func.getEmpresa().getRazaoSocial());

        nomeEmp.setEnabled(false);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvar(func);
            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmacaoDelete(func);
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirecionar();
            }
        });

    }

    public void mensagem(String mensagem) {
        Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_LONG).show();

    }

    public void carregarValoresEdt(Funcionario func){
        String cargoEdt = cargo.getText().toString();
        String nomeEdt = nome.getText().toString();
        String cpfEdt = MaskEditUtil.unmask(cpf.getText().toString());
        //String emailEdt = email.getText().toString();
        String dataEdt = data.getText().toString();
        String qtDepEdt = qtDep.getText().toString();

        func.setCargo(cargoEdt);
        func.setNome(nomeEdt);
        func.setCpf(cpfEdt);
        //func.setEmail(emailEdt);
        func.setDataAdmissao(dataEdt);
        func.setQtDependentes(Integer.parseInt(qtDepEdt));
        func.setSenha(func.getSenha());

    }

    public void logarExcluirFunc(Funcionario f){

        autenticacao = ConfiguracoesFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                f.getEmail(), f.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    AsyncTask<Void, Void, Void> demoLoad = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {


                            return null;
                        }
                    };
                    demoLoad.execute();
                    //UsuarioFirebase.redirecionaUsuarioLogado(CadastroFunc.this);
                    finish();
                }else{
                    try{
                        String excecao = "";
                        try {
                            throw task.getException();
                        }catch ( FirebaseAuthInvalidUserException e){
                            excecao = "Usuário não cadastrado!";

                            e.printStackTrace();

                        }catch ( FirebaseAuthInvalidCredentialsException e){

                            excecao = "E-mail e senha não correspondem a nenhum cadastro";


                        }catch ( Exception e){

                            excecao = "Erro ao cadastrar usuario: " +e.getMessage();
                            e.printStackTrace();

                        }

                        Toast.makeText(EditaFunc.this,
                                excecao, Toast.LENGTH_SHORT).show();


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }
        });

    }

    public void deletaUserAtual(){
        user = autenticacao.getCurrentUser();
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("DEU","OK! Works fine!");

                } else {
                    Log.w("ERRO","Something is wrong!");
                }
            }
        });
    }

    public void logarEmp(){

        Log.d("usu", "logarEmp: atual"+ usuarioAtual);
        Log.d("senha", "logarEmp: senha"+ senhaAtual);

        autenticacao = ConfiguracoesFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuarioAtual, senhaAtual
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    AsyncTask<Void, Void, Void> demoLoad = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {


                            return null;
                        }
                    };
                    demoLoad.execute();
                    //UsuarioFirebase.redirecionaUsuarioLogado(CadastroFunc.this);
                    finish();
                }else{
                    try{
                        String excecao = "";
                        try {
                            throw task.getException();
                        }catch ( FirebaseAuthInvalidUserException e){
                            excecao = "Usuário não cadastrado!";

                            e.printStackTrace();

                        }catch ( FirebaseAuthInvalidCredentialsException e){

                            excecao = "E-mail e senha não correspondem a nenhum cadastro";


                        }catch ( Exception e){

                            excecao = "Erro ao cadastrar usuario: " +e.getMessage();
                            e.printStackTrace();

                        }

                        Toast.makeText(EditaFunc.this,
                                excecao, Toast.LENGTH_SHORT).show();


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }
        });


    }

    public void salvar(final Funcionario func){

        carregarValoresEdt(func);

        ref.setValue(func).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mensagem("Registro alterado com Sucesso!");
                redirecionar();
            }
        });
    }

    public void redirecionar(){
        Intent i = new Intent(EditaFunc.this, ListaFunc.class);
        startActivity(i);
    }

    public void excluir(Funcionario f){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Funcionario").child(f.getId());
        databaseReference.removeValue();
        logarExcluirFunc(f);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        deletaUserAtual();
        logarEmp();
        redirecionar();
    }

    public void initFirebase(String id){
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref= firebaseDatabase.getReference("Funcionario").child(id);
    }

    public void confirmacaoDelete(final Funcionario func){

        AlertDialog.Builder confBox = new AlertDialog.Builder(this);
        confBox.setIcon(R.drawable.ic_action_alert);
        confBox.setTitle("Deletar Cadastro Funcionário");
        confBox.setMessage("Tem certeza que deseja deletar as informações do Funcionário "
                + nome.getText().toString() + "?");

        confBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                excluir(func);
                finish();
            }
        });

        confBox.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        confBox.show();
    }
}
