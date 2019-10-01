/*
    *CLASSE : AcoesSQL
    *FUNCÃO : Armazenar metodos de CRUD dos dados
 */
package lerserialbalanca.persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lerserialbalanca.models.Motorista;
import lerserialbalanca.models.Pecas;
import lerserialbalanca.models.Registro;

/**
 *
 * @author Desenvolvimento
 */
public class AcoesSQL {
    
    //Retorna um Motorista de uma determinada placa
    public Motorista procurarPlaca (String placa){
        Conexao conexao = new Conexao();
        Motorista mot = new Motorista();
        try {
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
        } catch (SQLException ex){
            ex.printStackTrace();
        } 
        return mot;
    }
    
    //Retorna número de registros de uma placa
    public int numRegistros (String placa){
        int num = 0;
        Conexao conexao = new Conexao();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT COUNT(*) as num FROM registro WHERE placa = ?");
            sql.setString(1, placa);
            ResultSet result = sql.executeQuery();     
            if(result.next()){
                num = result.getInt("num");
            }
            sql.close();
            return num;
        } catch (SQLException ex){
            ex.printStackTrace();
        } 
        return num;
    }
    
    //Cadastra motorista no banco de dados
    public boolean CadastrarMotorista(Motorista mot){
        Conexao conexao = new Conexao();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("INSERT INTO motorista (placa, nome, status, fornecedor, produto)"
                    + "VALUES (?, ?, ?, ?, ?)");
            sql.setString(1, mot.getPlaca());
            sql.setString(2, mot.getNome());
            sql.setString(3, "S");
            sql.setString(4, mot.getFornecedor());
            sql.setString(5, mot.getProduto());  
            int registros = sql.executeUpdate();
            sql.close();
            return (registros == 1);
        } catch(SQLException ex){
            ex.printStackTrace();
        } 
        return false;
    }
    
    //Edita o motorista no banco de dados
    public boolean editarMotorista(Motorista mot){
        Conexao conexao = new Conexao();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("UPDATE  motorista SET nome = ? , fornecedor = ?, produto = ?, status = ? WHERE placa = ?");
            sql.setString(1, mot.getNome());
            sql.setString(2, mot.getFornecedor());
            sql.setString(3, mot.getProduto());
            sql.setString(4, (mot.getStatus().equals("E"))? "S" : "E");
            sql.setString(5, mot.getPlaca());
            int registros = sql.executeUpdate();
            sql.close();
            return (registros == 1);
        } catch (SQLException ex){
            ex.printStackTrace();
        } 
        return false;
    }
    
    //Retorna uma lista de Motoristas
    public List<Motorista> listarMotoristas(){
        Conexao conexao = new Conexao();
        List<Motorista> motoristas = new ArrayList<Motorista>();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * from motorista");
            ResultSet result = sql.executeQuery();
            while(result.next()){
                Motorista mot = new Motorista(result.getString("placa"),result.getString("nome"),result.getString("fornecedor"), result.getString("produto"), result.getString("status"));
                motoristas.add(mot);
            }
            return motoristas;
        } catch (SQLException ex){
            ex.printStackTrace();
        } 
        return motoristas;
    }
    
    //Faz uma entrada de registro no banco
    public boolean entradaRegistro(Registro reg){
        Conexao conexao = new Conexao();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("INSERT INTO registro (placa, nome, produto, fornecedor, data_entrada, hora_entrada, peso_entrada)"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)");
            sql.setString(1, reg.getPlaca());
            sql.setString(2, reg.getNome());
            sql.setString(3, reg.getProduto());
            sql.setString(4, reg.getFornecedor());
            sql.setString(5, reg.getDt_entrada());
            sql.setString(6, reg.getH_entrada());
            sql.setString(7, reg.getPs_entrada());  
            int registros = sql.executeUpdate();
            sql.close();
            return (registros == 1);
        } catch(SQLException ex){
            ex.printStackTrace();
        } 
        return false;
    }
    
    //Faz uma saída de registro no banco(Atualiza o ultimo registro de entrada)
    public boolean saidaRegistro(Registro reg){
        Conexao conexao = new Conexao();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("UPDATE registro SET data_saida = ?, hora_saida = ?, peso_saida = ?, peso_liquido = ? where id = (SELECT id FROM registro where placa = ? order by id desc limit 1)");
            sql.setString(1, reg.getDt_saida());
            sql.setString(2, reg.getH_saida());
            sql.setString(3, reg.getPs_saida());
            sql.setString(4, reg.getPs_liquido());
            sql.setString(5, reg.getPlaca());
            int registros = sql.executeUpdate();
            sql.close();
            return (registros == 1);
        } catch(SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }
    
    //Retorna o ultimo registro de uma determinada placa
    public Registro getUltimoRegistro(String placa){
        Conexao conexao = new Conexao();
        Registro reg = new Registro();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * FROM registro where id = (SELECT id FROM registro where placa = ? order by id desc limit 1)");
            sql.setString(1, placa);
            ResultSet result = sql.executeQuery();
            while(result.next()){
                String pl = null;
                if(result.getString("peso_liquido") != null){
                    pl = result.getString("peso_liquido").contains(",") ? result.getString("peso_liquido").replace(",", ".") : result.getString("peso_liquido");
                } else {
                    pl = null;
                }
                reg.setId(result.getInt("id"));
                reg.setPlaca(result.getString("placa"));
                reg.setNome(result.getString("nome"));
                reg.setProduto(result.getString("produto"));
                reg.setFornecedor(result.getString("fornecedor"));
                reg.setDt_entrada(result.getString("data_entrada"));
                reg.setH_entrada(result.getString("hora_entrada"));
                reg.setPs_entrada(result.getString("peso_entrada"));
                reg.setDt_saida(result.getString("data_saida"));
                reg.setH_saida(result.getString("hora_saida"));
                reg.setPs_saida(result.getString("peso_saida"));
                reg.setPs_liquido(pl);
            }
            return reg;
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return reg;
    }
    
    //Retorna a data da ultima atividade de uma placa
    public String ultimaAtividade(String placa){
        Conexao conexao = new Conexao();
        Registro reg = new Registro();
        String atividade = "";
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * FROM registro where id = (SELECT id FROM registro where placa = ? order by id desc limit 1)");
            sql.setString(1, placa);
            ResultSet result = sql.executeQuery();
            
            SimpleDateFormat dateFormatSql = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormatView = new SimpleDateFormat("dd/MM/yyyy");
            Date data = new Date();
            
            while(result.next()){
                data = dateFormatSql.parse(result.getString("data_entrada"));
                String data_entrada = dateFormatView.format(data);
                String data_saida = "";
                if(result.getString("data_saida") != null){
                    data = dateFormatSql.parse(result.getString("data_saida"));
                    data_saida = dateFormatView.format(data);
                    atividade = data_saida + " " + result.getString("hora_saida");
                } else {
                    atividade = data_entrada + " " + result.getString("hora_entrada");
                }
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return atividade;
    }
    
    //Retorna o Registro de um determinado ID
    public Registro pegarRegistro(int id){
        Conexao conexao = new Conexao();
        Registro reg = new Registro();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * FROM registro where id = ?");
            sql.setInt(1, id);
            ResultSet result = sql.executeQuery();
            while(result.next()){
                String pl = null;
                if(result.getString("peso_liquido") != null){
                    pl = result.getString("peso_liquido").contains(",") ? result.getString("peso_liquido").replace(",", ".") : result.getString("peso_liquido");
                } else {
                    pl = null;
                }
                
                reg.setId(result.getInt("id"));
                reg.setPlaca(result.getString("placa"));
                reg.setNome(result.getString("nome"));
                reg.setProduto(result.getString("produto"));
                reg.setFornecedor(result.getString("fornecedor"));
                reg.setDt_entrada(result.getString("data_entrada"));
                reg.setH_entrada(result.getString("hora_entrada"));
                reg.setPs_entrada(result.getString("peso_entrada"));
                reg.setDt_saida(result.getString("data_saida"));
                reg.setH_saida(result.getString("hora_saida"));
                reg.setPs_saida(result.getString("peso_saida"));
                reg.setPs_liquido(pl);
            }
            return reg;
        } catch (SQLException ex){
            ex.printStackTrace();
        } 
        return reg;
    }
    
    //Retorna uma Lista de Registros
    public List<Registro> listarRegistros(){
        Conexao conexao = new Conexao();
        List<Registro> registros = new ArrayList<Registro>();
        String pl = " ";
        try{
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * from registro");
            ResultSet result = sql.executeQuery();
            SimpleDateFormat dateFormatSql = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormatView = new SimpleDateFormat("dd/MM/yyyy");
            Date data = new Date();
            
            while(result.next()){
                data = dateFormatSql.parse(result.getString("data_entrada"));
                String data_entrada = dateFormatView.format(data);
                String data_saida = "";
                if(result.getString("data_saida") != null){
                    data = dateFormatSql.parse(result.getString("data_saida"));
                    data_saida = dateFormatView.format(data);
                    pl = (result.getString("peso_liquido").contains(",")) ? result.getString("peso_liquido").replace(",", ".") : result.getString("peso_liquido");
                }else {
                    pl = " ";
                }

                
                Registro reg = new Registro(
                        result.getInt("id"),
                        result.getString("placa"),
                        result.getString("nome"),
                        result.getString("produto"),
                        result.getString("fornecedor"),
                        data_entrada,
                        result.getString("hora_entrada"),
                        result.getString("peso_entrada"),
                        data_saida,
                        result.getString("hora_saida"),
                        result.getString("peso_saida"),
                        pl
                );
                registros.add(reg);
            }
            return registros;
        } catch(SQLException sqlEx){
            System.out.println(sqlEx.getMessage());
        } catch(ParseException pEx){
            pEx.printStackTrace();
        } 
        return registros;
    }
    
    //Retorna uma Lista de Registros num periodo especifico
    public List<Registro> listarRegistros(String data_inicio, String data_fim){
        Conexao conexao = new Conexao();
        List<Registro> registros = new ArrayList<Registro>();
        String pl = " ";
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * from registro where data_entrada BETWEEN ? AND ?");
            sql.setString(1, data_inicio);
            sql.setString(2, data_fim);
            ResultSet result = sql.executeQuery();

            SimpleDateFormat dateFormatSql = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormatView = new SimpleDateFormat("dd/MM/yyyy");
            Date data = new Date();

            while(result.next()){
                data = dateFormatSql.parse(result.getString("data_entrada"));
                String data_entrada = dateFormatView.format(data);
                String data_saida = "";
                if(result.getString("data_saida") != null){
                    data = dateFormatSql.parse(result.getString("data_saida"));
                    data_saida = dateFormatView.format(data);
                    pl = result.getString("peso_liquido").contains(",") ? result.getString("peso_liquido").replace(",", ".") : result.getString("peso_liquido");
                }else {
                    pl = " ";
                }
                
                Registro reg = new Registro(
                        result.getInt("id"),
                        result.getString("placa"),
                        result.getString("nome"),
                        result.getString("produto"),
                        result.getString("fornecedor"),
                        data_entrada,
                        result.getString("hora_entrada"),
                        result.getString("peso_entrada"),
                        data_saida,
                        result.getString("hora_saida"),
                        result.getString("peso_saida"),
                        pl
                );
                registros.add(reg);
            }
            return registros;
        } catch(SQLException ex){
            ex.printStackTrace();
        } catch (ParseException pEx){
            pEx.printStackTrace();
        } 
        return registros;
    }
    
    //Retorna uma Lista de Registros de acordo com a placa
    public List<Registro> listarRegistros(String placa){
        Conexao conexao = new Conexao();
        List<Registro> registros = new ArrayList<Registro>();
        String pl = " ";
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * from registro where placa = ?");
            sql.setString(1, placa);
            ResultSet result = sql.executeQuery();

            SimpleDateFormat dateFormatSql = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormatView = new SimpleDateFormat("dd/MM/yyyy");
            Date data = new Date();

            while(result.next()){
                data = dateFormatSql.parse(result.getString("data_entrada"));
                String data_entrada = dateFormatView.format(data);
                String data_saida = "";
                if(result.getString("data_saida") != null){
                    data = dateFormatSql.parse(result.getString("data_saida"));
                    data_saida = dateFormatView.format(data);
                    pl = result.getString("peso_liquido").contains(",") ? result.getString("peso_liquido").replace(",", ".") : result.getString("peso_liquido");
                }else {
                    pl = " ";
                }
                Registro reg = new Registro(
                        result.getInt("id"),
                        result.getString("placa"),
                        result.getString("nome"),
                        result.getString("produto"),
                        result.getString("fornecedor"),
                        data_entrada,
                        result.getString("hora_entrada"),
                        result.getString("peso_entrada"),
                        data_saida,
                        result.getString("hora_saida"),
                        result.getString("peso_saida"),
                        pl
                );
                registros.add(reg);
            }
            return registros;
        } catch(SQLException ex){
            ex.printStackTrace();
        } catch (ParseException pEx){
            pEx.printStackTrace();
        } 
        return registros;
    }
    
    public void cadastrarPeca(Pecas pec){
        Conexao conexao = new Conexao();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("INSERT INTO pecas (nome, descricao, pmp, qtd_amostras, grandeza)"
                    + "VALUES (?, ?, ?, ?, ?)");
            sql.setString(1, pec.getNome());
            sql.setString(2, pec.getDescricao());
            sql.setString(3, pec.getPmp());
            sql.setString(4, pec.getQtd_amostras());
            sql.setString(5, pec.getGrandeza());
            sql.executeUpdate();
            sql.close();
        } catch(SQLException ex){
            ex.printStackTrace();
        } 
    }
    
    public Pecas procurarPeca(String nome){
        Conexao conexao = new Conexao();
        Pecas pec = null;
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT * from pecas WHERE nome LIKE ?% LIMIT 1");
            sql.setString(1, nome);
            ResultSet result = sql.executeQuery();
            while(result.next()){
                pec = new Pecas(
                   result.getString("nome"),
                   result.getString("descricao"),
                   result.getString("pmp"),
                   result.getString("qtd_amostras"),
                   result.getString("grandeza")
                );
            }
            sql.close();
        } catch(SQLException ex){
            ex.printStackTrace();
        }
        return pec;
    }
    
    public List<String> listarNomesPecas(){
        Conexao conexao = new Conexao();
        List<String> nomes = new ArrayList<String>();
        try {
            PreparedStatement sql = conexao.getConexao().prepareStatement("SELECT nome from pecas");
            ResultSet result = sql.executeQuery();
            while(result.next()){
                nomes.add(result.getString("nome"));
            }
            sql.close();
        } catch(SQLException ex){
            ex.printStackTrace();
        }
        return nomes;
    }
    
    
}
