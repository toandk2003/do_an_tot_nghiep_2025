package org.yenln8.ChatApp.seed;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yenln8.ChatApp.entity.NativeLanguage;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.NativeLanguageRepository;
import org.yenln8.ChatApp.repository.UserRepository;

import java.util.List;

@Configuration
@AllArgsConstructor
@Slf4j
public class NativeLanguageInitializer {
    @Bean
    public CommandLineRunner initNativeLanguage(NativeLanguageRepository nativeLanguageRepository) {
        return args -> {
            // Kiểm tra xem đã có dữ liệu chưa
            if (nativeLanguageRepository.count() == 0) {

                NativeLanguage nativeLanguage1 = NativeLanguage.builder()
                        .name("Vietnamese")
                        .locale(NativeLanguage.LOCALE.ENGLISH)
                        .build();

                NativeLanguage nativeLanguage2 = NativeLanguage.builder()
                        .name("English")
                        .locale(NativeLanguage.LOCALE.ENGLISH)
                        .build();

                NativeLanguage nativeLanguage3 = NativeLanguage.builder()
                        .name("Việt Nam")
                        .locale(NativeLanguage.LOCALE.VIETNAMESE)
                        .build();

                NativeLanguage nativeLanguage4 = NativeLanguage.builder()
                        .name("Nước Anh")
                        .locale(NativeLanguage.LOCALE.VIETNAMESE)
                        .build();

                nativeLanguageRepository.saveAll(List.of(nativeLanguage1, nativeLanguage2, nativeLanguage3, nativeLanguage4));

                log.info("✅ Seeded Native Language Record:");
            } else {
                log.info("📋 Database đã có dữ liệu Native Language, bỏ qua việc seed");
            }
        };
    }

}
