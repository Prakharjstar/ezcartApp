package com.shop.ecommerce.multivendor.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendVerificationOtpEmail(String userEmail, String otp) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("prakharjoshi960@gmail.com"); // Apna email
            helper.setTo(userEmail);
            helper.setSubject("Ezcart Login OTP");
            helper.setText("Your OTP is: " + otp, false);

            javaMailSender.send(mimeMessage);

            System.out.println("EMAIL SENT SUCCESSFULLY TO: " + userEmail);
            System.out.println("OTP for " + userEmail + " : " + otp); // console ke liye

        } catch (MailException | MessagingException e) {
            System.out.println("EMAIL FAILED: " + e.getMessage());
            throw new MailSendException("Failed to send Email");
        }
    }
}