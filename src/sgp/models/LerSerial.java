/*
   * CLASSE : LerSerial
   * FUNÇÃO : Tudo relacionado a leitura dos dados seriais e conversão de acordo com o equipamento.
 */
package sgp.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import sgp.config.VariaveisGlobais;
import sgp.utils.Formatacao;

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
            listaPortas(); //ADICIONA PORTAS DISPONIVEIS NA LISTA
            listaEquipamentos(); //ADICIONA EQUIPAMENTOS COMPATIVEIS NA LISTA
            setPort(porta); //PEGAR PORTA
            setEquipamento(equipamento); //PEGAR EQUIPAMENTO
            if (!VariaveisGlobais.isModoManual()) {
                serialPort = new SerialPort(getPort());
                selecionarConfigEquipamento(); //SELECIONA CONFIGURACAO DE ACORDO COM EQUIPAMENTO
                conSerial(); // CONEXÃO SERIAL
                serialPort.addEventListener(new SerialPortReader()); //ESCUTANDO EVENTOS DA PORTA SERIAL
            }
        } catch (SerialPortException ex) {
            VariaveisGlobais.setErroSerialDetectado(true);
            VariaveisGlobais.setMensagem("Houve um erro de conexão serial, verifique a porta.");
        }
    }

    //COMUNICAÇÃO SERIAL
    public void conSerial() {
        try {
            serialPort.openPort();
            serialPort.setParams(baud, databits, stopbit, parity, false, false);
        } catch (SerialPortException ex) {
            VariaveisGlobais.setErroSerialDetectado(true);
            VariaveisGlobais.setMensagem("Houve um erro de conexão serial, verifique a porta.");
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
        boolean ler = true;
        while (ler == true) {
            try {
                byte buffer[] = serialPort.readBytes(1);
                byte b = buffer[0];
                char c = (char) b;
                linha += c;
                if (c == '\n') {
                    ler = false;
                }
            } catch (SerialPortException ex) {
                ex.printStackTrace();
            }
        }
        return linha;
    }

    //RETORNA DADOS DA SERIAL
    public String receberDadosSerial() {
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
        }
    }

    //PEGA OS EQUIPAMENTOS COMPATIVEIS E ADICIONA A UMA LISTA DE EQUIPAMENTOS
    public void listaEquipamentos() {
        equipamentos.add("WT1000N"); //INDICADOR WEIGHTECH WT1000N
        equipamentos.add("3101C"); // INDICADOR ALFA LINHA 3100
        equipamentos.add("WT27"); // INDICADOR WEIGHTECH WT27
        equipamentos.add("MANUAL"); // INDICADOR WEIGHTECH WT27
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
            case "WT27":
                configWT27();
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
            case "WT27":
                padrao = "EB,B: 000000,T:000000,L: 000000";
                break;
        }
        return padrao;
    }

    //SELECIONA O TIPO DE FORMATAÇÃO DE ACORDO COM O EQUIPAMENTO
    public Map<String, String> selecionarDadosEquipamento() {
        switch (equipamento) {
            case "WT1000N":
                return Formatacao.formatarDadosWT1000N(receberDadosSerial());
            case "3101C":
                return Formatacao.formatarDados3101C(receberDadosSerial());
            case "WT27":
                return Formatacao.formatarDadosWT27(receberDadosSerial());
            case "MANUAL":
                return Formatacao.formatarDadosManuais();
        }
        return null;
    }

    /**
     * *******CONFIGURAÇÃO DE COMUNICAÇÃO ALFA 3101C****
     */
    public void config3101C() {
        setBaud(9600);
        setDatabits(8);
        setStopbit(1);
        setParity(0);
    }

    /**
     * *****************************************
     */

    /**
     * *******CONFIGURAÇÃO DE COMUNICAÇÃO WEIGHTECH WT1000N******
     */
    public void configWT1000N() {
        setBaud(9600);
        setDatabits(8);
        setStopbit(1);
        setParity(0);
    }

    /**
     * *******************************************
     */

    /**
     * ********CONFIGURAÇÃO DE COMUNICAÇÃO WEIGHTECH WT27******
     */
    public void configWT27() {
        setBaud(9600);
        setDatabits(8);
        setStopbit(1);
        setParity(0);
    }

    /**
     * ********************************************
     */

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
