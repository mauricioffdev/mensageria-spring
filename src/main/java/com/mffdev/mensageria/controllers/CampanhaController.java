package com.mffdev.mensageria.controllers;

import com.mffdev.mensageria.entities.Campanha;
import com.mffdev.mensageria.repositories.CampanhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/campanhas")
public class CampanhaController {

    @Autowired
    private CampanhaRepository campanhaRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(
            @RequestParam("titulo") String titulo,
            @RequestParam("assunto") String assunto,
            @RequestParam("conteudo") String conteudoHtml,
            @RequestParam(value = "ordem", defaultValue = "999") Integer ordem,
            @RequestParam(value = "pdf", required = false) MultipartFile arquivoPdf) {

        Campanha campanha = new Campanha();
        campanha.setTitulo(titulo);
        campanha.setAssuntoEmail(assunto);
        campanha.setConteudoHtml(conteudoHtml);
        campanha.setOrdemExibicao(ordem);
        campanha.setDataCriacao(LocalDateTime.now());

        if (arquivoPdf != null && !arquivoPdf.isEmpty()) {
            campanha.setNomeArquivoPdf(arquivoPdf.getOriginalFilename());
            try {
                campanha.setArquivoPdf(arquivoPdf.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Erro ao ler o arquivo PDF: " + e.getMessage());
            }
        }

        campanha = campanhaRepository.save(campanha);

        return ResponseEntity.ok(Map.of(
                "id", campanha.getId(),
                "titulo", campanha.getTitulo(),
                "ordem", campanha.getOrdemExibicao()));
    }
}