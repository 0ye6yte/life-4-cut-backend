package com.onebyte.life4cut.user.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.onebyte.life4cut.common.annotation.WithCustomMockUser;
import com.onebyte.life4cut.common.controller.ControllerTest;
import com.onebyte.life4cut.common.exception.ErrorCode;
import com.onebyte.life4cut.user.controller.dto.UpdateUserRequest;
import com.onebyte.life4cut.user.controller.dto.UserDuplicateResponse;
import com.onebyte.life4cut.user.controller.dto.UserFindResponse;
import com.onebyte.life4cut.user.domain.User;
import com.onebyte.life4cut.user.exception.UserNotFound;
import com.onebyte.life4cut.user.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest({UserController.class})
class UserControllerTest extends ControllerTest {

  @MockBean UserService userService;

  private static final String baseUri = "/api/v1/users";

  @WithMockUser
  @DisplayName("고유한 유저 닉네임으로 유저를 조회한다.")
  @Test
  void findUser() throws Exception {
    // given
    String nickname = "bell";
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("nickname", nickname);

    User user =
        User.builder()
            .id(1L)
            .nickname(nickname)
            .oauthId("12345")
            .oauthType("kakao")
            .profilePath("profilePath")
            .email("bell@gmail.com")
            .build();
    UserFindResponse result = UserFindResponse.of(user);

    // when
    Mockito.when(userService.findUserByNickname(nickname)).thenReturn(user);

    // then
    ResultActions actions = performGet(baseUri, params);
    actions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.data", equalTo(asParsedJson(result))))
        .andDo(
            document(
                "{class_name}/{method_name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("USER API")
                        .description("닉네임 유저 조회 API")
                        .queryParameters(parameterWithName("nickname").description("유저 닉네임"))
                        .requestFields()
                        .responseFields(
                            fieldWithPath("message").type(STRING).description("응답 메시지"),
                            fieldWithPath("data").type(OBJECT).description("데이터"),
                            fieldWithPath("data.userId").type(NUMBER).description("유저 ID"),
                            fieldWithPath("data.nickname").type(STRING).description("유저 닉네임"),
                            fieldWithPath("data.email").type(STRING).description("유저 이메일"),
                            fieldWithPath("data.profilePath")
                                .type(STRING)
                                .description("유저 프로필 사진 파일 경로"))
                        .build())))
        .andDo(print());
  }

  @WithMockUser
  @DisplayName("존재하지 않는 유저 조회")
  @Test
  void findUserNotFound() throws Exception {
    // given
    String nickname = "bell";
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("nickname", nickname);

    // when
    Mockito.when(userService.findUserByNickname(nickname)).thenThrow(new UserNotFound());

    // then
    ResultActions actions = performGet(baseUri, params);
    actions
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
        .andDo(
            document(
                "{class_name}/{method_name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("USER API")
                        .description("닉네임 유저 조회 API")
                        .requestFields()
                        .responseFields(
                            fieldWithPath("message").type(STRING).description("응답 메시지"),
                            fieldWithPath("data").type(OBJECT).description("데이터"))
                        .build())))
        .andDo(print());
  }

  @WithCustomMockUser
  @DisplayName("@AuthenticationPrincipal을 통해 본인을 조회한다.")
  @Test
  void findMe() throws Exception {
    // given
    String nickname = "bell";
    User user =
        User.builder()
            .id(1L)
            .nickname(nickname)
            .oauthId("12345")
            .oauthType("kakao")
            .profilePath("profilePath")
            .email("bell@gmail.com")
            .build();
    UserFindResponse result = UserFindResponse.of(user);

    // when
    Mockito.when(userService.findUser(anyLong())).thenReturn(user);

    // then
    ResultActions actions = performGet(baseUri + "/me");
    actions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.data", equalTo(asParsedJson(result))))
        .andDo(
            document(
                "{class_name}/{method_name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("USER API")
                        .description("마이페이지 정보 조회 API")
                        .requestFields()
                        .responseFields(
                            fieldWithPath("message").type(STRING).description("응답 메시지"),
                            fieldWithPath("data").type(OBJECT).description("데이터"),
                            fieldWithPath("data.userId").type(NUMBER).description("유저 ID"),
                            fieldWithPath("data.nickname").type(STRING).description("유저 닉네임"),
                            fieldWithPath("data.email").type(STRING).description("유저 이메일"),
                            fieldWithPath("data.profilePath")
                                .type(STRING)
                                .description("유저 프로필 사진 파일 경로"))
                        .build())))
        .andDo(print());
  }

  @WithMockUser
  @DisplayName("존재하는 유저 닉네임인지 확인하면 중복된다는 정보를 받는다.")
  @Test
  void checkDuplicatedNickname() throws Exception {
    // given
    String nickname = "bell";
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("nickname", nickname);

    Optional<User> user =
        Optional.of(
            User.builder()
                .id(1L)
                .nickname(nickname)
                .oauthId("12345")
                .oauthType("kakao")
                .profilePath("profilePath")
                .email("bell@gmail.com")
                .build());
    UserDuplicateResponse result = UserDuplicateResponse.of(user);

    // when
    Mockito.when(userService.findUserByNicknameForCheckingDuplication(nickname)).thenReturn(user);

    // then
    ResultActions actions = performGet(baseUri + "/duplicate", params);
    actions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.data", equalTo(asParsedJson(result))))
        .andDo(
            document(
                "{class_name}/{method_name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("USER API")
                        .description("닉네임으로 유저 조회 API")
                        .queryParameters(parameterWithName("nickname").description("유저 닉네임"))
                        .requestFields()
                        .responseFields(
                            fieldWithPath("message").type(STRING).description("응답 메시지"),
                            fieldWithPath("data.isDuplicated")
                                .type(BOOLEAN)
                                .description("유저 ID")
                                .description("유저 프로필 사진 파일 경로"))
                        .build())))
        .andDo(print());
  }

  @WithCustomMockUser
  @DisplayName("User의 닉네임과 프로필 이미지 수정을 할 수 있다")
  @Test
  void updateMe() throws Exception {
    // given
    String nickname = "bell";
    String newNickname = "newBell";
    UpdateUserRequest request = new UpdateUserRequest(newNickname);

    MockMultipartFile image =
        new MockMultipartFile("image", "image.png", MediaType.IMAGE_PNG_VALUE, "image".getBytes());
    MockMultipartFile data =
        new MockMultipartFile(
            "data",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsString(request).getBytes());

    // when
    // 반환 값이 없기 때문에 불필요

    // then
    ResultActions actions =
        mockMvc.perform(
            ((MockMultipartHttpServletRequestBuilder)
                    MockMvcRequestBuilders.multipart(HttpMethod.PATCH, baseUri + "/me")
                        .requestAttr(
                            RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
                            baseUri + "/me"))
                .file(image)
                .file(data));

    actions
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "{class_name}/{method_name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParameters.builder()
                        .tag("USER API")
                        .description("유저정보 수정 API")
                        .summary("유저정보를 수정한다.")
                        .build()),
                requestPartFields(
                    "data",
                    fieldWithPath("nickname")
                        .type(JsonFieldType.STRING)
                        .description("닉네임")
                        .optional()),
                requestPartBody("image")));
  }
}
