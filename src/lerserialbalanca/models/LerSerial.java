/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca.models;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

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

    public LerSerial(String porta, String equipamento){
        try {
            setPort(porta);
            setEquipamento(equipamento);
            serialPort = new SerialPort(getPort());
            selecionarConfigEquipamento();
            conSerial();
            serialPort.addEventListener(new SerialPortReader());
        } catch (SerialPortException ex){
            System.out.println(ex.getMessage());
        }
    }

    //COMUNICAÇÃO SERIAL
    public void conSerial(){
        try {
            serialPort.openPort();
            serialPort.setParams(baud, databits, stopbit, parity, false, false);
        } catch (SerialPortException ex){
            JOptionPane.showMessageDialog(null, ex.getPortName() + " - " + ex.getExceptionType(), "Erro", 0);
            System.exit(0);
        }
    }

    //RETORNA DADOS DA SERIAL
    public String dataSerial(){
        try {
            if(ok == true){
                return serialPort.readString(total_bytes);
            } 
            return padraoString();
        } catch (SerialPortException serEx){
            System.out.println(serEx.getMessage());
        } 
        
        return padraoString();
    }

    public void selecionarConfigEquipamento() {
        switch (equipamento) {
            case "WT1000N":
                configWT1000N();
        }
    }
    
    public String padraoString(){
        String padrao = null;
        switch(equipamento){
            case "WT1000N":
                padrao = "0,0000000,0000000,0000000";
        }
        return padrao;
    }

    public Map<String, String> selecionarDadosEquipamento() {
        switch (equipamento) {
            case "WT1000N":
                return dadosWT1000N();
        }
        return null;
    }

    public void configWT1000N() {
        setBaud(9600);
        setDatabits(8);
        setStopbit(1);
        setParity(0);
        setTotal_bytes(27);
    }

    public Map<String, String> dadosWT1000N(){
        Map<String, String> dados = new HashMap<String, String>();
        String dado = dataSerial();
        dados.put("estavel", dado.substring(0, 1).equals("0") ? "Estável" : "Oscilando");
        String peso_bru = dado.substring(2, 9);
        
        if (peso_bru.equals("000000 ") || peso_bru.equals("0000000")){
            peso_bru = "0";
        } else {
            if (peso_bru.contains("-")) {
                if (peso_bru.contains(".")) {
                    int pos = peso_bru.indexOf(".");
                    peso_bru = "-" + peso_bru.replace(peso_bru.substring(0, (pos - 1)), peso_bru.substring(1, (pos - 1)).replaceFirst("0*", ""));
                } else {
                    peso_bru = "-" + peso_bru.substring(1, 7).replaceFirst("0*", "");
                }
            } else {
                if (peso_bru.contains(".")) {
                    int pos = peso_bru.indexOf(".");
                    peso_bru = (pos <= 3) ? peso_bru.replaceFirst(peso_bru.substring(0, (pos - 1)), peso_bru.substring(0, (pos - 1)).replaceFirst("0*", "")) : peso_bru.replace(peso_bru.substring(0, (pos - 1)), peso_bru.substring(0, (pos - 1)).replaceFirst("0*", ""));
                } else {
                    peso_bru = peso_bru.replaceFirst("0*", "");
                }
            }
        }

        dados.put("peso_bru", peso_bru);
        dados.put("tara", dado.substring(10, 17));
        dados.put("peso_liq", dado.substring(18, 25));

        return dados;
    }

    static class SerialPortReader implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent event) {
            if(ok != true){
                if (event.isRXCHAR() && event.getEventValue() > 0) {
                    try {
                        byte buffer[] = serialPort.readBytes(1);
                        byte b = buffer[0];
                        char c = (char) b;
                        if (c == '\n') {
                            ok = true;
                        }
                    } catch (SerialPortException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        }
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
