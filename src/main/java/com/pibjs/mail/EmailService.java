package com.pibjs.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pibjs.config.EmailConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailSender emailSender;
    private final EmailConfig emailConfig;
    private final EmailValidator emailValidator;
    private final TemporaryFileManager fileManager;

    public void enviarEmail(String to, String subject, String mensagem) {
        emailValidator.validateEmailInput(to, subject, mensagem);

        emailSender.to(to)
                .withSubject(subject)
                .withMessage(mensagem)
                .send(emailConfig);

        log.info("Email enviado com sucesso para: {}", to);
    }

    public void enviarEmailComAnexo(String emailRequestJson, MultipartFile multipartFile) {
        emailValidator.validateMultipartFile(multipartFile);

        Path tempFilePath = null;
        try {
            EmailDTO emailDTO = parseEmailDTO(emailRequestJson);

            tempFilePath = fileManager.createTempFile(multipartFile);

            emailSender.to(emailDTO.getTo())
                    .withSubject(emailDTO.getSubject())
                    .withMessage(emailDTO.getMensagem())
                    .attach(tempFilePath.toString())
                    .send(emailConfig);

            log.info("Email com anexo enviado com sucesso para: {}", emailDTO.getTo());

        } catch (IOException e) {
            log.error("Erro ao processar email com anexo", e);
            throw new RuntimeException("Erro ao processar email com anexo", e);
        } finally {
            fileManager.deleteTempFile(tempFilePath);
        }
    }

    private EmailDTO parseEmailDTO(String emailRequestJson) throws IOException {
        try {
            return new ObjectMapper().readValue(emailRequestJson, EmailDTO.class);
        } catch (IOException e) {
            log.error("Error ao parsear email request JSON", e);
            throw new IOException("Invalid email request format", e);
        }
    }
}
