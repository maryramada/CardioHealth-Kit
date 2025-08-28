package com.example.cardiohealth.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import android.app.PendingIntent;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import com.example.cardiohealth.Network.*;
import com.example.cardiohealth.R;
import com.example.cardiohealth.Model.GlobalID;
import com.example.cardiohealth.Xml.XmlHandler;
import com.example.cardiohealth.DTO.PessoaDTO;
import com.example.cardiohealth.Model.Pessoa;
import com.example.cardiohealth.Helper.Utils;

public class RespostaFC extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String resposta = intent.getStringExtra("resposta");

        if (resposta != null) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Cancela a primeira notificação (ID 1)
            notificationManager.cancel(1);

            if (resposta.equals("sim")) {
                // Envia a segunda notificação usando o mesmo ID (1) para substituir a primeira
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        new Handler().postDelayed(() -> enviarNotificacaoSobreDor("CardioHealth Kit", "Sente dor no peito?", context), 500);
                    } else {
                        Log.d("Permissão", "Permissão para enviar notificações não concedida.");
                    }
                } else {
                    new Handler().postDelayed(() -> enviarNotificacaoSobreDor("CardioHealth Kit", "Sente dor no peito?", context), 500);
                }

                // Após a segunda notificação, envia a mensagem SOS para o contato de emergência
                enviarMensagemSOS(context);
            } else if (resposta.equals("nao")) {
                Toast.makeText(context, "Obrigado pela resposta!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enviarNotificacaoSobreDor(String titulo, String mensagem, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permissão de notificações necessária", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Intent intentSim = new Intent(context, RespostaNotificacaoReceiver.class);
        intentSim.putExtra("resposta", "sim");
        PendingIntent pendingIntentSim = PendingIntent.getBroadcast(
                context, 0, intentSim, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        Intent intentNao = new Intent(context, RespostaNotificacaoReceiver.class);
        intentNao.putExtra("resposta", "nao");
        PendingIntent pendingIntentNao = PendingIntent.getBroadcast(
                context, 1, intentNao, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "analyze_voice_channel")
                .setSmallIcon(R.drawable.ic_health_alert)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_check, "Sim", pendingIntentSim)
                .addAction(R.drawable.ic_clear, "Não", pendingIntentNao);

        // Importante: utiliza o mesmo ID (1) para substituir a notificação anterior
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    private void enviarMensagemSOS(Context context) {
        new Thread(() -> {
            GlobalID app = (GlobalID) context.getApplicationContext();
            Integer id = app.getGlobalVariable();
            String address = Utils.getWSAddress(context);
            String uri = address + "/pessoas/" + id;
            HttpRequest httpRequest = new HttpRequest(HttpRequestType.GET, uri, "");
            HttpResponse httpResponse = HttpConnection.makeRequest(httpRequest);

            if (httpResponse.getStatus() == HttpStatusCode.OK) {
                PessoaDTO dto = XmlHandler.deSerializeXML2PessoaDTO(httpResponse.getBody());
                Pessoa pessoa = Mapper.pessoaDTO2pessoa(dto);
                String contatoEmergencia = String.valueOf(pessoa.getContactoEmergencia());

                if (contatoEmergencia != null && !contatoEmergencia.trim().isEmpty()) {
                    new Handler(context.getMainLooper()).post(() -> enviarSMS(context, contatoEmergencia));
                } else {
                    new Handler(context.getMainLooper()).post(() -> Toast.makeText(context, "Contato de emergência não disponível", Toast.LENGTH_SHORT).show());
                }
            } else {
                new Handler(context.getMainLooper()).post(() -> Toast.makeText(context, "Erro ao obter dados da pessoa", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void enviarSMS(Context context, String numero) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numero, null, "Preciso de ajuda", null, null);
            new Handler(context.getMainLooper()).post(() ->
                    Toast.makeText(context, "Mensagem enviada com sucesso!", Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            new Handler(context.getMainLooper()).post(() ->
                    Toast.makeText(context, "Falha ao enviar a mensagem", Toast.LENGTH_SHORT).show());
            e.printStackTrace();
        }
    }
}
