package com.example.service.impl;

import com.example.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("🔐 Mã OTP đặt lại mật khẩu — Fashion Outfit Application");

            String htmlContent = """
                <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 480px; margin: 0 auto; padding: 32px; background: #f8f9fa; border-radius: 12px;">
                    <h2 style="color: #1a1a2e; text-align: center; margin-bottom: 8px;">Fashion Outfit Suggestions</h2>
                    <p style="color: #666; text-align: center; margin-bottom: 24px;">Bạn đã yêu cầu đặt lại mật khẩu</p>
                    <div style="background: #fff; border-radius: 8px; padding: 24px; text-align: center; box-shadow: 0 2px 8px rgba(0,0,0,0.08);">
                        <p style="color: #888; margin-bottom: 8px; font-size: 14px;">Mã OTP của bạn:</p>
                        <div style="font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #e94560; padding: 16px;">
                            %s
                        </div>
                        <p style="color: #999; font-size: 12px; margin-top: 12px;">⏳ Mã này sẽ hết hạn sau <strong>3 phút</strong></p>
                    </div>
                    <p style="color: #999; font-size: 12px; text-align: center; margin-top: 20px;">
                        Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.
                    </p>
                </div>
                """.formatted(otp);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("OTP email sent to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
