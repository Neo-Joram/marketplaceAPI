package com.oma.service;

import com.oma.model.Order;
import com.oma.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Value("${API_URL}")
    private String apiUrl;

    public void sendVerificationEmail(User user, String token) {
        String link = apiUrl+"/auth/confirm?token=" + token;
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
