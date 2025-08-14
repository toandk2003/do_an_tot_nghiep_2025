package org.yenln8.ChatApp.services.serviceImpl.auth.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.yenln8.ChatApp.dto.S3.UploadFileRequestDto;
import org.yenln8.ChatApp.dto.S3.UploadFileResponseDto;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.OnBoardingRequestDto;

public interface GeneratePresignedURLOnboarding {
    BaseResponseDto call(UploadFileRequestDto form, HttpServletRequest request);
}
