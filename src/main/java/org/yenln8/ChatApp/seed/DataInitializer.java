package org.yenln8.ChatApp.seed;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.*;
import org.yenln8.ChatApp.event.synchronize.SynchronizeUserEvent;
import org.yenln8.ChatApp.repository.*;
import org.yenln8.ChatApp.services.serviceImpl.user.interfaces.GetFullInfoAboutUserService;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@Slf4j
public class DataInitializer {
    @Value("${app.redis.streams.sync-stream}")
    private String syncStream;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AmazonS3 s3Client;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private NativeLanguageRepository nativeLanguageRepository;
    @Autowired
    private LearningLanguageRepository learningLanguageRepository;
    @Autowired
    private LimitResourceRepository limitResourceRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private RedisService redisService;
    @Autowired
    private NativeLanguageLocaleRepository nativeLanguageLocaleRepository;
    @Autowired
    private LearningLanguageLocaleRepository learningLanguageLocaleRepository;
    @Autowired
    private GetFullInfoAboutUserService getFullInfoAboutUserService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private TopicTestRepository topicTestRepository;
    @Autowired
    private DifficultyTestRepository difficultyTestRepository;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private QuestionTestRepository questionTestRepository;
    @Autowired
    private QuestionOptionRepository questionOptionRepository;
    @Bean
    @Transactional
    public CommandLineRunner seed(UserRepository userRepository) {
        return args -> {
            this.seedLearningNative();
            this.seedAttachment();
            this.seedLastOnline();
            this.seedUser();
            this.seedTopic();
            this.seedDifficulty();
            this.seedTest();
//            this.seedQuestionTest();
//            this.seedQuestionOption();
        };
    }

    private void seedLastOnline() {
        for (int i = 0; i < 21; i++) {
            String email = i == 4 ? "ChatBot@gmail.com" : "fakeUser" + i + "@gmail.com";
            Long lastOnlineSecondFromEpoch = this.redisService.getKey(this.redisService.getKeyLastOnlineWithPrefix(email), Long.class);
            if (lastOnlineSecondFromEpoch == null) {
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

    private void seedTopic() throws Exception {
        try {
            if (topicTestRepository.count() == 0) {
                var topic1 = TopicTest.builder()
                        .name("Family")
                        .build();
                var topic2 = TopicTest.builder()
                        .name("Work")
                        .build();
                var topic3 = TopicTest.builder()
                        .name("Education")
                        .build();
                var topic4 = TopicTest.builder()
                        .name("Music")
                        .build();
                var topic5 = TopicTest.builder()
                        .name("Entertainment")
                        .build();
                topicTestRepository.saveAll(List.of(topic1, topic2, topic3,topic4,topic5));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seedDifficulty() throws Exception {
        try {
            if (difficultyTestRepository.count() == 0) {
                var diff1 = DifficultyTests.builder()
                        .name("Easy")
                        .build();
                var diff2 = DifficultyTests.builder()
                        .name("Medium")
                        .build();
                var diff3 = DifficultyTests.builder()
                        .name("Hard")
                        .build();
                difficultyTestRepository.save(diff1);
                difficultyTestRepository.save(diff2);
                difficultyTestRepository.save(diff3);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void seedTest() throws Exception {
        var diffs = difficultyTestRepository.findAll();
        var topics = topicTestRepository.findAll();
        var learningLanguages = learningLanguageRepository.findAll();
        try {
            if (testRepository.count() == 0) {
                List<Test> tests = new ArrayList<>();
                for(int i = 1; i <= 21 ; i++){
                    var test = Test.builder()
                            .title("Title " + i)
                            .subTitle("Subtitle " + i)
                            .difficultyTests(diffs.get(new Random().nextInt(diffs.size())))
                            .topicTest(topics.get(new Random().nextInt(topics.size())))
                            .learningLanguage(learningLanguages.get(new Random().nextInt(learningLanguages.size())))
                            .build();
                    tests.add(test);
                }
                testRepository.saveAll(tests);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void seedQuestionTest() throws Exception {
        try {
            if (difficultyTestRepository.count() == 0) {
                var diff1 = DifficultyTests.builder()
                        .name("Easy")
                        .build();
                var diff2 = DifficultyTests.builder()
                        .name("Medium")
                        .build();
                var diff3 = DifficultyTests.builder()
                        .name("Hard")
                        .build();
                difficultyTestRepository.save(diff1);
                difficultyTestRepository.save(diff2);
                difficultyTestRepository.save(diff3);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void seedQuestionOption() throws Exception {
        try {
            if (difficultyTestRepository.count() == 0) {
                var diff1 = DifficultyTests.builder()
                        .name("Easy")
                        .build();
                var diff2 = DifficultyTests.builder()
                        .name("Medium")
                        .build();
                var diff3 = DifficultyTests.builder()
                        .name("Hard")
                        .build();
                difficultyTestRepository.save(diff1);
                difficultyTestRepository.save(diff2);
                difficultyTestRepository.save(diff3);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void seedUser() throws Exception {
        try {
            if (userRepository.count() == 0) {
                List<Attachment> attachments = this.attachmentRepository.findAll();
                List<NativeLanguage> nativeLanguages = this.nativeLanguageRepository.findAll();
                List<LearningLanguage> learningLanguages = this.learningLanguageRepository.findAll();

                for (int i = 0; i < 21; i++) {
                    Random random = new Random();
                    LearningLanguage learningLanguage = learningLanguages.get(random.nextInt(learningLanguages.size()));
                    NativeLanguage nativeLanguage = nativeLanguages.get(random.nextInt(nativeLanguages.size()));

                    Profile profile = this.profileRepository.save(Profile.builder()
                            .bio("XinchaoChatApp")
                            .location("VN")
                            .avatar(attachments.get(i))
                            .learningLanguage(learningLanguage)
                            .nativeLanguage(nativeLanguage)
                            .build());

                    User user = User.builder()
                            .email(i == 4 ? "ChatBot@gmail.com" : "fakeUser" + i + "@gmail.com")
                            .password(passwordEncoder.encode("ChatApp123456@"))
                            .fullName(i == 4 ? "CHAT BOT" : "fakeUser" + i)
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

                    GetProfileResponseDto userFullInfo = this.getFullInfoAboutUserService.call(user);
                    SynchronizeUserEvent synchronizeUserEvent = SynchronizeUserEvent.builder()
                            .userId(userFullInfo.getId())
                            .email(userFullInfo.getEmail())
                            .fullName(userFullInfo.getFullName())
                            .bio(userFullInfo.getBio())
                            .location(userFullInfo.getLocation())
                            .learningLanguageId(userFullInfo.getLearningLanguage().getId())
                            .nativeLanguageId(userFullInfo.getNativeLanguage().getId())
                            .nativeLanguageName(userFullInfo.getNativeLanguage().getName())
                            .learningLanguageName(userFullInfo.getLearningLanguage().getName())
                            .avatar(userFullInfo.getFileNameInS3())
                            .bucket(S3Constant.AVATAR_PRIVATE_BUCKET)
                            .status(User.STATUS.ACTIVE.toString())
                            .role(User.ROLE.USER.toString())
                            .maxLimitResourceMedia(S3Constant.MAX_LIMIT_RESOURCE)
                            .currentUsageResourceMedia(0L)
                            .createdAt(user.getCreatedAt())
                            .updatedAt(user.getUpdatedAt())
                            .rowVersion(userFullInfo.getRowVersion())
                            .eventType(Event.TYPE.SYNC_USER)
                            .deleted(0)
                            .build();

                    String body = null;
                    try {
                        body = objectMapper.writeValueAsString(synchronizeUserEvent);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    if (i == 4) {
                        userRepository.delete(user);
                    }
                    log.info("synchronizeUserEvent: {}", body);
                    eventRepository.save(Event.builder()
                            .destination(syncStream)
                            .payload(body)
                            .status(Event.STATUS.WAIT_TO_SEND)
                            .build());
                }
            } else {
                log.info("📋 Database đã có dữ liệu User, bỏ qua việc seed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            userRepository.deleteAll();
        }
    }

    private void seedLearningNative() {
        if (learningLanguageRepository.count() == 0) {

            //--------------------------------------
            // Tạo đối tượng LearningLanguage cho mỗi mã ngôn ngữ
            LearningLanguage learningLanguageVN = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.VN)
                    .build();
            LearningLanguage learningLanguageEN = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.EN)
                    .build();
            LearningLanguage learningLanguageES = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.ES)
                    .build();
            LearningLanguage learningLanguageFR = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.FR)
                    .build();
            LearningLanguage learningLanguageDE = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.DE)
                    .build();
            LearningLanguage learningLanguageCN = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.CN)
                    .build();
            LearningLanguage learningLanguageJP = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.JP)
                    .build();
            LearningLanguage learningLanguageKR = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.KR)
                    .build();
            LearningLanguage learningLanguageIN = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.IN)
                    .build();
            LearningLanguage learningLanguageRU = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.RU)
                    .build();
            LearningLanguage learningLanguagePT = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.PT)
                    .build();
            LearningLanguage learningLanguageSA = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.SA)
                    .build();
            LearningLanguage learningLanguageIT = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.IT)
                    .build();
            LearningLanguage learningLanguageTR = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.TR)
                    .build();
            LearningLanguage learningLanguageNL = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.NL)
                    .build();
            LearningLanguage learningLanguageTH = LearningLanguage.builder()
                    .code(LearningLanguage.CODE.TH)
                    .build();

            // Tạo đối tượng NativeLanguage cho mỗi mã ngôn ngữ
            NativeLanguage nativeLanguageVN = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.VN)
                    .build();
            NativeLanguage nativeLanguageEN = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.EN)
                    .build();
            NativeLanguage nativeLanguageES = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.ES)
                    .build();
            NativeLanguage nativeLanguageFR = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.FR)
                    .build();
            NativeLanguage nativeLanguageDE = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.DE)
                    .build();
            NativeLanguage nativeLanguageCN = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.CN)
                    .build();
            NativeLanguage nativeLanguageJP = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.JP)
                    .build();
            NativeLanguage nativeLanguageKR = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.KR)
                    .build();
            NativeLanguage nativeLanguageIN = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.IN)
                    .build();
            NativeLanguage nativeLanguageRU = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.RU)
                    .build();
            NativeLanguage nativeLanguagePT = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.PT)
                    .build();
            NativeLanguage nativeLanguageSA = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.SA)
                    .build();
            NativeLanguage nativeLanguageIT = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.IT)
                    .build();
            NativeLanguage nativeLanguageTR = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.TR)
                    .build();
            NativeLanguage nativeLanguageNL = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.NL)
                    .build();
            NativeLanguage nativeLanguageTH = NativeLanguage.builder()
                    .code(NativeLanguage.CODE.TH)
                    .build();

            // Lưu tất cả LearningLanguage bằng vòng lặp
            List<LearningLanguage> learningLanguages = new ArrayList<>();
            learningLanguages.add(learningLanguageVN);
            learningLanguages.add(learningLanguageEN);
            learningLanguages.add(learningLanguageES);
            learningLanguages.add(learningLanguageFR);
            learningLanguages.add(learningLanguageDE);
            learningLanguages.add(learningLanguageCN);
            learningLanguages.add(learningLanguageJP);
            learningLanguages.add(learningLanguageKR);
            learningLanguages.add(learningLanguageIN);
            learningLanguages.add(learningLanguageRU);
            learningLanguages.add(learningLanguagePT);
            learningLanguages.add(learningLanguageSA);
            learningLanguages.add(learningLanguageIT);
            learningLanguages.add(learningLanguageTR);
            learningLanguages.add(learningLanguageNL);
            learningLanguages.add(learningLanguageTH);

            for (LearningLanguage language : learningLanguages) {
                learningLanguageRepository.save(language);
            }

// Lưu tất cả NativeLanguage bằng vòng lặp
            List<NativeLanguage> nativeLanguages = new ArrayList<>();
            nativeLanguages.add(nativeLanguageVN);
            nativeLanguages.add(nativeLanguageEN);
            nativeLanguages.add(nativeLanguageES);
            nativeLanguages.add(nativeLanguageFR);
            nativeLanguages.add(nativeLanguageDE);
            nativeLanguages.add(nativeLanguageCN);
            nativeLanguages.add(nativeLanguageJP);
            nativeLanguages.add(nativeLanguageKR);
            nativeLanguages.add(nativeLanguageIN);
            nativeLanguages.add(nativeLanguageRU);
            nativeLanguages.add(nativeLanguagePT);
            nativeLanguages.add(nativeLanguageSA);
            nativeLanguages.add(nativeLanguageIT);
            nativeLanguages.add(nativeLanguageTR);
            nativeLanguages.add(nativeLanguageNL);
            nativeLanguages.add(nativeLanguageTH);

            for (NativeLanguage language : nativeLanguages) {
                nativeLanguageRepository.save(language);
            }

            // Danh sách các LearningLanguageLocale cho tiếng Anh và tiếng Việt
            List<LearningLanguageLocale> learningLanguageLocales = new ArrayList<>();

            // Tiếng Việt
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageVN)
                    .name("Vietnamese")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageVN)
                    .name("Tiếng Việt")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Anh
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageEN)
                    .name("English")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageEN)
                    .name("Tiếng Anh")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Tây Ban Nha
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageES)
                    .name("Spanish")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageES)
                    .name("Tiếng Tây Ban Nha")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Pháp
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageFR)
                    .name("French")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageFR)
                    .name("Tiếng Pháp")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Đức
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageDE)
                    .name("German")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageDE)
                    .name("Tiếng Đức")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Trung Quốc
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageCN)
                    .name("Mandarin")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageCN)
                    .name("Tiếng Quan Thoại")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Nhật
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageJP)
                    .name("Japanese")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageJP)
                    .name("Tiếng Nhật")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Hàn
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageKR)
                    .name("Korean")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageKR)
                    .name("Tiếng Hàn")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Hindi
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageIN)
                    .name("Hindi")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageIN)
                    .name("Tiếng Hindi")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Nga
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageRU)
                    .name("Russian")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageRU)
                    .name("Tiếng Nga")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Bồ Đào Nha
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguagePT)
                    .name("Portuguese")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguagePT)
                    .name("Tiếng Bồ Đào Nha")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Ả Rập
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageSA)
                    .name("Arabic")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageSA)
                    .name("Tiếng Ả Rập")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Ý
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageIT)
                    .name("Italian")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageIT)
                    .name("Tiếng Ý")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Thổ Nhĩ Kỳ
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageTR)
                    .name("Turkish")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageTR)
                    .name("Tiếng Thổ Nhĩ Kỳ")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Hà Lan
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageNL)
                    .name("Dutch")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageNL)
                    .name("Tiếng Hà Lan")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Thái
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageTH)
                    .name("Thai")
                    .locale(LearningLanguageLocale.LOCALE.ENGLISH)
                    .build());
            learningLanguageLocales.add(LearningLanguageLocale.builder()
                    .learningLanguage(learningLanguageTH)
                    .name("Tiếng Thái")
                    .locale(LearningLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            this.learningLanguageLocaleRepository.saveAll(learningLanguageLocales);

            // Danh sách các NativeLanguageLocale cho tiếng Anh và tiếng Việt
            List<NativeLanguageLocale> nativeLanguageLocales = new ArrayList<>();

            // Tiếng Việt
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageVN)
                    .name("Vietnamese")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageVN)
                    .name("Tiếng Việt")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Anh
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageEN)
                    .name("English")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageEN)
                    .name("Tiếng Anh")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Tây Ban Nha
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageES)
                    .name("Spanish")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageES)
                    .name("Tiếng Tây Ban Nha")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Pháp
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageFR)
                    .name("French")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageFR)
                    .name("Tiếng Pháp")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Đức
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageDE)
                    .name("German")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageDE)
                    .name("Tiếng Đức")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Trung Quốc
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageCN)
                    .name("Mandarin")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageCN)
                    .name("Tiếng Quan Thoại")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Nhật
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageJP)
                    .name("Japanese")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageJP)
                    .name("Tiếng Nhật")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Hàn
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageKR)
                    .name("Korean")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageKR)
                    .name("Tiếng Hàn")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Hindi
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageIN)
                    .name("Hindi")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageIN)
                    .name("Tiếng Hindi")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Nga
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageRU)
                    .name("Russian")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageRU)
                    .name("Tiếng Nga")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Bồ Đào Nha
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguagePT)
                    .name("Portuguese")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguagePT)
                    .name("Tiếng Bồ Đào Nha")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Ả Rập
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageSA)
                    .name("Arabic")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageSA)
                    .name("Tiếng Ả Rập")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Ý
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageIT)
                    .name("Italian")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageIT)
                    .name("Tiếng Ý")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Thổ Nhĩ Kỳ
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageTR)
                    .name("Turkish")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageTR)
                    .name("Tiếng Thổ Nhĩ Kỳ")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Hà Lan
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageNL)
                    .name("Dutch")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageNL)
                    .name("Tiếng Hà Lan")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            // Tiếng Thái
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageTH)
                    .name("Thai")
                    .locale(NativeLanguageLocale.LOCALE.ENGLISH)
                    .build());
            nativeLanguageLocales.add(NativeLanguageLocale.builder()
                    .nativeLanguage(nativeLanguageTH)
                    .name("Tiếng Thái")
                    .locale(NativeLanguageLocale.LOCALE.VIETNAMESE)
                    .build());

            this.nativeLanguageLocaleRepository.saveAll(nativeLanguageLocales);

            log.info("✅ Seeded Learning Language Record:");
        } else {
            log.info("📋 Database đã có dữ liệu Learning Language, bỏ qua việc seed");
        }
    }

}
