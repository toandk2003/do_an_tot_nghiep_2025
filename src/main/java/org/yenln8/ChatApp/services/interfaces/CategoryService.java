package org.yenln8.ChatApp.services.interfaces;

import org.yenln8.ChatApp.dto.base.BaseResponseDto;

public interface CategoryService {
    BaseResponseDto getListLeaningLanguage();

    BaseResponseDto getListNativeLanguage();
}
