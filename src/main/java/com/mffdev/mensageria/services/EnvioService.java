package com.mffdev.mensageria.services;

import com.mffdev.mensageria.entities.Aluno;
import com.mffdev.mensageria.entities.Campanha;
import com.mffdev.mensageria.entities.HistoricoEnvio;
import com.mffdev.mensageria.entities.StatusEnvio;
import com.mffdev.mensageria.repositories.AlunoRepository;
import com.mffdev.mensageria.repositories.CampanhaRepository;
import com.mffdev.mensageria.repositories.HistoricoEnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EnvioService {

    @Autowired
    private CampanhaRepository campanhaRepository;

    @Autowired
    private HistoricoEnvioRepository historicoEnvioRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Dispara uma campanha para toda a base ativa.
     * <p>
     * Regra de negocio:
     * - O historico registra apenas que o aluno recebeu <b>esta</b> campanha,
     *   servindo somente para evitar duplicidade caso o mesmo ID seja disparado
     *   novamente. Ele NAO bloqueia o envio de uma campanha nova por conta de
     *   campanhas anteriores de outros materiais.
     * - Novos leads e base antiga recebem igualmente a campanha em disparo.
     *
     * @param campanhaId   identificador da campanha que esta sendo disparada agora.
     * @param apenasNovos  quando true, restringe o disparo apenas para novos cadastros
     *                     (alunos sem historico de envio e sem flag emailEnviado).
     */
    public String dispararCampanha(Long campanhaId, boolean apenasNovos) {
        Campanha campanha = buscarCampanha(campanhaId);

        List<Aluno> alunosAtivos = alunoRepository.findByAtivoTrue();
        if (alunosAtivos.isEmpty()) {
            return "Nenhum aluno ativo na base.";
        }

        Set<Long> idsComHistoricoEnviado = new HashSet<>(
                historicoEnvioRepository.findAlunoIdsComHistoricoEnviado(StatusEnvio.ENVIADO));
        Set<Long> idsQueJaReceberamCampanha = new HashSet<>(
                historicoEnvioRepository.findAlunoIdsQueReceberamCampanha(campanhaId, StatusEnvio.ENVIADO));

        int enviados = 0;
        int falhas = 0;
        int jaReceberam = 0;
        int foraDoFiltro = 0;

        List<Campanha> apenasCampanhaNova = List.of(campanha);

        for (Aluno aluno : alunosAtivos) {
            if (apenasNovos && (idsComHistoricoEnviado.contains(aluno.getId())
                    || Boolean.TRUE.equals(aluno.getEmailEnviado()))) {
                foraDoFiltro++;
                continue;
            }

            if (idsQueJaReceberamCampanha.contains(aluno.getId())) {
                jaReceberam++;
                continue;
            }

            marcar(aluno, apenasCampanhaNova, StatusEnvio.PENDENTE);
            try {
                emailService.enviarCampanhas(aluno, apenasCampanhaNova);
                marcar(aluno, apenasCampanhaNova, StatusEnvio.ENVIADO);
                enviados++;
                System.out.println(">>> Enviado para: " + aluno.getNome() + " <" + aluno.getEmail() + ">");
            } catch (Exception e) {
                marcar(aluno, apenasCampanhaNova, StatusEnvio.FALHOU);
                falhas++;
                System.out.println(">>> Falha ao enviar para: " + aluno.getEmail() + " - Erro: " + e.getMessage());
            }
        }

        String resultado = String.format(
                "Campanha %s disparada! Enviados: %d | Falhas: %d | Ja haviam recebido (pulados): %d | "
                        + "Fora do filtro: %d | Base ativa: %d",
                campanha.getTitulo(), enviados, falhas, jaReceberam, foraDoFiltro, alunosAtivos.size());
        System.out.println(">>> " + resultado);
        return resultado;
    }

    /**
     * Envia uma campanha para um unico e-mail informado, apenas para teste.
     * Nao registra historico de envio nem altera o status da base.
     */
    public void enviarEmailTeste(Long campanhaId, String email) {
        Campanha campanha = buscarCampanha(campanhaId);

        try {
            emailService.enviarCampanhas(email, List.of(campanha));
            System.out.println(">>> [Teste] E-mail enviado para: " + email
                    + " | Campanha: " + campanha.getTitulo());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail de teste para " + email + ": " + e.getMessage());
        }
    }

    private Campanha buscarCampanha(Long campanhaId) {
        return campanhaRepository.findById(campanhaId)
                .orElseThrow(() -> new RuntimeException("Campanha nao encontrada para o id " + campanhaId
                        + " | campanhas cadastradas: "
                        + campanhaRepository.findAllByOrderByOrdemExibicaoAscDataCriacaoAsc()
                                .stream().map(Campanha::getId).toList()));
    }

    private void marcar(Aluno aluno, List<Campanha> campanhas, StatusEnvio status) {
        for (Campanha campanha : campanhas) {
            Optional<HistoricoEnvio> existente = historicoEnvioRepository
                    .findByAlunoIdAndCampanhaId(aluno.getId(), campanha.getId());

            HistoricoEnvio envio = existente.orElseGet(HistoricoEnvio::new);
            envio.setAluno(aluno);
            envio.setCampanha(campanha);
            envio.setStatus(status);
            envio.setDataEnvio(status == StatusEnvio.ENVIADO ? LocalDateTime.now() : null);

            historicoEnvioRepository.save(envio);
        }
    }
}