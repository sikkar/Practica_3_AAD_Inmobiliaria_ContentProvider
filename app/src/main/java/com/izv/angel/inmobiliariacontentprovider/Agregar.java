package com.izv.angel.inmobiliariacontentprovider;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Agregar extends Activity {

    private EditText etDir,etLoc,etTip,etPrec;
    private Button bt1;
    private Adaptador ad;
    private int posicion;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_agregar);
        bt1 = (Button)findViewById(R.id.btAgregar);
        etLoc = (EditText) findViewById(R.id.etLocalidad);
        etDir = (EditText) findViewById(R.id.etDireccion);
        etTip = (EditText) findViewById(R.id.etTipo);
        etPrec = (EditText) findViewById(R.id.etPrecio);
        Intent i = getIntent();
        if(i.getType().equals("editar")){
            id = i.getIntExtra("id",0);
            String args[] = new String[]{id+""};
            Cursor cur = getContentResolver().query(Contrato.TablaInmueble.CONTENT_URI,null,Contrato.TablaInmueble._ID + " = ? ",args,null);
            cur.moveToFirst();
            etLoc.setText(cur.getString(1));
            etDir.setText(cur.getString(2));
            etTip.setText(cur.getString(3));
            etPrec.setText(cur.getString(4)+"");
            bt1.setText(R.string.editarInm);
        }else{
            bt1.setText(R.string.agregarInm);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_agregar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void aniadir (View view){
        Intent i=getIntent();
        String localidad,direccion,tipo,precio;
        localidad = etLoc.getText().toString();
        direccion = etDir.getText().toString();
        tipo = etTip.getText().toString();
        precio = etPrec.getText().toString();
        if(i.getType().equals("editar")){
            Uri uri = Contrato.TablaInmueble.CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(Contrato.TablaInmueble.LOCALIDAD,localidad);
            values.put(Contrato.TablaInmueble.DIRECCION,direccion);
            values.put(Contrato.TablaInmueble.TIPO,tipo);
            values.put(Contrato.TablaInmueble.PRECIO,precio);
            values.put(Contrato.TablaInmueble.SUBIDO,0);
            String where = Contrato.TablaInmueble._ID +" = ? ";
            String args[] = new String[]{id+""};
            getContentResolver().update(uri,values,where,args);
        }
        if(i.getType().equals("agregar")){
            Uri uri = Contrato.TablaInmueble.CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(Contrato.TablaInmueble.LOCALIDAD,localidad);
            values.put(Contrato.TablaInmueble.DIRECCION,direccion);
            values.put(Contrato.TablaInmueble.TIPO,tipo);
            values.put(Contrato.TablaInmueble.PRECIO,precio);
            values.put(Contrato.TablaInmueble.SUBIDO,0);
            Uri u = getContentResolver().insert(uri,values);
        }

        setResult(RESULT_OK,i);
        finish();
    }
}
