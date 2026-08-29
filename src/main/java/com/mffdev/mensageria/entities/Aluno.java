package com.mffdev.mensageria.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_aluno")
public class Aluno implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    private Boolean ativo;

    @Column(nullable = false)
    private Boolean emailEnviado = false;

    public Aluno() {
    }

    public Aluno(Long id, String nome, String email, Boolean ativo, Boolean emailEnviado) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.ativo = ativo;
        this.emailEnviado = emailEnviado;
    }

    public Long getId() {
        return id; }
    public void setId(Long id) {
        this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Boolean getEmailEnviado() { return emailEnviado; }
    public void setEmailEnviado(Boolean emailEnviado) { this.emailEnviado = emailEnviado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aluno aluno = (Aluno) o;
        return Objects.equals(id, aluno.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}