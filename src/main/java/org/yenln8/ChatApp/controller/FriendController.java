package org.yenln8.ChatApp.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yenln8.ChatApp.dto.request.ExploreRequestDto;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestDto;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestReceivedRequestDto;
import org.yenln8.ChatApp.dto.request.GetListFriendRequestSentRequestDto;
import org.yenln8.ChatApp.services.interfaces.FriendService;

@RestController
@RequestMapping("/api/friends")
@AllArgsConstructor
@Validated
public class FriendController {
    private FriendService friendService;

    @GetMapping
    public ResponseEntity<?> getListFriend(GetListFriendRequestDto form) {
        return ResponseEntity.ok(this.friendService.getListFriend(form));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFriend(ExploreRequestDto form) {
        return ResponseEntity.ok(this.friendService.removeFriend());
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> makeFriendRequest(@PathVariable("id")
                                               @Min(1)
                                               @Max(Long.MAX_VALUE)
                                               @NotNull
                                               Long receiverId) {
        return ResponseEntity.ok(this.friendService.makeFriendRequest(receiverId));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable("id")
                                                     @Min(1)
                                                     @Max(Long.MAX_VALUE)
                                                     @NotNull
                                                     Long friendRequestId) {
        return ResponseEntity.ok(this.friendService.acceptFriendRequest());
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable("id")
                                                     @Min(1)
                                                     @Max(Long.MAX_VALUE)
                                                     @NotNull
                                                     Long friendRequestId) {
        return ResponseEntity.ok(this.friendService.rejectFriendRequest());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelFriendRequest(@PathVariable("id")
                                                 @Min(1)
                                                 @Max(Long.MAX_VALUE)
                                                 @NotNull
                                                 Long friendRequestId) {
        return ResponseEntity.ok(this.friendService.cancelFriendRequest(friendRequestId));
    }

    @GetMapping("/friend-requests-sent")
    public ResponseEntity<?> getListFriendRequestSent(GetListFriendRequestSentRequestDto form) {
        return ResponseEntity.ok(this.friendService.getListFriendRequestIMade(form));
    }

    @GetMapping("/friend-requests-received")
    public ResponseEntity<?> getListFriendRequestReceived(GetListFriendRequestReceivedRequestDto form) {
        return ResponseEntity.ok(this.friendService.getListFriendRequestIReceived(form));
    }

}
