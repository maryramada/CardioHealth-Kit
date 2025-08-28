package com.example.cardiohealth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import com.example.cardiohealth.Controller.LoginActivity;
import com.example.cardiohealth.Service.AlertaService;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    // Para Android 11, vamos procurar pelo nome do dispositivo
    private static final String DEVICE_NAME = "ESP32-Bluetooth";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;
    private InputStream mInputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(this, AlertaService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }


        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        Button sosButton = findViewById(R.id.sosButton);
        sosButton.setOnClickListener(v -> {
            String phoneNumber = "tel:918745175"; // Substitua pelo número desejado
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(phoneNumber));

            // Verifica se a permissão foi concedida antes de fazer a chamada
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
            } else {
                startActivity(callIntent);
            }
        });


        // Inicializa o Bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth não disponível", Toast.LENGTH_LONG).show();
            return;
        }

        checkPermissions();
    }

    private void checkPermissions() {
        Log.d(TAG, "Verificando permissões...");

        // Em Android 12 ou superior (API 31+), é necessário BLUETOOTH_CONNECT.
        // Em versões anteriores (como Android 11) utilizamos a permissão de localização.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                        REQUEST_BLUETOOTH_PERMISSION);
            } else {
                connectToDevice();
            }
        } else {
            // Para Android 6 até 11, verifique a permissão de localização.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            } else {
                connectToDevice();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissão Bluetooth concedida.");
                connectToDevice();
            } else {
                Log.d(TAG, "Permissão Bluetooth negada.");
                Toast.makeText(this, "Permissão Bluetooth negada!", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissão de Localização concedida.");
                connectToDevice();
            } else {
                Log.d(TAG, "Permissão de Localização negada.");
                Toast.makeText(this, "Permissão de Localização negada!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void connectToDevice() {
        // Em Android 12 ou superior, verifica a permissão BLUETOOTH_CONNECT.
        // Para Android 11, já garantimos o ACCESS_FINE_LOCATION.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissão BLUETOOTH_CONNECT não concedida");
                return;
            }
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth está desativado!", Toast.LENGTH_LONG).show();
            return;
        }

        final BluetoothDevice[] device = {null};

        try {
            // Procura entre os dispositivos emparelhados pelo nome
            for (BluetoothDevice pairedDevice : mBluetoothAdapter.getBondedDevices()) {
                Log.d(TAG, "Dispositivo emparelhado: " + pairedDevice.getName() + " - " + pairedDevice.getAddress());
                if (pairedDevice.getName().equals(DEVICE_NAME)) {
                    device[0] = pairedDevice;
                    break;
                }
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Erro ao acessar dispositivos emparelhados", e);
            return;
        }

        if (device[0] == null) {
            Toast.makeText(this, "Dispositivo não emparelhado. Emparelhe manualmente primeiro!", Toast.LENGTH_LONG).show();
            return;
        }

        // Conecta ao dispositivo em uma thread separada
        new Thread(() -> {
            try {
                BluetoothSocket socket = device[0].createInsecureRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                InputStream inputStream = socket.getInputStream();
                Log.d(TAG, "Conectado ao dispositivo Bluetooth");

                // Se desejar manter a referência para fechar depois:
                mBluetoothSocket = socket;
                mInputStream = inputStream;

                // Inicia a leitura dos dados recebidos
                startReadingData(inputStream);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao conectar ao Bluetooth", e);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Erro ao conectar!", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void startReadingData(InputStream inputStream) {
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, "Linha recebida: " + line);
                    // Verifica se a linha contém os dois valores separados por vírgula
                    if (line.contains(",")) {
                        String[] values = line.split(",");
                        if (values.length == 2) {
                            String heartRate = values[0].trim();
                            String spO2 = values[1].trim();
                            sendToPHP(heartRate, spO2);
                        } else {
                            Log.e(TAG, "Linha recebida com formato inesperado: " + line);
                        }
                    } else {
                        Log.e(TAG, "Linha recebida sem vírgula: " + line);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao ler dados do Bluetooth", e);
            }
        }).start();
    }


    private void sendToPHP(String heartRate, String spO2) {
        // Certifique-se de que os valores recebidos não sejam vazios ou nulos.
        if (heartRate == null || spO2 == null || heartRate.isEmpty() || spO2.isEmpty()) {
            Log.e("sendToPHP", "Valores de heartRate ou spO2 inválidos");
            return;
        }

        OkHttpClient client = new OkHttpClient();
        // URL do seu script PHP
        String url = "http://172.20.10.3/CardioHealth/Conection.php";

        RequestBody formBody = new FormBody.Builder()
                .add("heartRate", heartRate)
                .add("spO2", spO2)
                .add("pessoa_id", "28")
                .build();

        // Adiciona o header Content-Type (a FormBody já seta isso, mas de forma explícita pode ajudar)
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String resposta = response.body().string();
                    Log.d("PHP Response", resposta);
                } else {
                    Log.e("PHP Response", "Erro ao enviar dados: " + response.code());
                }
            } catch (Exception e) {
                Log.e("HTTP Request", "Erro ao fazer a requisição POST", e);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Fechar BluetoothSocket e InputStream quando a activity for destruída
        if (mBluetoothSocket != null) {
            try {
                mBluetoothSocket.close();
            } catch (Exception e) {
                Log.e(TAG, "Erro ao fechar o BluetoothSocket", e);
            }
        }
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (Exception e) {
                Log.e(TAG, "Erro ao fechar InputStream", e);
            }
        }
    }
}