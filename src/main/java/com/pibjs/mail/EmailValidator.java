package com.pibjs.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class EmailValidator {

    private static final String TO = "Recebedor do email";
    private static final String SUBJECT = "Assunto do email";
    private static final String MENSAGEM = "Mensagem do email";

    public void validateEmailInput(String to, String subject, String mensagem) {
        validateField(to, TO);
        validateField(subject, SUBJECT);
        validateField(mensagem, MENSAGEM);
    }

    private void validateField(String field, String fieldName) {
        if (field == null || field.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    public void validateMultipartFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }
    }
}
