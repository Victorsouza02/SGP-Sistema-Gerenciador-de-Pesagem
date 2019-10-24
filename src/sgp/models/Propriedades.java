/*
   * CLASSE : Propriedades
   * FUNÇÃO : Ler o arquivo de propriedade e gerenciar os valores
 */
package sgp.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import sgp.config.VariaveisGlobais;





public class Propriedades {
    private Properties prop;
    private final String PROP_URL = new File("").getAbsolutePath()+"\\config\\config.properties";
    private static String porta;
    private static String equipamento;
    private static String fonte;
    private static String nomeempresa;
    private static String enderecoempresa;
    private static String telempresa;
    private static String autorizacao;
    private static String altura;
    private static boolean somente_estavel;
    
    public Propriedades(){
        try {
            prop = new Properties();
            prop.load(new FileInputStream(PROP_URL));
            carregarPropriedades();
            if(getEquipamento().equals("MANUAL")){
                VariaveisGlobais.setModoManual(true);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //PEGA OS VALORES QUE ESTÃO NO ARQUIVO DE PROPRIEDADES
    private void carregarPropriedades(){
        try {
            setPorta(prop.getProperty("porta"));
            setEquipamento(prop.getProperty("equipamento"));
            setFonte(prop.getProperty("tamanhofonte"));
            setNomeempresa(new String(prop.getProperty("nomeempresa").getBytes("ISO-8859-1"), "UTF-8"));
            setEnderecoempresa(new String(prop.getProperty("enderecoempresa").getBytes("ISO-8859-1"), "UTF-8"));
            setTelempresa(prop.getProperty("telempresa"));
            setAutorizacao(prop.getProperty("chave"));
            setAltura(prop.getProperty("altura"));
            setSomente_estavel(prop.getProperty("somenteestavel").equals("S"));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
    
    //ALTERA OS VALORES NO ARQUIVO DE PROPRIEDADES(PORTA E EQUIPAMENTO)
    public void alterarPropriedades(String porta, String equipamento, String somente_estavel){
        try {
            prop.setProperty("porta", porta);
            prop.setProperty("equipamento", equipamento);
            prop.setProperty("somenteestavel", (somente_estavel.equals("SIM"))? "S" : "N");
            prop.store(new FileOutputStream(PROP_URL), null);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //ALTERA OS VALORES NO ARQUIVO DE PROPRIEDADES(NOME EMPRESA, ENDERECO EMPRESA, TEL EMPRESA E FONTE DA IMPRESSAO)
    public void alterarPropriedades(String fonte, String nome , String endereco, String tel, String altura){
        try {
            prop.setProperty("tamanhofonte", fonte);
            prop.setProperty("nomeempresa", nome);
            prop.setProperty("enderecoempresa", endereco);
            prop.setProperty("telempresa", tel);
            prop.setProperty("altura", altura);
            prop.store(new FileOutputStream(PROP_URL), null);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //GETTERS E SETTERS
    
    public static String getPorta() {
        return porta;
    }

    private static void setPorta(String porta_serial) {
        porta = porta_serial;
    }

    public static String getEquipamento() {
        return equipamento;
    }

    private static void setEquipamento(String equip) {
        equipamento = equip;
    }

    public static String getFonte() {
        return fonte;
    }

    private static void setFonte(String fonte_tam) {
        fonte = fonte_tam;
    }

    public static String getNomeempresa() {
        return nomeempresa;
    }

    private static void setNomeempresa(String nome) {
        nomeempresa = nome;
    }

    public static String getEnderecoempresa() {
        return enderecoempresa;
    }

    private static void setEnderecoempresa(String endereco) {
        enderecoempresa = endereco;
    }

    public static String getTelempresa() {
        return telempresa;
    }

    private static void setTelempresa(String tel) {
        telempresa = tel;
    }

    public static String getAutorizacao() {
        return autorizacao;
    }

    public static void setAutorizacao(String autorizacao) {
        Propriedades.autorizacao = autorizacao;
    }

    public static String getAltura() {
        return altura;
    }

    public static void setAltura(String altura) {
        Propriedades.altura = altura;
    }

    public static boolean isSomente_estavel() {
        return somente_estavel;
    }

    public static void setSomente_estavel(boolean somente_estavel) {
        Propriedades.somente_estavel = somente_estavel;
    }
    
    
    
    
    
    
}
