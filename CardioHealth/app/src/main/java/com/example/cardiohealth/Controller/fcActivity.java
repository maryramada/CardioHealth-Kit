package com.example.cardiohealth.Controller;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import com.example.cardiohealth.DTO.FreqCardiacaContainerDTO;
import com.example.cardiohealth.DTO.FreqCardiacaDTO;
import com.example.cardiohealth.DTO.PessoaDTO;
import com.example.cardiohealth.Helper.Utils;
import com.example.cardiohealth.Model.FreqCardiaca;
import com.example.cardiohealth.Model.GlobalID;
import com.example.cardiohealth.Model.Instant;
import com.example.cardiohealth.Model.Pessoa;
import com.example.cardiohealth.Network.HttpConnection;
import com.example.cardiohealth.Network.HttpRequest;
import com.example.cardiohealth.Network.HttpRequestType;
import com.example.cardiohealth.Network.HttpResponse;
import com.example.cardiohealth.Network.HttpStatusCode;
import com.example.cardiohealth.R;
import com.example.cardiohealth.Xml.XmlHandler;
import java.util.List;

public class fcActivity extends AppCompatActivity {
    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 3000;
    private TextView fcValueText;
    private TextView statusText;

    private final Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            getFrequenciaCardiacaFromWS();
            handler.postDelayed(this, UPDATE_INTERVAL); // Reexecuta após o intervalo
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fc);
        handler.post(updateTask);

        // Configuração dos botões inferiores
        ImageView btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> startActivity(new Intent(fcActivity.this, secondActivity.class)));

        ImageView btnHistorico = findViewById(R.id.btnHistorico);
        btnHistorico.setOnClickListener(v -> startActivity(new Intent(fcActivity.this, historicoActivity.class)));

        ImageView btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(v -> startActivity(new Intent(fcActivity.this, infoActivity.class)));

        ImageView btnUser = findViewById(R.id.btnUser);
        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(fcActivity.this, UserActivity.class);
            intent.putExtra(Utils.MODE, Utils.ACTIVITY_MODE_EDITING);
            startActivity(intent);
        });

        ImageView btnSOS = findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(v -> enviarMensagemSOS());


        // Inicializa os TextViews
        fcValueText = findViewById(R.id.fcValueText);
        statusText = findViewById(R.id.statusText);

        // Cria os canais de notificação (necessário para Android O ou superior)
        createNotificationChannels();

        // Inicia a busca dos dados da frequência cardíaca
        getFrequenciaCardiacaFromWS();
    }

    /**
     * Cria os canais de notificação para as notificações interativas e de alerta.
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                // Canal para notificações interativas (utilizado na pergunta “Está com dor no peito?”)
                NotificationChannel interactiveChannel = new NotificationChannel(
                        "analyze_voice_channel",
                        "Alertas Interativos",
                        NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(interactiveChannel);

                // Canal para notificações simples de frequência cardíaca (ex.: frequência baixa)
                NotificationChannel hrChannel = new NotificationChannel(
                        "heart_rate_channel",
                        "Alertas de Frequência Cardíaca",
                        NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(hrChannel);
            }
        }
    }

    /**
     * Avalia a frequência cardíaca e retorna uma string representando o estado.
     */
    private String evaluateHeartRate(int heartRate) {
        if (heartRate < 60) {
            return "Baixa";
        } else if (heartRate > 100) {
            return "Alta";
        } else {
            return "Normal";
        }
    }

    /**
     * Busca os dados da frequência cardíaca do Web Service e atualiza a interface.
     */
    private void getFrequenciaCardiacaFromWS() {
        GlobalID app = (GlobalID) getApplication();
        Integer id = app.getGlobalVariable();
        String address = Utils.getWSAddress(this);
        String uri = address + "/pessoas/" + id + "/frequenciaCardiaca";

        new Thread(() -> {
            HttpRequest httpRequest = new HttpRequest(HttpRequestType.GET, uri, "");
            HttpResponse httpResponse = HttpConnection.makeRequest(httpRequest);

            if (httpResponse.getStatus() == HttpStatusCode.OK) {
                try {
                    // Desserializa a resposta XML
                    FreqCardiacaContainerDTO freqList = XmlHandler.deSerializeFrequenciaCardiacaList(httpResponse.getBody());
                    List<FreqCardiacaDTO> freqCardiacasDTO = freqList.getFreqCardiacaList();
                    List<FreqCardiaca> freqCardiacas = Mapper.freqCardiacaDTO2FreqCardiaca(freqCardiacasDTO);

                    // Seleciona o registro mais recente
                    FreqCardiaca latestRecord = getLatestRecord(freqCardiacas);

                    // Atualiza a UI na thread principal
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
    private FreqCardiaca getLatestRecord(List<FreqCardiaca> records) {
        FreqCardiaca latestRecord = null;
        for (FreqCardiaca record : records) {
            if (latestRecord == null || getSeconds(record.getInstant()) > getSeconds(latestRecord.getInstant())) {
                latestRecord = record;
            }
        }
        return latestRecord;
    }

    /**
     * Converte um objeto Instant em segundos desde a meia-noite.
     */
    private int getSeconds(Instant instant) {
        return instant.getHora() * 3600 + instant.getMinuto() * 60 + instant.getSegundo();
    }

    /**
     * Atualiza os TextViews com o valor mais recente e dispara notificações conforme o estado.
     */
    private void updateUI(FreqCardiaca latestRecord) {
        if (latestRecord != null) {
            int heartRate = latestRecord.getNum();
            String state = evaluateHeartRate(heartRate);

            fcValueText.setText(String.format("%d bpm", heartRate));
            statusText.setText(String.format("Estado: %s", state));

            if (heartRate > 100) {
                // Se a frequência for alta, envia a notificação interativa perguntando se há dor no peito.
                sendInteractiveNotification("CardioHealth Kit", "Está a realizar algum tipo de esforço físico?");
            } else if (heartRate < 60) {
                // Se desejar, envia uma notificação simples para frequência baixa.
                sendHeartRateNotification(heartRate);
            }
        } else {
            fcValueText.setText("Carregando...");
            statusText.setText("Carregando frequência cardíaca...");
        }
    }

    /**
     * Envia uma notificação interativa com as ações “Sim” e “Não”.
     * Essa notificação dispara o FCRespostaNotif, que já possui a lógica de resposta.
     */
    private void sendInteractiveNotification(String title, String message) {
        // Verifica a permissão para enviar notificações (Android 13 ou superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão de notificações necessária", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Cria os PendingIntents para as ações que serão tratadas pelo FCRespostaNotif
        Intent intentSim = new Intent(this, RespostaFC.class);
        intentSim.putExtra("resposta", "nao");
        PendingIntent pendingIntentSim = PendingIntent.getBroadcast(
                this, 0, intentSim, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent intentNao = new Intent(this, RespostaFC.class);
        intentNao.putExtra("resposta", "sim");
        PendingIntent pendingIntentNao = PendingIntent.getBroadcast(
                this, 1, intentNao, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "analyze_voice_channel")
                .setSmallIcon(R.drawable.ic_health_alert)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_check, "Sim", pendingIntentSim)
                .addAction(R.drawable.ic_clear, "Não", pendingIntentNao);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    /**
     * Envia uma notificação simples para alertar sobre frequências fora do normal (exemplo para frequência baixa).
     */
    private void sendHeartRateNotification(int heartRate) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "heart_rate_channel",
                    "Alerta de Frequência Cardíaca",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        String title = "Alerta de Frequência Cardíaca";
        String description = (heartRate < 60)
                ? "Frequência Cardíaca muito baixa: " + heartRate + " bpm"
                : "Frequência: " + heartRate + " bpm";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "heart_rate_channel")
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_health_alert)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Aqui você precisa chamar build() para criar a Notification
        Notification notification = builder.build();

        notificationManager.notify(3, notification);
    }


    /**
     * Exibe uma mensagem de erro na interface.
     */
    private void showError(String message) {
        fcValueText.setText(message);
        statusText.setText(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTask);
    }

    /**
     * Método para enviar mensagem SOS.
     */
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

    /**
     * Envia uma mensagem SMS para o número informado.
     */
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
