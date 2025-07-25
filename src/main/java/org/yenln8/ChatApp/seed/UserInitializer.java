package org.yenln8.ChatApp.seed;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@Slf4j
public class UserInitializer {
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initUser(UserRepository userRepository) {
        return args -> {
            // Kiểm tra xem đã có dữ liệu chưa
            if (userRepository.count() == 0) {

                // User 1 - Admin
                User admin = User.builder()
                        .email("john@example.com")
                        .password(passwordEncoder.encode("123"))
                        .fullName("John Doe")
                        .status(User.STATUS.ACTIVE)
                        .role(User.ROLE.ADMIN)
                        .build();

                // User 2 - Normal User
                User normalUser = User.builder()
                        .email("jane@example.com")
                        .password(passwordEncoder.encode("123"))
                        .fullName("Jane Smith")
                        .status(User.STATUS.ACTIVE)
                        .role(User.ROLE.ADMIN)
                        .build();

                // User 3 - Admin User (NORMAL_USER role)
                User adminUser = User.builder()
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("123"))
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
        };
    }

}
