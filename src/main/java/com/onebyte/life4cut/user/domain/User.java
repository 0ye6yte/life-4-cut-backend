package com.onebyte.life4cut.user.domain;

import com.onebyte.life4cut.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "unique_user_oauth_index",
          columnNames = {
            "oauthType",
            "oauthId",
          }),
      @UniqueConstraint(
          name = "unique_user_nickname_index",
          columnNames = {"nickname"})
    })
public class User extends BaseEntity {

  @Column(unique = true, length = 30, nullable = false)
  private String nickname;

  @Column(length = 255)
  private String profilePath;

  @Column(nullable = false)
  private String email;

  @Column(length = 30)
  private String oauthType;

  @Column(length = 100)
  private String oauthId;

  @Column private LocalDateTime deletedAt;

  public void changeNickname(@NonNull String nickname) {
    if (nickname.equals("")) {
      return;
    }
    this.nickname = nickname;
  }

  public void changeProfilePath(@NonNull String profilePath) {
    if (profilePath.equals("")) {
      return;
    }
    this.profilePath = profilePath;
  }

  public void deleteSoftly(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }
}
