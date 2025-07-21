//package org.yenln8.ChatApp.common.schedule_task;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import org.yenln8.ChatApp.entity.AccountPending;
//import org.yenln8.ChatApp.entity.EmailOutbox;
//import org.yenln8.ChatApp.entity.OTP;
//import org.yenln8.ChatApp.repository.AccountPendingRepository;
//import org.yenln8.ChatApp.repository.EmailOutboxRepository;
//import org.yenln8.ChatApp.repository.OTPRepository;
//import org.yenln8.ChatApp.services.SendOTPRegistrationService;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Component
//@Slf4j
//@AllArgsConstructor
//public class SendEmailSchedule {
//    private static final int batchSize = 100;
//    private EmailOutboxRepository emailOutboxRepository;
//    private SendOTPRegistrationService sendOTPRegistrationService;
//
//    @Scheduled(fixedDelay = 500)
//    @Transactional
//    public void scheduleFreedomOTPRegister() {
//        log.info("Send email is running background..............");
//        try {
//            List<EmailOutbox> emailOutboxes = this.emailOutboxRepository.findAllByTypeAndDeletedAtIsNull(EmailOutbox.TYPE.REGISTER_ACCOUNT, PageRequest.of(0, batchSize)).getContent();
//            for (EmailOutbox emailOutbox : emailOutboxes) {
//                EmailOutbox.TYPE type = emailOutbox.getType();
//                switch (type) {
//                    case REGISTER_ACCOUNT: handleSendMailRegistration(emailOutbox);
//                }
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            throw e;
//        }
//    }
//
//    private void handleSendMailRegistration(EmailOutbox emailOutbox){
//        this.emailOutboxRepository.save(emailOutbox.toBuilder()
//                .deletedAt(LocalDateTime.now())
//                .deleted(emailOutbox.getId())
//                .build());
//
//        this.sendOTPRegistrationService.sendOTPRegistration(emailOutbox.getToEmail(), emailOutbox.getOtpCode());
//        log.warn("OTP {} sent to {} for registration", emailOutbox.getOtpCode(), emailOutbox.getToEmail());
//    }
//}
