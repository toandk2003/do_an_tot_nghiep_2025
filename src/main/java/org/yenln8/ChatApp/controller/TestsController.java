package org.yenln8.ChatApp.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.yenln8.ChatApp.common.util.ContextService;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.base.PaginationResponseDto;
import org.yenln8.ChatApp.dto.other.CurrentUser;
import org.yenln8.ChatApp.dto.request.GetListTestRequestDto;
import org.yenln8.ChatApp.dto.request.QuestionDTO;
import org.yenln8.ChatApp.dto.request.TestDTOChange;
import org.yenln8.ChatApp.entity.QuestionHistory;
import org.yenln8.ChatApp.entity.QuestionOptions;
import org.yenln8.ChatApp.entity.TestHistory;
import org.yenln8.ChatApp.repository.*;
import org.yenln8.ChatApp.services.interfaces.CategoryService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TestsController {
    private CategoryService categoryService;
    private DifficultyTestRepository difficultyTestRepository;
    private TopicTestRepository topicTestRepository;
    private LearningLanguageRepository learningLanguageRepository;
    private TestRepository testRepository;
    private QuestionTestRepository questionTestRepository;
    private QuestionOptionRepository questionOptionRepository;
    private QuestionHistoryRepository questionHistoryRepository;
    private TestHistoryRepository testHistoryRepository;
    private UserRepository userRepository;

    @GetMapping("/tests")
    public ResponseEntity<?> getListTest(GetListTestRequestDto form) {
        var topicId = form.getTopicId();
        var learningLanguageId = form.getLearningLanguageId();
        var difficulty = form.getDifficulty();
        log.info(topicId + " " + learningLanguageId + " " + difficulty);

        var topic = topicId == null ? null : topicTestRepository.findById(topicId).orElseThrow(() -> new IllegalArgumentException("Topic not found with ID = " + topicId));
        var learningLanguage = learningLanguageId == null ? null : learningLanguageRepository.findById(learningLanguageId).orElseThrow(() -> new IllegalArgumentException("Learning language not found with ID = " + learningLanguageId));
        var difficultyEntity = difficulty == null ? null : topicTestRepository.findById(difficulty).orElseThrow(() -> new IllegalArgumentException("Difficulty not found with ID = " + difficulty));

        int currentPage = form.getCurrentPage().intValue();
        int pageSize = form.getPageSize().intValue();
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize);

        var testPageable = this.testRepository.getListTest(topicId, difficulty, learningLanguageId, pageRequest);

        var tests = testPageable.getContent();
        var result = tests.stream().map(test -> TestDTOChange.builder()
                .code(test.getLearningLanguage().getCode().name())
                .id(test.getId())
                .title(test.getTitle())
                .subtitle(test.getSubTitle())
                .difficulty(test.getDifficultyTests().getId())
                .topic(test.getTopicTest().getName())
                .questions(test.getQuestionTests().stream().map(questionTest -> QuestionDTO.builder()
                        .id(questionTest.getId())
                        .question(questionTest.getContent())
                        .options(questionTest.getQuestionOptions().stream().map(QuestionOptions::getContent).toList())
//                        .answer(questionTest.getQuestionOptions().stream().filter(option -> option.getIsAnswer().equals(1L)).findFirst().get().getContent())
                        .answer(null)
                        .myAnswer(null)
                        .build()).toList())
                .build()).toList();

        var data = PaginationResponseDto.of(testPageable, result);

        return ResponseEntity.ok(BaseResponseDto.builder()
                .success(true)
                .message("success")
                .statusCode(200)
                .data(data)
                .build());
    }

    @GetMapping("/tests/history")
    public ResponseEntity<?> getListTestHistory(GetListTestRequestDto form) {
        var topicId = form.getTopicId();
        var learningLanguageId = form.getLearningLanguageId();
        var difficulty = form.getDifficulty();
        log.info("learningLanguageId: " + learningLanguageId + " difficulty: " + difficulty);
        CurrentUser currentUser = ContextService.getCurrentUser();
        Long userId = currentUser.getId();

        var topic = topicId == null ? null : topicTestRepository.findById(topicId).orElseThrow(() -> new IllegalArgumentException("Topic not found with ID = " + topicId));
        var learningLanguage = learningLanguageId == null ? null : learningLanguageRepository.findById(learningLanguageId).orElseThrow(() -> new IllegalArgumentException("Learning language not found with ID = " + learningLanguageId));
        var difficultyEntity = difficulty == null ? null : topicTestRepository.findById(difficulty).orElseThrow(() -> new IllegalArgumentException("Difficulty not found with ID = " + difficulty));

        int currentPage = form.getCurrentPage().intValue();
        int pageSize = form.getPageSize().intValue();
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize);

        var testHistoryPageable = this.testHistoryRepository.getListTestHistory(userId, topicId, difficulty, learningLanguageId, pageRequest);

        var testHistories = testHistoryPageable.getContent();
        var result = testHistories.stream().map(testHistory -> {
            var test = testHistory.getTest();
            return TestDTOChange.builder()
                    .code(test.getLearningLanguage().getCode().name())
                    .id(test.getId())
                    .title(test.getTitle())
                    .subtitle(test.getSubTitle())
                    .difficulty(test.getDifficultyTests().getId())
                    .topic(test.getTopicTest().getName())
                    .questions(test.getQuestionTests().stream().map(questionTest -> {
                        var quest = testHistory.getQuestionHistories().stream().filter(questionHistory -> questionHistory.getQuestionTest().getId().equals(questionTest.getId())).findFirst().orElse(null);
                        if(quest == null) throw new IllegalArgumentException("Question not found with ID = " + questionTest.getId());

                        String myAnswer =quest.getQuestionOption() == null ? null : quest.getQuestionOption().getContent();
                        return QuestionDTO.builder()
                                .id(questionTest.getId())
                                .question(questionTest.getContent())
                                .options(questionTest.getQuestionOptions().stream().map(QuestionOptions::getContent).toList())
                                .answer(questionTest.getQuestionOptions().stream().filter(option -> option.getIsAnswer().equals(1L)).findFirst().get().getContent())
                                .myAnswer(myAnswer)
                                .explain(questionTest.getExplain())
                                .build();
                    }).toList())
                    .build();
        }).toList();

        var data = PaginationResponseDto.of(testHistoryPageable, result);

        return ResponseEntity.ok(BaseResponseDto.builder()
                .success(true)
                .message("success")
                .statusCode(200)
                .data(data)
                .build());
    }

    @PostMapping("/tests")
    @Transactional
    public ResponseEntity<?> submitTest(@RequestBody TestDTOChange form) {
        var testId = form.getId();
        CurrentUser currentUser = ContextService.getCurrentUser();
        Long userId = currentUser.getId();
        var user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with ID = " + userId));
        var test = testRepository.findById(testId).orElseThrow(() -> new IllegalArgumentException("Test not found with ID = " + testId));

        TestHistory testHistory = TestHistory.builder()
                .user(user)
                .test(test)
                .build();
        testHistoryRepository.save(testHistory);

        List<QuestionHistory> questionHistories = new ArrayList<>();

        form.getQuestions().forEach(question -> {
            var questionId = question.getId();
            log.info("questionId" + " " + questionId);

            var questionEntity = questionTestRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("Question not found with ID = " + questionId));
            log.info("1" );

//            log.info("questionEntity" + " " + questionEntity);

            var answerOption = questionEntity.getQuestionOptions().stream().filter(option -> option.getIsAnswer().equals(1L)).findFirst().get();
//            log.info("answerOption" + " " + answerOption);
            log.info("2" );

            var myAnswerOption = question.getMyAnswer() == null ? null : questionEntity.getQuestionOptions().stream().filter(option -> option.getContent().equals(question.getMyAnswer())).findFirst().get();
//            log.info("myAnswerOption" + " " + myAnswerOption);
            log.info("3" );

            var answer = answerOption.getContent();
            log.info("answer" + " " + answer);
            question.setAnswer(answer);
            question.setExplain(questionEntity.getExplain());

            questionHistories.add(QuestionHistory.builder()
                    .testHistory(testHistory)
                    .questionTest(questionEntity)
                    .questionOption(myAnswerOption)
                    .build());
        });

        questionHistoryRepository.saveAll((questionHistories));

        return ResponseEntity.ok(BaseResponseDto.builder()
                .success(true)
                .message("success")
                .statusCode(200)
                .data(form)
                .build());
    }
}
