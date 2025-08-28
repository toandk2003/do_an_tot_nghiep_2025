package org.yenln8.ChatApp.seed;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.yenln8.ChatApp.common.constant.S3Constant;
import org.yenln8.ChatApp.common.util.RedisService;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.repository.*;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

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
    private RedisService redisService;
    private NativeLanguageLocaleRepository  nativeLanguageLocaleRepository;
    private LearningLanguageLocaleRepository  learningLanguageLocaleRepository;

    @Bean
    @Transactional
    public CommandLineRunner seed(UserRepository userRepository) {
        return args -> {
            this.seedLearningNative();
            this.seedAttachment();
            this.seedUser();
            this.seedLastOnline();
        };
    }

    private void seedLastOnline() {
        for (int i = 0; i < 21; i++) {
            String email = "fakeUser" + i + "@gmail.com";
            Long lastOnlineSecondFromEpoch = this.redisService.getKey(this.redisService.getKeyLastOnlineWithPrefix(email), Long.class);
            if(lastOnlineSecondFromEpoch == null) {
                this.redisService.setKey(this.redisService.getKeyLastOnlineWithPrefix(email), Instant.now().getEpochSecond());
            }
        }
    }

    private void seedAttachment() throws IOException {
        if (attachmentRepository.count() == 0) {
            // Inline code - chỉ cần thay "sample.jpg" thành tên ảnh của bạn
            // Upload file
            for (int i = 0; i < 21; i++) {
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
            List<NativeLanguage> nativeLanguages = this.nativeLanguageRepository.findAll();
            List<LearningLanguage> learningLanguages = this.learningLanguageRepository.findAll();

            for (int i = 0; i < 21; i++) {
                Random random = new Random();
                LearningLanguage learningLanguage =  learningLanguages.get(random.nextInt(learningLanguages.size()));
                NativeLanguage nativeLanguage = nativeLanguages.get(random.nextInt(nativeLanguages.size()));

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

    private void seedLearningNative() {
        if (learningLanguageRepository.count() == 0) {

            //--------------------------------------
            LearningLanguage learningLanguageVN = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.VN)
                    .build();
            LearningLanguage learningLanguageEN = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.EN)
                    .build();

            NativeLanguage nativeLanguageVN = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.VN)
                    .build();

            NativeLanguage nativeLanguageEN = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.EN)
                    .build();

            learningLanguageRepository.save(learningLanguageVN);
            learningLanguageRepository.save(learningLanguageEN);

            nativeLanguageRepository.save(nativeLanguageVN);
            nativeLanguageRepository.save(nativeLanguageEN);

            LearningLanguageLocale record1 = LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageVN)
                    .name("Vietnamese")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build();

            LearningLanguageLocale record3 = LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageVN)
                    .name("Việt Nam")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build();

            LearningLanguageLocale record2 = LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageEN)
                    .name("English")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build();

            LearningLanguageLocale record4 = LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageEN)
                    .name("Nước Anh")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build();
            this.learningLanguageLocaleRepository.saveAll(List.of(record1, record2, record3, record4));

            //-------------------------------------------------
            NativeLanguageLocale record5 = NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageVN)
                    .name("Vietnamese")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build();

            NativeLanguageLocale record6 = NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageVN)
                    .name("Việt Nam")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build();

            NativeLanguageLocale record7 = NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageEN)
                    .name("English")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build();

            NativeLanguageLocale record8 = NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageEN)
                    .name("Nước Anh")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build();
            this.nativeLanguageLocaleRepository.saveAll(List.of(record5, record6, record7, record8));

            log.info("✅ Seeded Learning Language Record:");
        } else {
            log.info("📋 Database đã có dữ liệu Learning Language, bỏ qua việc seed");
        }
    }

}
