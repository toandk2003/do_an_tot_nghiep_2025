package org.yenln8.ChatApp.services.serviceImpl.category.implement;

import lombok.AllArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.entity.LearningLanguage;
import org.yenln8.ChatApp.repository.LearningLanguageRepository;
import org.yenln8.ChatApp.services.serviceImpl.category.interfaces.GetListLearningLanguageService;

import java.util.List;

@Service
@AllArgsConstructor
public class GetListLearningLanguageServiceImpl  implements GetListLearningLanguageService {
    private LearningLanguageRepository learningLanguageRepository;

    @Override
    public BaseResponseDto call() {
        LearningLanguage.LOCALE locale = LocaleContextHolder.getLocale().getLanguage().equals("en") ? LearningLanguage.LOCALE.ENGLISH : LearningLanguage.LOCALE.VIETNAMESE  ;

        List<LearningLanguage> learningLanguages = this.learningLanguageRepository.findAllByLocale(locale);

        return BaseResponseDto.builder()
                .success(true)
                .message("success")
                .statusCode(200)
                .data(learningLanguages)
                .build();
    }
}
