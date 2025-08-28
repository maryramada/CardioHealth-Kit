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
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;
import com.example.cardiohealth.Controller.AnaliseVozActivity;
import com.example.cardiohealth.DTO.PessoaDTO;
import com.example.cardiohealth.Helper.Utils;
import com.example.cardiohealth.Model.GlobalID;
import com.example.cardiohealth.Model.Pessoa;
import com.example.cardiohealth.Network.*;
import com.example.cardiohealth.R;
import com.example.cardiohealth.Xml.XmlHandler;

public class AudioAnalysisActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_SEND_SMS = 1;
    private static final int SAMPLE_RATE = 8000;
    private static final int ANALYSIS_DURATION = 4000; // 4 segundos
    private TextView tvAnalysisResult;
    private AudioDispatcher dispatcher;
    private float previousPitch = -1;
    private float previousAmplitude = -1;
    private float totalJitter = 0;
    private float totalShimmer = 0;
    private float totalF0 = 0;
    private float totalHNR = 0;
    private int count = 0;
    private Button btnSegundaAnalise;
    private ImageButton btnBackArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_analysis);

        tvAnalysisResult = findViewById(R.id.tvAnalysisResult);

        btnBackArrow = findViewById(R.id.btnBackArrow);
        btnBackArrow.setOnClickListener(v -> {
            Intent intent = new Intent(AudioAnalysisActivity.this, AnaliseVozActivity.class);
            startActivity(intent);
            finish();
        });

        // Configuração dos ícones inferiores
        ImageView btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(AudioAnalysisActivity.this, secondActivity.class);
            startActivity(intent);
        });

        ImageView btnHistorico = findViewById(R.id.btnHistorico);
        btnHistorico.setOnClickListener(v -> {
            Intent intent = new Intent(AudioAnalysisActivity.this, historicoActivity.class);
            startActivity(intent);
        });

        ImageView btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(AudioAnalysisActivity.this, infoActivity.class);
            startActivity(intent);
        });
        ImageView btnUser = findViewById(R.id.btnUser);
        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(AudioAnalysisActivity.this, UserActivity.class);
            intent.putExtra(Utils.MODE,Utils.ACTIVITY_MODE_EDITING);
            startActivity(intent);
        });

        ImageView btnSOS = findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(v -> enviarMensagemSOS());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            startAudioAnalysis();
        }
    }

    private void startAudioAnalysis() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, 1024, 0);

        PitchDetectionHandler pitchHandler = (result, event) -> {
            final float pitchInHz = result.getPitch();
            final float amplitude = calculateAmplitude(event.getFloatBuffer());

            runOnUiThread(() -> {
                if (pitchInHz > 0) {
                    if (previousPitch != -1) {
                        float jitter = (Math.abs(pitchInHz - previousPitch) / previousPitch) * 100;                        totalJitter += jitter;
                    }
                    if (previousAmplitude != -1) {
                        float shimmer = Math.abs(amplitude - previousAmplitude) / previousAmplitude;
                        totalShimmer += shimmer;
                    }

                    totalF0 += pitchInHz;
                    totalHNR += calculateHNR(event.getFloatBuffer());

                    previousPitch = pitchInHz;
                    previousAmplitude = amplitude;

                    String resultText = "Frequência Fundamental: " + pitchInHz + " Hz\n";
                    resultText += "Jitter: " + (count > 0 ? totalJitter / count : 0) + "%\n";
                    resultText += "Shimmer: " + (count > 0 ? totalShimmer / count : 0) + " dB\n";
                    resultText += "HNR: " + (count > 0 ? totalHNR / count : 0) + " dB\n";
                    tvAnalysisResult.setText(resultText);

                    count++;
                } else {
                    tvAnalysisResult.setText("Nenhuma voz detetada.");
                }
            });
        };

        dispatcher.addAudioProcessor(new PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.YIN, SAMPLE_RATE, 1024, pitchHandler));

        new Thread(dispatcher, "Audio Dispatcher").start();

        new Handler().postDelayed(() -> stopAnalysis(), ANALYSIS_DURATION);
    }

    private void stopAnalysis() {
        if (dispatcher != null) {
            dispatcher.stop();
        }

        float averageJitter = totalJitter / count;
        float averageShimmer = totalShimmer / count;
        float averageF0 = totalF0 / count;
        float averageHNR = totalHNR / count;

        String healthMessage = determineVoiceHealth(averageJitter, averageShimmer, averageHNR, averageF0);

        String finalResult = "Frequência Fundamental: " + averageF0 + " Hz\n" +
                "Jitter: " + averageJitter + "%\n" +
                "Shimmer: " + averageShimmer + " dB\n" +
                "HNR: " + averageHNR + " dB\n" +
                healthMessage;

        tvAnalysisResult.setText(finalResult);

        btnSegundaAnalise = findViewById(R.id.btnSegundaAnalise);
        btnSegundaAnalise.setVisibility(View.VISIBLE);

        btnSegundaAnalise.setOnClickListener(v -> {
            Intent intent = new Intent(AudioAnalysisActivity.this, GravacaoTexto.class);
            intent.putExtra("mode", "realTimeAnalysis");
            startActivity(intent);
            finish();
        });
    }

    private String determineVoiceHealth(float averageJitter, float averageShimmer, float averageHNR, float averageF0) {

        if (Float.isNaN(averageJitter) || Float.isNaN(averageShimmer) || Float.isNaN(averageHNR) || Float.isNaN(averageF0)) {
            return "\nNão foi possível realizar a análise de voz. Por favor, tente novamente.";
        }

        if (averageF0 == 0 || averageHNR == 0 || averageJitter == 0 || averageShimmer == 0) {
            return "\nRealize a análise novamente!";
        }

        if ((averageJitter > 2 || averageShimmer < 0.05) || (averageHNR < 20 && (averageF0 < 70 || averageF0 > 240))) {
            sendAlert("Realize a próxima análise.");
            return "\nAnomalias detetadas.";
        } else {
            return "\nAnálise concluída com sucesso! ";
        }
    }

    private float calculateAmplitude(float[] audioBuffer) {
        float sum = 0;
        for (float sample : audioBuffer) {
            sum += Math.abs(sample);
        }
        return sum / audioBuffer.length;
    }

    private float calculateHNR(float[] audioBuffer) {
        float harmonicPower = 0, noisePower = 0;
        for (float sample : audioBuffer) {
            if (Math.abs(sample) > 0.01) harmonicPower += sample * sample;
            else noisePower += sample * sample;
        }
        return noisePower == 0 ? 100 : 10 * (float) Math.log10(harmonicPower / noisePower);
    }

    public void sendAlert(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }

        String channelId = "stroke_alert_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "AVC Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Alerta de Análise Vocal")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_health_alert)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(1, notification);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dispatcher != null) {
            dispatcher.stop();
        }
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
}
