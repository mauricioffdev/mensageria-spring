package com.mffdev.mensageria.controllers;

import com.mffdev.mensageria.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/disparar-csv")
    public ResponseEntity<String> dispararViaCsv(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("assunto") String assunto,
            @RequestParam("conteudo") String conteudo) {

        emailService.dispararCampanhaCsv(arquivo, assunto, conteudo);

        return ResponseEntity.ok("Campanha disparada com sucesso a partir do CSV!");
    }

    @PostMapping("/disparar-com-pdf")
    public ResponseEntity<String> dispararComPdf(
            @RequestParam("csv") MultipartFile arquivoCsv,
            @RequestParam("pdf") MultipartFile arquivoPdf,
            @RequestParam("assunto") String assunto,
            @RequestParam("conteudo") String conteudoHtml) {

        emailService.dispararCampanhaComPdf(arquivoCsv, arquivoPdf, assunto, conteudoHtml);

        return ResponseEntity.ok("Campanha com HTML e PDF disparada com sucesso!");
    }

    @PostMapping("/disparar-banco")
    public ResponseEntity<String> dispararBanco(
            @RequestParam("pdf") MultipartFile arquivoPdf,
            @RequestParam("assunto") String assunto,
            @RequestParam("conteudo") String conteudoHtml) {

        String resultado = emailService.dispararCampanhaDoBanco(arquivoPdf, assunto, conteudoHtml);

        return ResponseEntity.ok(resultado);
    }
}
