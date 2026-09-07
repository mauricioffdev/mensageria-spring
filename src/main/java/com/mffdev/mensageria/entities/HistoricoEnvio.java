package com.mffdev.mensageria.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_historico_envio",
        uniqueConstraints = @UniqueConstraint(name = "uk_aluno_campanha", columnNames = {"aluno_id", "campanha_id"}))
public class HistoricoEnvio implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campanha_id", nullable = false)
    private Campanha campanha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEnvio status;

    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;

    public HistoricoEnvio() {
    }

    public HistoricoEnvio(Long id, Aluno aluno, Campanha campanha, StatusEnvio status, LocalDateTime dataEnvio) {
        this.id = id;
        this.aluno = aluno;
        this.campanha = campanha;
        this.status = status;
        this.dataEnvio = dataEnvio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public Campanha getCampanha() { return campanha; }
    public void setCampanha(Campanha campanha) { this.campanha = campanha; }

    public StatusEnvio getStatus() { return status; }
    public void setStatus(StatusEnvio status) { this.status = status; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoricoEnvio that = (HistoricoEnvio) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}