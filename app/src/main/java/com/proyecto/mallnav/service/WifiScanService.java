package com.proyecto.mallnav.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.proyecto.mallnav.R;
import com.proyecto.mallnav.models.AccessPoint;
import com.proyecto.mallnav.models.Cuadricula;
import com.proyecto.mallnav.models.Sector;
import com.proyecto.mallnav.ui.activities.MainActivity;
import com.proyecto.mallnav.utils.CuadriculaListProvider;
import com.proyecto.mallnav.viewmodel.NavigationViewModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WifiScanService extends Service {
    private static final String CHANNEL_ID = "WifiScanServiceChannel";
    private WifiManager wifiManager;
    private static NavigationViewModel viewModel;
    private Handler handler = new Handler();
    private static Set<String> bssidList = CuadriculaListProvider.obtenerBSSIDDeLista();
    private List<List<ScanResult>> fingerprintRaw = new ArrayList<>();
    private int captureCount = 0;
    public static Cuadricula nearestCuad;
    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            List<ScanResult> resultadosEscaneo = realizarEscaneoWifi();
            if(resultadosEscaneo !=null ){
                geolocalizarUsuario(resultadosEscaneo);
            }
            handler.postDelayed(this, 5000); // Escaneo cada 5 segundos
        }
    };

    private Runnable fprintRunnable = new Runnable() {
        @Override
        public void run() {
            if (captureCount < 12) {
                List<ScanResult> scanResults = realizarEscaneoWifi();
                if (scanResults != null) {
                    fingerprintRaw.add(scanResults);
                    captureCount++;
                    Log.d("Fingerprinting", "Captura " + captureCount + " realizada.");
                } else {
                    Log.e("Fingerprinting", "Error al realizar el escaneo Wi-Fi.");
                }
                handler.postDelayed(this, 5000); // Reprograma la siguiente captura
            } else {
                Log.d("Fingerprinting", "Capturas completas. Generando CSV...");

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Fingerprinting", "No cuentas con el permiso de almacenamiento externo publico");
                } else {
                    // Ya tiene el permiso, procede a generar los CSV
                    generarCsv(fingerprintRaw);
                }

                stopFingerprinting(); // Detén el proceso después de generar los archivos
            }
        }
    };



    @Override
    public void onCreate() {
        super.onCreate();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        nearestCuad = null;
        crearCanalNotificacion();
    }
    public static void setViewModel(NavigationViewModel vm) {
        viewModel = vm;
    }
    private List<ScanResult> realizarEscaneoWifi() {
        boolean success = wifiManager.startScan();
        if (success) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("PermisosUbicacion","No cuenta con los permisos de ubicacion necesarios");
                return null;
            }
            List<ScanResult> scanResults = wifiManager.getScanResults();
            return scanResults;

        } else {
            Log.e("WifiScanService", "Error en el escaneo Wi-Fi");
            return null;
        }
    }

    private void geolocalizarUsuario(List<ScanResult> scanResults){
        nearestCuad = getNearestCuad(scanResults);
        if (nearestCuad != null) {
            showCurrentPosition(nearestCuad.getNodo().getNodoX(), nearestCuad.getNodo().getNodoY());
            Toast.makeText(getApplicationContext(),"Estas en la cuadricula: "+nearestCuad.getCuadriculaId(), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"No es posible ubicarlo!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCurrentPosition(float nodoX, float nodoY) {
        if (viewModel != null) {
            viewModel.updatePosition(nodoX, nodoY);
        }
    }

    private Cuadricula getNearestCuad(List<ScanResult> scanResults) {
        Cuadricula nearestCuad = null;
        double distanciaMin = Double.MAX_VALUE;
        List<ScanResult> filteredScanResults = filterScanResults(scanResults);
        Log.d("CalculoDistancia","filteredScanResults: "+ filteredScanResults.size());

        for(Cuadricula cuadricula : CuadriculaListProvider.cuadriculaList){
            Log.d("CalculoDistancia","Cuadricula: "+ cuadricula.getCuadriculaId());
            List<AccessPoint> fingerprint = cuadricula.getListaRefinada();
            double distance = calcularDistancia(fingerprint, filteredScanResults);
            Log.d("CalculoDistancia","Distancia: "+ distance);
                if (distance < distanciaMin){
                    distanciaMin = distance;
                    nearestCuad = cuadricula;
                }
        }
        Log.d("CalculoDistancia","CuadriculaDevuelta: "+ nearestCuad.getCuadriculaId());
        Log.d("CalculoDistancia","DistanciaMinima: "+ distanciaMin);
        return nearestCuad;
    }

    private double calcularDistancia(List<AccessPoint> fingerprint, List<ScanResult> filteredScanResults){
        double mismatch = Double.MAX_VALUE;
        double suma = 0.0;
        int coincidencias = 0;

        for (AccessPoint ap : fingerprint) {
            for (ScanResult result : filteredScanResults){
                if (result.BSSID.equals(ap.getBssid())) {
                    Log.d("CalculoDistancia","ScanResultsCoincidentes, BSSID: "+ result.BSSID + ", RSSI: " + result.level);
                    suma = suma + Math.pow((ap.getRssi() - result.level), 2);
                    coincidencias++;
                }
            }
        }
        //Log.d("CalculoDistancia","Coincidencias: " + coincidencias);
        if (coincidencias > 0) {
            double distancia = Math.sqrt(suma);
            // Penalizar si hay pocas coincidencias
            distancia = distancia + ((filteredScanResults.size() - coincidencias)*50);
            return distancia;
        } else {
            return mismatch;  // Sin coincidencias, distancia máxima
        }
    }


    private List<ScanResult> filterScanResults(List<ScanResult> scanResults){
        List<ScanResult> filteredScanResults = new ArrayList<>();
        for(ScanResult result : scanResults){
            if (bssidList.contains(result.BSSID)) {
                filteredScanResults.add(result);
            }
        }
        return filteredScanResults;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            Log.e("WifiScanService", "Intent o acción nula, terminando el servicio.");
            stopSelf();
            return START_NOT_STICKY;
        }

        Intent notificationIntent = new Intent(this, MainActivity.class); // Cambia MainActivity a tu actividad principal
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification;

        handler.removeCallbacks(scanRunnable);
        handler.removeCallbacks(fprintRunnable);

        if(intent.getAction().equals("geolocalizar")) {
            notification = crearNotificacion("Servicio de Escaneo Wi-Fi",
                    "Escaneando redes Wi-Fi cercanas para geolocalización...",
                    R.drawable.bg_adjust_btn_active,
                    pendingIntent);

            startForeground(1, notification);
            handler.post(scanRunnable);
        }
        else {
            notification = crearNotificacion("Servicio de Escaneo Wi-Fi",
                    "Escaneando redes Wi-Fi cercanas para fingerprinting...",
                    R.drawable.network_check_24px,
                    pendingIntent);

            startForeground(2, notification);
            handler.post(fprintRunnable);
        }

        return START_STICKY;
    }

    private Notification crearNotificacion(String titulo, String texto, int icono, PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setSmallIcon(icono)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Canal de Servicio de Escaneo Wi-Fi",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void generarCsv(List<List<ScanResult>> fingerprintRaw) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File appFolder = new File(storageDir, "MallNav/FingerprintingData");


        if (!appFolder.exists()) {
            if (!appFolder.mkdirs()) {
                Log.e("generarCsv", "No se pudo crear el directorio");
                return;
            }
        }

        // Recorrer cada captura de fingerprintRaw
        for (int i = 0; i < fingerprintRaw.size(); i++) {
            List<ScanResult> scanResults = fingerprintRaw.get(i);

            // Crear el archivo CSV con un nombre único
            String fileName = "captura_" + (i+1) + ".csv";
            File file = new File(appFolder, fileName);

            try (FileWriter writer = new FileWriter(file)) {
                // Escribir encabezados
                writer.append("BSSID,SSID,RSSI\n");

                // Escribir datos de cada ScanResult
                for (ScanResult result : scanResults) {
                    writer.append(result.BSSID).append(",")
                            .append(result.SSID).append(",")
                            .append(String.valueOf(result.level)).append("\n");
                }

                Log.d("generarCsv", "Archivo generado: " + file.getAbsolutePath());

            } catch (IOException e) {
                Log.e("generarCsv", "Error al generar CSV", e);
            }
        }
        Toast.makeText(getApplicationContext(),"CSV generado",Toast.LENGTH_SHORT).show();
    }

    private void stopFingerprinting() {
        handler.removeCallbacks(fprintRunnable); // Asegura que no quede activo
        stopForeground(true); // Elimina la notificación persistente
        stopSelf(); // Detiene el servicio
        Log.d("Fingerprinting", "Servicio de fingerprinting detenido.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(fprintRunnable);//por seguridad
        handler.removeCallbacks(scanRunnable);
        Log.d("WifiScanService","El servicio ha sido detenido");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}