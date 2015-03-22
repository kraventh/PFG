package es.dlacalle.pfg;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link es.dlacalle.pfg.NotificacionesFragment.NotificationFragmentListener} interface
 * to handle interaction events.
 * Use the {@link NotificacionesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificacionesFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private NotificationFragmentListener mListener;

    private Preference app_monitorizada;

    public NotificacionesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.notificaciones_prefs);

        Preference limpia_cache = findPreference("limpiar_historico_notif");
        limpia_cache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference pref) {

                ((MainActivity) getActivity()).textView.setText("");
                Toast.makeText(getActivity().getApplicationContext(), "Histórico eliminado", Toast.LENGTH_SHORT).show();

                return true;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {

            case "habilitar_historico_notificaciones":
                CheckBoxPreference cb_hhn = (CheckBoxPreference) findPreference(key);
                if (cb_hhn.isChecked()) ((MainActivity) getActivity()).textView.setText("");
                else ((MainActivity) getActivity()).textView.
                        setText("Histórico deshabilitado. Habilítelo en el menú \"Notificaciones\"");

                break;
        }


    }

    /*
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment

    
            return inflater.inflate(R.layout.fragment_notificaciones, container, false);
        }
    */
    public void actualizaAppMonitorizada(String nombre, String paquete, Drawable icono) {
        app_monitorizada = findPreference("app_monitorizada");
        app_monitorizada.setTitle(nombre);
        app_monitorizada.setSummary(paquete);
        app_monitorizada.setIcon(icono);

    }

    public void actualizaAppMonitorizada(String nombre, String paquete, int icono) {
        app_monitorizada = findPreference("app_monitorizada");
        app_monitorizada.setTitle(nombre);
        app_monitorizada.setSummary(paquete);
        app_monitorizada.setIcon(icono);

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NotificationFragmentListener) activity;
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
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
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
    public interface NotificationFragmentListener {
        public void onFragmentInteraction(Uri uri);
    }


}
