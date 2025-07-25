package org.yenln8.ChatApp.seed;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yenln8.ChatApp.entity.LearningLanguage;
import org.yenln8.ChatApp.entity.NativeLanguage;
import org.yenln8.ChatApp.entity.Profile;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.LearningLanguageRepository;
import org.yenln8.ChatApp.repository.NativeLanguageRepository;
import org.yenln8.ChatApp.repository.ProfileRepository;

import java.util.List;

@Configuration
@AllArgsConstructor
@Slf4j
public class ProfileInitializer {

    @Bean
    public CommandLineRunner initProdile(ProfileRepository repository) {
        return args -> {
            // Kiểm tra xem đã có dữ liệu chưa
            if (repository.count() == 0) {

//                Profile record1 = Profile.builder()
//                        .user(User.builder().id(1L).build())
//                        .build();

//                repository.saveAll(List.of(record1));
                //TODO

                log.info("✅ Seeded Profile Record:");
            } else {
                log.info("📋 Database đã có dữ liệu Profile, bỏ qua việc seed");
            }
        };
    }

}
