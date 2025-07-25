package org.yenln8.ChatApp.seed;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yenln8.ChatApp.entity.LearningLanguage;
import org.yenln8.ChatApp.entity.NativeLanguage;
import org.yenln8.ChatApp.repository.LearningLanguageRepository;
import org.yenln8.ChatApp.repository.NativeLanguageRepository;

import java.util.List;

@Configuration
@AllArgsConstructor
@Slf4j
public class LearningLanguageInitializer {

    @Bean
    public CommandLineRunner initLearningLanguage(LearningLanguageRepository learningLanguageRepository) {
        return args -> {
            // Kiểm tra xem đã có dữ liệu chưa
            if (learningLanguageRepository.count() == 0) {

                LearningLanguage record1 = LearningLanguage.builder()
                        .name("Vietnamese")
                        .locale(LearningLanguage.LOCALE.ENGLISH)
                        .build();

                LearningLanguage record2 = LearningLanguage.builder()
                        .name("English")
                        .locale(LearningLanguage.LOCALE.ENGLISH)
                        .build();

                LearningLanguage record3 = LearningLanguage.builder()
                        .name("Việt Nam")
                        .locale(LearningLanguage.LOCALE.VIETNAMESE)
                        .build();

                LearningLanguage record4 = LearningLanguage.builder()
                        .name("Nước Anh")
                        .locale(LearningLanguage.LOCALE.VIETNAMESE)
                        .build();

                learningLanguageRepository.saveAll(List.of(record1, record2, record3, record4));

                log.info("✅ Seeded Learning Language Record:");
            } else {
                log.info("📋 Database đã có dữ liệu Learning Language, bỏ qua việc seed");
            }
        };
    }

}
