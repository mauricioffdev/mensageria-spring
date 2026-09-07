package com.mffdev.mensageria.repositories;

import com.mffdev.mensageria.entities.Campanha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampanhaRepository extends JpaRepository<Campanha, Long> {

    List<Campanha> findAllByOrderByOrdemExibicaoAscDataCriacaoAsc();
}