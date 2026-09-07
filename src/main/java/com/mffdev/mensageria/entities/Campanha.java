package com.mffdev.mensageria.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_campanha")
public class Campanha implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(name = "assunto_email", nullable = false)
    private String assuntoEmail;

    @Lob
    @Column(name = "conteudo_html", columnDefinition = "LONGTEXT")
    private String conteudoHtml;

    @Lob
    @Column(name = "arquivo_pdf", columnDefinition = "LONGBLOB")
    private byte[] arquivoPdf;

    @Column(name = "nome_arquivo_pdf")
    private String nomeArquivoPdf;

    @Column(name = "ordem_exibicao")
    private Integer ordemExibicao;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    public Campanha() {
    }

    public Campanha(Long id, String titulo, String assuntoEmail, String conteudoHtml,
                    byte[] arquivoPdf, String nomeArquivoPdf, Integer ordemExibicao,
                    LocalDateTime dataCriacao) {
        this.id = id;
        this.titulo = titulo;
        this.assuntoEmail = assuntoEmail;
        this.conteudoHtml = conteudoHtml;
        this.arquivoPdf = arquivoPdf;
        this.nomeArquivoPdf = nomeArquivoPdf;
        this.ordemExibicao = ordemExibicao;
        this.dataCriacao = dataCriacao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAssuntoEmail() { return assuntoEmail; }
    public void setAssuntoEmail(String assuntoEmail) { this.assuntoEmail = assuntoEmail; }

    public String getConteudoHtml() { return conteudoHtml; }
    public void setConteudoHtml(String conteudoHtml) { this.conteudoHtml = conteudoHtml; }

    public byte[] getArquivoPdf() { return arquivoPdf; }
    public void setArquivoPdf(byte[] arquivoPdf) { this.arquivoPdf = arquivoPdf; }

    public String getNomeArquivoPdf() { return nomeArquivoPdf; }
    public void setNomeArquivoPdf(String nomeArquivoPdf) { this.nomeArquivoPdf = nomeArquivoPdf; }

    public Integer getOrdemExibicao() { return ordemExibicao; }
    public void setOrdemExibicao(Integer ordemExibicao) { this.ordemExibicao = ordemExibicao; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Campanha campanha = (Campanha) o;
        return Objects.equals(id, campanha.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}