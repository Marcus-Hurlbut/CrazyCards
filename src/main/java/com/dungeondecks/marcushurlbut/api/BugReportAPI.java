package com.dungeondecks.marcushurlbut.api;

import com.dungeondecks.marcushurlbut.Message;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@CrossOrigin(origins = "http://192.168.86.37:8081")

@RestController
public class BugReportAPI {

    @Value("#{environment.SENDGRID_API_KEY}")
    private String SENDGRID_API_KEY;
    
    // Method to send an email
    @PostMapping("/api/bugReport")
    public boolean sendEmail(@RequestBody Message message) {
        Email from = new Email("support@dungeondecks.net");
        String subject = "Bug Report";
        Email to = new Email("support@dungeondecks.net");
        Content content = new Content("text/plain", message.getContent());

        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
            return true;

        } catch (IOException ex) {
            System.out.println("Error sending email: " + ex.getMessage());
            return false;
        }
    }
}
