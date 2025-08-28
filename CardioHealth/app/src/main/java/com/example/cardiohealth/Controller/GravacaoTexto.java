package com.example.cardiohealth.Controller;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;
import com.example.cardiohealth.DTO.PessoaDTO;
import com.example.cardiohealth.Helper.Utils;
import com.example.cardiohealth.Model.GlobalID;
import com.example.cardiohealth.Model.Pessoa;
import com.example.cardiohealth.Network.*;
import com.example.cardiohealth.R;
import com.example.cardiohealth.Xml.XmlHandler;

public class GravacaoTexto extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_SEND_SMS = 1;
    private static final int PERMISSION_REQUEST_POST_NOTIFICATIONS = 2;

    private Button btnIniciarPararGravacao;
    private TextView tvTextoLido;
    private ImageButton btnBackArrow;
    private long startTime;
    private boolean isRecording = false;
    private static final String TEXTO = "O rato roeu a rolha do rei da Rússia. \n A vaca malhada foi molhada por outra vaca molhada e malhada. \n O peito do pé do Pedro é preto. \n A aranha arranha a rã. A rã arranha a aranha.";

    private static final int SAMPLE_RATE = 8000;
    private AudioDispatcher dispatcher;
    private float previousPitch = -1;
    private float totalPitchVariation = 0;
    private int pitchCount = 0;
    private float totalSilenceTime = 0;
    private float totalSpeakingTime = 0;
    private static final String CHANNEL_ID = "analyze_voice_channel";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravacao_texto);



        // Configuração dos botões inferiores
        ImageView btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> startActivity(new Intent(GravacaoTexto.this, secondActivity.class)));

        ImageView btnHistorico = findViewById(R.id.btnHistorico);
        btnHistorico.setOnClickListener(v -> startActivity(new Intent(GravacaoTexto.this, historicoActivity.class)));

        ImageView btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(v -> startActivity(new Intent(GravacaoTexto.this, infoActivity.class)));

        ImageView btnUser = findViewById(R.id.btnUser);
        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(GravacaoTexto.this, UserActivity.class);
            intent.putExtra(Utils.MODE, Utils.ACTIVITY_MODE_EDITING);
            startActivity(intent);
        });

        ImageView btnSOS = findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(v -> enviarMensagemSOS());

        createNotificationChannel();

        btnIniciarPararGravacao = findViewById(R.id.btnIniciarPararGravacao);
        tvTextoLido = findViewById(R.id.tvTextoLido);
        btnBackArrow = findViewById(R.id.btnBackArrow);

        tvTextoLido.setText(TEXTO);

        btnIniciarPararGravacao.setOnClickListener(v -> {
            if (isRecording) {
                pararGravacao();
            } else {
                iniciarGravacao();
            }
        });

        btnBackArrow.setOnClickListener(v -> {
            startActivity(new Intent(this, AnaliseVozActivity.class));
            finish();
        });

        //configurarBotoesInferiores();

        solicitarPermissaoMicrofone();
    }

    private void solicitarPermissaoMicrofone() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Analyze Voice Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal para notificações sobre análise de voz.");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void iniciarGravacao() {
        btnIniciarPararGravacao.setText("Parar Gravação");
        startTime = System.currentTimeMillis();
        isRecording = true;
        iniciarAnaliseAudio();
    }

    private void pararGravacao() {
        btnIniciarPararGravacao.setText("Iniciar Gravação");
        isRecording = false;
        if (dispatcher != null) {
            dispatcher.stop();
        }
        finalizarAnalise();
    }

    private void iniciarAnaliseAudio() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, 1024, 0);

        PitchDetectionHandler pitchHandler = (result, event) -> runOnUiThread(() -> {
            float pitchInHz = result.getPitch();
            if (pitchInHz > 0) {
                if (previousPitch != -1) {
                    totalPitchVariation += Math.abs(pitchInHz - previousPitch);
                    totalSpeakingTime += 0.1;
                }
                previousPitch = pitchInHz;
                pitchCount++;
            } else {
                totalSilenceTime += 0.1;
            }
        });

        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, SAMPLE_RATE, 1024, pitchHandler));
        new Thread(dispatcher, "Audio Dispatcher").start();
    }

    private void finalizarAnalise() {
        long durationInSeconds = (System.currentTimeMillis() - startTime) / 1000;

        int syllableCount = countSyllables(TEXTO);
        double syllablesPerSecond = syllableCount / (double) durationInSeconds;
        double speakingRatio = totalSpeakingTime / (totalSpeakingTime + totalSilenceTime);
        double averagePitchVariation = (pitchCount > 1) ? totalPitchVariation / pitchCount : 0;

        String resultText = String.format(
                "Tempo: %d segundos\nSílabas/s: %.2f\nFala: %.2f%% do tempo\nVariação de frequência: %.2f Hz\n%s",
                durationInSeconds, syllablesPerSecond, speakingRatio * 100, averagePitchVariation,
                avaliarArrastamentoVocal(syllablesPerSecond, speakingRatio, averagePitchVariation));

        tvTextoLido.setText(resultText);
    }

    private int countSyllables(String text) {
        return text.replaceAll("[^aeiouyáéíóúãõêô]", "").length() / 2;
    }

    private String avaliarArrastamentoVocal(double syllablesPerSecond, double speakingRatio, double pitchVariation) {
        if (syllablesPerSecond >= 2 && syllablesPerSecond <= 4
                && speakingRatio >= 0.4 && speakingRatio <= 0.7
                && pitchVariation >= 10 && pitchVariation <= 30) {
            return "Análise concluída com sucesso!";
        } else {
            enviarNotificacaoInterativa("CardioHealth Kit", "Está com dificuldades ao falar?", "Sim", "Não");
            return "";
        }
    }

    public void enviarNotificacaoInterativa(String titulo, String mensagem, String opcao1, String opcao2) {

        Log.d("funçao", "entra na funçao da notif");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Apenas Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_POST_NOTIFICATIONS);
                return;
            }
        }

        Intent intentSim = new Intent(this, RespostaNotificacaoReceiver.class);
        intentSim.putExtra("resposta", "sim");
        PendingIntent pendingIntentSim = PendingIntent.getBroadcast(
                this, 0, intentSim, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent intentNao = new Intent(this, RespostaNotificacaoReceiver.class);
        intentNao.putExtra("resposta", "nao");
        PendingIntent pendingIntentNao = PendingIntent.getBroadcast(
                this, 1, intentNao, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        // Criar a notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_health_alert)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_check, opcao1, pendingIntentSim)
                .addAction(R.drawable.ic_clear, opcao2, pendingIntentNao);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
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
