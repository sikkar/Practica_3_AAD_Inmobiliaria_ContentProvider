package com.izv.angel.inmobiliariacontentprovider;


import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentoDos extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private AdaptadorFotos adf;
    private ListView lv;
    private long itemid;



    public FragmentoDos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragmento_dos, container, false);
    }

    public void llenarListView(long id) {
        Log.v("count",Principal.count+"");
        itemid=id;
        getLoaderManager().initLoader(Principal.count, null, this);
        adf = new AdaptadorFotos(getActivity().getBaseContext(),null);
        lv = (ListView) getActivity().findViewById(R.id.listView2);
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
        Principal.count++;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Contrato.TablaFotos.CONTENT_URI;
        String seleccion = Contrato.TablaFotos.IDIMUEBLE + " = ? ";
        String argu[] = new String[]{String.valueOf(itemid)};
        CursorLoader curl =new CursorLoader( getActivity().getBaseContext(), uri, null, seleccion, argu , Contrato.TablaFotos._ID +" collate localized asc");
        Log.v("cursorini", curl.getSelection());
        return curl;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Principal.count=0;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adf.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adf.swapCursor(null);
    }
}
