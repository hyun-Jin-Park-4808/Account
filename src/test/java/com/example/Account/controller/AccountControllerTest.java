package com.example.Account.controller;

import com.example.Account.dto.AccountDto;
import com.example.Account.dto.CreateAccount;
import com.example.Account.dto.DeleteAccount;
import com.example.Account.exception.AccountException;
import com.example.Account.service.AccountService;
import com.example.Account.type.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class) // 특정 컨트롤러만 격리시켜서 유닛 단위로 테스트 가능
class AccountControllerTest {
    @MockBean // 다른 의존되고 있는 클래스는 목빈을 통해 자동으로 빈으로 등록
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Test
    void successCreateAccount () throws Exception {
        //given
        given(accountService.createAccount(anyLong(), anyLong())) // 목빈으로 받는 accountSevice에서 createAccount에 어떤 값이든 넣어서 호출하면
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build()); // 이런 식으로 응답을 주게 된다라는 모킹 작업
        //when
        //then
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateAccount.Request(1L, 100L)
                        )))
                .andExpect(status().isOk()) // isOk 라는 응답이 올것이라 예상한다.
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print()); // 전체 응답값 프린트하겠다.
    }


    @Test
        void successGetAccountsByUserId () throws Exception {
            //given
            List<AccountDto> accountDtos =
                    Arrays.asList(
                        AccountDto.builder()
                                .accountNumber("1234567890")
                                .balance(1000L).build(),
                        AccountDto.builder()
                                .accountNumber("1111111111")
                                .balance(2000L).build(),
                        AccountDto.builder()
                                .accountNumber("2222222222")
                                .balance(3000L).build()
                            );
            given(accountService.getAccountsByUserId(anyLong()))
                    .willReturn(accountDtos);

            //when
            //then
        mockMvc.perform(get("/account?user_id=1"))
                .andDo(print())
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
                .andExpect(jsonPath("$[0].balance").value(1000))
                .andExpect(jsonPath("$[1].accountNumber").value("1111111111"))
                .andExpect(jsonPath("$[1].balance").value(2000))
                .andExpect(jsonPath("$[2].accountNumber").value("2222222222"))
                .andExpect(jsonPath("$[2].balance").value(3000));
    }


    @Test
        void successDeleteAccount () throws Exception {
            //given
            given(accountService.deleteAccount(anyLong(), anyString())) // 목빈으로 받는 accountSevice에서 createAccount에 어떤 값이든 넣어서 호출하면
                    .willReturn(AccountDto.builder()
                            .userId(1L)
                            .accountNumber("1234567890")
                            .registeredAt(LocalDateTime.now())
                            .unRegisteredAt(LocalDateTime.now())
                            .build()); // 이런 식으로 응답을 주게 된다라는 모킹 작업
            //when
            //then
            mockMvc.perform(delete("/account")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                            new DeleteAccount.Request(1L, "1234567890")
                            )))
                    .andExpect(status().isOk()) // isOk 라는 응답이 올것이라 예상한다.
                    .andExpect(jsonPath("$.userId").value(1))
                    .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                    .andDo(print()); // 전체 응답값 프린트하겠다.
        }


    @Test
        void failGetAccount() throws Exception {
            //given => 목킹 하기
            given(accountService.getAccount(anyLong()))
                    .willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
            //when
            //then
            mockMvc.perform(get("/account/876")) // 컨트롤하려는 특정 url 호출
                    .andDo(print()) // 결과 띄워줌.
                    .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND")) // 결과 검증
                    .andExpect(jsonPath("$.errorMessage").value("계좌가 없습니다."))
                    .andExpect(status().isOk());
        }




}