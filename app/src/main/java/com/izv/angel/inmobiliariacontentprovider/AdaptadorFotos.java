package com.izv.angel.inmobiliariacontentprovider;


import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

public class AdaptadorFotos extends CursorAdapter {

    public class ViewHolder{
        public ImageView iv;
    }

    public AdaptadorFotos(Context context, Cursor c) {
        super(context, c, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        View v = i.inflate(R.layout.detalle_fotos, parent, false);
        return v;
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {

        ViewHolder vh = new ViewHolder();

        vh.iv =(ImageView) convertView.findViewById(R.id.ivFoto);

        vh.iv.setTag(cursor.getString(2));
        vh.iv.setImageBitmap(BitmapFactory.decodeFile(cursor.getString(2)));
        convertView.setTag(vh);
        //vh.iv.setImageURI(Uri.parse(foto.getFoto()));
    }
}
