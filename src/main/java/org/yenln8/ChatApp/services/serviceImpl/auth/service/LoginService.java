package org.yenln8.ChatApp.services.serviceImpl.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.LoginRequestDto;

@Service
public class LoginService {
    public BaseResponseDto call(LoginRequestDto loginRequestDto) {
        validate(loginRequestDto);

        save(loginRequestDto);

        return BaseResponseDto.builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    private void validate(LoginRequestDto form){
        // Kiem tra dinh dang email, password

        // email co hop le ->> call api check

        // password co thoa man:
            // - it nhat 1 ki tu so
            // + it nhat 1 ki tu chu cai viet hoa
            // + it nhat 1 ki tu chu cai viet thuong
            // + it nhat 1 ki tu dac biet thuoc !@#$%^&*()_+-=[]{};\\':\"|,./<>?`~

        // Kiem lan login vuot qua muc cho phep

        // Lay ra thong tin user trong db, so sanh tai khoan + pass

        // Neu khong khop, tra ve 404 NotFound

        // Kiem tra tai khoan co bi khoa khong

        // Kiem tra tai khoan co bi BAN khong

        // Neu thanh cong, gen token sau do tra ve
        // day la logic  login + register, mai can tach ra
    }

    private void  save(LoginRequestDto form){
        return;
    }
}
