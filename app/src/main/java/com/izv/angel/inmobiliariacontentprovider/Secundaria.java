package com.izv.angel.inmobiliariacontentprovider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;


public class Secundaria extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    protected static final int CAMERA_REQUEST = 1;
    protected static final int GALLERY_PICTURE = 2;
    private Intent pictureActionIntent = null;
    private AdaptadorFotos adf;
    private long idInm;
    private ListView lv;

    /*////////////////////////////////////////////////////////////////////*/
    /*                                                                    */
    /*                Metodos ON                                          */
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
            File file = new File(cursor.getString(2));
            if(file.delete()){
                Uri uri = Contrato.TablaFotos.CONTENT_URI;
                String where = Contrato.TablaFotos._ID +" = ? ";
                String args[] = new String[]{borrar+""};
                getContentResolver().delete(uri,where,args);
            }else{
                Log.v("no se puede borrar", file.getName());
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_secundaria);
        Intent i=getIntent();
        idInm=i.getLongExtra("id",0);
        getLoaderManager().initLoader(0, null, this);
        adf = new AdaptadorFotos(this,null);
        lv = (ListView) findViewById(R.id.listView2);
        lv.setAdapter(adf);
        lv.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = view.getTag();
                AdaptadorFotos.ViewHolder vh;
                vh = (AdaptadorFotos.ViewHolder)o;
                String s=(String)vh.iv.getTag();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + s), "image/jpeg");
                startActivity(intent);
            }
        });
        registerForContextMenu(lv);
    }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.longmenufotos, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_secundaria, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_aniadir) {
            abrirDialogo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*////////////////////////////////////////////////////////////////////*/
    /*                                                                    */
    /*               CursorLoader Para fotos                              */
    /*                                                                    */
    /*////////////////////////////////////////////////////////////////////*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Contrato.TablaFotos.CONTENT_URI;
        String seleccion = Contrato.TablaFotos.IDIMUEBLE + " = ? ";
        String argu[] = new String[]{String.valueOf(idInm)};
        CursorLoader curl =new CursorLoader( this, uri, null, seleccion, argu , Contrato.TablaFotos._ID +" collate localized asc");
        return curl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adf.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adf.swapCursor(null);
    }

     /*////////////////////////////////////////////////////////////////////*/
    /*                                                                    */
    /*               Dialogo seleccion origen fotos                       */
    /*                                                                    */
    /*////////////////////////////////////////////////////////////////////*/

    private void abrirDialogo() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle(R.string.foto_inmueble);
        myAlertDialog.setMessage(R.string.abrir_donde);
        myAlertDialog.setPositiveButton(R.string.galeria, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                pictureActionIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
                pictureActionIntent.setType("image/*");
                pictureActionIntent.putExtra("return-data", true);
                startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
            }
        });

        myAlertDialog.setNegativeButton(R.string.camara,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                pictureActionIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (pictureActionIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        Uri uri = Contrato.TablaFotos.CONTENT_URI;
                        ContentValues values = new ContentValues();
                        values.put(Contrato.TablaFotos.IDIMUEBLE,idInm);
                        values.put(Contrato.TablaFotos.FOTO, photoFile.getPath());
                        Uri u = getContentResolver().insert(uri,values);
                    } catch (IOException ex) {

                    }
                    if (photoFile != null) {
                        pictureActionIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
                        startActivityForResult(pictureActionIntent, CAMERA_REQUEST);
                    }
                }
            }
        });
        myAlertDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InputStream stream = null;
        Bitmap imagen=null;
        String currentDateTimeString;
        File archivo=null;
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_PICTURE:
                    try {
                        stream = getContentResolver().openInputStream(data.getData());
                        imagen = BitmapFactory.decodeStream(stream);
                        stream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    currentDateTimeString=currentDateTimeString.replace(" ","_");
                    currentDateTimeString=currentDateTimeString.replace("/","_");
                    archivo = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM),"inmueble_"+idInm+"_"+currentDateTimeString+".jpg");
                    try {
                        FileOutputStream out = new FileOutputStream(archivo);
                        imagen.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Uri uri = Contrato.TablaFotos.CONTENT_URI;
                    ContentValues values = new ContentValues();
                    values.put(Contrato.TablaFotos.IDIMUEBLE, idInm);
                    values.put(Contrato.TablaFotos.FOTO, archivo.getPath());
                    Uri u = getContentResolver().insert(uri,values);
                    break;
            }
        }
    }

    private File createImageFile() throws IOException {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        currentDateTimeString=currentDateTimeString.replace(" ","_");
        currentDateTimeString=currentDateTimeString.replace("/","_");
        currentDateTimeString=currentDateTimeString.replace("-","_");
        String imageFileName="inmueble_"+idInm+"_"+currentDateTimeString+"";
        File storageDir =  getExternalFilesDir(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir  );
        return image;
    }
}
