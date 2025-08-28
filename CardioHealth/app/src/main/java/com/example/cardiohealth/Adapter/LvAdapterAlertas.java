package com.example.cardiohealth.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.cardiohealth.Model.Alertas;
import com.example.cardiohealth.Model.Instant;
import com.example.cardiohealth.R;

import java.util.ArrayList;

public class LvAdapterAlertas extends BaseAdapter {
    private final Context context;
    private final int layoutId;
    private final ArrayList<Alertas> items;

    public LvAdapterAlertas(Context context, int layoutId, ArrayList<Alertas> items) {
        this.context = context;
        this.layoutId = layoutId;
        this.items = items;
    }

    public void setItems(ArrayList<Alertas> items) {
        this.items.clear();
        this.items.addAll(items);
//        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Alertas row = this.items.get(position);
        View itemView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(layoutId, null);
        } else {
            itemView = convertView;
        }

        // Configuração dos campos do item
        TextView tvTipo = itemView.findViewById(R.id.tvTipoAlerta);
        tvTipo.setText(row.getTipo());

        TextView tvDescricao = itemView.findViewById(R.id.tvDescricaoAlerta);
        tvDescricao.setText(row.getDescricao());

        TextView tvValor = itemView.findViewById(R.id.tvValorAlerta);
        tvValor.setText(String.valueOf(row.getValor()));

        TextView tvDataHora = itemView.findViewById(R.id.tvDataHoraAlerta);
        tvDataHora.setText(getDataHoraFormatada(row.getDataHora()));

        return itemView;
    }

    private String getDataHoraFormatada(Instant data) {
        return String.format("%02d/%02d/%04d %02d:%02d:%02d",
                data.getData().getDia(), data.getData().getMes(), data.getData().getAno(), data.getHora(), data.getMinuto(), data.getSegundo());
    }


}
