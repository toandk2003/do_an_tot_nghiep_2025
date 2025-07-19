//package org.yenln8.ChatApp.common.schedule_task;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.yenln8.ChatApp.dto.base.BaseResponseDto;
//import org.yenln8.ChatApp.services.RedisService;
//
//@Component
//@Slf4j
//@AllArgsConstructor
//public class TestSchedule {
//    private RedisService redisService;
//    @Scheduled(fixedRate = 10000)
//    public void schedule() {
//        redisService.setKey("xinchao", BaseResponseDto.builder().success(true).build(),1);
//        for(int i = 0 ; i < 100; i++) {
//            System.out.println("i");
//        }
////        System.out.println(redisTemplate.opsForValue().get("lll"));
//    }
//}
