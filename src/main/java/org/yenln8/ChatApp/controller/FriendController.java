package org.yenln8.ChatApp.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yenln8.ChatApp.dto.base.BaseResponseDto;
import org.yenln8.ChatApp.dto.request.ExploreRequestDto;
import org.yenln8.ChatApp.services.interfaces.FriendService;
import org.yenln8.ChatApp.services.interfaces.UserService;

@RestController
@RequestMapping("/api/friends")
@AllArgsConstructor
@Validated
public class FriendController {
    private FriendService friendService;

    @GetMapping
    public ResponseEntity<?> getListFriend(ExploreRequestDto form) {
        return ResponseEntity.ok(this.friendService.getListFriend());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFriend(ExploreRequestDto form) {
        return ResponseEntity.ok(this.friendService.removeFriend());
    }

    @PostMapping
    public ResponseEntity<?> makeFriendRequest(@RequestBody @Valid ExploreRequestDto form) {
        return ResponseEntity.ok(this.friendService.makeFriendRequest());
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody @Valid ExploreRequestDto form) {
        return ResponseEntity.ok(this.friendService.acceptFriendRequest());
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectFriendRequest(@RequestBody @Valid ExploreRequestDto form) {
        return ResponseEntity.ok(this.friendService.rejectFriendRequest());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelFriendRequest(@RequestBody @Valid ExploreRequestDto form) {
        return ResponseEntity.ok(this.friendService.cancelFriendRequest());
    }

    @GetMapping("/friend-requests-sent")
    public ResponseEntity<?> getListFriendRequestIMade(ExploreRequestDto form) {
        return ResponseEntity.ok(this.friendService.getListFriendRequestIMade());
    }

    @GetMapping("/friend-requests-received")
    public ResponseEntity<?> getListFriendRequestIReceived(ExploreRequestDto form) {
        return ResponseEntity.ok(this.friendService.getListFriendRequestIReceived());
    }

}
