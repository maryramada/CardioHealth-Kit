package com.example.cardiohealth.Controller;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.example.cardiohealth.DTO.PessoaDTO;
import com.example.cardiohealth.DTO.SatOxContainerDTO;
import com.example.cardiohealth.DTO.SatOxDTO;
import com.example.cardiohealth.Helper.Utils;
import com.example.cardiohealth.Model.GlobalID;
import com.example.cardiohealth.Model.Pessoa;
import com.example.cardiohealth.Model.SatOx;
import com.example.cardiohealth.Model.Instant;
import com.example.cardiohealth.Model.view.SatOxContainer;
import com.example.cardiohealth.Network.HttpConnection;
import com.example.cardiohealth.Network.HttpRequest;
import com.example.cardiohealth.Network.HttpRequestType;
import com.example.cardiohealth.Network.HttpResponse;
import com.example.cardiohealth.Network.HttpStatusCode;
import com.example.cardiohealth.R;
import com.example.cardiohealth.Xml.XmlHandler;

import java.util.List;

public class spActivity extends AppCompatActivity {
    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 3000;
    private static final int PERMISSION_REQUEST_SEND_SMS = 1;


    private TextView statusText;
    private TextView spO2ValueText;

    private final Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            getSpO2FromWS();
            handler.postDelayed(this, UPDATE_INTERVAL); // Reexecuta após o intervalo
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sp_activity);
        handler.post(updateTask);

        // Inicializa os TextViews
        statusText = findViewById(R.id.statusText);
        spO2ValueText = findViewById(R.id.spO2ValueText);

        // Inicia a busca dos dados de SpO₂ reais
        getSpO2FromWS();

        // Configuração dos botões inferiores
        ImageView btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(spActivity.this, secondActivity.class);
            startActivity(intent);
        });

        ImageView btnHistorico = findViewById(R.id.btnHistorico);
        btnHistorico.setOnClickListener(v -> {
            Intent intent = new Intent(spActivity.this, historicoActivity.class);
            startActivity(intent);
        });

        ImageView btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(spActivity.this, infoActivity.class);
            startActivity(intent);
        });

        ImageView btnUser = findViewById(R.id.btnUser);
        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(spActivity.this, UserActivity.class);
            intent.putExtra(Utils.MODE,Utils.ACTIVITY_MODE_EDITING);
            startActivity(intent);
        });

        ImageView btnSOS = findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(v -> enviarMensagemSOS());
    }



    /**
     * Avalia o SpO₂ com base nos valores normais:
     * - Normal: 95–100%
     * - Baixo: < 95%
     *
     * @param spO2 valor de SpO₂
     * @return Status ("Normal" ou "Baixo")
     */
    private String evaluateSpO2(int spO2) {
        if (spO2 < 95) {
            return "Baixo";
        } else {
            return "Normal";
        }
    }

    /**
     * Busca os dados de SpO₂ do Web Service e atualiza a interface.
     * Seleciona o registro mais recente comparando o horário (instante) dos registros.
     */
    private void getSpO2FromWS() {
        GlobalID app = (GlobalID) getApplication();
        Integer id = app.getGlobalVariable();
        String address = Utils.getWSAddress(this);
        String uri = address + "/pessoas/" + id + "/saturacaoOxigenio";

        new Thread(() -> {
            HttpRequest httpRequest = new HttpRequest(HttpRequestType.GET, uri, "");
            HttpResponse httpResponse = HttpConnection.makeRequest(httpRequest);

            if (httpResponse.getStatus() == HttpStatusCode.OK) {
                try {
                    // Desserializa a resposta XML
                    SatOxContainerDTO satList = XmlHandler.deSerializeSatOxList(httpResponse.getBody());
                    List<SatOxDTO> satOxDTO = satList.getSatOxList();
                    List<SatOx> satOxs = Mapper.satOxDTO2SatOx(satOxDTO);

                    // Seleciona o registro com o horário mais recente
                    SatOx latestRecord = getLatestRecord(satOxs);

                    // Atualiza a UI com o valor mais recente
                    runOnUiThread(() -> updateUI(latestRecord));

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> showError("Erro ao carregar dados."));
                }
            } else {
                runOnUiThread(() -> showError("Erro de conexão."));
            }
        }).start();
    }

    /**
     * Retorna o registro com o horário mais recente.
     */
    private SatOx getLatestRecord(List<SatOx> records) {
        SatOx latestRecord = null;
        for (SatOx record : records) {
            if (latestRecord == null || getSeconds(record.getInstant()) > getSeconds(latestRecord.getInstant())) {
                latestRecord = record;
            }
        }
        return latestRecord;
    }

    /**
     * Converte o instante em segundos desde a meia-noite.
     */
    private int getSeconds(Instant instant) {
        return instant.getHora() * 3600 + instant.getMinuto() * 60 + instant.getSegundo();
    }

    /**
     * Atualiza os TextViews com o valor mais recente.
     */
    private void updateUI(SatOx latestRecord) {
        if (latestRecord != null) {
            int spO2 = latestRecord.getValor();
            String SpStatus = evaluateSpO2(spO2);
            spO2ValueText.setText(String.format(" %d%%", spO2));

            statusText.setText(String.format("Estado: %s", SpStatus));

            // Verifica se o SpO₂ está abaixo de 95% e envia a notificação
            if (spO2 < 95) {
                sendSpO2Notification(spO2);
            }
        } else {
            spO2ValueText.setText(" Carregando...");
            statusText.setText("Carregando SpO2...");
        }
    }

    /**
     * Exibe uma mensagem de erro na UI.
     */
    private void showError(String message) {
        spO2ValueText.setText(message);
        statusText.setText(message);
    }

    private void sendSpO2Notification(int spO2) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        // Configuração do canal de notificação para versões Android O ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "spO2_channel",
                    "Alerta de SpO₂",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        String titulo = "Alerta de SpO₂";
        String descricao = "SpO₂: " + spO2 + "%";

        // Ajusta a mensagem se o SpO₂ estiver abaixo do valor normal
        if (spO2 < 95) {
            descricao = "SpO₂ muito baixo: " + spO2 + "%";
        }

        // Criando a notificação
        Notification notification = new NotificationCompat.Builder(this, "spO2_channel")
                .setContentTitle(titulo)
                .setContentText(descricao)
                .setSmallIcon(R.drawable.ic_health_alert) // Ícone da notificação
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        // Envia a notificação
        notificationManager.notify(1, notification);
    }

    private void enviarMensagemSOS() {
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
}
