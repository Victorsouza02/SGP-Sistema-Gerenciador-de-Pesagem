/*
   * CLASSE : Propriedades
   * FUNÇÃO : Ler o arquivo de propriedade e gerenciar os valores
 */
package lerserialbalanca.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;



public class Propriedades {
    private Properties prop;
    private final String PROP_URL = new File("").getAbsolutePath()+"\\config\\config.properties";
    private String porta;
    private String equipamento;
    private String fonte;
    private String nomeempresa;
    private String enderecoempresa;
    private String telempresa;
    
    public Propriedades(){
        try {
            prop = new Properties();
            prop.load(new FileInputStream(PROP_URL));
            carregarPropriedades();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //PEGA OS VALORES QUE ESTÃO NO ARQUIVO DE PROPRIEDADES
    public void carregarPropriedades(){
        try {
            setPorta(prop.getProperty("porta"));
            setEquipamento(prop.getProperty("equipamento"));
            setFonte(prop.getProperty("tamanhofonte"));
            setNomeempresa(new String(prop.getProperty("nomeempresa").getBytes("ISO-8859-1"), "UTF-8"));
            setEnderecoempresa(new String(prop.getProperty("enderecoempresa").getBytes("ISO-8859-1"), "UTF-8"));
            setTelempresa(prop.getProperty("telempresa"));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
    
    //ALTERA OS VALORES NO ARQUIVO DE PROPRIEDADES(PORTA E EQUIPAMENTO)
    public void alterarPropriedades(String porta, String equipamento){
        try {
            prop.setProperty("porta", porta);
            prop.setProperty("equipamento", equipamento);
            prop.store(new FileOutputStream(PROP_URL), null);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //ALTERA OS VALORES NO ARQUIVO DE PROPRIEDADES(NOME EMPRESA, ENDERECO EMPRESA, TEL EMPRESA E FONTE DA IMPRESSAO)
    public void alterarPropriedades(String fonte, String nome , String endereco, String tel){
        try {
            prop.setProperty("tamanhofonte", fonte);
            prop.setProperty("nomeempresa", nome);
            prop.setProperty("enderecoempresa", endereco);
            prop.setProperty("telempresa", tel);
            prop.store(new FileOutputStream(PROP_URL), null);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //GETTERS E SETTERS
    
    public String getPorta() {
        return porta;
    }

    public void setPorta(String porta) {
        this.porta = porta;
    }

    public String getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(String equipamento) {
        this.equipamento = equipamento;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getNomeempresa() {
        return nomeempresa;
    }

    public void setNomeempresa(String nomeempresa) {
        this.nomeempresa = nomeempresa;
    }

    public String getEnderecoempresa() {
        return enderecoempresa;
    }

    public void setEnderecoempresa(String enderecoempresa) {
        this.enderecoempresa = enderecoempresa;
    }

    public String getTelempresa() {
        return telempresa;
    }

    public void setTelempresa(String telempresa) {
        this.telempresa = telempresa;
    }
    
    
    
    
    
}
