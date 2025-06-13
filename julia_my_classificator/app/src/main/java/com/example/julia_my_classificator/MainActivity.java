package com.example.julia_my_classificator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends Activity {
    private float luz = 0f;
    private float prox = 0f;
    private TextView txtLuz, txtProx;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLuz = findViewById(R.id.text_luz);
        txtProx = findViewById(R.id.text_proximidade);
        btn = findViewById(R.id.btn_devolver);

        Intent it = getIntent();
        if (it != null){
            luz = it.getFloatExtra("luminosidade", 0f);
            prox = it.getFloatExtra("proximidade", 0f);
            txtLuz.setText("Luminosidade: " + luz + " lx");
            txtProx.setText("Proximidade: " + prox + " cm");
        }
        btn.setOnClickListener(v -> {
            boolean luzFraca = luz < 20.0;
            boolean distante = prox > 3.0;

            Intent resultIt = new Intent();
            resultIt.putExtra("luz_fraca", luzFraca);
            resultIt.putExtra("distante", distante);
            setResult(RESULT_OK, resultIt);
            finish();
        });

    }
}