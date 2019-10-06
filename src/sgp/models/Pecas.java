
package sgp.models;

import java.text.DecimalFormat;
import java.util.List;
import sgp.persistence.AcoesSQL;


public class Pecas {
    private int cod;
    private String nome;
    private String descricao;
    private String pmp;
    private String qtd_amostras;
    private String grandeza;
    
    public Pecas(String nome, String descricao, String pmp, String qtd_amostras, String grandeza){
        setNome(nome);
        setDescricao(descricao);
        setPmp(pmp);
        setQtd_amostras(qtd_amostras);
        setGrandeza(grandeza);
    }
    
    public Pecas(){
        
    }
    
    private String calculoPmp(String peso_liq, String qtd_amostras){
        if(!qtd_amostras.equals("0")){
            DecimalFormat df = new DecimalFormat("#.###");
            String teste = String.valueOf(Float.parseFloat(peso_liq)/Integer.parseInt(qtd_amostras)).replaceAll(",", ".");
            System.out.println(Float.parseFloat(peso_liq) + "/" + Integer.parseInt(qtd_amostras) + " = " + teste );
            Number n = Double.parseDouble(teste);
            Double d = n.doubleValue();
            return String.valueOf(df.format(d)).replaceAll(",", ".");
        }
        return "0";
    }
     
    public void salvarPeca(){
        AcoesSQL acao = new AcoesSQL();
        acao.cadastrarPeca(this);
    }
    
    private void editarPeca(){
        
    }
    
    public Pecas procurarPeca(int cod){
        AcoesSQL acao = new AcoesSQL();
        return acao.procurarPeca(cod);
    }
    
    public static List<String> listarNomesPecas(){
        AcoesSQL acao = new AcoesSQL();
        return acao.listarNomesPecas();
    }
    
    private String calculoPecas(String peso_liq, String pmp){
        if(pmp.equals("0")){
            return "0";
        }
        int pecas = 0;
        Float peso = Float.parseFloat(peso_liq);
        Float pmp_var = Float.parseFloat(pmp);
        Float result = peso/pmp_var;
        pecas = result.intValue();
        return String.valueOf(pecas);
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPmp() {
        return pmp;
    }

    public void setPmp(String pmp) {
        this.pmp = pmp;
    }

    public String getQtd_amostras() {
        return qtd_amostras;
    }

    public void setQtd_amostras(String qtd_amostras) {
        this.qtd_amostras = qtd_amostras;
    }

    public String getGrandeza() {
        return grandeza;
    }

    public void setGrandeza(String grandeza) {
        this.grandeza = grandeza;
    }
    
    
    
    
    
    
}
