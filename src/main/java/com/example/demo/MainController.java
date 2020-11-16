package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    private JavaMailSender javaMailSender;

    @GetMapping("/")
    public String emailSend() {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("prajapatisuresh6334@gmail.com");
        msg.setSubject("Room Booked / Payment Received");
        msg.setText("\nRoom Booked...");

        javaMailSender.send(msg);

        return "MESSAGE SENT";

    }
}
