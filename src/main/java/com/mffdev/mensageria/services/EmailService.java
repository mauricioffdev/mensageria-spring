package com.mffdev.mensageria.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import com.mffdev.mensageria.entities.Aluno;
import com.mffdev.mensageria.repositories.AlunoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AlunoRepository alunoRepository;

    public void dispararCampanhaCsv(MultipartFile arquivoCsv, String assunto, String textoPadrao) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(arquivoCsv.getInputStream()))) {

            String linhaEmail;
            int contagem = 0;

            while ((linhaEmail = br.readLine()) != null) {
                linhaEmail = linhaEmail.trim();

                if (!linhaEmail.isEmpty()) {
                    MimeMessage mensagem = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");
                    helper.setTo(linhaEmail);
                    helper.setSubject(assunto);
                    helper.setText(textoPadrao, false);

                    mailSender.send(mensagem);
                    contagem++;
                    System.out.println(">>> Enviado para: " + linhaEmail);
                }
            }

            System.out.println(">>> Disparo em massa concluido! Total: " + contagem + " e-mails.");

        } catch (IOException | MessagingException e) {
            throw new RuntimeException("Erro ao processar o arquivo CSV: " + e.getMessage());
        }
    }

    public void dispararCampanhaComPdf(MultipartFile arquivoCsv, MultipartFile arquivoPdf,
                                        String assunto, String htmlConteudo) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(arquivoCsv.getInputStream()))) {

            String linhaEmail;
            int contagem = 0;

            while ((linhaEmail = br.readLine()) != null) {
                linhaEmail = linhaEmail.trim();

                if (!linhaEmail.isEmpty()) {
                    MimeMessage mensagem = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");
                    helper.setTo(linhaEmail);
                    helper.setSubject(assunto);
                    helper.setText(htmlConteudo, true);

                    if (arquivoPdf != null && !arquivoPdf.isEmpty()) {
                        Resource pdfResource = new ByteArrayResource(arquivoPdf.getBytes());
                        helper.addAttachment(arquivoPdf.getOriginalFilename(), pdfResource);
                    }

                    mailSender.send(mensagem);
                    contagem++;
                    System.out.println(">>> Enviado para: " + linhaEmail);
                }
            }

            System.out.println(">>> Disparo em massa com PDF concluido! Total: " + contagem + " e-mails.");

        } catch (IOException | MessagingException e) {
            throw new RuntimeException("Erro ao processar o disparo com PDF: " + e.getMessage());
        }
    }

    public String dispararCampanhaDoBanco(MultipartFile arquivoPdf, String assunto, String htmlConteudo) {
        List<Aluno> alunos = alunoRepository.findByEmailEnviadoFalse();

        if (alunos.isEmpty()) {
            return "Nenhum novo lead encontrado para envio.";
        }

        int enviados = 0;
        int falhas = 0;

        for (Aluno aluno : alunos) {
            try {
                MimeMessage mensagem = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");
                helper.setTo(aluno.getEmail());
                helper.setSubject(assunto);
                helper.setText(htmlConteudo, true);

                if (arquivoPdf != null && !arquivoPdf.isEmpty()) {
                    Resource pdfResource = new ByteArrayResource(arquivoPdf.getBytes());
                    helper.addAttachment(arquivoPdf.getOriginalFilename(), pdfResource);
                }

                mailSender.send(mensagem);

                aluno.setEmailEnviado(true);
                alunoRepository.save(aluno);

                enviados++;
                System.out.println(">>> Enviado para: " + aluno.getNome() + " <" + aluno.getEmail() + ">");
            } catch (Exception e) {
                falhas++;
                System.out.println(">>> Falha ao enviar para: " + aluno.getEmail() + " - Erro: " + e.getMessage());
            }
        }

        String resultado = String.format(
                "Campanha disparada do banco! Enviados: %d | Falhas: %d | Total processado: %d",
                enviados, falhas, alunos.size());
        System.out.println(">>> " + resultado);
        return resultado;
    }
}
