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
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.example.cardiohealth.Adapter.LvAdapterAlertas;
import com.example.cardiohealth.DTO.AlertaContainerDTO;
import com.example.cardiohealth.DTO.ErrorDTO;
import com.example.cardiohealth.DTO.PessoaDTO;
import com.example.cardiohealth.Helper.Response;
import com.example.cardiohealth.Model.Alertas;
import com.example.cardiohealth.Model.GlobalID;
import com.example.cardiohealth.Model.Pessoa;
import com.example.cardiohealth.Model.view.AlertasContainer;
import com.example.cardiohealth.Network.*;
import com.example.cardiohealth.R;
import com.example.cardiohealth.Helper.Utils;
import com.example.cardiohealth.Xml.XmlHandler;
import java.util.ArrayList;


public class AlertaActivity extends AppCompatActivity {
    private ListView lvAlertas;
    private ArrayList<Alertas> alertas;
    private LvAdapterAlertas adapter;
    private static final String CHANNEL_ID = "alertas_channel";
    private static final int PERMISSION_REQUEST_SEND_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertas_activity);

        setupBottomMenu();

        lvAlertas = findViewById(R.id.lvAlertas);
        alertas = new ArrayList<>();
        adapter = new LvAdapterAlertas(this, R.layout.alertas_item, alertas);
        lvAlertas.setAdapter(adapter);

        lvAlertas.setOnItemClickListener((adapterView, view, i, l) -> {
            if (!alertas.isEmpty()) {
                Toast.makeText(AlertaActivity.this, "Alerta clicado: " + alertas.get(i).getTipo(), Toast.LENGTH_SHORT).show();
            }
        });

        Button btnApagarTudo = findViewById(R.id.btnApagarTudo);
        btnApagarTudo.setOnClickListener(v -> deleteAlertasFromWS());

        getAlertasFromWS();
    }

    private void setupBottomMenu() {
        findViewById(R.id.btnHome).setOnClickListener(v -> startActivity(new Intent(AlertaActivity.this, secondActivity.class)));
        findViewById(R.id.btnHistorico).setOnClickListener(v -> startActivity(new Intent(AlertaActivity.this, historicoActivity.class)));
        findViewById(R.id.btnInfo).setOnClickListener(v -> startActivity(new Intent(AlertaActivity.this, infoActivity.class)));
        findViewById(R.id.btnUser).setOnClickListener(v -> {
            Intent intent = new Intent(AlertaActivity.this, UserActivity.class);
            intent.putExtra(Utils.MODE, Utils.ACTIVITY_MODE_EDITING);
            startActivity(intent);
        });

        ImageView btnSOS = findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(v -> enviarMensagemSOS());
    }
    private void checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
        }
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

    private void setDataOnUi(AlertasContainer data) {
        ArrayList<Alertas> sortedAlertas = new ArrayList<>(data.getAlertas());
        sortedAlertas.sort((a1, a2) -> a2.getDataHora().compareTo(a1.getDataHora()));
        adapter.setItems(sortedAlertas);
        adapter.notifyDataSetChanged();

        if (!sortedAlertas.isEmpty()) {
            sendNotification(sortedAlertas.get(0));
        }
    }

    private void setErrorOnUi(String message) {
        Toast.makeText(AlertaActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void getAlertasFromWS() {
        GlobalID app = (GlobalID) getApplication();
        Integer id = app.getGlobalVariable();
        String address = Utils.getWSAddress(this);
        String uri = address + "/pessoas/" + id + "/alertas";

        new Thread(() -> {
            HttpRequest httpRequest = new HttpRequest(HttpRequestType.GET, uri, "");
            HttpResponse httpResponse = HttpConnection.makeRequest(httpRequest);

            Response response = null;
            if (httpResponse.getStatus() == HttpStatusCode.OK) {
                AlertaContainerDTO dto = XmlHandler.deSerializeXML2AlertasContainerDTO(httpResponse.getBody());
                AlertasContainer data = Mapper.alertaContainerDTO2alertaContainer(dto);
                response = new Response(HttpStatusCode.OK, data);
            } else if (httpResponse.getStatus() == HttpStatusCode.Conflict) {
                ErrorDTO error = XmlHandler.deSerializeXML2ErrorDto(httpResponse.getBody());
                response = new Response(HttpStatusCode.Conflict, error.getMessage());
            }

            Response finalResponse = response;
            runOnUiThread(() -> {
                if (finalResponse != null) {
                    if (finalResponse.getStatus() == HttpStatusCode.OK && finalResponse.getBody() instanceof AlertasContainer) {
                        setDataOnUi((AlertasContainer) finalResponse.getBody());
                    } else if (finalResponse.getBody() instanceof String) {
                        setErrorOnUi((String) finalResponse.getBody());
                    }
                }
            });
        }).start();
    }

    private void deleteAlertasFromWS() {
        GlobalID app = (GlobalID) getApplication();
        Integer id = app.getGlobalVariable();
        if (id == null) {
            runOnUiThread(() -> Toast.makeText(AlertaActivity.this, "Erro: ID do usuário é nulo.", Toast.LENGTH_SHORT).show());
            return;
        }

        String address = Utils.getWSAddress(this);
        String uri = address + "/alertas/" + id ;
        System.out.println("Enviando DELETE para: " + uri);

        new Thread(() -> {
            HttpRequest httpRequest = new HttpRequest(HttpRequestType.DELETE, uri, "");
            HttpResponse httpResponse = HttpConnection.makeRequest(httpRequest);

            System.out.println("Resposta do servidor: " + httpResponse.getStatus() + " - " + httpResponse.getBody());

            runOnUiThread(() -> {
                if (httpResponse.getStatus() == 204) {
                    alertas.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(AlertaActivity.this, "Todos os alertas foram apagados.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AlertaActivity.this, "Erro ao apagar alertas. Código: " + httpResponse.getStatus(), Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
}

    private void sendNotification(Alertas alerta) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alertas CardioHealth",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        String titulo = "Novo Alerta";
        String descricao = alerta.getDescricao();

        if ("Frequência Cardíaca".equals(alerta.getTipo())) {
            titulo = "Alerta de Frequência Cardíaca";
            descricao = alerta.getValor() < 60 ? "Frequência muito baixa: " + alerta.getValor() + " bpm" :
                    alerta.getValor() > 100 ? "Frequência muito alta: " + alerta.getValor() + " bpm" : descricao;
        } else if ("Saturação Oxigênio".equals(alerta.getTipo())) {
            titulo = "Alerta de Saturação de Oxigênio";
            descricao = "Saturação baixa: " + alerta.getValor() + "%";
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(titulo)
                .setContentText(descricao)
                .setSmallIcon(R.drawable.ic_health_alert)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(1, notification);
    }

}
