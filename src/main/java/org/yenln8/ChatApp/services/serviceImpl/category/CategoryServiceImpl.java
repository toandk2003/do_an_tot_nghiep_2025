package org.yenln8.ChatApp.services.serviceImpl.category;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.services.CategoryService;
import org.yenln8.ChatApp.services.serviceImpl.category.interfaces.GetListLearningLanguageService;
import org.yenln8.ChatApp.services.serviceImpl.category.interfaces.GetListNativeLanguageService;

@Service
@AllArgsConstructor
public class CategoryServiceImpl  implements CategoryService {
    private GetListNativeLanguageService getListNativeLanguageService;
    private GetListLearningLanguageService  getListLearningLanguageService;

    @Override
    public BaseResponseDto getListLeaningLanguage() {
        return this.getListLearningLanguageService.call();
    }

    @Override
    public BaseResponseDto getListNativeLanguage() {
        return this.getListNativeLanguageService.call();
    }
}
