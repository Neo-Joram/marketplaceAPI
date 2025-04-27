package com.oma.service;

import com.oma.model.Order;
import com.oma.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static java.awt.SystemColor.text;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(User user, String token) {
        String link = "http://localhost:8080/auth/confirm?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@baeldung.com");
        message.setTo(user.getEmail());
        message.setSubject("Email verification request");
        message.setText("Hello " + user.getNames() + ", Your account has been created." +
                        "Click " + link + " to verify your email."
        );
        mailSender.send(message);
    }

    public void sendOrderConfirmation(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@baeldung.com");
        message.setTo(order.getBuyer().getEmail());
        message.setSubject("Email verification request");
        message.setText("Your order has been placed. OrderId:"+order.getId());
        mailSender.send(message);
    }
}

