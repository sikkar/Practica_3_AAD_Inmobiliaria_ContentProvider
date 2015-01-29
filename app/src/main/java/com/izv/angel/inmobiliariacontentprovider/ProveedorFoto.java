package com.izv.angel.inmobiliariacontentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class ProveedorFoto extends ContentProvider {

    public static String AUTORIDAD = "com.izv.angel.inmobiliariacontentprovider.proveedorfoto";
    private Ayudante adb;
    private static final UriMatcher convierteUri2Int;
    private static final int FOTOS = 1;
    private static final int FOTO_ID = 2;

    static{
        convierteUri2Int = new UriMatcher(UriMatcher.NO_MATCH);
        convierteUri2Int.addURI(AUTORIDAD,Contrato.TablaFotos.TABLA, FOTOS);
        convierteUri2Int.addURI(AUTORIDAD,Contrato.TablaFotos.TABLA + "/#", FOTO_ID);
    }

    public ProveedorFoto() {
    }

    @Override
    public boolean onCreate() {
        adb = new Ayudante(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {
        switch (convierteUri2Int.match(uri)) {
            case FOTOS:
                return Contrato.TablaFotos.CONTENT_TYPE_FOTOS;
            case FOTO_ID:
                return Contrato.TablaFotos.CONTENT_TYPE_FOTO;
            default:
                return null;
        }
    }

    @Override
    public int delete(Uri uri, String condicion, String[] parametros) {
        SQLiteDatabase db = adb.getWritableDatabase();
        switch (convierteUri2Int.match(uri)) {
            case FOTOS: break;
            case FOTO_ID:
                condicion = condicion + "_id = " + uri.getLastPathSegment();
                parametros = new String[]{uri.getLastPathSegment()};
                break;
            default: throw new IllegalArgumentException("URI " + uri);
        }
        int cuenta = db.delete(Contrato.TablaFotos.TABLA, condicion, parametros);
        getContext().getContentResolver().notifyChange(uri, null);
        return cuenta;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (convierteUri2Int.match(uri) != FOTOS) {
            throw new IllegalArgumentException("URI " + uri);
        }
        SQLiteDatabase bd = adb.getWritableDatabase();
        long id = bd.insert(Contrato.TablaFotos.TABLA, null, values);
        if (id > 0) {
            Uri uriElemento = ContentUris.withAppendedId(Contrato.TablaFotos.CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(uriElemento, null);
            return uriElemento;
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues valores, String condicion, String[] parametros) {
        SQLiteDatabase db = adb.getWritableDatabase();
        int cuenta;
        switch (convierteUri2Int.match(uri)) {
            case FOTO_ID:
                condicion= Contrato.TablaFotos.TABLA+ " = ? ";
                parametros = new String[]{uri.getLastPathSegment()};
                break;
            case FOTOS:
                break;
            default: throw new IllegalArgumentException("URI " + uri);
        }
        cuenta = db.update(Contrato.TablaFotos.TABLA, valores, condicion,parametros);
        getContext().getContentResolver().notifyChange(uri, null);
        return cuenta;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (convierteUri2Int.match(uri)) {
            case FOTOS:
                break;
            case FOTO_ID:
                selection = Contrato.TablaFotos.IDIMUEBLE + " = ? ";
                //selectionArgs=new String[] {uri.getLastPathSegment()};
                break;
            default:
                throw new IllegalArgumentException("URI " + uri);
        }
        SQLiteDatabase bd = adb.getReadableDatabase();
        Cursor cursor = bd.query(Contrato.TablaFotos.TABLA,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
}
