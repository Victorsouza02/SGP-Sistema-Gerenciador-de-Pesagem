/*
  * CLASSE : Motorista
  * Função : Armazenar os metodos relacionados ao motorista
 */
package lerserialbalanca.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lerserialbalanca.persistence.AcoesSQL;


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

    public Motorista procurarPlaca(String placa) { //PROCURA PLACA E PEGA OS VALORES
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

    public void cadastrar() { //CADASTRO
        AcoesSQL acao = new AcoesSQL();
        acao.CadastrarMotorista(this);
    }
    
    public void editar(){ //EDIÇÃO
        AcoesSQL acao = new AcoesSQL();
        acao.editarMotorista(this);

    }
    
    //RETORNA UMA LISTA(OBSERVABLE LIST) DE MOTORISTAS
    public ObservableList<Motorista> listaDeMotoristas() {
        AcoesSQL acao = new AcoesSQL();
        return FXCollections.observableList(acao.listarMotoristas());
    }
    
    //RETORNA O NUMERO DE REGISTROS DESSE MOTORISTA
    public static int numRegistros(String placa){
        AcoesSQL acao = new AcoesSQL();
        return acao.numRegistros(placa);
    }
    
    //RETORNA A DATA E HORA DA ULTIMA ATIVIDADE DESSSE MOTORISTA
    public static String ultimaAtividade(String placa){
        AcoesSQL acao = new AcoesSQL();
        return acao.ultimaAtividade(placa);
    }

    //GETTERS E SETTERS
    
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
}
