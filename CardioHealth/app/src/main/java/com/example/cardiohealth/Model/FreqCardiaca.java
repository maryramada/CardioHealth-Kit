package com.example.cardiohealth.Model;

import com.example.cardiohealth.DTO.InstantDTO;

public class FreqCardiaca {
    private final int num;
    private final Instant instant;

    public FreqCardiaca(int num, Instant instant) {
        this.num = num;
        this.instant = instant;
    }



    public int getNum() {
        return num;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setNum(int num) {
    }

    public void setInstant(InstantDTO instant) {
    }

    @Override
    public String toString() {
        return num + " bpm em " + instant;
    }
}

