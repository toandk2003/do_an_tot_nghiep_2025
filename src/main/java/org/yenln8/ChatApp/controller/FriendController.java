package org.yenln8.ChatApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yenln8.ChatApp.dto.request.*;
import org.yenln8.ChatApp.entity.User;
import org.yenln8.ChatApp.repository.UserRepository;
import org.yenln8.ChatApp.services.interfaces.FriendService;
import org.yenln8.ChatApp.services.serviceImpl.friend.implement.CheckFriendStatusServiceImpl;

@RestController
@RequestMapping("/api/friends")
@AllArgsConstructor
@Validated
public class FriendController {
    private FriendService friendService;
    private CheckFriendStatusServiceImpl checkFriendStatusService;
    private UserRepository userRepository;
    @GetMapping
    public ResponseEntity<?> getListFriend(GetListFriendRequestDto form) {
        return ResponseEntity.ok(this.friendService.getListFriend(form));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFriend(@PathVariable("id")
                                              @Min(1)
                                              @Max(Long.MAX_VALUE)
                                              @NotNull
                                              Long friendId) {
        return ResponseEntity.ok(this.friendService.removeFriend(friendId));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> makeFriendRequest(@PathVariable("id")
                                               Long receiverId,
                                               @RequestBody MakeFriendRequestDto form,
                                               HttpServletRequest request) {
        String email = form.getEmail();
        if(email != null) {
            User user = userRepository.findByEmailAndDeletedAtIsNull(email).orElse(null);
            if(user == null) throw new IllegalArgumentException("User with email " + email + " not found");

            return ResponseEntity.ok(this.friendService.makeFriendRequest(user.getId(), request));

        }
        return ResponseEntity.ok(this.friendService.makeFriendRequest(receiverId, request));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable("id")
                                                     @Min(1)
                                                     @Max(Long.MAX_VALUE)
                                                     @NotNull
                                                     Long friendRequestId,
                                                 HttpServletRequest request
    ) {
        return ResponseEntity.ok(this.friendService.acceptFriendRequest(friendRequestId,request));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable("id")
                                                     @Min(1)
                                                     @Max(Long.MAX_VALUE)
                                                     @NotNull
                                                     Long friendRequestId) {
        return ResponseEntity.ok(this.friendService.rejectFriendRequest(friendRequestId));
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

    @GetMapping("/check-status")
    public ResponseEntity<?> getListFriendRequestSent(CheckStatusFriendRequestDto form) {
        return ResponseEntity.ok(this.checkFriendStatusService.call(form));
    }

}
