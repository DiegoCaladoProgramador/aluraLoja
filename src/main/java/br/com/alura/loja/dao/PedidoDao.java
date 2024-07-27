package br.com.alura.loja.dao;

import br.com.alura.loja.modelo.Pedido;
import br.com.alura.loja.modelo.Produto;
import br.com.alura.loja.vo.RelatorioDeVendasVo;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PedidoDao {
    private EntityManager em;

    public PedidoDao(EntityManager em) {
        this.em = em;
    }
    public void cadastrar(Pedido pedido) {
        this.em.persist(pedido);
    }

    public BigDecimal valorTotalVendido(){
        String jpql = "select sum(p.valorTotal) from Pedido p";
        return em.createQuery(jpql, BigDecimal.class)
                .getSingleResult();
    }

    public List<RelatorioDeVendasVo> relatorioDeVendas(){
        String jpql = "select new br.com.alura.loja.vo.RelatorioDeVendasVo(" +
                "produto.nome, " +
                "SUM(item.quantidade), " +
                "MAX(pedido.data))" +
                "FROM Pedido pedido " +
                "JOIN pedido.produtos item " +
                "JOIN item.produto produto " +
                "GROUP BY produto.nome " +
                "ORDER BY item.quantidade DESC";
        return em.createQuery(jpql, RelatorioDeVendasVo.class)
                .getResultList();
    }

    public Pedido buscarPedidoComCliente(Long id) {
        return em.createQuery("SELECT p FROM Pedido p JOIN FETCH p.cliente where p.id = :id", Pedido.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<Produto> buscarPorParametros(String nome, BigDecimal preco, LocalDate dataCadastro){
        String jpql = "SELECT p FROM Produto p WHERE 1=1 ";
        if(nome != null && !nome.trim().isEmpty()){
            jpql += " AND p.nome = :nome";
        }
        if(preco != null){
            jpql += " AND p.preco = :preco";
        }
        if(dataCadastro != null){
            jpql += " AND p.dataCadastro = :dataCadastro";
        }
        TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
        if(nome != null && !nome.trim().isEmpty()){
            query.setParameter("nome", nome);
        }
        if(preco != null){
            query.setParameter("preco", preco);
        }
        if(dataCadastro != null){
            query.setParameter("dataCadastro", dataCadastro);
        }

        return query.getResultList();
    }

    public List<Produto> buscarPorParametrosComCriteria(String nome, BigDecimal preco, LocalDate dataCadastro){
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Produto> query =  builder.createQuery(Produto.class);
        Root<Produto> from = query.from(Produto.class);

        Predicate filtros = builder.and();
        if(nome != null && !nome.trim().isEmpty()){
            filtros = builder.and(filtros, builder.equal(from.get("nome"), nome));
        }
        if(preco != null){
            filtros = builder.and(filtros, builder.equal(from.get("preco"), preco));
        }
        if(dataCadastro != null){
            filtros = builder.and(filtros, builder.equal(from.get("dataCadastro"), dataCadastro));
        }
        query.where(filtros);

        return em.createQuery(query).getResultList();
    }

}
