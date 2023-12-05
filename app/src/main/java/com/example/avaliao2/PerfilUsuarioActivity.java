package com.example.avaliao2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "PerfilUsuarioPrefs";
    private static final String PREF_SEXO = "sexo";
    private static final String PREF_PESO = "peso";
    private static final String PREF_ALTURA = "altura";
    private static final String PREF_DATA_NASCIMENTO = "dataNascimento";

    private RadioGroup radioGroupSexo;
    private EditText editTextPeso;
    private EditText editTextAltura;
    private EditText editTextDataNascimento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        radioGroupSexo = findViewById(R.id.radioGroupSexo);
        editTextPeso = findViewById(R.id.editTextPeso);
        editTextAltura = findViewById(R.id.editTextAltura);
        editTextDataNascimento = findViewById(R.id.editTextDataNascimento);

        // Carregar as configurações salvas
        carregarConfiguracoes();

        // Adicionar listeners de texto
        adicionarListenerDeTexto(editTextPeso);
        adicionarListenerDeTexto(editTextAltura);
        adicionarListenerDeTexto(editTextDataNascimento);

        // Adicionar listener de seleção de sexo
        radioGroupSexo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                salvarConfiguracoes();
            }
        });
    }

    private void salvarConfiguracoes() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        // Salvar as configurações no SharedPreferences
        editor.putString(PREF_SEXO, obterSexoSelecionado());
        editor.putString(PREF_PESO, editTextPeso.getText().toString());
        editor.putString(PREF_ALTURA, editTextAltura.getText().toString());
        editor.putString(PREF_DATA_NASCIMENTO, editTextDataNascimento.getText().toString());

        editor.apply();
    }

    private void carregarConfiguracoes() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Carregar as configurações salvas e definir os RadioButtons selecionados
        selecionarRadioButtonPorTexto(radioGroupSexo, settings.getString(PREF_SEXO, ""));
        editTextPeso.setText(settings.getString(PREF_PESO, ""));
        editTextAltura.setText(settings.getString(PREF_ALTURA, ""));
        editTextDataNascimento.setText(settings.getString(PREF_DATA_NASCIMENTO, ""));
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

    private String obterSexoSelecionado() {
        int selectedId = radioGroupSexo.getCheckedRadioButtonId();
        if (selectedId != View.NO_ID) {
            RadioButton radioButton = findViewById(selectedId);
            return radioButton.getText().toString();
        }
        return "";
    }

    private void adicionarListenerDeTexto(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Salvar automaticamente quando houver alterações nos campos de texto
                salvarConfiguracoes();
            }
        });
    }
}
