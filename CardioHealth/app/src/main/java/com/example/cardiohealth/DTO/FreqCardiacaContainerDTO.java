package com.example.cardiohealth.DTO;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import java.util.List;

@Root(name = "freqCardiacas")
public class FreqCardiacaContainerDTO {

    @ElementList(inline = true, required = false)
    private List<FreqCardiacaDTO> freqCardiacaList;

    public List<FreqCardiacaDTO> getFreqCardiacaList() {
        return freqCardiacaList;
    }

    public void setFreqCardiacaList(List<FreqCardiacaDTO> freqCardiacaList) {
        this.freqCardiacaList = freqCardiacaList;
    }
}

