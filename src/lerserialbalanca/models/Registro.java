/*
   * CLASSE : Registro
   * FUNÇÃO : Gerencia os registros de entrada/saida de motoristas
 */
package lerserialbalanca.models;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lerserialbalanca.persistence.AcoesSQL;

/**
 *
 * @author Desenvolvimento
 */
public class Registro {
    private int id;
    private String placa;
    private String nome;
    private String produto;
    private String fornecedor;
    private String ps_entrada;
    private String ps_saida;
    private String ps_liquido;
    private String dt_entrada;
    private String h_entrada;
    private String dt_saida;
    private String h_saida;
    private static int num_registros;
    
    public Registro(){
    
    }
    
    public Registro(int id,String placa, String nome, String produto, String fornecedor, String dt_entrada, String h_entrada, String ps_entrada, String dt_saida, String h_saida, String ps_saida, String ps_liquido){
        setId(id);
        setPlaca(placa);
        setNome(nome);
        setProduto(produto);
        setFornecedor(fornecedor);
        setDt_entrada(dt_entrada);
        setH_entrada(h_entrada);
        setPs_entrada(ps_entrada);
        setDt_saida(dt_saida);
        setH_saida(h_saida);
        setPs_saida(ps_saida);
        setPs_liquido(ps_liquido);
    }
    
    //REGISTRA ENTRADA DOS MOTORISTAS
    public boolean registrarEntrada(String placa, String ps_entrada){
        AcoesSQL acao = new AcoesSQL();
        Motorista mot = acao.procurarPlaca(placa);
        SimpleDateFormat fmtDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat fmtTime = new SimpleDateFormat("HH:mm:ss");
        Date data = new Date();
        this.setPlaca(placa);
        this.setNome(mot.getNome());
        this.setProduto(mot.getProduto());
        this.setFornecedor(mot.getFornecedor());
        this.setDt_entrada(fmtDate.format(data));
        this.setH_entrada(fmtTime.format(data));
        this.setPs_entrada(ps_entrada);
        return acao.entradaRegistro(this);
    }
    
    //REGISTRA SAIDA DO MOTORISTA
    public boolean registrarSaida(String placa, String ps_entrada, String ps_saida){
        AcoesSQL acao = new AcoesSQL();
        SimpleDateFormat fmtDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat fmtTime = new SimpleDateFormat("HH:mm:ss");
        Date data = new Date();
        Registro reg = new Registro();
        reg.setPlaca(placa);
        reg.setDt_saida(fmtDate.format(data));
        reg.setH_saida(fmtTime.format(data));
        reg.setPs_saida(ps_saida);
        DecimalFormat df = new DecimalFormat("#.###");
        Number n = Float.parseFloat(ps_saida) - Float.parseFloat(ps_entrada);
        Double d = n.doubleValue();
        reg.setPs_liquido(String.valueOf(df.format(d)));
        return acao.saidaRegistro(reg);
    }
    
    //RETORNA O ULTIMO REGISTRO DE UMA DETERMINADA PLACA
    public Registro ultimoRegistro(String placa){
        AcoesSQL acao = new AcoesSQL();
        return acao.getUltimoRegistro(placa);
    }
    
    //RETORNA UMA OBSERVABLE LIST DE REGISTROS
    public ObservableList<Registro> listaDeRegistros() {
        AcoesSQL acao = new AcoesSQL();
        return FXCollections.observableList(acao.listarRegistros());
    }
    
    //RETORNA UMA OBSERVABLE LIST DE REGISTROS DE UMA DETERMINADA PLACA
    public ObservableList<Registro> listaDeRegistros(String placa) {
        AcoesSQL acao = new AcoesSQL();
        List<Registro> registros = acao.listarRegistros(placa);
        Motorista.setNum_registros(registros.size() + 1);
        return FXCollections.observableList(registros);
    }
    
    //RETORNA UMA OBSERVABLE LIST DE REGISTROS NUM PERIODO ESPECIFICO
    public List<Registro> listaDeRegistros(String data_ini, String data_fim) {
        AcoesSQL acao = new AcoesSQL();
        return acao.listarRegistros(data_ini, data_fim);
    }


    //GETTERS E SETTERS
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    
    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
    
    
    public String getPs_entrada() {
        return ps_entrada;
    }

    public void setPs_entrada(String ps_entrada) {
        this.ps_entrada = ps_entrada;
    }

    public String getPs_saida() {
        return ps_saida;
    }

    public void setPs_saida(String ps_saida) {
        this.ps_saida = ps_saida;
    }

    public String getPs_liquido() {
        return ps_liquido;
    }

    public void setPs_liquido(String ps_liquido) {
        this.ps_liquido = ps_liquido;
    }

    public String getDt_entrada() {
        return dt_entrada;
    }

    public void setDt_entrada(String dt_entrada) {
        this.dt_entrada = dt_entrada;
    }

    public String getH_entrada() {
        return h_entrada;
    }

    public void setH_entrada(String h_entrada) {
        this.h_entrada = h_entrada;
    }

    public String getDt_saida() {
        return dt_saida;
    }

    public void setDt_saida(String dt_saida) {
        this.dt_saida = dt_saida;
    }

    public String getH_saida() {
        return h_saida;
    }

    public void setH_saida(String h_saida) {
        this.h_saida = h_saida;
    }

    public static int getNum_registros() {
        return num_registros;
    }

    public static void setNum_registros(int num_registros) {
        Registro.num_registros = num_registros;
    }
    
    
    
    
}
