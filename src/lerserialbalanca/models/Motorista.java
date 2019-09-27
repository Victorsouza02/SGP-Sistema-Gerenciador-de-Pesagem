/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca.models;

import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lerserialbalanca.persistence.AcoesSQL;

/**
 *
 * @author Desenvolvimento
 */
public class Motorista {

    private String placa;
    private String nome;
    private String status;
    private String fornecedor;
    private String produto;
    private static int num_registros;
    
    public Motorista(){
    
    }
    
    public Motorista(String placa, String nome, String fornecedor, String produto, String status){
        setPlaca(placa);
        setNome(nome);
        setFornecedor(fornecedor);
        setProduto(produto);
        setStatus(status);
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public static int getNum_registros() {
        return num_registros;
    }

    public static void setNum_registros(int num_registros) {
        Motorista.num_registros = num_registros;
    }
    
    

    public Motorista procurarPlaca(String placa) {
        AcoesSQL acao = new AcoesSQL();
        Motorista mot = acao.procurarPlaca(placa);
        if (mot.getNome() != null) {
            setPlaca(mot.getPlaca());
            setNome(mot.getNome());
            setFornecedor(mot.getFornecedor());
            setProduto(mot.getProduto());
            setStatus(mot.getStatus());
            return mot;
        }
        return mot;
    }

    public void cadastrar() {
        AcoesSQL acao = new AcoesSQL();
        if (acao.CadastrarMotorista(this)) {
            System.out.println("Motorista cadastrado com sucesso");
        } else {
            System.out.println("Erro no cadastro");
        };
    }
    
    public void editar(){
        AcoesSQL acao = new AcoesSQL();
        if (acao.editarMotorista(this)) {
            System.out.println("Motorista editado com sucesso");
        } else {
            System.out.println("Erro na edição");
        };
    }
    
    public ObservableList<Motorista> listaDeMotoristas() {
        AcoesSQL acao = new AcoesSQL();
        return FXCollections.observableList(acao.listarMotoristas());
    }
    
    public static int numRegistros(String placa){
        AcoesSQL acao = new AcoesSQL();
        return acao.numRegistros(placa);
    }
    
    public static String ultimaAtividade(String placa){
        AcoesSQL acao = new AcoesSQL();
        return acao.ultimaAtividade(placa);
    }

}
