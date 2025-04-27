package com.oma.service;

import com.oma.model.Order;
import com.oma.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import io.github.cdimascio.dotenv.Dotenv;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    Dotenv dotenv = Dotenv.load();

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(User user, String token) {
        String link = dotenv.get("API_URL")+"/auth/confirm?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@marketplace.com");
        message.setTo(user.getEmail());
        message.setSubject("Verify your email");
        message.setText("Hello " + user.getNames() + ",\n\nPlease verify your email by clicking the link below:\n" + link);

        mailSender.send(message);
    }

    public void sendOrderConfirmation(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@marketplace.com");
        message.setTo(order.getBuyer().getEmail());
        message.setSubject("Order Confirmation");
        message.setText("Thank you for your order!\n\nOrder ID: " + order.getId());

        mailSender.send(message);
    }
}
