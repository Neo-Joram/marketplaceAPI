package com.oma.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PaymentService {
    private final Random random = new Random();
    public String simplePay() {
            boolean success = random.nextDouble() < 0.7; // 70% success rate
            if (success) {
                return "successful";
            } else {
                return "failed";
            }
        }
}
