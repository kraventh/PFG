package es.dlacalle.pfg;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
 * Activities containing this fragment MUST implement the {@link es.dlacalle.pfg.ConfigFragment.ConfigFragmentListener}
 * interface.
 */
public class ConfigFragment extends Fragment implements AbsListView.OnItemClickListener {

    private ConfigFragmentListener mListener;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private MiArrayAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConfigFragment() {
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


        List<FilaAppList> aplicaciones = new ArrayList<>();
        PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> paquetes = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo infoPaquete : paquetes) {
            FilaAppList app = new FilaAppList();
            app.setNombreApp(infoPaquete.loadLabel(getActivity().getPackageManager()).toString());
            //Restringimos a Whatsapp y Maps
            if (app.getNombreApp().equals("WhatsApp") || app.getNombreApp().equals("Maps")) {
                app.setIcon(infoPaquete.loadIcon(getActivity().getPackageManager()));
                app.setNombrePaquete(infoPaquete.packageName);
                aplicaciones.add(app);
            }
        }
        mAdapter = new MiArrayAdapter(this.getActivity(), aplicaciones);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config, container, false);

        // Set the adapter
        /*
      The fragment's ListView/GridView.
     */
        AbsListView mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ConfigFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " debe implementar OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //if (null != mListener) {
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        //mListener.ConfigFragmentInteraction(DummyContent.ITEMS.get(position).id);
        //}

        //Si lo deselecciono reseteo la preferencia.
        if (this.mAdapter.getItem(position).getSeleccionado()) {
            this.mAdapter.getItem(position).setSeleccionado(false);
            mListener.ConfigFragmentInteraction("Ninguna", "No hay aplicación seleccionada",
                    R.drawable.ic_launcher);

        } else {
            //Si lo selecciono me aseguro que de los demás no está ninguno seleccionado
            // y asigno los datos de la aplicación seleccionada

            for (int i = 0; i < this.mAdapter.getCount(); i++) {
                this.mAdapter.getItem(i).setSeleccionado(false);
            }
            this.mAdapter.getItem(position).setSeleccionado(true);

            //Paso la información al activity para que la redirija al NotificacionesFragment
            //ya que los fragments no deben comunicarse nunca directamente entre ellos segun
            //la documentación de google

            mListener.ConfigFragmentInteraction(
                    this.mAdapter.getItem(position).getNombreApp(),
                    this.mAdapter.getItem(position).getNombrePaquete(),
                    this.mAdapter.getItem(position).getIcon()
            );

        }
        this.mAdapter.notifyDataSetChanged();

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

    public interface ConfigFragmentListener {
        public void ConfigFragmentInteraction(String nombre, String paquete, Object icono);

    }


}
