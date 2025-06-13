package com.example.julia_my_sensor;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import androidx.appcompat.widget.SwitchCompat;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends Activity implements SensorEventListener {
    private static final int REQUEST_CODE_CLASSIFICACAO = 1001;
    private SensorManager sensorManager;
    private Sensor sensorLuz, sensorProximidade;
    private float ultimaluz = 0f;
    private float ultimaprox = 0f;
    private SwitchCompat switchLanterna, switchVibracao;
    private Button btn;

    LanternaHelper lanterna;
    MotorHelper motor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchLanterna = findViewById(R.id.switch_lanterna);
        switchVibracao = findViewById(R.id.switch_motor);
        btn = findViewById(R.id.btn);

        switchLanterna.setEnabled(false);
        switchVibracao.setEnabled(false);

        lanterna = new LanternaHelper(this);
        motor = new MotorHelper(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorProximidade = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        btn.setOnClickListener(v -> enviarLeituras(v));
    }
    protected void onResume(){
        super.onResume();
        if (sensorLuz != null) {
            sensorManager.registerListener(this, sensorLuz, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorProximidade != null) {
            sensorManager.registerListener(this, sensorProximidade, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
        lanterna.desligar();
        motor.pararVibracao();
    }

    protected void onDestroy(){
        super.onDestroy();
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Lógica de leitura do sensor
        Sensor sn = event.sensor;

        if(sn.getType() == Sensor.TYPE_LIGHT){
           ultimaluz = event.values[0];
        } else if (sn.getType() == Sensor.TYPE_PROXIMITY){
            ultimaprox = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // não preciso dessa informação
    }

    public void enviarLeituras(View v){
        Intent it = new Intent("com.example.ACTION_CLASSIFICAR");
        it.putExtra("luminosidade", ultimaluz);
        it.putExtra("proximidade", ultimaprox);
        startActivityForResult(it, REQUEST_CODE_CLASSIFICACAO);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_CLASSIFICACAO && resultCode == RESULT_OK && data != null){
            boolean luzFraca = data.getBooleanExtra("luz_fraca", false);
            boolean longe = data.getBooleanExtra("distante", false);

            if(luzFraca){
                lanterna.ligar();
                switchLanterna.setChecked(true);
            } else {
                lanterna.desligar();
                switchLanterna.setChecked(false);
            }

            if(longe){
                motor.iniciarVibracao();
                switchVibracao.setChecked(true);
            } else {
                motor.pararVibracao();
                switchVibracao.setChecked(false);
            }

        }

    }
}