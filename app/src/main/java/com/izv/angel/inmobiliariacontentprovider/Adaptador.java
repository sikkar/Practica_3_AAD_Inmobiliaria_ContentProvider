package com.izv.angel.inmobiliariacontentprovider;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class Adaptador extends CursorAdapter {


    public class ViewHolder{
        public TextView tv1,tv2,tv3,tv4;
        public ImageView iv;
    }

    public Adaptador(Context context, Cursor c) {
        super(context, c, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        View v = i.inflate(R.layout.detalle, parent, false);
        return v;
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {

        ViewHolder vh = new ViewHolder();

        vh.tv1=(TextView) convertView.findViewById(R.id.tvLocalidad);
        vh.tv2=(TextView) convertView.findViewById(R.id.tvDireccion);
        vh.tv3=(TextView) convertView.findViewById(R.id.tvTipo);
        vh.tv4=(TextView) convertView.findViewById(R.id.tvPrecio);
        vh.iv =(ImageView) convertView.findViewById(R.id.ivCasa);

        String localidad = cursor.getString(1);
        String direccion = cursor.getString(2);
        String tipo = cursor.getString(3);
        int precio = cursor.getInt(4);

        vh.tv1.setText(localidad);
        vh.tv2.setText(direccion);
        vh.tv3.setText(tipo);
        vh.tv4.setText(precio+" €");

        if(tipo.equals("Casa")){
            vh.iv.setImageResource(R.drawable.casa);
        }else{
            if(tipo.equals("Garaje")){
                vh.iv.setImageResource(R.drawable.cochera);
            }
            else{
                vh.iv.setImageResource(R.drawable.trastero);
            }
        }
    }

}
