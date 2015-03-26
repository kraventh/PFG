/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package es.dlacalle.pfg;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import es.dlacalle.common.activities.SampleActivityBase;
import es.dlacalle.common.logger.Log;
import es.dlacalle.common.logger.LogWrapper;
import es.dlacalle.common.logger.MessageOnlyLogFilter;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p/>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */

public class MainActivity extends SampleActivityBase
        implements ConfigFragment.ConfigFragmentListener,
        NotificacionesFragment.NotificationFragmentListener {

    public static final String TAG = "MainActivity";

    //Campos
    public TextView textView;

    //Fragments
    private PFGFragment pfgFragment;
    private ConfigFragment configFragment;
    private NotificacionesFragment notifiFragment;

    //Receiver para las notificaciones
    private NotificationReceiver nReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FRagments
        pfgFragment = new PFGFragment();
        configFragment = new ConfigFragment();
        notifiFragment = new NotificacionesFragment();

        //Receiver para las notificaciones
        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("es.dlacalle.pfg.servicios.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver, filter);

        //Campo
        textView = (TextView) findViewById(R.id.principal_textview);


        if (savedInstanceState == null) {

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Add the fragment to the 'fragment_container' FrameLayout
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back

            transaction.replace(R.id.sample_content_fragment, notifiFragment, "notif").commit();
            getFragmentManager().beginTransaction().replace(R.id.sample_content_fragment, configFragment, "config").commit();
            getFragmentManager().beginTransaction().replace(R.id.sample_content_fragment, pfgFragment, "pfg").commit();

            //getEstadoGeneral();

            /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Map<String, ?> keys = preferences.getAll();

            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                textView.setText(textView.getText() + "\n" + entry.getKey() + ": " +
                        entry.getValue().toString());
            }*/

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect_scan_bt: {
                // Launch the DeviceListActivity to see devices and do scan
/*                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, Constants.REQUEST_CONNECT_DEVICE_SECURE);
                return true;*/

                if (!(getFragmentManager().findFragmentByTag("pfg").isVisible())) {
                    pfgFragment.onOptionsItemSelected(item);
                    //}
                    //case R.id.menu_home: {

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.sample_content_fragment, pfgFragment);
                    transaction.addToBackStack(null);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.commit();
                }
                pfgFragment.getEstadoGeneral();
                break;
            }
            case R.id.menu_settings: {
                //Cambia al ConfigFragment para seleccionar la aplicación

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.sample_content_fragment, configFragment);
                transaction.addToBackStack(null);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
                break;
            }
            case R.id.menu_act_notificaciones: {
                //Cambia al ConfigFragment para seleccionar la aplicación

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.sample_content_fragment, notifiFragment);
                transaction.addToBackStack(null);

                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void onButtonClick(View view) {
        if (isExternalStorageWritable()) {
            try {
                // Ruta a la tarjetaSD
                File sdcard = Environment.getExternalStorageDirectory();

                // Añadimos la ruta a nuestro directorio
                File dir = new File(sdcard.getAbsolutePath() + "/PFG/");

                // Creamos nuestro directorio
                if (!dir.mkdir()) Log.d(TAG, "Directorio no creado");

                // Creamos el archivo en el que grabaremos los datos
                File file = new File(dir, "pfg.log");

                //Creamos el stream de salida
                FileOutputStream os = new FileOutputStream(file);

                //Recuperamos el texto a guardar
                String data = textView.getText().toString();

                //Lo escribimos y cerramos
                os.write(data.getBytes());
                os.close();
            } catch (Exception e) {
                Log.d(TAG, "Error guardando log");
            }
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Create a chain of targets that will receive log data
     */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        /*LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());
*/
        Log.i(TAG, "Ready");
    }

    //Función que prepara el texto para enviar al dispositivo externo
    public void parseNotifMaps(String notifText) {
        String mensaje = "maps;";
        boolean rotonda = false;

        Log.d(TAG, "Mensaje=> " + mensaje);

        try {
            //Primero elimino el nombre del destino para facilitar las extracciones posteriores
            notifText = notifText.substring(notifText.indexOf(": "));
            //
            // Empezamos con las situaciones
            //
            // el mensaje lo compondremos de la siguiente manera:
            // maps;direccion_a_pintar;

            if (notifText.contains("Buscando GPS")) {
                mensaje += "Esperando GPS...";
            }
            //Me da igual norte o sur, es siempre salir recto
            else if (notifText.contains("norte") || notifText.contains("sur")) {
                mensaje += "arriba;"; //Direccion a pintar

            }
            //Puede que tengamos que girar
            else if (notifText.startsWith("Gira")) {
                // a la izquierda
                if (notifText.contains("izquierda")) mensaje += "izquierda;";
                    //o a la derecha
                else mensaje += "derecha;";
            }
            //O puede ser una rotonda
            else if (notifText.contains("rotonda")) {
                rotonda = true;
                //Estoy considerando una rotonda como máximo de 5 salidas
                if (notifText.contains("primera")) mensaje += "rotonda1;";
                else if (notifText.contains("segunda")) mensaje += "rotonda2;";
                else if (notifText.contains("tercera")) mensaje += "rotonda3;";
                else if (notifText.contains("cuarta")) mensaje += "rotonda4;";
                else if (notifText.contains("quinta")) mensaje += "rotonda5;";
                else if (notifText.contains("recto")) mensaje += "rotondaRecto;";
                    //O puede ser que estés en una de esas rotondas con urbanización incluida y
                    //tengas que salirte ya por la próxima salida
                else if (notifText.startsWith("Sal de la rotonda")) {
                    mensaje += "rotondaSalida;";
                }

                //si no sales en
                mensaje += notifText.substring(notifText.indexOf(Constants.EN) + Constants.EN.length()) + ";";
                //sales hacia
                mensaje += notifText.substring(notifText.lastIndexOf(Constants.HACIA) + Constants.HACIA.length()) + ";";
            }

            // O puede que te tengas que incorporar a una autovía o autopista
            else if (notifText.startsWith(Constants.INCORPORATE)) {
                mensaje += notifText.substring(Constants.INCORPORATE.length()) + ";";
            }

            // O puede que simplemente tengas que continuar por donde vas
            else if (notifText.startsWith(Constants.CONTINUA)) {
                mensaje += notifText.substring(Constants.CONTINUA.length()) + ";";
            }

            Log.d(TAG, "Mensaje prerotonda => " + mensaje);

            if (!rotonda) {
                //Añadimos el nombre de la vía por la que vamos
                if (notifText.contains("en ") && notifText.contains("hacia")) {
                    int inicio = notifText.indexOf("en ");
                    int fin = notifText.lastIndexOf("hacia");

                    Log.d(TAG, "Que mierda pasa" + inicio + ":" + fin);
                    String tmp = notifText.substring(inicio + 3, fin-1);
                    mensaje = mensaje + tmp + ";";
                }
                //Añadimos el nombre de la vía a la que nos dirigimos
                if (notifText.contains("hacia")) {
                    int fin = notifText.lastIndexOf("hacia");
                    String tmp = notifText.substring(fin + 6, notifText.indexOf('\n'));
                    mensaje = mensaje + tmp + ";";
                }
            }

            Log.d(TAG, "Mensaje posrotonda => " + mensaje);
            //Añadimos la distancia al destino estimada en tiempo y kilometros
            if(notifText.contains(" hasta el destino")) {
                mensaje += notifText.substring(notifText.indexOf('\n')+2, notifText.indexOf(" hasta el destino")) + ";";
                //Añadimos la hora aproximada de llegada
                mensaje += notifText.substring(notifText.indexOf("aproximada a las ") + 17);
            }
            Log.d(TAG, "Mensaje=> " + mensaje);

            textView.setText(mensaje + "\n\n" + textView.getText());
        } catch (NullPointerException e) {
            mensaje = (String) textView.getText();
            textView.setText("Instrucción errónea" + "\n\n" + mensaje);
        }
    }

    //Interfaces

    //Interfaces con ConfigFragment
    public void ConfigFragmentInteraction(String nombre, String paquete, Object icono) {
        notifiFragment.actualizaAppMonitorizada(nombre, paquete, icono);
    }


    //Interfaces con NotificacionFragment
    public void onFragmentInteraction(Uri uri) {

    }

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = "";
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            //Obtengo el nombre del paquete cuyas notificaciones quiero atender
            //"Ninguna" es el valor que devuelve por defecto si el campo estuviese vacío
            if (pref.getBoolean("habilitar_historico_notificaciones", false)) {
                String prefPackage = pref.getString("app_monitorizada_paquete", "Ninguna");
                try {
                    String pkgName = intent.getStringExtra("notification_paquete");
                    String notificacion = intent.getStringExtra("notification_event");
                    temp = notificacion + "\n\n" + textView.getText();

                    if (pkgName.equals(prefPackage)) {
                        textView.setText(temp);
                        Log.d(TAG, "Notificacion recibida");

                        if (pkgName.equals(Constants.MAPS)) {
                            Log.d(TAG, "Es de Maps=> " + notificacion);

                            parseNotifMaps(notificacion);
                        }
                    }
                } catch (Exception e) {
                    if (pref.getBoolean("mostrar_notif_desestimadas", false)) {
                        textView.setText("Notificación desestimada: " + temp + "\n\n");
                    }
                }
            }


        }
    }

}
