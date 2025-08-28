package com.example.cardiohealth.Model;

import android.app.Application;

public class GlobalID extends Application {
    private Integer globalVariable;
    @Override
    public void onCreate() {
        super.onCreate();
        globalVariable = null;
    }

    public Integer getGlobalVariable() {
        return globalVariable;
    }

    public void setGlobalVariable(Integer globalVariable) {
        this.globalVariable = globalVariable;
    }
}
