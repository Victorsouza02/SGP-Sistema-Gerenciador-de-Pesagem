/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca.models;

import java.util.Date;

/**
 *
 * @author Desenvolvimento
 */
public class Registro extends Motorista{
    private String ps_entrada;
    private String ps_saida;
    private String ps_liquido;
    private String fornecedor;
    private String produto;
    private Date dt_entrada;
    private Date h_entrada;
    private Date dt_saida;
    private Date h_saida;
    
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

    public Date getDt_entrada() {
        return dt_entrada;
    }

    public void setDt_entrada(Date dt_entrada) {
        this.dt_entrada = dt_entrada;
    }

    public Date getH_entrada() {
        return h_entrada;
    }

    public void setH_entrada(Date h_entrada) {
        this.h_entrada = h_entrada;
    }

    public Date getDt_saida() {
        return dt_saida;
    }

    public void setDt_saida(Date dt_saida) {
        this.dt_saida = dt_saida;
    }

    public Date getH_saida() {
        return h_saida;
    }

    public void setH_saida(Date h_saida) {
        this.h_saida = h_saida;
    }
}
