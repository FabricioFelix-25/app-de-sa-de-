package com.example.avaliao2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.avaliao2.databinding.ActivityMonitorarExercicioBinding;
import com.google.android.gms.maps.GoogleMap;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnMonitorar = findViewById(R.id.btnMonitorar);
        Button btnHistorico = findViewById(R.id.btnHistorico);
        Button btnPerfil = findViewById(R.id.btnPerfil);
        Button btnConfiguracao = findViewById(R.id.btnConfiguracao);
        Button btnSobre = findViewById(R.id.btnSobre);

        btnMonitorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, MapsActivity.class);
               startActivity(intent);

           }
        });

        btnHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PerfilUsuarioActivity.class);
                startActivity(intent);
            }
        });

        btnConfiguracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, ConfiguracaoActivity.class);
               startActivity(intent);
            }
        });

        btnSobre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SobreActivity.class);
                startActivity(intent);
            }
        });
    }
}