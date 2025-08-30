package org.yenln8.ChatApp.services.serviceImpl.category.implement;

import lombok.AllArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.LearningLanguageMiniDto;
import org.yenln8.ChatApp.entity.LearningLanguage;
import org.yenln8.ChatApp.entity.LearningLanguageLocale;
import org.yenln8.ChatApp.repository.LearningLanguageRepository;
import org.yenln8.ChatApp.services.serviceImpl.category.interfaces.GetListLearningLanguageService;

import java.util.List;

@Service
@AllArgsConstructor
public class GetListLearningLanguageServiceImpl implements GetListLearningLanguageService {
    private LearningLanguageRepository learningLanguageRepository;

    @Override
    public BaseResponseDto call() {
        LearningLanguageLocale.LOCALE locale = LocaleContextHolder.getLocale().getLanguage().equals("en") ? LearningLanguageLocale.LOCALE.ENGLISH : LearningLanguageLocale.LOCALE.VIETNAMESE;

        List<LearningLanguage> learningLanguages = this.learningLanguageRepository.findAll();

        List<LearningLanguageMiniDto> data = learningLanguages.stream().map(item -> LearningLanguageMiniDto.builder()
                .id(item.getId())
                .name(item.getLearningLanguageLocales()
                        .stream()
                        .filter(ele -> ele.getLocale().equals(locale))
                        .findFirst()
                        .map(LearningLanguageLocale::getName)
                        .get()
                )
                .build()).toList();

        return BaseResponseDto.builder()
                .success(true)
                .message("success")
                .statusCode(200)
                .data(data)
                .build();
    }
}
