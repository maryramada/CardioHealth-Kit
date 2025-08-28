package com.example.cardiohealth.Controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.cardiohealth.DTO.PessoaDTO;
import com.example.cardiohealth.Helper.Utils;
import com.example.cardiohealth.Model.GlobalID;
import com.example.cardiohealth.Model.Pessoa;
import com.example.cardiohealth.Network.*;
import com.example.cardiohealth.R;
import com.example.cardiohealth.Xml.XmlHandler;

public class secondActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_SEND_SMS = 1;
    private static final int PERMISSION_REQUEST_CALL_PHONE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        checkAndRequestSmsPermission();

        // Configurando o botão "Frequência Cardíaca"
        Button btnFrequenciaCardiaca = findViewById(R.id.btnFrequenciaCardiaca);
        btnFrequenciaCardiaca.setOnClickListener(v -> {
            Intent intent = new Intent(secondActivity.this, fcActivity.class);
            startActivity(intent);
        });

        // Configurando o botão "SpO2"
        Button btnSpO2 = findViewById(R.id.btnSpO2);
        btnSpO2.setOnClickListener(v -> {
            Intent intent = new Intent(secondActivity.this, spActivity.class);
            startActivity(intent);
        });

        // Configurando o botão "Análise Facial"
        Button btnAnaliseFacial = findViewById(R.id.btnAnaliseFacial);
        btnAnaliseFacial.setOnClickListener(v -> {
            Intent intent = new Intent(secondActivity.this, AnaliseFacialActivity.class);
            startActivity(intent);
        });

        // Configurando o botão "Análise da Voz"
        Button btnAnaliseVoz = findViewById(R.id.btnAnaliseVoz);
        btnAnaliseVoz.setOnClickListener(v -> {
            Intent intent = new Intent(secondActivity.this, AnaliseVozActivity.class);
            startActivity(intent);
        });

        // NOVO: Configurando o botão SOS para chamada de emergência (localizado abaixo da análise de voz)
        Button btnSOSCall = findViewById(R.id.btnSOSCall);
        btnSOSCall.setOnClickListener(v -> ligarParaContatoEmergencia());

        // Configurando a barra inferior com os botões
        ImageView btnSOS = findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(v -> enviarMensagemSOS());

        ImageView btnHistorico = findViewById(R.id.btnHistorico);
        btnHistorico.setOnClickListener(v -> {
            Intent intent = new Intent(secondActivity.this, historicoActivity.class);
            startActivity(intent);
        });

        ImageView btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(secondActivity.this, secondActivity.class);
            startActivity(intent);
        });

        ImageView btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(secondActivity.this, infoActivity.class);
            startActivity(intent);
        });

        ImageView btnUser = findViewById(R.id.btnUser);
        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(secondActivity.this, UserActivity.class);
            intent.putExtra(Utils.MODE, Utils.ACTIVITY_MODE_EDITING);
            startActivity(intent);
        });
    }

    private void checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
        }
    }

    public void enviarMensagemSOS() {
        new Thread(() -> {
            GlobalID app = (GlobalID) getApplication();
            Integer id = app.getGlobalVariable();
            String address = Utils.getWSAddress(this);
            String uri = address + "/pessoas/" + id;
            HttpRequest httpRequest = new HttpRequest(HttpRequestType.GET, uri, "");
            HttpResponse httpResponse = HttpConnection.makeRequest(httpRequest);

            if (httpResponse.getStatus() == HttpStatusCode.OK) {
                PessoaDTO dto = XmlHandler.deSerializeXML2PessoaDTO(httpResponse.getBody());
                Pessoa pessoa = Mapper.pessoaDTO2pessoa(dto);
                String contatoEmergencia = String.valueOf(pessoa.getContactoEmergencia());

                if (contatoEmergencia != null && !contatoEmergencia.trim().isEmpty()) {
                    runOnUiThread(() -> enviarSMS(contatoEmergencia));
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Contato de emergência não disponível", Toast.LENGTH_SHORT).show());
                }
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Erro ao obter dados da pessoa", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void enviarSMS(String numero) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numero, null, "Preciso de ajuda", null, null);
            Toast.makeText(this, "Mensagem enviada com sucesso!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Falha ao enviar a mensagem", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Método para ligar para o contato de emergência
    private void ligarParaContatoEmergencia() {
        new Thread(() -> {
            GlobalID app = (GlobalID) getApplication();
            Integer id = app.getGlobalVariable();
            String address = Utils.getWSAddress(this);
            String uri = address + "/pessoas/" + id;
            HttpRequest httpRequest = new HttpRequest(HttpRequestType.GET, uri, "");
            HttpResponse httpResponse = HttpConnection.makeRequest(httpRequest);

            if (httpResponse.getStatus() == HttpStatusCode.OK) {
                PessoaDTO dto = XmlHandler.deSerializeXML2PessoaDTO(httpResponse.getBody());
                Pessoa pessoa = Mapper.pessoaDTO2pessoa(dto);
                String contatoEmergencia = String.valueOf(pessoa.getContactoEmergencia());
                if (contatoEmergencia != null && !contatoEmergencia.trim().isEmpty()) {
                    runOnUiThread(() -> iniciarChamada(contatoEmergencia));
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Contato de emergência não disponível", Toast.LENGTH_SHORT).show());
                }
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Erro ao obter dados da pessoa", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // Método que inicia a chamada para o número informado
    private void iniciarChamada(String numero) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + numero));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
            return;
        }
        startActivity(callIntent);
    }
}
