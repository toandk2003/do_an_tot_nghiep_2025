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
            testRepository.deleteAll();
            questionTestRepository.deleteAll();
            questionOptionRepository.deleteAll();
            this.seedTestForVietNam();
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
                topicTestRepository.saveAll(List.of(topic1, topic2, topic3, topic4, topic5));
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

    private void seedTestForVietNam() throws Exception {
        var easy = difficultyTestRepository.findById(1L).get();
        var medium = difficultyTestRepository.findById(2L).get();
        var hard = difficultyTestRepository.findById(3L).get();

        var topicFamily = topicTestRepository.findById(1L).get();//Family
        var topicWork = topicTestRepository.findById(2L).get(); //Work
        var topicEducation = topicTestRepository.findById(3L).get(); //Education
        var topicMusic = topicTestRepository.findById(4L).get(); //Music
        var topicEntertainment = topicTestRepository.findById(5L).get(); //Entertainment

        var learningLanguage = learningLanguageRepository.findById(1L).get(); // Vietnam

        // easy -> topicFamily -> 2 bai test
        // medium -> topicFamily -> 2 bai test
        // hard -> topicFamily -> 2 bai test
        var test1 = Test.builder()
                .title("Gia đình")
                .subTitle("Dễ - 20 câu hỏi")
                .difficultyTests(easy)
                .topicTest(topicFamily)
                .learningLanguage(learningLanguage)
                .build();
        var test2 = Test.builder()
                .title("Gia đình")
                .subTitle("Trung bình - 20 câu hỏi")
                .difficultyTests(medium)
                .topicTest(topicFamily)
                .learningLanguage(learningLanguage)
                .build();
        var test3 = Test.builder()
                .title("Gia đình")
                .subTitle("Khó - 20 câu hỏi")
                .difficultyTests(hard)
                .topicTest(topicFamily)
                .learningLanguage(learningLanguage)
                .build();
        testRepository.saveAll(List.of(test1, test2, test3));
        String[][] questions = {
                {
                        "Gia đình là gì?",
                        "Nhóm người sống chung một nơi",
                        "Nhóm người có quan hệ bạn bè",
                        "Tập hợp người gắn bó bằng hôn nhân, huyết thống hoặc nuôi dưỡng",
                        "Tổ chức xã hội tự do",
                        "Tập hợp người gắn bó bằng hôn nhân, huyết thống hoặc nuôi dưỡng"
                },
                {
                        "Gia đình được hình thành chủ yếu dựa trên mối quan hệ nào?",
                        "Quan hệ kinh tế",
                        "Quan hệ huyết thống, hôn nhân, nuôi dưỡng",
                        "Quan hệ xã hội",
                        "Quan hệ bạn bè",
                        "Quan hệ huyết thống, hôn nhân, nuôi dưỡng"
                },
                {
                        "Vì sao gia đình được coi là tế bào của xã hội?",
                        "Vì gia đình đông người",
                        "Vì gia đình tồn tại lâu đời",
                        "Vì gia đình là nền tảng hình thành xã hội",
                        "Vì gia đình quản lý xã hội",
                        "Vì gia đình là nền tảng hình thành xã hội"
                },
                {
                        "Chức năng nào sau đây KHÔNG phải của gia đình?",
                        "Chức năng sinh sản",
                        "Chức năng giáo dục",
                        "Chức năng kinh tế",
                        "Chức năng quân sự",
                        "Chức năng quân sự"
                },
                {
                        "Chức năng giáo dục của gia đình thể hiện ở đâu?",
                        "Dạy kiến thức khoa học",
                        "Hình thành nhân cách, đạo đức",
                        "Quản lý nhà nước",
                        "Phát triển công nghệ",
                        "Hình thành nhân cách, đạo đức"
                },
                {
                        "Vai trò quan trọng nhất của gia đình đối với trẻ em là gì?",
                        "Cung cấp tiền bạc",
                        "Giáo dục và chăm sóc",
                        "Quản lý thời gian",
                        "Kiểm soát hành vi",
                        "Giáo dục và chăm sóc"
                },
                {
                        "Gia đình hạnh phúc là gia đình có đặc điểm nào?",
                        "Giàu có",
                        "Đông con",
                        "Các thành viên yêu thương, tôn trọng nhau",
                        "Có địa vị xã hội cao",
                        "Các thành viên yêu thương, tôn trọng nhau"
                },
                {
                        "Yếu tố nào ảnh hưởng trực tiếp đến hạnh phúc gia đình?",
                        "Sự quan tâm và chia sẻ",
                        "Số lượng thành viên",
                        "Diện tích nhà ở",
                        "Nghề nghiệp",
                        "Sự quan tâm và chia sẻ"
                },
                {
                        "Trách nhiệm của cha mẹ đối với con cái là gì?",
                        "Kiểm soát mọi hành vi",
                        "Nuôi dưỡng, giáo dục và bảo vệ",
                        "Chỉ cung cấp tài chính",
                        "Quyết định thay con",
                        "Nuôi dưỡng, giáo dục và bảo vệ"
                },
                {
                        "Trách nhiệm của con cái đối với cha mẹ là gì?",
                        "Nghe lời tuyệt đối",
                        "Phụ thuộc hoàn toàn",
                        "Kính trọng, hiếu thảo",
                        "Quyết định thay cha mẹ",
                        "Kính trọng, hiếu thảo"
                },
                {
                        "Gia đình truyền thống thường có đặc điểm nào?",
                        "Ít thế hệ",
                        "Quan hệ bình đẳng tuyệt đối",
                        "Nhiều thế hệ cùng chung sống",
                        "Không có vai trò cha mẹ",
                        "Nhiều thế hệ cùng chung sống"
                },
                {
                        "Gia đình hiện đại có đặc điểm nào?",
                        "Nhiều thế hệ",
                        "Quan hệ áp đặt",
                        "Bình đẳng, dân chủ hơn",
                        "Phụ thuộc họ hàng",
                        "Bình đẳng, dân chủ hơn"
                },
                {
                        "Mối quan hệ trong gia đình cần dựa trên nguyên tắc nào?",
                        "Áp đặt",
                        "Lợi ích cá nhân",
                        "Yêu thương, tôn trọng",
                        "Quyền lực",
                        "Yêu thương, tôn trọng"
                },
                {
                        "Nguyên nhân phổ biến dẫn đến mâu thuẫn gia đình là gì?",
                        "Thiếu giao tiếp, chia sẻ",
                        "Nhà ở chật",
                        "Đông con",
                        "Khác nghề nghiệp",
                        "Thiếu giao tiếp, chia sẻ"
                },
                {
                        "Gia đình có vai trò gì trong việc giữ gìn văn hóa?",
                        "Phát minh văn hóa",
                        "Truyền lại giá trị, truyền thống",
                        "Quản lý xã hội",
                        "Kiểm soát con người",
                        "Truyền lại giá trị, truyền thống"
                },
                {
                        "Gia đình ảnh hưởng đến nhân cách cá nhân thông qua yếu tố nào?",
                        "Môi trường sống và giáo dục",
                        "Tiền bạc",
                        "Địa vị xã hội",
                        "Quyền lực",
                        "Môi trường sống và giáo dục"
                },
                {
                        "Trách nhiệm chung của các thành viên trong gia đình là gì?",
                        "Chỉ lo cho bản thân",
                        "Chia sẻ, giúp đỡ lẫn nhau",
                        "Phụ thuộc người khác",
                        "Tránh trách nhiệm",
                        "Chia sẻ, giúp đỡ lẫn nhau"
                },
                {
                        "Gia đình có vai trò gì đối với xã hội hiện đại?",
                        "Không còn quan trọng",
                        "Chỉ mang tính cá nhân",
                        "Là nền tảng ổn định xã hội",
                        "Chỉ phục vụ kinh tế",
                        "Là nền tảng ổn định xã hội"
                },
                {
                        "Biểu hiện của một gia đình hạnh phúc là gì?",
                        "Giàu có",
                        "Ít mâu thuẫn",
                        "Yêu thương, gắn bó",
                        "Có nhiều tài sản",
                        "Yêu thương, gắn bó"
                },
                {
                        "Điều quan trọng nhất để xây dựng gia đình hạnh phúc là gì?",
                        "Tiền bạc",
                        "Quyền lực",
                        "Sự yêu thương và tôn trọng",
                        "Địa vị xã hội",
                        "Sự yêu thương và tôn trọng"
                }
        };

        seedForTest(test1, questions);
        seedForTest(test2, questions);
        seedForTest(test3, questions);
    }

    public void seedForTest(Test test, String[][] questions){
        for (int i = 0; i < questions.length; i++) {
            var question = questions[i];
            var contentQuestion = question[0];
            var contentOpt1 = question[1];
            var contentOpt2 = question[2];
            var contentOpt3 = question[3];
            var contentOpt4 = question[4];
            var contentAnswer = question[5];
            var quest = QuestionTests.builder()
                    .orderNumber((i + 1) * 1L)
                    .content(contentQuestion)
                    .test(test)
                    .build();
            questionTestRepository.saveAll(List.of(quest));


            var op1 = QuestionOptions.builder()
                    .content(contentOpt1)
                    .questionTest(quest)
                    .isAnswer(contentOpt1.equals(contentAnswer) ? 1L : 0L)// 1 is answer, 0 is no answer
                    .build();
            var op2 = QuestionOptions.builder()
                    .content(contentOpt2)
                    .questionTest(quest)
                    .isAnswer(contentOpt2.equals(contentAnswer) ? 1L : 0L)// 1 is answer, 0 is no answer
                    .build();
            var op3 = QuestionOptions.builder()
                    .content(contentOpt3)
                    .questionTest(quest)
                    .isAnswer(contentOpt3.equals(contentAnswer) ? 1L : 0L)// 1 is answer, 0 is no answer
                    .build();
            var op4 = QuestionOptions.builder()
                    .content(contentOpt4)
                    .questionTest(quest)
                    .isAnswer(contentOpt4.equals(contentAnswer) ? 1L : 0L)// 1 is answer, 0 is no answer
                    .build();
            questionOptionRepository.saveAll(List.of(op1,op2,op3,op4));
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
                            .email(i == 4 ? "ChatBot@gmail.com" : genEmailByIndex(i))
                            .password(passwordEncoder.encode("ChatApp123456@"))
                            .fullName(i == 4 ? "CHAT BOT" : genFullNameByIndex(i))
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
    public String genEmailByIndex(int i){
        String[] emails = {
                "john.doe@gmail.com",
                "jane.smith@gmail.com",
                "mike.johnson@gmail.com",
                "sarah.wilson@gmail.com",
                "david.brown@gmail.com",
                "emma.davis@gmail.com",
                "chris.miller@gmail.com",
                "lisa.garcia@gmail.com",
                "ryan.martinez@gmail.com",
                "ashley.lopez@gmail.com",
                "kevin.anderson@gmail.com",
                "amanda.taylor@gmail.com",
                "brandon.thomas@gmail.com",
                "jessica.white@gmail.com",
                "tyler.harris@gmail.com",
                "nicole.clark@gmail.com",
                "austin.lewis@gmail.com",
                "stephanie.walker@gmail.com",
                "jordan.hall@gmail.com",
                "melissa.young@gmail.com",
                "smith.young@gmail.com",
        };

        if(i >= 0 && i < emails.length){
            return emails[i];
        }
        return "unknown@gmail.com";
    }

    public String genFullNameByIndex(int i){
        String[] fullNames = {
                "John Doe",
                "Jane Smith",
                "Mike Johnson",
                "Sarah Wilson",
                "David Brown",
                "Emma Davis",
                "Chris Miller",
                "Lisa Garcia",
                "Ryan Martinez",
                "Ashley Lopez",
                "Kevin Anderson",
                "Amanda Taylor",
                "Brandon Thomas",
                "Jessica White",
                "Tyler Harris",
                "Nicole Clark",
                "Austin Lewis",
                "Stephanie Walker",
                "Jordan Hall",
                "Melissa Young",
                "Melissa Smith",
                "Bob Young"
        };

        if(i >= 0 && i < fullNames.length){
            return fullNames[i];
        }
        return "Unknown User";
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
