package org.yenln8.ChatApp.seed;

import lombok.AllArgsConstructor;
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
                        .email("john@example.com")
                        .password(passwordEncoder.encode("123"))
                        .firstName("John")
                        .lastName("Doe")
                        .fullName("John Doe")
                        .status(User.STATUS.ACTIVE)
                        .role(User.ROLE.ADMIN)
                        .build();

                // User 2 - Normal User
                User normalUser = User.builder()
                        .email("jane@example.com")
                        .password(passwordEncoder.encode("123"))
                        .firstName("Jane")
                        .lastName("Smith")
                        .fullName("Jane Smith")
                        .status(User.STATUS.ACTIVE)
                        .role(User.ROLE.ADMIN)
                        .build();

                // User 3 - Admin User (NORMAL_USER role)
                User adminUser = User.builder()
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("123"))
                        .firstName("Admin")
                        .lastName("User")
                        .fullName("Admin User")
                        .status(User.STATUS.ACTIVE)
                        .role(User.ROLE.ADMIN)
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
