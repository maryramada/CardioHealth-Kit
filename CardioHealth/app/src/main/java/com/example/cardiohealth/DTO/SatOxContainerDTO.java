package com.example.cardiohealth.DTO;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import java.util.List;

@Root(name = "satOxs")
public class SatOxContainerDTO {

    @ElementList(inline = true, required = false)
    private List<SatOxDTO> satOxList;

    public List<SatOxDTO> getSatOxList() {
        return satOxList;
    }

    public void setSatOxList(List<SatOxDTO> satOxList) {
        this.satOxList = satOxList;
    }
}