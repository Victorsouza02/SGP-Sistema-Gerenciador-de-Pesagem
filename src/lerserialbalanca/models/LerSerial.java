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

    public LerSerial(String porta, String equipamento) {
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

    public String lerLinha() {
        String linha = "";
        boolean ler = false;
        while (ler == false) {
            try {
                byte buffer[] = serialPort.readBytes(1);
                byte b = buffer[0];
                char c = (char) b;
                linha += c;
                if (c == '\n') {
                    ler = true;
                }
            } catch (SerialPortException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println(linha);
        return linha;
    }

    //RETORNA DADOS DA SERIAL
    public String dataSerial() {
        if (ok == true) {
            return lerLinha();
        }
        return padraoString();
    }

    //PEGA AS PORTAS DISPONIVEIS E ADICIONA A UMA LISTA DE PORTAS
    public void listaPortas() {
        String[] portNames = SerialPortList.getPortNames();
        for (int i = 0; i < portNames.length; i++) {
            portas.add(portNames[i]);
            //System.out.println(portNames[i]);
        }
    }

    //PEGA OS EQUIPAMENTOS COMPATIVEIS E ADICIONA A UMA LISTA DE EQUIPAMENTOS
    public void listaEquipamentos() {
        equipamentos.add("WT1000N");
        equipamentos.add("3101C");
    }

    //SELEÇÃO DE CONFIGURAÇÃO DE EQUIPAMENTO
    public void selecionarConfigEquipamento() {
        switch (equipamento) {
            case "WT1000N":
                configWT1000N();
                break;
            case "3101C":
                config3101C();
                break;
        }
    }

    //PADRAO DE STRING PARA CADA EQUIPAMENTO
    public String padraoString() {
        String padrao = null;
        switch (equipamento) {
            case "WT1000N":
                padrao = "0,0000000,0000000,0000000";
                break;
            case "3101C":
                padrao = "PB: 00000 T: 00000";
                break;
        }
        return padrao;
    }

    //SELECIONA O TIPO DE FORMATAÇÃO DE ACORDO COM O EQUIPAMENTO
    public Map<String, String> selecionarDadosEquipamento() {
        switch (equipamento) {
            case "WT1000N":
                return dadosWT1000N();
            case "3101C":
                return dados3101C();
        }
        return null;
    }

    //OUVINDO PORTA SERIAL E RETIRANDO DADOS DESNECESSÁRIOS
    static class SerialPortReader implements SerialPortEventListener {

        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.getEventType() > 0) {
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

    /**
     * ******INDICADOR ALFA 3101C****
     */
    public void config3101C() {
        setBaud(9600);
        setDatabits(8);
        setStopbit(1);
        setParity(0);
    }

    public Map<String, String> dados3101C() {
        Map<String, String> dados = new HashMap<String, String>();
        String dado = dataSerial();
        String peso_bru = "";
        String peso_liq = "";
        String tara = "";
        boolean sobrecarga = dado.contains("S<BRE");
        boolean temVirgula = dado.contains(",");
        boolean comTara = dado.substring(0, 2).equals("PL");

        if (temVirgula) {
            peso_bru = dado.substring(3, 10).replaceAll(",", ".");
            peso_liq = dado.substring(3, 10).replaceAll(",", ".");
            tara = dado.substring(13, 20).replaceAll(",", ".");
        } else if (!temVirgula && !sobrecarga) {
            peso_bru = dado.substring(3, 9);
            peso_liq = dado.substring(3, 9);
            tara = dado.substring(13, 18);
        }

        System.out.println(dado);

        if (!sobrecarga) { //SE NÃO ESTIVER COM SOBRECARGA
            dados.put("estavel", !dado.contains("*") ? "Estável" : "Oscilando");
            List<String> pesos = new ArrayList<String>();
            pesos.add(peso_bru);
            pesos.add(peso_liq);
            pesos.add(tara);
            int cnt = 0;
            for (String peso : pesos) {
                if (peso.equals(" 00000")) {
                    peso = "0";
                } else {
                    if (peso.contains("-")) {
                        if (peso.contains(".")) {
                            int pos = peso.indexOf(".");
                            peso = "-" + peso.replace(peso.substring(0, (pos - 1)), peso.substring(1, (pos - 1)).replaceFirst("0*", ""));
                        } else {
                            peso = "-" + peso.substring(1, 7).replaceFirst("0*", ""); //OK
                        }
                    } else {
                        if (peso.contains(".")) {
                            int pos = peso.indexOf(".");
                            peso = (pos <= 2) ? peso.replaceFirst(peso.substring(1, (pos - 1)), peso.substring(1, (pos - 1)).replaceFirst("0*", "")) : peso.replace(peso.substring(0, (pos - 1)), peso.substring(1, (pos - 1)).replaceFirst("0*", ""));
                        } else {
                            peso = peso.substring(1, 5).replaceFirst("0*", ""); //OK
                        }
                    }
                }
                pesos.set(cnt, peso);
                switch (cnt) {
                    case 0:
                        peso_bru = peso;
                        break;
                    case 1:
                        peso_liq = peso;
                        break;
                    case 2:
                        tara = peso;
                        break;
                }
                cnt++;
            }

            if (comTara) {
                if (temVirgula) {
                    Float pb = Float.parseFloat(peso_liq) + Float.parseFloat(tara);
                    dados.put("peso_bru", String.valueOf(pb));
                } else {
                    int pb = Integer.parseInt(peso_liq) + Integer.parseInt(tara);
                    dados.put("peso_bru", String.valueOf(pb));
                }
            } else {
                dados.put("peso_bru", peso_bru);
            }
            dados.put("peso_liq", peso_liq);
            dados.put("tara", tara);
        } else { //SE ESTIVER COM SOBRECARGA
            dados.put("estavel", "Sobrecarga");
            dados.put("peso_bru", "0");
            dados.put("peso_liq", "0");
            dados.put("tara", "0");
        }

        System.out.println("Peso Bruto: " + dados.get("peso_bru") + "/  Peso Liquido : " + dados.get("peso_liq") + "/  Tara : " + dados.get("tara"));

        return dados;
    }

    /**
     * ******INDICADOR ALFA 3101C****
     */
    /**
     * ******INDICADOR WT1000N******
     */
    public void configWT1000N() {
        setBaud(9600);
        setDatabits(8);
        setStopbit(1);
        setParity(0);
    }

    public Map<String, String> dadosWT1000N() {
        Map<String, String> dados = new HashMap<String, String>();
        String dado = dataSerial();
        boolean sobrecarga = dado.contains("OL");

        if (!sobrecarga) {
            String peso_bru = dado.substring(2, 9);
            String peso_liq = dado.substring(18, 25);
            
            List<String> pesos = new ArrayList<String>();
            pesos.add(peso_bru);
            pesos.add(peso_liq);
            int cnt = 0;
            for (String peso : pesos) {
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
                switch (cnt) {
                    case 0:
                        peso_bru = peso;
                        break;
                    case 1:
                        peso_liq = peso;
                        break;
                }
                cnt++;
            }
            dados.put("estavel", dado.substring(0, 1).equals("0") ? "Estável" : "Oscilando");
            dados.put("peso_bru", peso_bru);
            dados.put("tara", dado.substring(10, 17));
            dados.put("peso_liq", peso_liq);
        } else {
            dados.put("estavel", "Sobrecarga");
            dados.put("peso_bru", "0");
            dados.put("tara", "0");
            dados.put("peso_liq", "0");
        }

        
        return dados;
    }

    /**
     * ******INDICADOR WT1000N******
     */
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
