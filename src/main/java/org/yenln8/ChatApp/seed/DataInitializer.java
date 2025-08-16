package org.yenln8.ChatApp.seed;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@AllArgsConstructor
@Slf4j
public class DataInitializer {
    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private ProfileRepository profileRepository;
    private NativeLanguageRepository nativeLanguageRepository;
    private LearningLanguageRepository learningLanguageRepository;
    private LimitResourceRepository limitResourceRepository;

    @Bean
    @Transactional
    public CommandLineRunner seed(UserRepository userRepository) {
        return args -> {
            this.seedLang();
            this.seedNative();
            this.seedProfile();
            this.seedUser();
        };
    }

    private void seedUser() {
        if (userRepository.count() == 0) {
            Profile profile = this.profileRepository.findById(1L).orElse(null);
            // User 1 - Admin
            User admin = User.builder()
                    .email("ngocyenptit153@gmail.com")
                    .password(passwordEncoder.encode("ChatApp123456@"))
                    .fullName("PTIT")
                    .status(User.STATUS.ACTIVE)
                    .role(User.ROLE.USER)
                    .profile(profile)
                    .build();


            // Lưu cả 3 bản ghi vào database
            userRepository.save(admin);

            LimitResource limitResource = LimitResource.builder()
                    .maxLimit(S3Constant.MAX_LIMIT_RESOURCE)
                    .type(LimitResource.TYPE.MEDIA)
                    .currentUsage(0L)
                    .userId(admin.getId())
                    .build();
            this.limitResourceRepository.save(limitResource);

            log.info("✅ Đã khởi tạo 3 bản ghi User vào database:");
            log.info("   - admin (ADMIN role)");
            log.info("   - user (NORMAL_USER role)");
            log.info("   - admin_user (NORMAL_USER role)");

        } else {
            log.info("📋 Database đã có dữ liệu User, bỏ qua việc seed");
        }

    }

    private void seedProfile() {
        // Kiểm tra xem đã có dữ liệu chưa

        /// ///
        if (profileRepository.count() == 0) {
            NativeLanguage nativeLanguage = this.nativeLanguageRepository.findById(1L).orElse(null);
            LearningLanguage learningLanguage = this.learningLanguageRepository.findById(1L).orElse(null);

            Profile record1 = Profile.builder()
                    .nativeLanguage(nativeLanguage)
                    .learningLanguage(learningLanguage)
                    .location("Nga Thang")
                    .bio("xinchao")
                    .deleted(0L)
                    .build();

            profileRepository.saveAll(List.of(record1));

            log.info("✅ Seeded Profile Record:");
        } else {
            log.info("📋 Database đã có dữ liệu Profile, bỏ qua việc seed");
        }
    }

    private void seedLang() {
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
    }

    private void seedNative() {
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
    }
}
