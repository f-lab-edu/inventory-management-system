package inventory.notification.service;

import inventory.common.exception.CustomException;
import inventory.common.exception.ExceptionCode;
import inventory.notification.domain.Notification;
import inventory.notification.domain.enums.NotificationType;
import inventory.notification.repository.NotificationRepository;
import inventory.notification.service.request.LowStockProduct;
import inventory.notification.service.request.RecipientInfo;
import inventory.notification.service.util.MailTemplateGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final MailTemplateGenerator mailTemplateGenerator;
    private final NotificationRepository notificationRepository;

    @Value("${notification.mail.from}")
    private String fromEmail;

    public void sendLowStockNotification(RecipientInfo recipient, List<LowStockProduct> products) {
        try {
            String subject = mailTemplateGenerator.generateLowStockSubject();
            String content = mailTemplateGenerator.generateLowStockContent(recipient, products);

            sendEmail(recipient.recipientEmail(), subject, content);

            saveNotification(recipient, NotificationType.LOW_STOCK);
        } catch (MessagingException e) {
            throw new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR, "");
        }
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, false);

        mailSender.send(message);
    }

    private void saveNotification(RecipientInfo recipient, NotificationType type) {
        Notification notification = Notification.builder()
                .recipientName(recipient.recipientName())
                .recipientEmail(recipient.recipientEmail())
                .notificationType(type)
                .build();
        notificationRepository.save(notification);
    }
}
