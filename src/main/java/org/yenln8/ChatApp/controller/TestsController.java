package org.yenln8.ChatApp.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.base.PaginationResponseDto;
import org.yenln8.ChatApp.dto.request.GetListTestRequestDto;
import org.yenln8.ChatApp.dto.request.QuestionDTO;
import org.yenln8.ChatApp.dto.request.TestDTOChange;
import org.yenln8.ChatApp.dto.response.GetListFriendResponseDto;
import org.yenln8.ChatApp.dto.response.GetProfileResponseDto;
import org.yenln8.ChatApp.entity.Friend;
import org.yenln8.ChatApp.entity.QuestionOptions;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.*;
import org.yenln8.ChatApp.services.interfaces.CategoryService;

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

    @GetMapping("/tests")
    public ResponseEntity<?> getListTest(GetListTestRequestDto form) {
        var topicId =  form.getTopicId();
        var learningLanguageId =  form.getLearningLanguageId();
        var difficulty =   form.getDifficulty();
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
                        .answer(questionTest.getQuestionOptions().stream().filter(option -> option.getIsAnswer().equals(1L)).findFirst().get().getContent())
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
}
