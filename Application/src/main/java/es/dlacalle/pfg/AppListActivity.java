package es.dlacalle.pfg;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
public class AppListActivity extends ListActivity implements AbsListView.OnItemClickListener {

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private MiArrayAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AppListActivity() {
    }

    /* Pasos para crear mi propio ArrayAdapter a fin de utilizar un ListView personalizado
            * 1. Crear la clase que guardará los objetos, con sus propiedades, getters y setters.(Ver FilaAppList)
            * 2. Crear la clase MiArrayAdapter que extiende ArrayAdapter<FilaAppList>
            * 3. Creamos una List<FilaAppList> que rellenaremos con un ArrayList de objetos tipo FilaAppList
            * 4. Cargamos los datos en el ArrayList
            * 5. Instanciamos MiArrayAdapter pasándole la List de objetos FilaAppList
            * 6. Asignamos el ArrayAdapter a la ListView de la ListActivity mediante setListAdapter
            *   porque la Activity es de tipo ListActivity, si fuera tipo Activity habría que crear
            *   el ListView, asociarlo con su elemento del Layout y utilizar el método setAdapter. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_config);


        List<FilaAppList> aplicaciones = new ArrayList<>();
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> paquetes = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo infoPaquete : paquetes) {
            FilaAppList app = new FilaAppList();
            app.setNombreApp(infoPaquete.loadLabel(getPackageManager()).toString());
            //Restringimos a Whatsapp y Maps
            if (app.getNombreApp().equals("WhatsApp") || app.getNombreApp().equals("Maps")) {
                app.setIcon(infoPaquete.loadIcon(getPackageManager()));
                app.setNombrePaquete(infoPaquete.packageName);
                aplicaciones.add(app);
            }
        }
        mAdapter = new MiArrayAdapter(this, aplicaciones);

        AbsListView mListView = (AbsListView) findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //if (null != mListener) {
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        //mListener.ConfigFragmentInteraction(DummyContent.ITEMS.get(position).id);
        //}
        String titulo;
        String paquete;

        //Si lo deselecciono reseteo la preferencia.
        if (this.mAdapter.getItem(position).getSeleccionado()) {
            this.mAdapter.getItem(position).setSeleccionado(false);
            titulo = "Ninguna";
            paquete = "No hay aplicación seleccionada";

        } else {
            //Si lo selecciono me aseguro que de los demás no está ninguno seleccionado
            // y asigno los datos de la aplicación seleccionada

            for (int i = 0; i < this.mAdapter.getCount(); i++) {
                this.mAdapter.getItem(i).setSeleccionado(false);
            }
            this.mAdapter.getItem(position).setSeleccionado(true);
            titulo = this.mAdapter.getItem(position).getNombreApp();
            paquete = this.mAdapter.getItem(position).getNombrePaquete();


        }
        this.mAdapter.notifyDataSetChanged();

        // Actualizo las preferencias
        SharedPreferences.Editor editPrefs = getPreferences(MODE_PRIVATE).edit();

        editPrefs.putString("app_monitorizada_titulo", titulo);
        editPrefs.putString("app_monitorizada_paquete", paquete);

        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}
