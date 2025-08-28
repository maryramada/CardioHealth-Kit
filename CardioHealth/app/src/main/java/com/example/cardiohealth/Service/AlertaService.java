package com.example.cardiohealth.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.cardiohealth.DTO.AlertaContainerDTO;
import com.example.cardiohealth.DTO.AlertasDTO;
import com.example.cardiohealth.Helper.Utils;
import com.example.cardiohealth.Network.*;
import com.example.cardiohealth.R;
import com.example.cardiohealth.Xml.XmlHandler;

import java.util.Timer;
import java.util.TimerTask;

public class AlertaService extends Service {
    private static final String CHANNEL_ID = "alertas_foreground";
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
        startAlertMonitoring();
    }

    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Monitoramento de Alertas",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Monitoramento Ativo")
                .setContentText("O app está monitorando sua saúde em tempo real.")
                .setSmallIcon(R.drawable.ic_health_alert)
                .build();

        startForeground(1, notification);
    }

    private void startAlertMonitoring() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                verificarAlertas();
            }
        }, 0, 30000); // Verifica a cada 30 segundos
    }

    private void verificarAlertas() {
        String address = Utils.getWSAddress(this);
        String uri = address + "/pessoas/{id}/alertas";

        new Thread(() -> {
            HttpRequest httpRequest = new HttpRequest(HttpRequestType.GET, uri, "");
            HttpResponse httpResponse = HttpConnection.makeRequest(httpRequest);

            if (httpResponse.getStatus() == HttpStatusCode.OK) {
                AlertaContainerDTO alertaContainer = XmlHandler.deSerializeXML2AlertasContainerDTO(httpResponse.getBody());

                if (alertaContainer != null && alertaContainer.getAlertas() != null) {
                    for (AlertasDTO alerta : alertaContainer.getAlertas()) {
                        sendNotification(alerta);
                    }
                }

            }
        }).start();
    }

    private void sendNotification(AlertasDTO alerta) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Alerta de Saúde")
                .setContentText(alerta.getDescricao())
                .setSmallIcon(R.drawable.ic_health_alert)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(2, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
