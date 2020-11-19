package br.com.ifpr.pgto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.com.ifpr.pgto.Empresa.CadastroFunc;
import br.com.ifpr.pgto.Empresa.ListaFunc;
import br.com.ifpr.pgto.Empresa.LoginEmp;

public class Principal extends AppCompatActivity {

    Button btnEmp, btnF, btnAjuda;
    TextView txrazao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        btnEmp= findViewById(R.id.btnEmpr);
        btnF = findViewById(R.id.btnFunc);
        btnAjuda = findViewById(R.id.btnAjuda);

        

        btnEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Principal.this, LoginEmp.class);
                startActivity(i);
                finish();
            }
        });

        btnAjuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Principal.this, ListaFunc.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Manipulando as acoes da barra de acoes
        if(id == R.id.action_filtro){
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
