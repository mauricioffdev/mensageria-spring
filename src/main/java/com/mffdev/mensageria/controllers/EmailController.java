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
}