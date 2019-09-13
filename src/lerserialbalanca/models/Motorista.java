/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca.models;

import lerserialbalanca.persistence.Acoes;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    public boolean procurarPlaca(String placa) throws ClassNotFoundException, SQLException {
        Acoes acao = new Acoes();
        Motorista mot = acao.procurarPlaca(placa);
        if (mot.getNome() != null) {
            setPlaca(mot.getPlaca());
            setNome(mot.getNome());
            setFornecedor(mot.getFornecedor());
            setProduto(mot.getProduto());
            setStatus(mot.getStatus());
            return true;
        } else {
            return false;
        }

    }

    public void cadastrar() throws ClassNotFoundException, SQLException {
        Acoes acao = new Acoes();
        if (acao.CadastrarMotorista(this)) {
            System.out.println("Motorista cadastrado com sucesso");
        } else {
            System.out.println("Erro no cadastro");
        };
    }
    
    public void editar() throws ClassNotFoundException, SQLException{
        Acoes acao = new Acoes();
        if (acao.editarMotorista(this)) {
            System.out.println("Motorista editado com sucesso");
        } else {
            System.out.println("Erro na edição");
        };
    }
    
    public ObservableList<Motorista> listaDeMotoristas() throws ClassNotFoundException, SQLException {
        Acoes acao = new Acoes();
        return FXCollections.observableList(acao.listarMotoristas());
    }

}
