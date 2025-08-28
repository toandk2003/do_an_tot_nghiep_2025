package org.yenln8.ChatApp.services.serviceImpl.category.implement;

import lombok.AllArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.entity.LearningLanguage;
import org.yenln8.ChatApp.entity.LearningLanguageLocale;
import org.yenln8.ChatApp.entity.NativeLanguage;
import org.yenln8.ChatApp.entity.NativeLanguageLocale;
import org.yenln8.ChatApp.repository.LearningLanguageRepository;
import org.yenln8.ChatApp.services.serviceImpl.category.interfaces.GetListLearningLanguageService;

import java.util.List;

@Service
@AllArgsConstructor
public class GetListLearningLanguageServiceImpl  implements GetListLearningLanguageService {
    private LearningLanguageRepository learningLanguageRepository;

    @Override
    public BaseResponseDto call() {
        LearningLanguageLocale.LOCALE locale = LocaleContextHolder.getLocale().getLanguage().equals("en") ? LearningLanguageLocale.LOCALE.ENGLISH : LearningLanguageLocale.LOCALE.VIETNAMESE  ;

        List<LearningLanguage> learningLanguages = this.learningLanguageRepository.findAll();
        var data = learningLanguages.stream().map(item -> item.getLearningLanguageLocales().stream().filter(languageLocale -> languageLocale.getLocale().equals(locale) ));
        return BaseResponseDto.builder()
                .success(true)
                .message("success")
                .statusCode(200)
                .data(data)
                .build();
    }
}
