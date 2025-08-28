package com.example.cardiohealth.Model.view;
import com.example.cardiohealth.Model.SatOx;

import java.util.ArrayList;

public class SatOxContainer {
    SatOx satOx;
    private ArrayList<SatOx> satOxs;

    public SatOxContainer(SatOx satOx, ArrayList<SatOx> satOxs) {
        this.satOx = satOx;
        this.satOxs = satOxs;
    }

    public SatOxContainer(ArrayList<SatOx> satOxs) {


    }
    public SatOx getSatOx() {
        return satOx;
    }

    public void setSatOx(SatOx satOx) {
        this.satOx = satOx;
    }

    public ArrayList<SatOx> getSatOxs() {
        return satOxs;
    }

    public void setSatOxs(ArrayList<SatOx> satOxs) {
        this.satOxs = satOxs;
    }
}