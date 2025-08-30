package org.yenln8.ChatApp.services.serviceImpl.category.implement;

import lombok.AllArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.NativeLanguageMiniDto;
import org.yenln8.ChatApp.entity.NativeLanguage;
import org.yenln8.ChatApp.entity.NativeLanguageLocale;
import org.yenln8.ChatApp.repository.NativeLanguageRepository;
import org.yenln8.ChatApp.services.serviceImpl.category.interfaces.GetListNativeLanguageService;

import java.util.List;

@Service
@AllArgsConstructor
public class GetListNativeLanguageServiceImpl implements GetListNativeLanguageService {
    private NativeLanguageRepository nativeLanguageRepository;

    @Override
    public BaseResponseDto call() {
        NativeLanguageLocale.LOCALE locale = LocaleContextHolder.getLocale().getLanguage().equals("en") ? NativeLanguageLocale.LOCALE.ENGLISH : NativeLanguageLocale.LOCALE.VIETNAMESE;

        List<NativeLanguage> nativeLanguages = this.nativeLanguageRepository.findAll();

        List<NativeLanguageMiniDto> data = nativeLanguages.stream().map(item -> NativeLanguageMiniDto.builder()
                .id(item.getId())
                .name(item.getNativeLanguageLocales()
                        .stream()
                        .filter(ele -> ele.getLocale().equals(locale))
                        .findFirst()
                        .map(NativeLanguageLocale::getName)
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
