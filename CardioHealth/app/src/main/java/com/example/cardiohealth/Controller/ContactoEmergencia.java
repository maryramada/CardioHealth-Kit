package com.example.cardiohealth.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class ContactoEmergencia extends BroadcastReceiver {

    secondActivity secondActivity;

    @Override
    public void onReceive(Context context, Intent intent) {
        String resposta = intent.getStringExtra("resposta");

        if (resposta != null) {
            if (resposta.equals("sim")) {
                int id = intent.getIntExtra("id", -1);
                if (id != -1) {
                    secondActivity.enviarMensagemSOS();
                } else {
                    Toast.makeText(context, "Erro: ID da pessoa não encontrado.", Toast.LENGTH_SHORT).show();
                }
            } else if (resposta.equals("nao")) {
                Toast.makeText(context, "Obrigado pela resposta. \nVolte a fazer a análise mais tarde.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

