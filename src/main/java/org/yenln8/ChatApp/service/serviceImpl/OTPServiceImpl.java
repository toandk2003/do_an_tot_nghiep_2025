//package org.yenln8.ChatApp.service.serviceImpl;
//
//import org.yenln8.ChatApp.dto.VerifyOTPResponseDto;
//import org.yenln8.ChatApp.service.OTPService;
//
//import java.util.Random;
//
//public class OTPServiceImpl implements OTPService {
//    @Override
//    public String generateOTP() {
//        Random random = new Random();
//        int otp = 100000 + random.nextInt(900000);
//        return String.valueOf(otp);
//    }
//
//    @Override
//    public VerifyOTPResponseDto verify(String otp) {
//        return null;
//    }
//}
