package com.example.cardiohealth.DTO;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.example.cardiohealth.Model.Instant;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;
import java.time.LocalDateTime;

@RequiresApi(api = Build.VERSION_CODES.O)
@Order(elements = {"tipo", "descricao", "valor", "instant"})
@Root(name =  "alerta")
public class AlertasDTO {
    @Element(name = "tipo")
    private String tipo;
    @Element(name = "descricao")
    private String descricao;
    @Element(name = "valor")
    private int valor;
    @Element(name = "instant")
    private InstantDTO dataHora;

    public AlertasDTO () {
    }

    public AlertasDTO(String tipo, String descricao, int valor, InstantDTO dataHora) {
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public InstantDTO getDataHora() {
        return dataHora;
    }

    public void setDataHora(InstantDTO dataHora) {
        this.dataHora = dataHora;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }


    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
