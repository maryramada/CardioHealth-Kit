package com.example.cardiohealth.Controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.cardiohealth.DTO.PessoaDTO;
import com.example.cardiohealth.DTO.SatOxContainerDTO;
import com.example.cardiohealth.DTO.SatOxDTO;
import com.example.cardiohealth.Model.GlobalID;
import com.example.cardiohealth.Model.Instant;
import com.example.cardiohealth.Model.Pessoa;
import com.example.cardiohealth.Model.SatOx;
import com.example.cardiohealth.Network.HttpConnection;
import com.example.cardiohealth.Network.HttpRequest;
import com.example.cardiohealth.Network.HttpRequestType;
import com.example.cardiohealth.Network.HttpResponse;
import com.example.cardiohealth.Network.HttpStatusCode;
import com.example.cardiohealth.Helper.Utils;
import com.example.cardiohealth.R;
import com.example.cardiohealth.Xml.XmlHandler;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class SaturacaoOxActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_SEND_SMS = 1;


    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saturacao_oxigenio_activity);

        // Configuração dos botões inferiores
        ImageView btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(SaturacaoOxActivity.this, secondActivity.class);
            startActivity(intent);
            finish();
        });

        ImageView btnHistorico = findViewById(R.id.btnHistorico);
        btnHistorico.setOnClickListener(v -> {
            Intent intent = new Intent(SaturacaoOxActivity.this, historicoActivity.class);
            startActivity(intent);
            finish();
        });

        ImageView btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(SaturacaoOxActivity.this, infoActivity.class);
            startActivity(intent);
            finish();
        });

        ImageView btnUser = findViewById(R.id.btnUser);
        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(SaturacaoOxActivity.this, UserActivity.class);
            startActivity(intent);
            finish();
        });

        ImageView btnSOS = findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(v -> enviarMensagemSOS());


        // Referência ao LineChart para o gráfico
        lineChart = findViewById(R.id.lineChart);

        // Obter dados da API
        getSatOxFromWS();
    }

    private void setDataOnGraph(List<SatOx> data) {
        if (data == null || data.isEmpty()) {
            Toast.makeText(this, "Nenhum dado disponível para exibir.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criando lista de entradas para o gráfico
        List<Entry> entries = new ArrayList<>();

        // Limitar a quantidade de dados exibidos no gráfico
        int limit = Math.min(data.size(), 100); // Exibir no máximo 100 pontos

        // Adicionar os dados ao gráfico
        for (int i = 0; i < limit; i++) {
            SatOx record = data.get(i);
            entries.add(new Entry(i * 2, record.getValor()));
        }

        // Criando o conjunto de dados do gráfico
        LineDataSet dataSet = new LineDataSet(entries, "Saturação de Oxigénio");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setDrawValues(false);

        // Determinar a largura do gráfico para a linha de limite
        float maxX = (limit - 1) * 2; // Último valor no eixo X

        // Criando a linha de limite (Exemplo: 90% de saturação como limite inferior)
        LineDataSet lowerLimit = createLimitLine(95, "Limite Inferior", maxX, Color.RED);

        // Criando o LineData e adicionando os conjuntos
        LineData lineData = new LineData(dataSet, lowerLimit);

        // Configurando o gráfico
        lineChart.setData(lineData);
        lineChart.getLegend().setEnabled(true); // Ativar legenda para a linha
        lineChart.getDescription().setEnabled(false); // Remover descrição padrão
        lineChart.invalidate(); // Atualizar o gráfico
    }

    private LineDataSet createLimitLine(float limit, String label, float maxX, int color) {
        List<Entry> limitEntries = new ArrayList<>();
        limitEntries.add(new Entry(0, limit)); // Início da linha
        limitEntries.add(new Entry(maxX, limit)); // Fim da linha

        LineDataSet limitLine = new LineDataSet(limitEntries, label);
        limitLine.setColor(color);
        limitLine.setLineWidth(2);
        limitLine.setDrawValues(false); // Não exibir valores na linha
        limitLine.setDrawCircles(false); // Não exibir círculos nos pontos

        return limitLine;
    }

    private void setErrorOnUi(String message) {
        Toast.makeText(SaturacaoOxActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void getSatOxFromWS() {
        GlobalID app = (GlobalID) getApplication();
        Integer id = app.getGlobalVariable();
        String address = Utils.getWSAddress(this);
        String uri = address + "/pessoas/" + id + "/saturacaoOxigenio";

        new Thread(() -> {
            HttpRequest httpRequest = new HttpRequest(HttpRequestType.GET, uri, "");
            HttpResponse httpResponse = HttpConnection.makeRequest(httpRequest);

            switch (httpResponse.getStatus()) {
                case HttpStatusCode.OK:
                    try {
                        SatOxContainerDTO satList = XmlHandler.deSerializeSatOxList(httpResponse.getBody());
                        List<SatOxDTO> satOxDTO = satList.getSatOxList();
                        List<SatOx> satOxs = Mapper.satOxDTO2SatOx(satOxDTO);
                        runOnUiThread(() -> setDataOnGraph(satOxs));
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> setErrorOnUi("Erro ao processar os dados."));
                    }
                    break;

                case HttpStatusCode.Conflict:
                    runOnUiThread(() -> setErrorOnUi("Erro: conflito nos dados da API."));
                    break;

                default:
                    runOnUiThread(() -> setErrorOnUi("Erro desconhecido."));
                    break;
            }
        }).start();
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
