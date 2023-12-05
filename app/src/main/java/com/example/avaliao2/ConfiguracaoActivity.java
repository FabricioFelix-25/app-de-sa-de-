package com.example.avaliao2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

public class ConfiguracaoActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ConfiguracaoPrefs";
    private static final String PREF_TIPO_EXERCICIO = "tipoExercicio";
    private static final String PREF_UNIDADE_VELOCIDADE = "unidadeVelocidade";
    private static final String PREF_ORIENTACAO_MAPA = "orientacaoMapa";
    private static final String PREF_TIPO_MAPA = "tipoMapa";
    private static final String PREF_ESTILO_MAPA = "estiloMapa";
    private RadioGroup radioGroupTipoMapa;

    private RadioGroup radioGroupTipoExercicio;
    private RadioGroup radioGroupUnidadeVelocidade;
    private RadioGroup radioGroupOrientacaoMapa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        radioGroupTipoExercicio = findViewById(R.id.radioGroupTipoExercicio);
        radioGroupUnidadeVelocidade = findViewById(R.id.radioGroupUnidadeVelocidade);
        radioGroupOrientacaoMapa = findViewById(R.id.radioGroupOrientacaoMapa);
        radioGroupTipoMapa = findViewById(R.id.radioGroupTipoMapa);

        // Carregar as configurações salvas
        carregarConfiguracoes();
    }

    private void salvarConfiguracoes() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        // Salvar as configurações no SharedPreferences
        editor.putString(PREF_TIPO_EXERCICIO, obterTextoSelecionado(radioGroupTipoExercicio));
        editor.putString(PREF_UNIDADE_VELOCIDADE, obterTextoSelecionado(radioGroupUnidadeVelocidade));
        editor.putString(PREF_ORIENTACAO_MAPA, obterTextoSelecionado(radioGroupOrientacaoMapa));
        editor.putString(PREF_TIPO_MAPA, obterTextoSelecionado(radioGroupTipoMapa));

        editor.apply();
    }

    private void carregarConfiguracoes() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Carregar as configurações salvas e definir os RadioButtons selecionados
        selecionarRadioButtonPorTexto(radioGroupTipoExercicio, settings.getString(PREF_TIPO_EXERCICIO, ""));
        selecionarRadioButtonPorTexto(radioGroupUnidadeVelocidade, settings.getString(PREF_UNIDADE_VELOCIDADE, ""));
        selecionarRadioButtonPorTexto(radioGroupOrientacaoMapa, settings.getString(PREF_ORIENTACAO_MAPA, ""));
        selecionarRadioButtonPorTexto(radioGroupTipoMapa, settings.getString(PREF_TIPO_MAPA, ""));
    }

    private void selecionarRadioButtonPorTexto(RadioGroup radioGroup, String texto) {
        if (texto != null && !texto.isEmpty()) {
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
    }

    private String obterTextoSelecionado(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != View.NO_ID) {
            RadioButton radioButton = findViewById(selectedId);
            return radioButton.getText().toString();
        }
        return "";
    }

    @Override
    protected void onPause() {
        super.onPause();
        salvarConfiguracoes(); // Salvar configurações quando a atividade está pausada
    }
}

