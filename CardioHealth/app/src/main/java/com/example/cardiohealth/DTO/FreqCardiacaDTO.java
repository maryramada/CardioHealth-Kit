package com.example.cardiohealth.DTO;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

@Order(elements = {"num", "instant"})
@Root(name =  "freqCardiaca")
public class FreqCardiacaDTO {
    @Element(name = "num")
    private int num;
    @Element(name = "instant")
    private InstantDTO instant;

    public FreqCardiacaDTO (){

    }

    public FreqCardiacaDTO(int num, InstantDTO instant) {
        this.num = num;
        this.instant = instant;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public InstantDTO getInstant() {
        return instant;
    }

    public void setInstant(InstantDTO instant) {
        this.instant = instant;
    }
}
