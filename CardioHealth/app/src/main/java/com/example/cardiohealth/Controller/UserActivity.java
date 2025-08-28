package com.example.cardiohealth.Controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.cardiohealth.DTO.ErrorDTO;
import com.example.cardiohealth.DTO.PessoaDTO;
import com.example.cardiohealth.Helper.Response;
import com.example.cardiohealth.Helper.Utils;
import com.example.cardiohealth.Model.Data;
import com.example.cardiohealth.Model.GlobalID;
import com.example.cardiohealth.Model.Pessoa;
import com.example.cardiohealth.Network.*;
import com.example.cardiohealth.R;
import com.example.cardiohealth.Xml.XmlHandler;
import java.util.Calendar;

public class UserActivity extends AppCompatActivity {
    int mode;
    Button btnSalvar, btnVoltar;
    EditText nomeEditText, passwordEditText, emailEditText, telemovelEditText, contactoEmergenciaEditText;
    DatePicker nascimentoEditText;
    LinearLayout bottomNav;
    TextView btnTerminarSessao, btnApagarConta;
    private static final int PERMISSION_REQUEST_SEND_SMS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        // Inicialização dos componentes da interface
        btnSalvar = findViewById(R.id.btnSalvar);
        btnVoltar = findViewById(R.id.btnVoltar);
        nomeEditText = findViewById(R.id.editNome);
        passwordEditText = findViewById(R.id.editPassword);
        nascimentoEditText = findViewById(R.id.dpBirthday);
        emailEditText = findViewById(R.id.editEmail);
        telemovelEditText = findViewById(R.id.editTelemovel);
        contactoEmergenciaEditText = findViewById(R.id.editContactoEmergencia);
        bottomNav = findViewById(R.id.bottomNav);

        // Referências para os TextViews que funcionarão como botões extras
        btnTerminarSessao = findViewById(R.id.btnTerminarSessao);
        btnApagarConta = findViewById(R.id.btnApagarConta);

        // Listener para "Terminar Sessão" - redireciona para LoginActivity
        btnTerminarSessao.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Listener para "Apagar Conta" - chama o método para deletar a pessoa
        btnApagarConta.setOnClickListener(v -> {
            deletePessoaFromWS();
        });

        Button btnVoltar = findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        // Configuração dos ícones inferiores
        ImageView btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, secondActivity.class);
            startActivity(intent);
        });

        ImageView btnHistorico = findViewById(R.id.btnHistorico);
        btnHistorico.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, historicoActivity.class);
            startActivity(intent);
        });

        ImageView btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, infoActivity.class);
            startActivity(intent);
        });

        ImageView btnSOS = findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(v -> enviarMensagemSOS());


        // Configurando o botão "Salvar"
        btnSalvar.setOnClickListener(v -> {
            // Validação dos campos
            String nome = nomeEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String telemovelStr = telemovelEditText.getText().toString().trim();
            String contactoEmergenciaStr = contactoEmergenciaEditText.getText().toString().trim();

            if (nome.isEmpty() || password.isEmpty() || email.isEmpty() ||
                    telemovelStr.isEmpty() || contactoEmergenciaStr.isEmpty()) {
                Toast.makeText(UserActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int telemovel;
            try {
                telemovel = Integer.parseInt(telemovelStr);
            } catch (NumberFormatException e) {
                Toast.makeText(UserActivity.this, "Número de telemóvel inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            int contactoEmergencia;
            try {
                contactoEmergencia = Integer.parseInt(contactoEmergenciaStr);
            } catch (NumberFormatException e) {
                Toast.makeText(UserActivity.this, "Contacto de Emergência inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar data de nascimento
            Data nascimento = new Data(nascimentoEditText.getDayOfMonth(),
                    nascimentoEditText.getMonth(),
                    nascimentoEditText.getYear());

            if (nascimento == null) {
                Toast.makeText(UserActivity.this, "Data de nascimento inválida", Toast.LENGTH_SHORT).show();
                return;
            }

            // Todos os campos estão válidos
            Pessoa pessoa = new Pessoa(nome, password, nascimento, email, telemovel, contactoEmergencia);

            switch (mode) {
                case Utils.ACTIVITY_MODE_ADDING:
                    postPessoa2WS(pessoa);
                    finish();
                case Utils.ACTIVITY_MODE_EDITING:
                    putPessoa2WS(pessoa);

            }
        });

        Intent intent = getIntent();
        mode = intent.getIntExtra(Utils.MODE, Utils.ACTIVITY_MODE_NOTHING);
        configureUI();
    }

    private void configureUI() {
        if (mode == Utils.ACTIVITY_MODE_ADDING) {
            // No modo de criação, todos os campos ficam editáveis e em branco
            nomeEditText.setEnabled(true);
            passwordEditText.setEnabled(true);
            nascimentoEditText.setEnabled(true);
            emailEditText.setEnabled(true);
            telemovelEditText.setEnabled(true);
            contactoEmergenciaEditText.setEnabled(true);
            bottomNav.setVisibility(View.GONE);
            btnSalvar.setText("Criar");
        } else if (mode == Utils.ACTIVITY_MODE_EDITING) {
            // No modo de edição, os campos também ficam editáveis,
            // mas iniciam com os dados já preenchidos (buscados do WS)
            nomeEditText.setEnabled(true);
            passwordEditText.setEnabled(true);
            nascimentoEditText.setEnabled(true);
            emailEditText.setEnabled(true);
            telemovelEditText.setEnabled(true);
            contactoEmergenciaEditText.setEnabled(true);
            btnVoltar.setVisibility(View.GONE);
            btnSalvar.setText("Salvar Alterações");
            btnTerminarSessao.setVisibility(View.VISIBLE);
            btnApagarConta.setVisibility(View.VISIBLE);
            getPessoaFromWS();
        }
    }

    private void setDataOnUi(Pessoa data) {
        if (data != null) {
            nomeEditText.setText(data.getNome());
            passwordEditText.setText(data.getPassword());
            emailEditText.setText(data.getEmail());
            telemovelEditText.setText(String.valueOf(data.getTelemovel()));
            contactoEmergenciaEditText.setText(String.valueOf(data.getContactoEmergencia()));

            Data date = data.getNascimento();
            // Atualiza o DatePicker com o ano, mês e dia
            nascimentoEditText.updateDate(date.getAno(), date.getMes(), date.getDia());
        } else {
            // Se não houver dados, limpa os campos
            nomeEditText.setText("");
            passwordEditText.setText("");
            emailEditText.setText("");
            telemovelEditText.setText("");
            contactoEmergenciaEditText.setText("");
            Calendar calendar = Calendar.getInstance();
            nascimentoEditText.updateDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
        }
    }
    private void setSuccessOnUi(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void setErrorOnUi(String message) {
        Toast.makeText(UserActivity.this, message, Toast.LENGTH_LONG).show();
    }


    private void postPessoa2WS(Pessoa data) {
        String address = Utils.getWSAddress(this);
        String uri = address + "/pessoas";
        PessoaDTO dto = Mapper.pessoa2pessoaDTO(data);
        final String body = XmlHandler.serializePessoaDTO2XML(dto);

        new Thread(() -> {
            Response response = null;
            HttpRequest httpRequest = new HttpRequest(HttpRequestType.POST, uri, body);
            HttpResponse httpResponse = HttpConnection.makeRequest(httpRequest);

            switch (httpResponse.getStatus()) {
                case HttpStatusCode.Created:
                    response = new Response(HttpStatusCode.Created, null);
                    break;
                case HttpStatusCode.Conflict:
                    ErrorDTO error = XmlHandler.deSerializeXML2ErrorDto(httpResponse.getBody());
                    response = new Response(HttpStatusCode.Conflict, error.getMessage());
                    break;
            }

            final Response result = response;
            runOnUiThread(() -> {
                if (result != null) {
                    if (result.getStatus() == HttpStatusCode.Created) {
                        Toast.makeText(UserActivity.this, "Adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish(); // Fecha a UserActivity para evitar voltar
                    } else if (result.getStatus() == HttpStatusCode.Conflict) {
                        Object object = result.getBody();
                        if (object instanceof String) {
                            String message = (String) object;
                            Toast.makeText(UserActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }).start();

    }

    private void deletePessoaFromWS() {
        GlobalID app = (GlobalID) getApplication();
        Integer id = app.getGlobalVariable();
        String address = Utils.getWSAddress(this);
        String uri = address + "/pessoas/" + id;
        String body = "";

        new Thread(() -> {
            Response response;
            HttpRequest httpRequest = new HttpRequest(HttpRequestType.DELETE, uri, body);
            HttpResponse httpResponse = HttpConnection.makeRequest(httpRequest);

            // Adicionando a verificação para No Content (204)
            if (httpResponse.getStatus() == HttpStatusCode.OK ||
                    httpResponse.getStatus() == HttpStatusCode.Accepted ||
                    httpResponse.getStatus() == HttpStatusCode.NoContent) {
                response = new Response(HttpStatusCode.OK, "Pessoa eliminada com sucesso!");
            } else if (httpResponse.getStatus() == HttpStatusCode.Conflict) {
                ErrorDTO error = XmlHandler.deSerializeXML2ErrorDto(httpResponse.getBody());
                response = new Response(HttpStatusCode.Conflict, error.getMessage());
            } else {
                response = new Response(httpResponse.getStatus(), "Erro inesperado");
            }

            final Response result = response;
            runOnUiThread(() -> {
                if (result != null) {
                    if (result.getStatus() == HttpStatusCode.OK) {
                        // Exibe a mensagem de sucesso
                        Toast.makeText(UserActivity.this, result.getBody().toString(), Toast.LENGTH_SHORT).show();
                        // Redireciona imediatamente para o LoginActivity
                        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UserActivity.this, result.getBody().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }).start();
    }


    private void getPessoaFromWS() {
        GlobalID app = (GlobalID) getApplication();
        Integer id = app.getGlobalVariable();
        String address = Utils.getWSAddress(this);
        String uri = address + "/pessoas/" + id;
        String body = "";
        new Thread() {
            public void run() {
                Response response = null;
                HttpRequest httpRequest = new HttpRequest(HttpRequestType.GET, uri, body);
                HttpResponse httpResponse = HttpConnection.makeRequest(httpRequest);
                switch (httpResponse.getStatus()) {
                    case HttpStatusCode.OK:
                    case HttpStatusCode.Accepted:
                        PessoaDTO dto = XmlHandler.deSerializeXML2PessoaDTO(httpResponse.getBody());
                        Pessoa data = Mapper.pessoaDTO2pessoa(dto);
                        response = new Response(HttpStatusCode.OK, data);
                        break;
                    case HttpStatusCode.Conflict:
                        ErrorDTO error = XmlHandler.deSerializeXML2ErrorDto(httpResponse.getBody());
                        response = new Response(HttpStatusCode.Conflict, error.getMessage());
                        break;
                }
                final Response result = response;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null) {
                            Object object = result.getBody();
                            switch (result.getStatus()) {
                                case HttpStatusCode.OK:
                                    if (object instanceof Pessoa) {
                                        Pessoa data = (Pessoa) object;
                                        setDataOnUi(data);
                                    }
                                    break;
                                case HttpStatusCode.Conflict:
                                    if (object instanceof String) {
                                        String message = (String) object;
                                        setErrorOnUi(message);
                                    }
                                    break;
                                default:
                                    setErrorOnUi(Utils.UNKNOWN_ACTION);
                                    break;
                            }
                        }
                    }
                });
            }
        }.start();
    }

    private void putPessoa2WS(Pessoa data) {
        GlobalID app = (GlobalID) getApplication();
        Integer id = app.getGlobalVariable();
        String address = Utils.getWSAddress(this);
        String uri = address + "/pessoas/" + id;
        PessoaDTO dto = Mapper.pessoa2pessoaDTO(data);
        final String body = XmlHandler.serializePessoaDTO2XML(dto);

        new Thread() {
            public void run() {
                Response response;
                HttpRequest httpRequest = new HttpRequest(HttpRequestType.PUT, uri, body);
                HttpResponse httpResponse = HttpConnection.makeRequest(httpRequest);

                switch (httpResponse.getStatus()) {
                    case HttpStatusCode.OK:
                        // Sucesso: o status OK indica que os dados foram atualizados
                        response = new Response(HttpStatusCode.OK, null);
                        break;
                    case HttpStatusCode.Conflict:
                        ErrorDTO error = XmlHandler.deSerializeXML2ErrorDto(httpResponse.getBody());
                        response = new Response(HttpStatusCode.Conflict, error.getMessage());
                        break;
                    default:
                        // Opcional: tratar outros códigos de status conforme necessário
                        response = new Response(httpResponse.getStatus(), httpResponse.getBody());
                        break;
                }

                final Response result = response;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null) {
                            Object object = result.getBody();
                            switch (result.getStatus()) {
                                case HttpStatusCode.OK:
                                    // Exibe a mensagem de sucesso na UI
                                    Toast.makeText(UserActivity.this, "Alterações salvas com sucesso!", Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpStatusCode.Conflict:
                                    if (object instanceof String) {
                                        String message = (String) object;
                                        setErrorOnUi(message);
                                    }
                                    break;
                                default:
                                    setErrorOnUi(Utils.UNKNOWN_ACTION);
                                    break;
                            }
                        }
                    }
                });
            }
        }.start();
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