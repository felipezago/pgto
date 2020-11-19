package br.com.ifpr.pgto.Model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.List;

import br.com.ifpr.pgto.Empresa.EditaFunc;
import br.com.ifpr.pgto.Empresa.ListaFunc;
import br.com.ifpr.pgto.Empresa.VisualizaFunc;
import br.com.ifpr.pgto.Firebase.UsuarioFirebase;
import br.com.ifpr.pgto.R;

public class RecyclerAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{
    Context ctx;
    List<Funcionario> funcionarios;
    Empresa e= UsuarioFirebase.getDadosEmpresaLogada();
    Gson gson = new Gson();

    public RecyclerAdapter(Context c, List<Funcionario> u){
        ctx = c;
        funcionarios = u;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.linha, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        /*myViewHolder.nome.setText(usuarios.get(i).getNick().toUpperCase());
        myViewHolder.pontos.setText("Pontos: "+Integer.toString(usuarios.get(i).getTotal()));
        myViewHolder.posic.setText(Integer.toString(i+1)+"º ");*/
        holder.nome.setText(funcionarios.get(position).getNome()+" - "+funcionarios.get(position).getCargo());

        final Funcionario func = new Funcionario();
        func.setCargo(funcionarios.get(position).getCargo());
        func.setQtDependentes(funcionarios.get(position).getQtDependentes());
        func.setEmpresa(funcionarios.get(position).getEmpresa());
        func.setSenha(funcionarios.get(position).getSenha());
        func.setNome(funcionarios.get(position).getNome());
        func.setEmail(funcionarios.get(position).getEmail());
        func.setCpf(funcionarios.get(position).getCpf());
        func.setDataAdmissao(funcionarios.get(position).getDataAdmissao());
        func.setId(funcionarios.get(position).getId());

        holder.btnVisualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), VisualizaFunc.class);

                String json = gson.toJson(func);

                i.putExtra("func", json);
                ctx.startActivity(i);

            }
        });

        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), EditaFunc.class);

                String json = gson.toJson(func);

                i.putExtra("funcEdit", json);
                ctx.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return funcionarios.size();
    }

    class MyViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder{

        TextView nome, btnEditar, btnVisualizar;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        nome = itemView.findViewById(R.id.txNome);
        btnEditar = itemView.findViewById(R.id.btnEditar);
        btnVisualizar = itemView.findViewById(R.id.btnVisualizar);
        }
    }
}



