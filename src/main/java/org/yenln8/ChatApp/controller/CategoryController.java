package org.yenln8.ChatApp.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yenln8.ChatApp.services.AuthService;
import org.yenln8.ChatApp.services.CategoryService;

@RestController
@RequestMapping("/api/category")
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;

    @GetMapping("/learning-languages")
    public ResponseEntity<?> getListLearningLanguages() {
        return ResponseEntity.ok(this.categoryService.getListLeaningLanguage());
    }

    @GetMapping("/native-languages")
    public ResponseEntity<?> getListNativeLanguages() {
        return ResponseEntity.ok(this.categoryService.getListNativeLanguage());
    }
}
