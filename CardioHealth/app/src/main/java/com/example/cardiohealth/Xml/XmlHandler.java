package com.example.cardiohealth.Xml;

import com.example.cardiohealth.Controller.Mapper;
import com.example.cardiohealth.DTO.*;
import com.example.cardiohealth.Model.FreqCardiaca;
import com.example.cardiohealth.Model.Instant;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class XmlHandler {
    public static ErrorDTO deSerializeXML2ErrorDto(String xmlData) {
        ErrorDTO data = null;
        if (xmlData != null) {
            Serializer serializer = new Persister();
            try {
                data = serializer.read(ErrorDTO.class, xmlData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static String serializeErrorDto2XML(ErrorDTO data) {
        StringWriter writer = new StringWriter();
        if (data != null) {
            Serializer serializer = new Persister();
            try {
                serializer.write(data, writer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return writer.toString();
    }

    public static String serializePessoaDTO2XML(PessoaDTO data) {
        StringWriter writer = new StringWriter();

        if (data != null) {
            Serializer serializer = new Persister();
            try {
                serializer.write(data, writer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return writer.toString();
    }

    public static AlertaContainerDTO deSerializeXML2AlertasContainerDTO(String xmlData) {
        AlertaContainerDTO data = null;
        if (xmlData != null) {
            Serializer serializer = new Persister();
            try {
                data = serializer.read(AlertaContainerDTO.class, xmlData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static String serializePessoaLogInDTO2XML(PessoaLogInDTO data) {
        StringWriter writer = new StringWriter();

        if (data != null) {
            Serializer serializer = new Persister();
            try {
                serializer.write(data, writer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return writer.toString();
    }

    public static Integer deSerializeXML2int(String xmlData) {
        Integer data = null;
        if (xmlData != null) {
            Serializer serializer = new Persister();
            try {
                data = serializer.read(Integer.class, xmlData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static FreqCardiacaContainerDTO deSerializeFrequenciaCardiacaList(String xmlData) {
        FreqCardiacaContainerDTO data = null;
        if (xmlData != null) {
            Serializer serializer = new Persister();
            try {
                data = serializer.read(FreqCardiacaContainerDTO.class, xmlData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;

    }

    public static PessoaDTO deSerializeXML2PessoaDTO(String body) {
        PessoaDTO data = null;
        if (body != null) {
            Serializer serializer = new Persister();
            try {
                data = serializer.read(PessoaDTO.class, body);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static SatOxContainerDTO deSerializeSatOxList(String xmlData) {
        SatOxContainerDTO data = null;
        if (xmlData != null) {
            Serializer serializer = new Persister();
            try {
                data = serializer.read(SatOxContainerDTO.class, xmlData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }


}

