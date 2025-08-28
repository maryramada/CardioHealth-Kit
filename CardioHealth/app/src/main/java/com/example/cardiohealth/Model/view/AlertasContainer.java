package com.example.cardiohealth.Model.view;

import com.example.cardiohealth.Model.Alertas;


import java.util.ArrayList;

public class AlertasContainer {
    Alertas alerta;
    private ArrayList<Alertas> alertas;
    public AlertasContainer(ArrayList<Alertas> alertas)
    {
        this.alertas = alertas;
    }
    public ArrayList<Alertas> getAlertas()
    {
        return alertas;
    }
}
