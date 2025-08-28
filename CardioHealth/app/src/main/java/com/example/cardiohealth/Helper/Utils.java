package com.example.cardiohealth.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class Utils {

    public static final String MODE                         = "MODE";

    public static final int ACTIVITY_MODE_NOTHING           = 0;
    public static final int ACTIVITY_MODE_ADDING            = 1;
    public static final int ACTIVITY_MODE_DELETING          = 2;
    public static final int ACTIVITY_MODE_EDITING           = 3;
    public static final int ACTIVITY_MODE_DETAILS           = 4;

    public static final String UNKNOWN_MODE                 = "Mode unknown";

    public static final int NOME_DEFAULT_VALUE                = -1;
    public static final String nome                           = "nome";

    public static final String OPERATION_ADD_SUCESSS        = "Inserido com sucesso";
    public static final String OPERATION_UPDATE_SUCESSS     = "Alterado com sucesso";
    public static final String OPERATION_DELETE_SUCESSS     = "Eliminado com sucesso";
    public static final String OPERATION_NO_DATA            = "Sem dados";

    public static final String UNKNOWN_ACTION               = "Unknown Action";

    public static final String MYSHPREFS                  = "MySharedPreferences";

    public static final String IP1 = "IP1";
    public static final String IP2 = "IP2";
    public static final String IP3 = "IP3";
    public static final String IP4 = "IP4";
    public static final String PORT = "PORT";

    // Valores default fixos (os que você quer que prevaleçam)
    private static final int IP1_DEFAULT_VALUE = 172;
    private static final int IP2_DEFAULT_VALUE = 20;
    private static final int IP3_DEFAULT_VALUE = 10;
    private static final int IP4_DEFAULT_VALUE = 3;
    private static final int PORT_DEFAULT_VALUE = 8080;

    // Método que retorna o endereço do WebService utilizando os valores definidos no código
    public static String getWSAddress(Context context) {
        return "http://" + IP1_DEFAULT_VALUE + "." + IP2_DEFAULT_VALUE + "." + IP3_DEFAULT_VALUE + "." + IP4_DEFAULT_VALUE + ":" + PORT_DEFAULT_VALUE + "/api";
    }

    // O método de set ainda pode salvar os valores no SharedPreferences,
    // mas os métodos de leitura abaixo não os utilizarão.
    public static void setWSAddress(Context context, int ip1, int ip2, int ip3, int ip4, int port) {
        SharedPreferences settings = getSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(IP1, ip1);
        editor.putInt(IP2, ip2);
        editor.putInt(IP3, ip3);
        editor.putInt(IP4, ip4);
        editor.putInt(PORT, port);
        editor.commit();
    }

    // Retorna os números de IP sempre com os valores default
    public static int getIPNumber(Context context, String IP) {
        switch(IP) {
            case IP1:
                return IP1_DEFAULT_VALUE;
            case IP2:
                return IP2_DEFAULT_VALUE;
            case IP3:
                return IP3_DEFAULT_VALUE;
            case IP4:
                return IP4_DEFAULT_VALUE;
            default:
                return 0;
        }
    }

    // Retorna o endereço IP formado pelos valores default
    public static String getIPAddress(Context context) {
        return IP1_DEFAULT_VALUE + "." + IP2_DEFAULT_VALUE + "." + IP3_DEFAULT_VALUE + "." + IP4_DEFAULT_VALUE;
    }

    // Retorna a porta default
    public static int getPortNumber(Context context) {
        return PORT_DEFAULT_VALUE;
    }

    // Obtém a instância de SharedPreferences (permanece inalterado)
    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(MYSHPREFS, Context.MODE_PRIVATE);
    }

    // Método que obtém o IP da interface WIFI (não foi alterado)
    public String getIpAddress(Context context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return ip;
    }
}
