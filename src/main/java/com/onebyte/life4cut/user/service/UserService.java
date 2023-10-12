package com.onebyte.life4cut.user.service;

import com.onebyte.life4cut.auth.dto.OAuthInfo;
import com.onebyte.life4cut.common.constants.S3Env;
import com.onebyte.life4cut.support.fileUpload.FileUploadResponse;
import com.onebyte.life4cut.support.fileUpload.FileUploader;
import com.onebyte.life4cut.support.fileUpload.MultipartFileUploadRequest;
import com.onebyte.life4cut.user.controller.dto.UpdateUserRequest;
import com.onebyte.life4cut.user.controller.dto.UserSignInRequest;
import com.onebyte.life4cut.user.domain.User;
import com.onebyte.life4cut.user.exception.UserNotFound;
import com.onebyte.life4cut.user.exception.UserNotUnique;
import com.onebyte.life4cut.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final FileUploader fileUploader;
  private final S3Env s3Env;

  public User save(UserSignInRequest request) {
    return userRepository.save(request.toEntity());
  }

  @Transactional(readOnly = true)
  public User findUser(long id) {
    return userRepository.findUser(id).orElseThrow(UserNotFound::new);
  }

  @Transactional(readOnly = true)
  public Optional<User> findUserByOAuthInfo(OAuthInfo oAuthInfo) {
    List<User> result = userRepository.findUserByOAuthInfo(oAuthInfo);
    if (result.size() > 1) {
      throw new UserNotUnique();
    }
    return result.stream().findAny();
  }

  @Transactional(readOnly = true)
  public User findUserByNickname(String nickname) {
    return userRepository.findUserByNickname(nickname).orElseThrow(UserNotFound::new);
  }

  @Transactional(readOnly = true)
  public Optional<User> findUserByNicknameForCheckingDuplication(String nickname) {
    return userRepository.findUserByNickname(nickname);
  }

  public void updateUser(long id, UpdateUserRequest request, MultipartFile image) {
    User user = userRepository.findUser(id).orElseThrow(UserNotFound::new);
    user.changeNickname(request.nickname());

    if (image == null) {
      return;
    }

    FileUploadResponse response =
        fileUploader.upload(MultipartFileUploadRequest.of(image, s3Env.bucket()));
    String imagePath = response.key();
    user.changeProfilePath(imagePath);
  }
}
