package org.yenln8.ChatApp.seed;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class DataInitializer {
    private final PasswordEncoder passwordEncoder;
    @Bean
    public CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            // Kiểm tra xem đã có dữ liệu chưa
            if (userRepository.count() == 0) {

                // User 1 - Admin
                User admin = User.builder()
                        .username("admin")
                        .email("john@example.com")
                        .password(passwordEncoder.encode("123"))
                        .fullName("John Doe")
                        .phone("0123456789")
                        .status(1)
                        .role("ADMIN")
                        .deleted(0)
                        .build();

                // User 2 - Normal User
                User normalUser = User.builder()
                        .username("user")
                        .email("jane@example.com")
                        .password(passwordEncoder.encode("123"))
                        .fullName("Jane Smith")
                        .phone("0987654321")
                        .status(1)
                        .role("NORMAL_USER")
                        .deleted(0)
                        .build();

                // User 3 - Admin User (NORMAL_USER role)
                User adminUser = User.builder()
                        .username("admin_user")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("123"))
                        .fullName("Admin User")
                        .phone("0555123456")
                        .status(1)
                        .role("NORMAL_USER")
                        .deleted(0)
                        .build();

                // Lưu cả 3 bản ghi vào database
                userRepository.save(admin);
                userRepository.save(normalUser);
                userRepository.save(adminUser);

                System.out.println("✅ Đã khởi tạo 3 bản ghi User vào database:");
                System.out.println("   - admin (ADMIN role)");
                System.out.println("   - user (NORMAL_USER role)");
                System.out.println("   - admin_user (NORMAL_USER role)");
            } else {
                System.out.println("📋 Database đã có dữ liệu User, bỏ qua việc seed");
            }
        };
    }
}
