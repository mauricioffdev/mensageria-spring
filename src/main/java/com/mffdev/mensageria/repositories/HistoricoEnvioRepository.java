package com.mffdev.mensageria.repositories;

import com.mffdev.mensageria.entities.Campanha;
import com.mffdev.mensageria.entities.HistoricoEnvio;
import com.mffdev.mensageria.entities.StatusEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoricoEnvioRepository extends JpaRepository<HistoricoEnvio, Long> {

    List<HistoricoEnvio> findByAlunoId(Long alunoId);

    Optional<HistoricoEnvio> findByAlunoIdAndCampanhaId(Long alunoId, Long campanhaId);

    @Query("""
            select he.campanha from HistoricoEnvio he
            where he.aluno.id = :alunoId and he.status = :status
            order by he.campanha.ordemExibicao asc, he.campanha.dataCriacao asc
            """)
    List<Campanha> findCampanhasRecebidas(@Param("alunoId") Long alunoId, @Param("status") StatusEnvio status);

    @Query("""
            select c from Campanha c
            where c.id not in (
                select he.campanha.id from HistoricoEnvio he
                where he.aluno.id = :alunoId and he.status = :status
            )
            order by c.ordemExibicao asc, c.dataCriacao asc
            """)
    List<Campanha> findCampanhasNaoRecebidas(@Param("alunoId") Long alunoId, @Param("status") StatusEnvio status);

    @Query("""
            select distinct he.aluno.id from HistoricoEnvio he
            where he.status = :status
            """)
    List<Long> findAlunoIdsComHistoricoEnviado(@Param("status") StatusEnvio status);

    @Query("""
            select distinct he.aluno.id from HistoricoEnvio he
            where he.campanha.id = :campanhaId and he.status = :status
            """)
    List<Long> findAlunoIdsQueReceberamCampanha(@Param("campanhaId") Long campanhaId, @Param("status") StatusEnvio status);
}