package com.example.cardiohealth.Model.view;
import com.example.cardiohealth.Model.FreqCardiaca;
import java.util.ArrayList;

public class FreqCardiacaContainer {
    FreqCardiaca freqCardiaca;
    private ArrayList<FreqCardiaca> freqCardiacas;
    public FreqCardiacaContainer (ArrayList<FreqCardiaca> freqCardiacas)
    {
        this.freqCardiacas = freqCardiacas;
    }
    public ArrayList<FreqCardiaca> getFreqCardiacas()
    {
        return freqCardiacas;
    }
}