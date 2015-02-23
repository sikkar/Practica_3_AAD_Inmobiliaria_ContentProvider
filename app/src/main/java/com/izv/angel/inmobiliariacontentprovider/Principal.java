package com.izv.angel.inmobiliariacontentprovider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class Principal extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Adaptador ad;
    private final int ACTIVIDADAGREGAR = 1;
    private final int ACTIVIDADEDITAR = 2;
    private final int ACTIVIDADDOS = 3;
    private boolean horizontal;
    private ListView lv;
    private FragmentoDos fdos;
    static int count = 0;
    private String url = "localhost:8080/InmobiliariaHibernate/controlandroid?target=inmobiliaria&op=insert&action=op";
    private SharedPreferences pc;
    private String user;


    /*////////////////////////////////////////////////////////////////////*/
    /*                                                                    */
    /*                  Metodos ON                                        */
    /*                                                                    */
    /*////////////////////////////////////////////////////////////////////*/
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        if (id == R.id.action_borrar) {
            Cursor cursor = (Cursor) lv.getItemAtPosition(index);
            int borrar = cursor.getInt(0);
            Uri uri = Contrato.TablaInmueble.CONTENT_URI;
            String where = Contrato.TablaInmueble._ID + " = ? ";
            String args[] = new String[]{borrar + ""};
            getContentResolver().delete(uri, where, args);
        } else {
            if (id == R.id.action_editar) {
                Intent i = new Intent(Principal.this, Agregar.class);
                i.setType("editar");
                Cursor cursor = (Cursor) lv.getItemAtPosition(index);
                int editar = cursor.getInt(0);
                i.putExtra("id", editar);
                startActivityForResult(i, ACTIVIDADEDITAR);
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
        horizontal = fdos != null && fdos.isInLayout();
        ad = new Adaptador(this, null);
        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(ad);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (horizontal) {
                    fdos.llenarListView(id);
                } else {
                    Intent intent = new Intent(Principal.this, Secundaria.class);
                    intent.putExtra("id", id);
                    startActivityForResult(intent, ACTIVIDADDOS);
                }
            }
        });
        registerForContextMenu(lv);
        pc = getPreferences(Context.MODE_PRIVATE);
        String r = pc.getString("usuario", "ninguno");
        if(r.equals("ninguno")){
            setuser();
        }
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
            Intent i = new Intent(Principal.this, Agregar.class);
            i.setType("agregar");
            startActivityForResult(i, ACTIVIDADAGREGAR);
            return true;
        } else {
            if (id == R.id.action_user) {
                setuser();
                return true;
            } else {
                if (id == R.id.action_subir) {
                    subirInmuebles();
                    return true;
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        long id;
        if (resultCode == RESULT_OK) {
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

    /*////////////////////////////////////////////////////////////////////*/
    /*                                                                    */
    /*                 Preferencias compartidas                           */
    /*                                                                    */
    /*////////////////////////////////////////////////////////////////////*/

    private void setuser() {
        pc = getPreferences(Context.MODE_PRIVATE);
        String r = pc.getString("usuario", "ninguno");
        if (r.equals("ninguno")) {
            user = "";
        } else {
            user = r;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Usuario:");
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.dialogo_usuario, null);
        dialog.setView(vista);
        final EditText et1;
        et1 = (EditText) vista.findViewById(R.id.etUser);
        et1.setText(user);
        dialog.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferences.Editor ed = pc.edit();
                        ed.putString("usuario", et1.getText().toString());
                        ed.commit();
                    }
                });
        dialog.setNegativeButton("Cancelar", null);
        dialog.show();
    }

    /*////////////////////////////////////////////////////////////////////*/
    /*                                                                    */
    /*                  Cursor Loader ContentProvider                     */
    /*                                                                    */
    /*////////////////////////////////////////////////////////////////////*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Contrato.TablaInmueble.CONTENT_URI;
        return new CursorLoader(this, uri, null, null, null, Contrato.TablaInmueble.LOCALIDAD + " collate localized asc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ad.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ad.swapCursor(null);
    }

    /*////////////////////////////////////////////////////////////////////*/
    /*                                                                    */
    /*                Metodos de subir a la web                           */
    /*                                                                    */
    /*////////////////////////////////////////////////////////////////////*/


    private void subirInmuebles(){

        Uri uri = Contrato.TablaInmueble.CONTENT_URI;
        String seleccion = Contrato.TablaInmueble.SUBIDO + " = ? ";
        String argu[] = new String[]{"0"};
        Cursor cursor = getContentResolver().query(uri, null, seleccion, argu,null);
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String[] datos = new String[]{id + "", cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)};
                ContentValues values = new ContentValues();
                values.put(Contrato.TablaInmueble.LOCALIDAD, cursor.getString(1));
                values.put(Contrato.TablaInmueble.DIRECCION, cursor.getString(2));
                values.put(Contrato.TablaInmueble.TIPO, cursor.getString(3));
                values.put(Contrato.TablaInmueble.PRECIO, cursor.getString(4));
                values.put(Contrato.TablaInmueble.SUBIDO, 1);
                String where = Contrato.TablaInmueble._ID + " = ? ";
                String args[] = new String[]{id + ""};
                getContentResolver().update(uri, values, where, args);
                new Descarga().execute(datos);
            }
        }else{
            Toast.makeText(this,"No hay Inmuebles para subir",Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    class Descarga extends AsyncTask<String [] ,Integer , String > {

        private String idinm;

        @Override
        protected String doInBackground(String[]... params) {
            String url ="http://192.168.1.130:8080/InmobiliariaHibernate/controladorandroid?target=inmobiliaria&op=insert&action=op";
            String r=null;
            for(String[] p:params){
                idinm=p[0];
                r=postInmueble(url,p);
            }
            return r;
        }

        @Override
        protected void onPostExecute(String strings) {
            String resul = strings.substring(4,strings.length());
            String argufot[] = new String[]{idinm+""};
            Cursor c = getContentResolver().query(Contrato.TablaFotos.CONTENT_URI,null,Contrato.TablaFotos.IDIMUEBLE + " = ? ",argufot,null);
            if(c.getCount()>0){
                while(c.moveToNext()) {
                    String [] datosFoto = new String[]{resul.trim(),c.getString(2)};
                    String ruta = c.getString(2);
                    new UploadFoto().execute(datosFoto);
                }
            }
            Toast.makeText(Principal.this,"Guardado con id:"+ resul.trim(), Toast.LENGTH_SHORT).show();
        }

        public String postInmueble(String urlPeticion, String []parametros) {
            URL url = null;
            try {
                url = new URL(urlPeticion);
            } catch (MalformedURLException e) {
                Log.v("error", e.toString());
            }
            URLConnection conexion=null;
            OutputStreamWriter out = null;
            try {
                conexion = url.openConnection();
                conexion.setDoOutput(true);
                out = new OutputStreamWriter(conexion.getOutputStream());
                String datos="&localidad="+parametros[1]+"&direccion="+parametros[2]+"&tipo="+parametros[3]+"&precio="+parametros[4]+"&usuario="+user;
                out.write(datos);
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.v("error", e.toString());
            }
            BufferedReader in = null;
            String resultado=null;
            try {
                in = new BufferedReader( new InputStreamReader(conexion.getInputStream()));
                String decodedString;
                while ((decodedString = in.readLine()) != null) {
                    resultado+=decodedString+"\n";
                }
                in.close();
            } catch (IOException e) {
                Log.v("error", e.toString());
            }
            return resultado;
        }
    }

    class UploadFoto extends AsyncTask <String[] ,Integer , String >{

        @Override
        protected String doInBackground(String[]... params) {
            String baseServidor = "http://192.168.1.130:8080/InmobiliariaHibernate/controladorandroid?target=inmobiliaria&op=imagen&action=op";
            String r=null;
            for(String[] s:params){
                r = postFile(baseServidor,"archivo",s[1],s[0].trim());
            }
            return r;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(Principal.this, s.trim(), Toast.LENGTH_SHORT).show();
        }

        public String postFile(String urlPeticion, String nombreParametro, String nombreArchivo, String id) { //nombreParametro es el nombre que le pondriamos en el formulario
            String resultado="";                                                                   //nombreArchivo es el nombre uri.getpath() del intent del archivo ruta+nombre
            int status=0;
            try {
                URL url = new URL(urlPeticion);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                conexion.setDoOutput(true);
                conexion.setRequestMethod("POST");

                FileBody fileBody = new FileBody(new File(nombreArchivo));
                MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);
                multipartEntity.addPart(nombreParametro, fileBody);
                multipartEntity.addPart("id", new StringBody(id));
                conexion.setRequestProperty("Content-Type", multipartEntity.getContentType().getValue());
                OutputStream out = conexion.getOutputStream();

                try {
                    multipartEntity.writeTo(out);
                } catch(Exception e){
                    Log.v("excepcion1",e.toString());
                    return e.toString();
                } finally {
                    out.close();
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String decodedString;
                while ((decodedString = in.readLine()) != null) {
                    resultado+=decodedString+"\n";
                }
                in.close();
                status = conexion.getResponseCode();
            } catch (MalformedURLException ex) {
                Log.v("excepcion2",ex.toString());
                return ex.toString();
            } catch (IOException ex) {
                Log.v("excepcion3",ex.toString());
                return ex.toString();
            }
            return resultado;
        }

    }
}
