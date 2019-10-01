/*
   * CLASSE : LerSerial
   * FUNÇÃO : Tudo relacionado a leitura dos dados seriais e conversão de acordo com o equipamento.
 */
package lerserialbalanca.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 *
 * @author Desenvolvimento
 */
public class LerSerial {

    private byte[] buffer;
    private static SerialPort serialPort;
    private String port;
    private String equipamento;
    private int baud;
    private int databits;
    private int stopbit;
    private int parity;
    private int total_bytes;
    private static boolean ok = false;
    public static List<String> portas = new ArrayList<String>();
    public static List<String> equipamentos = new ArrayList<String>();

    public LerSerial(String porta, String equipamento){
        // AO INICIAR A CLASSE
        try {
            setPort(porta); //PEGAR PORTA
            setEquipamento(equipamento); //PEGAR EQUIPAMENTO
            serialPort = new SerialPort(getPort());
            selecionarConfigEquipamento(); //SELECIONA CONFIGURACAO DE ACORDO COM EQUIPAMENTO
            conSerial(); // CONEXÃO SERIAL
            serialPort.addEventListener(new SerialPortReader()); //ESCUTANDO EVENTOS DA PORTA SERIAL
            listaPortas(); //ADICIONA PORTAS DISPONIVEIS NA LISTA
            listaEquipamentos(); //ADICIONA EQUIPAMENTOS COMPATIVEIS NA LISTA
        } catch (SerialPortException ex) {
            ex.printStackTrace();
        }
    }

    //COMUNICAÇÃO SERIAL
    public void conSerial() {
        try {
            serialPort.openPort();
            serialPort.setParams(baud, databits, stopbit, parity, false, false);
        } catch (SerialPortException ex) {
            JOptionPane.showMessageDialog(null, ex.getPortName() + " - " + ex.getExceptionType(), "Erro", 0);
            System.exit(0);
        }
    }

    //FECHAR COMUNICAÇÃO SERIAL
    public static void fecharSerial() {
        try {
            serialPort.closePort();
        } catch (SerialPortException ex) {
            ex.printStackTrace();
        }
    }

    //LIMPA INFORMAÇÕES INCORRETAS DA SERIAL
    public void limparLixo() {
        while (ok == false) {
            try {
                byte buffer[] = serialPort.readBytes(1);
                byte b = buffer[0];
                char c = (char) b;
                System.out.print(c);
                if (c == '\n') {
                    ok = true;
                }
            } catch (SerialPortException ex) {
                ex.printStackTrace();
            }
        }
    }

    //RETORNA DADOS DA SERIAL
    public String dataSerial() {
        try {
            if (ok == true) {
                return serialPort.readString(total_bytes);
            }
            return padraoString();
        } catch (SerialPortException serEx) {
            serEx.printStackTrace();
        }

        return padraoString();
    }
    
    //PEGA AS PORTAS DISPONIVEIS E ADICIONA A UMA LISTA DE PORTAS
    public void listaPortas(){
        String[] portNames = SerialPortList.getPortNames();
        for (int i = 0; i < portNames.length; i++) {
            portas.add(portNames[i]);
            //System.out.println(portNames[i]);
        }
    }
    
    //PEGA OS EQUIPAMENTOS COMPATIVEIS E ADICIONA A UMA LISTA DE EQUIPAMENTOS
    public void listaEquipamentos() {
        equipamentos.add("WT1000N");
    }

    
    //SELEÇÃO DE CONFIGURAÇÃO DE EQUIPAMENTO
    public void selecionarConfigEquipamento() {
        switch (equipamento) {
            case "WT1000N":
                configWT1000N();
        }
    }

    //PADRAO DE STRING PARA CADA EQUIPAMENTO
    public String padraoString() {
        String padrao = null;
        switch (equipamento) {
            case "WT1000N":
                padrao = "0,0000000,0000000,0000000";
        }
        return padrao;
    }
    
    //SELECIONA O TIPO DE FORMATAÇÃO DE ACORDO COM O EQUIPAMENTO
    public Map<String, String> selecionarDadosEquipamento() {
        switch (equipamento) {
            case "WT1000N":
                return dadosWT1000N();
        }
        return null;
    }
    
    //OUVINDO PORTA SERIAL E RETIRANDO DADOS DESNECESSÁRIOS
     static class SerialPortReader implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent event) {
            if(event.getEventType() > 0){
                while (ok == false) {
                    try {
                        byte buffer[] = serialPort.readBytes(1);
                        byte b = buffer[0];
                        char c = (char) b;
                        if (c == '\n') {
                            ok = true;
                        }
                    } catch (SerialPortException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    /********INDICADOR WT1000N*******/
    public void configWT1000N() {
        setBaud(9600);
        setDatabits(8);
        setStopbit(1);
        setParity(0);
        setTotal_bytes(27);
    }

    public Map<String, String> dadosWT1000N() {
        Map<String, String> dados = new HashMap<String, String>();
        String dado = dataSerial();
        dados.put("estavel", dado.substring(0, 1).equals("0") ? "Estável" : "Oscilando");
        String peso_bru = dado.substring(2, 9);
        String peso_liq = dado.substring(18, 25);
        
        List<String> pesos = new ArrayList<String>();
        pesos.add(peso_bru);
        pesos.add(peso_liq);
        int cnt = 0;
        for(String peso : pesos){
            if (peso.equals("000000 ") || peso.equals("0000000")) {
                peso = "0";
            } else {
                if (peso.contains("-")) {
                    if (peso.contains(".")) {
                        int pos = peso.indexOf(".");
                        peso = "-" + peso.replace(peso.substring(0, (pos - 1)), peso.substring(1, (pos - 1)).replaceFirst("0*", ""));
                    } else {
                        peso = "-" + peso.substring(1, 7).replaceFirst("0*", "");
                    }
                } else {
                    if (peso.contains(".")) {
                        int pos = peso.indexOf(".");
                        peso = (pos <= 3) ? peso.replaceFirst(peso.substring(0, (pos - 1)), peso.substring(0, (pos - 1)).replaceFirst("0*", "")) : peso.replace(peso.substring(0, (pos - 1)), peso.substring(0, (pos - 1)).replaceFirst("0*", ""));
                    } else {
                        peso = peso.replaceFirst("0*", "");
                    }
                }
            }
            pesos.set(cnt, peso);
            switch (cnt){
                case 0 :
                    peso_bru = peso;
                    break;
                case 1 : 
                    peso_liq = peso;
                    break; 
            }
            cnt++;
        }
        

        dados.put("peso_bru", peso_bru);
        dados.put("tara", dado.substring(10, 17));
        dados.put("peso_liq", peso_liq);
        return dados;
    }
    /********INDICADOR WT1000N*******/
    
    

    // GETTERS/SETTERS
    public static boolean isOk() {
        return ok;
    }

    public static void setOk(boolean ok) {
        LerSerial.ok = ok;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getBaud() {
        return baud;
    }

    public void setBaud(int baud) {
        this.baud = baud;
    }

    public int getDatabits() {
        return databits;
    }

    public void setDatabits(int databits) {
        this.databits = databits;
    }

    public int getStopbit() {
        return stopbit;
    }

    public void setStopbit(int stopbit) {
        this.stopbit = stopbit;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public int getTotal_bytes() {
        return total_bytes;
    }

    public void setTotal_bytes(int total_bytes) {
        this.total_bytes = total_bytes;
    }

    public String getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(String equipamento) {
        this.equipamento = equipamento;
    }

 

}
