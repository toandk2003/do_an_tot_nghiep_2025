package org.yenln8.ChatApp.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.repository.DifficultyTestRepository;
import org.yenln8.ChatApp.repository.TopicTestRepository;
import org.yenln8.ChatApp.services.interfaces.CategoryService;

@RestController
@RequestMapping("/api/category")
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;
    private DifficultyTestRepository difficultyTestRepository;
    private TopicTestRepository topicTestRepository;

    @GetMapping("/learning-languages")
    public ResponseEntity<?> getListLearningLanguages() {
        return ResponseEntity.ok(this.categoryService.getListLeaningLanguage());
    }

    @GetMapping("/native-languages")
    public ResponseEntity<?> getListNativeLanguages() {
        return ResponseEntity.ok(this.categoryService.getListNativeLanguage());
    }

    @GetMapping("/topic-tests")
    public ResponseEntity<?> getTopicTests() {
        var res = topicTestRepository.findAll();
        return ResponseEntity.ok( BaseResponseDto.builder()
                .success(true)
                .message("success")
                .statusCode(200)
                .data(res)
                .build());
    }

    @GetMapping("/difficulty-tests")
    public ResponseEntity<?> getDifficult() {
        var res = difficultyTestRepository.findAll();
        return ResponseEntity.ok( BaseResponseDto.builder()
                .success(true)
                .message("success")
                .statusCode(200)
                .data(res)
                .build());
    }
}
