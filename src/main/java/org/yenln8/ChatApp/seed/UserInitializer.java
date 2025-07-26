package org.yenln8.ChatApp.seed;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yenln8.ChatApp.entity.Profile;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.ProfileRepository;
import org.yenln8.ChatApp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@AllArgsConstructor
@Slf4j
public class UserInitializer {
    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private ProfileRepository profileRepository;


    @Bean
    public CommandLineRunner initUser(UserRepository userRepository) {
        return args -> {
           this.seedUser();
           this.seedProfile();
        };
    }

    private void seedUser(){
        if (userRepository.count() == 0) {

            // User 1 - Admin
            User admin = User.builder()
                    .email("ngocyenptit153@gmail.com")
                    .password(passwordEncoder.encode("ChatApp123456@"))
                    .fullName("PTIT")
                    .status(User.STATUS.ACTIVE)
                    .role(User.ROLE.USER)
                    .build();

            // User 2 - Normal User
            User normalUser = User.builder()
                    .email("jane@example.com")
                    .password(passwordEncoder.encode("ChatApp123456@"))
                    .fullName("Jane Smith")
                    .status(User.STATUS.ACTIVE)
                    .role(User.ROLE.ADMIN)
                    .build();

            // User 3 - Admin User (NORMAL_USER role)
            User adminUser = User.builder()
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("ChatApp123456@"))
                    .fullName("Admin User")
                    .status(User.STATUS.ACTIVE)
                    .role(User.ROLE.ADMIN)
                    .build();

            // Lưu cả 3 bản ghi vào database
            userRepository.save(admin);
            userRepository.save(normalUser);
            userRepository.save(adminUser);

            log.info("✅ Đã khởi tạo 3 bản ghi User vào database:");
            log.info("   - admin (ADMIN role)");
            log.info("   - user (NORMAL_USER role)");
            log.info("   - admin_user (NORMAL_USER role)");

        } else {
            log.info("📋 Database đã có dữ liệu User, bỏ qua việc seed");
        }

    }

    private void seedProfile(){
        // Kiểm tra xem đã có dữ liệu chưa

        /// ///
        if (profileRepository.count() == 0) {
            User user = this.userRepository.findById(1L).orElse(null);

            Profile record1 = Profile.builder()
                    .user(user)
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

}
