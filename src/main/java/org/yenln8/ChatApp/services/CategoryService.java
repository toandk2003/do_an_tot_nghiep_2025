package org.yenln8.ChatApp.services;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;

public interface CategoryService {
    BaseResponseDto getListLeaningLanguage();

    BaseResponseDto getListNativeLanguage();
}
