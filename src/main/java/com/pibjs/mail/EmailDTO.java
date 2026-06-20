package com.pibjs.mail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDTO {
    private String to;
    private String subject;
    private String mensagem;
}
