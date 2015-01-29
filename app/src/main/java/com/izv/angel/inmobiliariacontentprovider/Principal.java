package com.izv.angel.inmobiliariacontentprovider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class Principal extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Adaptador ad;
    private final int ACTIVIDADAGREGAR=1;
    private final int ACTIVIDADEDITAR=2;
    private final int ACTIVIDADDOS=3;
    private boolean horizontal;
    private ListView lv;
    private FragmentoDos fdos;
    static int count=0;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        if (id == R.id.action_borrar) {
            Cursor cursor = (Cursor) lv.getItemAtPosition(index);
            int borrar = cursor.getInt(0);
            Uri uri = Contrato.TablaInmueble.CONTENT_URI;
            String where = Contrato.TablaInmueble._ID +" = ? ";
            String args[] = new String[]{borrar+""};
            getContentResolver().delete(uri,where,args);
        } else {
            if (id == R.id.action_editar) {
                Intent i = new Intent(Principal.this,Agregar.class);
                i.setType("editar");
                Cursor cursor = (Cursor) lv.getItemAtPosition(index);
                int editar = cursor.getInt(0);
                i.putExtra("id",editar);
                startActivityForResult(i,ACTIVIDADEDITAR);
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        getLoaderManager().initLoader(0, null, this);
        fdos = (FragmentoDos) getFragmentManager().findFragmentById(R.id.fragmento2land);
        horizontal = fdos!=null && fdos.isInLayout();
        ad = new Adaptador(this,null);
        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(ad);
        lv.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(horizontal){
                    fdos.llenarListView(id);
                }else{
                    Intent intent = new Intent(Principal.this,Secundaria.class);
                    intent.putExtra("id",id);
                    startActivityForResult(intent, ACTIVIDADDOS);
                }
            }
        });
        registerForContextMenu(lv);
        SharedPreferences pc;
        pc = getPreferences(Context.MODE_PRIVATE);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.longmenu, menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_aniadir) {
            Intent i = new Intent(Principal.this,Agregar.class);
            i.setType("agregar");
            startActivityForResult(i,ACTIVIDADAGREGAR);
            return true;
        }else{
            if(id== R.id.action_user){
                setuser();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setuser(){
        final SharedPreferences pc;
        final String user;
        pc = getPreferences(Context.MODE_PRIVATE);
        String r = pc.getString("usuario", "ninguno");
        if(r.equals("ninguno")){
            user="";
        }else{
            user = r;
        }
        AlertDialog.Builder dialog= new AlertDialog.Builder(this);
        dialog.setTitle("Usuario:");
        LayoutInflater inflater= LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.dialogo_usuario, null);
        dialog.setView(vista);
        final EditText et1;
        et1=(EditText)vista.findViewById(R.id.etUser);
        et1.setText(user);
        dialog.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferences.Editor ed = pc.edit();
                        ed.putString("usuario", et1.getText().toString());
                        ed.commit();
                    }
                });
        dialog.setNegativeButton("Cancelar",null);
        dialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Contrato.TablaInmueble.CONTENT_URI;
        return new CursorLoader( this, uri, null, null, null, Contrato.TablaInmueble.LOCALIDAD +" collate localized asc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ad.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ad.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        long id;
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTIVIDADAGREGAR:

                    Toast.makeText(this, R.string.insertado, Toast.LENGTH_LONG).show();
                    break;
                case ACTIVIDADEDITAR:

                    Toast.makeText(this, R.string.edicion, Toast.LENGTH_LONG).show();
                    break;

            }
        }
    }
}
