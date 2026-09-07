package com.mffdev.mensageria.services;

import com.mffdev.mensageria.entities.Aluno;
import com.mffdev.mensageria.entities.Campanha;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    /**
     * Envia um e-mail para o aluno contendo as campanhas informadas, na ordem em que
     * aparecem na lista. O HTML de cada campanha e concatenado e os PDFs anexados.
     */
    public void enviarCampanhas(Aluno aluno, List<Campanha> campanhas) throws MessagingException, IOException {
        enviarCampanhas(aluno.getEmail(), campanhas);
    }

    /**
     * Envia o e-mail direto para um endereco informado (sem depender de um Aluno cadastrado).
     */
    public void enviarCampanhas(String email, List<Campanha> campanhas) throws MessagingException, IOException {
        if (campanhas == null || campanhas.isEmpty()) {
            return;
        }

        MimeMessage mensagem = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject(campanhas.get(0).getAssuntoEmail());

        StringBuilder html = new StringBuilder();
        for (int i = 0; i < campanhas.size(); i++) {
            Campanha campanha = campanhas.get(i);
            if (i > 0) {
                html.append("<hr/><br/>");
            }
            html.append(campanha.getConteudoHtml());

            byte[] pdf = campanha.getArquivoPdf();
            String nomePdf = campanha.getNomeArquivoPdf();
            if (pdf != null && pdf.length > 0 && nomePdf != null && !nomePdf.isEmpty()) {
                helper.addAttachment(nomePdf, new ByteArrayResource(pdf));
            }
        }
        helper.setText(html.toString(), true);

        mailSender.send(mensagem);
    }
}