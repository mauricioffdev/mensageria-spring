package com.mffdev.mensageria.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void dispararCampanhaCsv(MultipartFile arquivoCsv, String assunto, String textoPadrao) {

        // O try-with-resources garante que o BufferedReader seja fechado no final
        try (BufferedReader br = new BufferedReader(new InputStreamReader(arquivoCsv.getInputStream()))) {

            String linhaEmail;
            int contagem = 0;

            // Lê o arquivo linha por linha até acabar
            while ((linhaEmail = br.readLine()) != null) {

                // Limpa espaços em branco e ignora linhas vazias
                linhaEmail = linhaEmail.trim();

                if (!linhaEmail.isEmpty()) {
                    SimpleMailMessage mensagem = new SimpleMailMessage();
                    mensagem.setTo(linhaEmail);
                    mensagem.setSubject(assunto);
                    mensagem.setText(textoPadrao);

                    mailSender.send(mensagem);
                    contagem++;
                    System.out.println(">>> Enviado para: " + linhaEmail);
                }
            }

            System.out.println(">>> Disparo em massa concluído! Total: " + contagem + " e-mails.");

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o arquivo CSV: " + e.getMessage());
        }
    }
}