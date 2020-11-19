package br.com.ifpr.pgto.Empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.ifpr.pgto.Firebase.ConfiguracoesFirebase;
import br.com.ifpr.pgto.Firebase.UsuarioFirebase;
import br.com.ifpr.pgto.Util.MaskEditUtil;
import br.com.ifpr.pgto.Model.Empresa;
import br.com.ifpr.pgto.Principal;
import br.com.ifpr.pgto.R;

public class LoginEmp extends AppCompatActivity {

    EditText edtSenha, edtCnpj, edtEmail;
    Button btnVoltar, btnEntrar, btnNovo, btnAlterar;
    TextView txEmail, txSenha, txErro, txrazao;
    FirebaseDatabase firebaseDatabase;
    private FirebaseAuth autenticacao;
    DatabaseReference ref;
    View layout, relalt, relgeral, relmsg, relRaz;
    String cnpj1, cnpj2;
    boolean exit = true;
    boolean existe = false;
    List<Empresa> lista;
    boolean achou = false, naoAchou = false;
    ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_emp);

        initVariables();
        initFirebase();




        edtCnpj.addTextChangedListener(MaskEditUtil.mask(edtCnpj, MaskEditUtil.FORMAT_CNPJ));

        btnNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginEmp.this, CadEmp.class);
                startActivity(i);
                finish();

            }
        });

        btnAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginEmp.this, LoginEmp.class);
                startActivity(i);
                finish();
            }
        });

        edtCnpj.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(edtCnpj.isFocused()){
                    threadCNPJ();
                }
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginEmp.this, Principal.class);
                startActivity(i);
            }
        });

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarLoginEmpresa(view);
            }
        });

    }



    public void validarLoginEmpresa(View view){

        String txemail = edtEmail.getText().toString();
        String txsenha = edtSenha.getText().toString();

        if( !txemail.isEmpty() ){
            if( !txsenha.isEmpty() ){

                final Empresa empre = new Empresa();
                empre.setEmail(txemail);
                empre.setSenha(txsenha);
                //userRecord = FirebaseAuth.getInstance().getUserByEmail(e.getEmail());

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Empresa u = dataSnapshot1.getValue(Empresa.class);
                            lista.add(u);
                        }

                        int i =0;

                        for(Empresa a: lista){
                            i++;

                            if(empre.getEmail().equals(a.getEmail())){
                                logarUsuario(empre);
                                break;
                            }

                            if(i == lista.size()){
                                Toast.makeText(LoginEmp.this, "O E-mail informado não está no cadastro de empresas, favor informar outro!", Toast.LENGTH_LONG).show();
                            }


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }else{
                Toast.makeText(LoginEmp.this,
                        "Preencha a senha!", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(LoginEmp.this,
                    "Preencha o e-mail!", Toast.LENGTH_SHORT).show();
        }

    }

    public void logarUsuario(final Empresa empresa){
        autenticacao = ConfiguracoesFirebase.getFirebaseAutenticacao();
        showLoadingBar();
        autenticacao.signInWithEmailAndPassword(
                empresa.getEmail(), empresa.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    hideLoadingBar();
                    AsyncTask<Void, Void, Void> demoLoad = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            try {
                                Thread.sleep(500);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    };
                    demoLoad.execute();
                    UsuarioFirebase.redirecionaUsuarioLogado(LoginEmp.this);
                    finish();
                }else{
                    try{
                        String excecao = "";
                        try {
                            throw task.getException();
                        }catch ( FirebaseAuthInvalidUserException e){
                            excecao = "Usuário não cadastrado!";
                            e.printStackTrace();
                            hideLoadingBar();

                        }catch ( FirebaseAuthInvalidCredentialsException e){

                            excecao = "E-mail e senha não correspondem a nenhum cadastro";
                            hideLoadingBar();
                        }catch ( Exception e){

                            excecao = "Erro ao cadastrar usuario: " +e.getMessage();
                            e.printStackTrace();
                            hideLoadingBar();
                        }

                        Toast.makeText(LoginEmp.this,
                                excecao, Toast.LENGTH_SHORT).show();
                        hideLoadingBar();


                    }catch (Exception e){
                        e.printStackTrace();
                        hideLoadingBar();
                    }

                }

            }
        });

    }

    public void initVariables(){
        edtSenha = findViewById(R.id.edtSenha);
        edtCnpj = findViewById(R.id.edtCNPJ);
        edtEmail = findViewById(R.id.edtEmail);
        btnEntrar = findViewById(R.id.btnEntrarEmp);
        btnNovo = findViewById(R.id.btnNova);
        btnVoltar = findViewById(R.id.btnVoltar);
        txEmail = findViewById(R.id.txEm);
        txSenha = findViewById(R.id.txSenha);
        layout = findViewById(R.id.rel2);
        relalt = findViewById(R.id.relAlt);
        relgeral = findViewById(R.id.rel);
        relmsg = findViewById(R.id.relMsg);
        txErro = findViewById(R.id.msgERRO);
        lista= new ArrayList<Empresa>();
        txrazao = findViewById(R.id.txCNPJ);
        pb= findViewById(R.id.progressbar);

        btnAlterar = findViewById(R.id.btnAlterar);
    }

    public void initFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref= firebaseDatabase.getReference("Empresas");
    }

    public void mostraCampos(){

            edtCnpj.getLayoutParams().width = 500;
            layout.setVisibility(View.VISIBLE);
            relalt.setVisibility(View.VISIBLE);
            edtCnpj.setFocusable(false);
            relmsg.setVisibility(View.GONE);


            //hideKeyboard(LoginEmp.this);
    }

    public void mostraMsg(){
        relmsg.setVisibility(View.VISIBLE);
    }

    public void clearFocus(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtCnpj.getWindowToken(),0);
    }

    public void showLoadingBar(){
        pb.setVisibility(View.VISIBLE);
    }

    public void hideLoadingBar(){
        pb.setVisibility(View.GONE);
    }

    private void threadCNPJ(){
        new Thread(new Runnable() {
            public void run() {

                //Thread.currentThread().interrupt();

                if(!achou || !naoAchou){

                    while(edtCnpj.isFocused()){

                        if(edtCnpj.length() == 18){
                            hideKeyboard(LoginEmp.this);
                            //showLoadingBar();
                            String unmasked = MaskEditUtil.unmask(edtCnpj.getText().toString());
                            cnpj2 = unmasked;
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        Empresa u = dataSnapshot1.getValue(Empresa.class);
                                        lista.add(u);
                                    }

                                    int i = 0;

                                    Log.d("TAG", "onDataChange: "+lista.size());

                                    if(lista.size() == 0){
                                        txErro.setText("CNPJ " + cnpj2 + " Não existente na base de dados, favor criar uma empresa.");
                                        mostraMsg();
                                        naoAchou = true;
                                        clearFocus();
                                        edtCnpj.setText("");
                                    }else{
                                        for (Empresa emp : lista) {
                                            cnpj1 = emp.getCnpj();

                                            if (cnpj1.equals(cnpj2)) {
                                                clearFocus();
                                                mostraCampos();
                                                clearFocus();
                                                txrazao.setText(emp.getRazaoSocial());

                                                achou = true;
                                            } else {
                                                i++;
                                                if (i == lista.size()) {
                                                    txErro.setText("CNPJ " + cnpj2 + " Não existente na base de dados, favor criar uma empresa.");
                                                    mostraMsg();
                                                    naoAchou = true;
                                                    clearFocus();
                                                    edtCnpj.setText("");
                                                }
                                                //hideLoadingBar();
                                            }
                                        }
                                    }



                                }



                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }}
                    }


            }

        }).start();
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        imm.showSoftInput(view, 0);
    }


}
