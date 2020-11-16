//package com.example.demo.kafka;
//
//import java.io.IOException;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class Consumer {
//
//	@Autowired
//	private JavaMailSender javaMailSender;
//
//    @KafkaListener(topics = "sendemail", groupId = "groupId")
//    public void consume(String message) throws IOException {
//
//       // Gson gson = new GsonBuilder().create();
//        //Apartments apt=gson.fromJson(message, Apartments.class);
//
//
//        String email=message.substring(0, message.indexOf(","));
//        String body=message.substring(message.indexOf(",")+1);
//        System.out.println(email+"\n"+body);
//
//
//        SimpleMailMessage msg = new SimpleMailMessage();
//		msg.setTo(email);
//		msg.setSubject("Room Booked / Payment Received");
//		msg.setText(body+"\nRoom Booked...");
//		System.out.println("Sending...");
//		javaMailSender.send(msg);
//		System.out.println("sent.");
//
//    }
//}
