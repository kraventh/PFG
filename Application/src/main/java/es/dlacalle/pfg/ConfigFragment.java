package es.dlacalle.pfg;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ConfigFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private MiArrayAdapter mAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConfigFragment.
     */
    public static ConfigFragment newInstance() {
        ConfigFragment fragment = new ConfigFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


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

        //addPreferencesFromResource(R.xml.preferencias);

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
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }

        if (this.mAdapter.getItem(position).getSeleccionado())
            this.mAdapter.getItem(position).setSeleccionado(false);
        else this.mAdapter.getItem(position).setSeleccionado(true);
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
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }


}
