package org.example.projeto_ws.Model;

import java.io.Serializable;

public class FreqCardiaca implements Serializable {
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

    @Override
    public String toString() {
        return num + " bpm em " + instant;
    }
}
