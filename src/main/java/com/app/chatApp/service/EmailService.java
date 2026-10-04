package com.app.chatApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service 
public class EmailService {

    @Value("${spring.mail.from}")
    private String mailFrom;

    private JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendLoginOTP(String toEmail, String otp, String name) {
        // System.out.println("Sending login OTP email to: " + toEmail);
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(mailFrom);
            helper.setTo(toEmail);
            helper.setSubject("HeyChat login OTP");

            // CALLING THE TEMPLATE METHOD HERE:
            String htmlContent = buildOtpEmailTemplate(name, otp, "Login");
            helper.setText(htmlContent, true); // 'true' enables HTML rendering

            javaMailSender.send(mimeMessage);
            // System.out.println("Successfully sent login OTP email to: " + toEmail);
        } catch (MessagingException e) {
            // System.err.println("Failed to send login OTP: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    // --- 2. SIGNUP METHOD ---
    public void sendSignupOTP(String toEmail, String otp, String name) {
        // System.out.println("Sending signup verification email to: " + toEmail);
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(mailFrom);
            helper.setTo(toEmail);
            helper.setSubject("HeyChat signup OTP");

            // CALLING THE TEMPLATE METHOD HERE:
            String htmlContent = buildOtpEmailTemplate(name, otp, "Signup");
            helper.setText(htmlContent, true); // 'true' enables HTML rendering

            javaMailSender.send(mimeMessage);
            // System.out.println("Successfully sent signup verification email to: " + toEmail);
        } catch (MessagingException e) {
            // System.err.println("Failed to send signup OTP: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
    // --- 3. THE TEMPLATE METHOD (Called by both methods above) ---
    private String buildOtpEmailTemplate(String name, String otp, String actionType) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>%s Verification Code</title>
            </head>
            <body style="margin: 0; padding: 0; background-color: #f4f5f7; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
              <table width="100%%" border="0" cellspacing="0" cellpadding="0" style="background-color: #f4f5f7; padding: 40px 16px;">
                <tr>
                  <td align="center">
                    <table width="100%%" border="0" cellspacing="0" cellpadding="0" style="max-width: 520px; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06); border: 1px solid #e9ecef;">
                      
                      <!-- Header Banner -->
                      <tr>
                        <td style="background: linear-gradient(135deg, #6d28d9, #4f46e5); padding: 32px; text-align: center;">
                          <h1 style="margin: 0; font-size: 24px; font-weight: 700; color: #ffffff;">HeyChat</h1>
                          <p style="margin: 4px 0 0 0; font-size: 13px; color: #e0e7ff; text-transform: uppercase; letter-spacing: 0.5px;">%s Verification</p>
                        </td>
                      </tr>

                      <!-- Body -->
                      <tr>
                        <td style="padding: 32px; text-align: left;">
                          <h2 style="margin: 0 0 12px 0; font-size: 18px; font-weight: 600; color: #1e293b;">Hello %s,</h2>
                          <p style="margin: 0 0 20px 0; font-size: 14px; line-height: 1.6; color: #475569;">
                            Please use the verification code below to complete your %s on  HeyChat:
                          </p>

                          <!-- OTP Box -->
                          <table width="100%%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                              <td align="center" style="background-color: #f8fafc; border: 2px dashed #cbd5e1; border-radius: 8px; padding: 18px;">
                                <span style="font-family: monospace; font-size: 32px; font-weight: 700; letter-spacing: 8px; color: #4f46e5;">
                                  %s
                                </span>
                              </td>
                            </tr>
                          </table>

                          <p style="margin: 20px 0 0 0; font-size: 13px; color: #64748b; text-align: center;">
                            ⏳ This OTP will expire in <strong>5 minutes</strong>.
                          </p>
                        </td>
                      </tr>

                      <!-- Footer -->
                      <tr>
                        <td style="background-color: #f8fafc; border-top: 1px solid #f1f5f9; padding: 16px 32px; text-align: center;">
                          <p style="margin: 0; font-size: 12px; color: #94a3b8;">
                            Thank you for using HeyChat!
                          </p>
                        </td>
                      </tr>

                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(actionType, actionType, name, actionType.toLowerCase(), otp);
    }
}
