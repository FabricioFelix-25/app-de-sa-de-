package com.example.avaliao2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final String PREFS_NAME = "ConfiguracaoPrefs";
    private static final String PREF_TIPO_EXERCICIO = "tipoExercicio";
    private static final String PREF_UNIDADE_VELOCIDADE = "unidadeVelocidade";
    private static final String PREF_ORIENTACAO_MAPA = "orientacaoMapa";
    private static final String PREF_TIPO_MAPA = "tipoMapa";
    private String tipoExercicioSelecionado;
    private String unidadeVelocidadeSelecionada;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location lastLocation;
    private boolean isGPSEnabled;

    private TextView txtVelocidade, txtDistancia, txtChronometer;
    private Chronometer chronometer;
    private double distanciaTotal = 0;
    private static final int REQUEST_PERMISSIONS_LOCATION = 1;
    private static final int REQUEST_LOCATION = 2;

    private RadioGroup radioGroupTipoExercicio, radioGroupUnidadeVelocidade, radioGroupOrientacaoMapa, radioGroupTipoMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps1);

        radioGroupTipoExercicio = findViewById(R.id.radioGroupTipoExercicio);
        radioGroupUnidadeVelocidade = findViewById(R.id.radioGroupUnidadeVelocidade);
        radioGroupOrientacaoMapa = findViewById(R.id.radioGroupOrientacaoMapa);
        radioGroupTipoMapa = findViewById(R.id.radioGroupTipoMapa);

        initializeViews();
        initializeMap();
        checkAndRequestPermissions();
        checkGPSStatus();
        carregarConfiguracoes();
        
        radioGroupTipoMapa.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);
            String estiloMapaSelecionado = radioButton.getText().toString();

            // Salvar a escolha do usuário nas configurações
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(PREF_TIPO_MAPA, estiloMapaSelecionado);
            editor.apply();

            // Atualizar o estilo do mapa (se necessário)
            carregarConfiguracoes();
        });
    }

    private void atualizarEstiloMapa(String estiloMapa) {
        if (mMap == null) {
            return; // O mapa ainda não foi inicializado
        }

        switch (estiloMapa) {
            case "Normal":
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "Satélite":
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            // Adicionar mais casos conforme necessário para outros estilos de mapa
        }
    }



    private void initializeViews() {
        txtVelocidade = findViewById(R.id.txtVelocidade);
        txtDistancia = findViewById(R.id.txtDistancia);
        txtChronometer = findViewById(R.id.txtChronometer);
        //chronometer = findViewById(R.id.txtChronometer);
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setupMap();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_LOCATION);
        }
    }

    private void checkGPSStatus() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
           // Log.d(TAG, "GPS desativado.");
        }
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                habilitaMyLocation();
            } else {
                Toast.makeText(this, "Sem permissão para mostrar sua localização", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings mapUI = mMap.getUiSettings();
        mapUI.setAllGesturesEnabled(true);
        mapUI.setCompassEnabled(true);
        mapUI.setZoomControlsEnabled(true);

        // Define tipo do mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMapType(googleMap.MAP_TYPE_SATELLITE);
// habilita mapas indoor e 3D
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);

        habilitaMyLocation();

        carregarConfiguracoes();
    }

    private void habilitaMyLocation() {
        mMap.setOnMyLocationButtonClickListener(() -> {
            Toast.makeText(MapsActivity.this, "Clicou botão de localização", Toast.LENGTH_SHORT).show();
            return false;
        });

        mMap.setOnMyLocationClickListener(location -> {
            Toast.makeText(MapsActivity.this, "Current location (Lat,Lon,Alt):\n" +
                    "(" + location.getLatitude() + "," + location.getLongitude() + ","
                    + location.getAltitude() + ")", Toast.LENGTH_LONG).show();
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        if (lastLocation != null) {
            LatLng userLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Você está aqui"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

            if (chronometer == null) {
                //chronometer = findViewById(R.id.txtChronometer);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Você está aqui"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

        updateLocation(location);
    }

    private void carregarConfiguracoes() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Carregar as configurações salvas e definir os RadioButtons selecionados
        tipoExercicioSelecionado = settings.getString(PREF_TIPO_EXERCICIO, "");
        unidadeVelocidadeSelecionada = settings.getString(PREF_UNIDADE_VELOCIDADE, "");

        selecionarRadioButtonPorTexto(radioGroupTipoExercicio, tipoExercicioSelecionado);
        selecionarRadioButtonPorTexto(radioGroupUnidadeVelocidade, unidadeVelocidadeSelecionada);
        selecionarRadioButtonPorTexto(radioGroupOrientacaoMapa, settings.getString(PREF_ORIENTACAO_MAPA, ""));
        selecionarRadioButtonPorTexto(radioGroupTipoMapa, settings.getString(PREF_TIPO_MAPA, ""));

        String tipoMapaSelecionado = settings.getString(PREF_TIPO_MAPA, "");
        selecionarRadioButtonPorTexto(radioGroupTipoMapa, tipoMapaSelecionado);

        if (tipoMapaSelecionado.isEmpty()) {
            selecionarRadioButtonPorTexto(radioGroupTipoMapa, "Normal");

        }
        atualizarEstiloMapa(tipoMapaSelecionado);
    }

    private void selecionarRadioButtonPorTexto(RadioGroup radioGroup, String texto) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View view = radioGroup.getChildAt(i);
            if (view instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) view;
                if (radioButton.getText().toString().equals(texto)) {
                    radioButton.setChecked(true);
                    break;
                }
            }
        }
    }


    private void updateLocation(Location newLocation) {
        LatLng newLatLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(newLatLng).title("Nova posição"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));

        if (lastLocation != null) {
            distanciaTotal += lastLocation.distanceTo(newLocation);
            txtDistancia.setText("Distância: " + distanciaTotal + " metros");
        }

        float velocidade = newLocation.getSpeed();
        txtVelocidade.setText("Velocidade: " + velocidade + " m/s");

        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        txtChronometer.setText("Tempo: " + formatElapsedTime(elapsedMillis));

        lastLocation = newLocation;

        updateLocationDetails(newLocation);
    }
    private void updateLocationDetails(Location newLocation) {
        LatLng newLatLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(newLatLng).title("Nova posição"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));

        if (lastLocation != null) {
            distanciaTotal += lastLocation.distanceTo(newLocation);
            txtDistancia.setText("Distância: " + distanciaTotal + " metros");
        }

        // Usar unidade de velocidade configurada
        float velocidade = newLocation.getSpeed();
        if ("Kilômetros por hora".equals(unidadeVelocidadeSelecionada)) {
            velocidade = velocidade * 3.6f; // Converter de metros por segundo para quilômetros por hora
        }
        txtVelocidade.setText("Velocidade: " + velocidade + " " + unidadeVelocidadeSelecionada);

        // Calcular e exibir o gasto calórico
        calcularEGastarCalorias(newLocation);
        // ... (restante do código)
    }
    private void calcularEGastarCalorias(Location newLocation) {
        // Implemente a lógica para calcular e exibir o gasto calórico aqui
        // Use tipoExercicioSelecionado para determinar o tipo de exercício escolhido
    }

    private String formatElapsedTime(long elapsedMillis) {
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
        int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }
}
