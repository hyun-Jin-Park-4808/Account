package com.example.Account.controller;

import com.example.Account.aop.AccountLock;
import com.example.Account.dto.CancelBalance;
import com.example.Account.dto.QueryTransactionResponse;
import com.example.Account.dto.TransactionDto;
import com.example.Account.dto.UseBalance;
import com.example.Account.exception.AccountException;
import com.example.Account.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 잔액 관련 컨트롤러
 * 1. 잔액 사용
 * 2. 잔액 사용 취소
 * 3. 거래 확인
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class transactionController {
    private final TransactionService transactionService;

    @PostMapping("/transaction/use") // 잔액 사용
    @AccountLock
    public UseBalance.Response useBalance (
            @Valid @RequestBody UseBalance.Request request
            ) throws InterruptedException {
        try {
            Thread.sleep(3000L);
            return UseBalance.Response.from(
                    transactionService.useBalance(request.getUserId(),
                            request.getAccountNumber(), request.getAmount())
            );
        } catch (AccountException e) { // 문제 발생하면
            log.error("Failed to use balance. "); // 에러 메시지 띄우고

            transactionService.saveFailedUseTransaction ( // 문제점 저장하기
                    request.getAccountNumber(),
                    request.getAmount()
            );
            throw e; // 어떤 에러인지 알려주기
        }
    }

    @PostMapping("/transaction/cancel")
    @AccountLock
    public CancelBalance.Response cancelBalance ( // 잔액 사용 취소
            @Valid @RequestBody CancelBalance.Request request
    ) {
        try {
            return CancelBalance.Response.from(
                    transactionService.cancelBalance(request.getTransactionId(),
                            request.getAccountNumber(), request.getAmount())
            );
        } catch (AccountException e) { // 문제 발생하면
            log.error("Failed to cancel balance. "); // 에러 메시지 띄우고

            transactionService.saveFailedCancelTransaction ( // 문제점 저장하기
                    request.getAccountNumber(),
                    request.getAmount()
            );
            throw e; // 어떤 에러인지 알려주기
        }
    }

    @GetMapping("/transaction/{transactionId}") // 거래 조회
    public QueryTransactionResponse queryTransaction (
            @PathVariable String transactionId) {
        return QueryTransactionResponse.from(
                transactionService.queryTransaction(transactionId)
        );
    }
}
