package com.example.cardiohealth.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cardiohealth.DTO.AlertaContainerDTO;
import com.example.cardiohealth.DTO.ErrorDTO;
import com.example.cardiohealth.DTO.PessoaDTO;
import com.example.cardiohealth.DTO.PessoaLogInDTO;
import com.example.cardiohealth.Helper.Response;
import com.example.cardiohealth.Helper.Utils;
import com.example.cardiohealth.Model.GlobalID;
import com.example.cardiohealth.Model.PessoaLogIn;
import com.example.cardiohealth.Model.view.AlertasContainer;
import com.example.cardiohealth.Network.*;
import com.example.cardiohealth.R;
import com.example.cardiohealth.Xml.XmlHandler;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Campos de entrada
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button createAccountButton = findViewById(R.id.createAccountButton);

        // Configurar clique no botão de login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    emailEditText.setError("Por favor, insira seu email.");
                } else if (password.isEmpty()) {
                    passwordEditText.setError("Por favor, insira sua senha.");
                } else {
                    PessoaLogIn pLogIn = new PessoaLogIn(email, password);
                    logInPessoa(pLogIn);
                }

            }
        });

        // Configurar clique no botão de criar conta
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                intent.putExtra(Utils.MODE,Utils.ACTIVITY_MODE_ADDING);
                startActivity(intent);
            }
        });
    }

    private void logInPessoa(PessoaLogIn pLogIn) {
        GlobalID app = (GlobalID) getApplication();
        String address = Utils.getWSAddress(this);
        String uri = address + "/login";
        PessoaLogInDTO dto = Mapper.pessoaLogIn2pessoaLogInDTO(pLogIn);
        final String body = XmlHandler.serializePessoaLogInDTO2XML(dto);

        new Thread(() -> {
            Response response = null;
            HttpRequest httpRequest = new HttpRequest(HttpRequestType.POST, uri, body);
            HttpResponse httpResponse = HttpConnection.makeRequest(httpRequest);

            switch (httpResponse.getStatus()) {
                case HttpStatusCode.Accepted:
                   int id = XmlHandler.deSerializeXML2int(httpResponse.getBody());
                    response = new Response(HttpStatusCode.OK, id);
                    app.setGlobalVariable(id);
                    Intent intent = new Intent(LoginActivity.this, secondActivity.class);
                    startActivity(intent);
                    finish();
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
                        Toast.makeText(LoginActivity.this, "Adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, secondActivity.class);
                        startActivity(intent);
                        finish(); // Fecha a UserActivity para evitar voltar
                    } else if (result.getStatus() == HttpStatusCode.Conflict) {
                        Object object = result.getBody();
                        if (object instanceof String) {
                            String message = (String) object;
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }).start();
    }
}
