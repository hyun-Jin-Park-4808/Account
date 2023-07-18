package com.example.Account.controller;

import com.example.Account.domain.Account;
import com.example.Account.dto.AccountInfo;
import com.example.Account.dto.CreateAccount;
import com.example.Account.dto.DeleteAccount;
import com.example.Account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/account")
    public CreateAccount.Response createAccount( // 계좌 생성
            @RequestBody @Valid CreateAccount.Request request
    ) {
        return CreateAccount.Response.from(
                accountService.createAccount(
                        request.getUserId(),
                        request.getInitialBalance()
                )
        );
    }
    @DeleteMapping("/account")
    public DeleteAccount.Response deleteAccount( // 계좌 해지
            @RequestBody @Valid DeleteAccount.Request request
    ) {
        return DeleteAccount.Response.from(
                accountService.deleteAccount(
                        request.getUserId(),
                        request.getAccountNumber()
                )
        );
    }

    @GetMapping("/account")
    public List<AccountInfo> getAccountsByUserId( // 계좌 조회
            @RequestParam("user_id") Long userId
    ) {
        return accountService.getAccountsByUserId(userId)
                .stream().map(accountDto
                        -> AccountInfo.builder()
                        .accountNumber(accountDto.getAccountNumber())
                        .balance(accountDto.getBalance()).build())
                .collect(Collectors.toList());
    }

    @GetMapping("/account/{id}") // id 담아서 아래 메서드 호출
    public Account getAccount(
            @PathVariable Long id) {
        return accountService.getAccount(id);
        // 입력한 id 계좌 AccountService에 등록한 메서드로 불러옴.
    }
}
