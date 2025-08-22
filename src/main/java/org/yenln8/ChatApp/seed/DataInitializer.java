package org.yenln8.ChatApp.seed;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@AllArgsConstructor
@Slf4j
public class DataInitializer {
    private final PasswordEncoder passwordEncoder;
    private final AmazonS3 s3Client;
    private UserRepository userRepository;
    private ProfileRepository profileRepository;
    private NativeLanguageRepository nativeLanguageRepository;
    private LearningLanguageRepository learningLanguageRepository;
    private LimitResourceRepository limitResourceRepository;
    private AttachmentRepository attachmentRepository;

    @Bean
    @Transactional
    public CommandLineRunner seed(UserRepository userRepository) {
        return args -> {
            this.seedLang();
            this.seedNative();
            this.seedAttachment();
            this.seedUser();
        };
    }

    private void seedAttachment() throws IOException {
        if (attachmentRepository.count() == 0 ) {
            // Inline code - chỉ cần thay "sample.jpg" thành tên ảnh của bạn
            // Upload file
            for(int i = 0 ; i < 21; i++){
                ClassPathResource resource = new ClassPathResource("image/abc.png");
                byte[] imageBytes = resource.getInputStream().readAllBytes();
                MultipartFile multipartFile = new MockMultipartFile("image", "abc.png", "image/png", imageBytes);
                String originalFileName = multipartFile.getOriginalFilename();
                String contentType = multipartFile.getContentType();
                Long fileSize = multipartFile.getSize();

                TimeBasedEpochGenerator generator = Generators.timeBasedEpochGenerator();
                String fileNameInS3 = generator.generate().toString() + "_" + originalFileName;

                log.info("fileNameInS3: {}", fileNameInS3);
                log.info("contentType: {}", contentType);
                log.info("fileSize: {}", fileSize);

                // Upload trực tiếp lên S3
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(contentType);
                metadata.setContentLength(fileSize);

                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        S3Constant.AVATAR_PRIVATE_BUCKET,
                        fileNameInS3,
                        multipartFile.getInputStream(),
                        metadata
                );
                PutObjectResult result = s3Client.putObject(putObjectRequest);
                log.info("Upload successful. ETag: {}", result.getETag());

                Attachment attachmentToSave = Attachment.builder()
                        .originalFileName(originalFileName)
                        .s3BucketName(S3Constant.AVATAR_PRIVATE_BUCKET)
                        .fileNameInS3(fileNameInS3)
                        .fileSize(fileSize)
                        .contentType(contentType)
                        .status(Attachment.STATUS.CONFIRMED) // Đổi thành SUCCESS vì đã upload xong
                        .expireAt(LocalDateTime.now().plusDays(S3Constant.EXPIRE_TIME_ATTACHMENT))
                        .createdBy(1L)
                        .build();

                Attachment savedAttachment = this.attachmentRepository.save(attachmentToSave);
            }

        }
    }

    private void seedUser() {
        if (userRepository.count() == 0) {
            List<Attachment> attachments = this.attachmentRepository.findAll();
            LearningLanguage learningLanguage = this.learningLanguageRepository.findById(1L).orElse(null);
            NativeLanguage nativeLanguage = this.nativeLanguageRepository.findById(1L).orElse(null);

            for(int i = 0 ; i < 21; i++){
                Profile profile = this.profileRepository.save(Profile.builder()
                                .bio("XinchaoChatApp")
                                .location("VN")
                                .avatar(attachments.get(i))
                                .learningLanguage(learningLanguage)
                                .nativeLanguage(nativeLanguage)
                        .build());

                User user = User.builder()
                        .email("fakeUser" + i + "@gmail.com")
                        .password(passwordEncoder.encode("ChatApp123456@"))
                        .fullName("fakeUser" + i)
                        .status(User.STATUS.ACTIVE)
                        .role(User.ROLE.USER)
                        .profile(profile)
                        .build();


                // Lưu cả 3 bản ghi vào database
                userRepository.save(user);

                LimitResource limitResource = LimitResource.builder()
                        .maxLimit(S3Constant.MAX_LIMIT_RESOURCE)
                        .type(LimitResource.TYPE.MEDIA)
                        .currentUsage(0L)
                        .userId(user.getId())
                        .build();
                this.limitResourceRepository.save(limitResource);
            }
        } else {
            log.info("📋 Database đã có dữ liệu User, bỏ qua việc seed");
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
