/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca.persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lerserialbalanca.models.Motorista;

/**
 *
 * @author Desenvolvimento
 */
public class Acoes {
    
    public Motorista procurarPlaca (String placa) throws ClassNotFoundException, SQLException{
        Conexao conexao = new Conexao();
        Motorista mot = new Motorista();
        PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * from motorista where placa = ?");
        sql.setString(1, placa);
        ResultSet result = sql.executeQuery();     
        if(result.next()){
            mot.setPlaca(result.getString("placa"));
            mot.setNome(result.getString("nome"));
            mot.setFornecedor(result.getString("fornecedor"));
            mot.setProduto(result.getString("produto"));
            mot.setStatus(result.getString("status"));
            if(result.wasNull()){
                mot = null;
            }
        }
        
        sql.close();
        return mot;
    }
    
    public boolean CadastrarMotorista(Motorista mot) throws ClassNotFoundException, SQLException{
        Conexao conexao = new Conexao();
        //Motorista mot = new Motorista();
        PreparedStatement sql = conexao.getConexao().prepareStatement("INSERT INTO motorista (placa, nome, status, fornecedor, produto)"
                + "VALUES (?, ?, ?, ?, ?)");
        sql.setString(1, mot.getPlaca());
        sql.setString(2, mot.getNome());
        sql.setString(3, "E");
        sql.setString(4, mot.getFornecedor());
        sql.setString(5, mot.getProduto());  
        int registros = sql.executeUpdate();
        sql.close();
        return (registros == 1);
    }
    
    public boolean editarMotorista(Motorista mot) throws ClassNotFoundException, SQLException{
        Conexao conexao = new Conexao();
        PreparedStatement sql = conexao.getConexao().prepareStatement("UPDATE  motorista SET nome = ? , fornecedor = ?, produto = ?, status = ? WHERE placa = ?");
        sql.setString(1, mot.getNome());
        sql.setString(2, mot.getFornecedor());
        sql.setString(3, mot.getProduto());
        sql.setString(4, (mot.getStatus().equals("E"))? "S" : "E");
        sql.setString(5, mot.getPlaca());
        
        int registros = sql.executeUpdate();
        sql.close();
        return (registros == 1);
    }
    
    public List<Motorista> listarMotoristas() throws ClassNotFoundException, SQLException{
        Conexao conexao = new Conexao();
        //Motorista mot = new Motorista();
        PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * from motorista");
        ResultSet result = sql.executeQuery();
        List<Motorista> motoristas = new ArrayList<Motorista>();
        while(result.next()){
            Motorista mot = new Motorista(result.getString("placa"),result.getString("nome"),result.getString("fornecedor"), result.getString("produto"), result.getString("status"));
            motoristas.add(mot);
        }
        return motoristas;
    }
}
