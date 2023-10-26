package com.onebyte.life4cut.user.controller;

import com.onebyte.life4cut.auth.dto.CustomUserDetails;
import com.onebyte.life4cut.common.web.ApiResponse;
import com.onebyte.life4cut.user.controller.dto.UpdateUserRequest;
import com.onebyte.life4cut.user.controller.dto.UserDuplicateResponse;
import com.onebyte.life4cut.user.controller.dto.UserFindResponse;
import com.onebyte.life4cut.user.domain.User;
import com.onebyte.life4cut.user.service.UserService;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@RestController
public class UserController {

  private final UserService userService;

  @GetMapping
  public ApiResponse<UserFindResponse> findUser(@RequestParam("nickname") String nickname) {
    User result = userService.findUserByNickname(nickname);
    return ApiResponse.OK(UserFindResponse.of(result));
  }

  @GetMapping("/me")
  public ApiResponse<UserFindResponse> findMe(@AuthenticationPrincipal CustomUserDetails user) {
    User result = userService.findUser(user.getUserId());
    return ApiResponse.OK(UserFindResponse.of(result));
  }

  @PatchMapping("/me")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void updateMe(
      @AuthenticationPrincipal CustomUserDetails user,
      @Valid @RequestPart(value = "data", required = false) UpdateUserRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    userService.updateUser(user.getUserId(), request, image);
  }

  @GetMapping("/duplicate")
  public ApiResponse<UserDuplicateResponse> checkDuplicatedNickname(
      @RequestParam("nickname") String nickname) {
    Optional<User> result = userService.findUserByNicknameForCheckingDuplication(nickname);
    return ApiResponse.OK(UserDuplicateResponse.of(result));
  }
}
