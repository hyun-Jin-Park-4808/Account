package com.example.Account.service;

import com.example.Account.exception.AccountException;
import com.example.Account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    private final RedissonClient redissonClient;

    public void lock(String accountNUmber) { // 자물쇠 얻는 클래스
        RLock lock = redissonClient.getLock(getLockKey(accountNUmber));
        log.debug("Trying lock for accountNumber : {}", accountNUmber);

         try { // 위에서 가져온 락으로 스핀락을 시도
             boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS);
             // 최대 1초 동안 기다리면서 이 락을 찾아보고, 락을 찾으면 15초 동안 갖고있다가 풀어주겠다.
             if (!isLock) {
                 log.error("==Lock acquisition failed");
                 throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
             }
         } catch (AccountException e) {
             throw e;
         } catch (Exception e) {
             log.error("Redis lock failed", e);
         }
    }

    public void unlock(String accountNumber) {
        log.debug("Unlock for accountNumber: {} ", accountNumber);
        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }

    private static String getLockKey(String accountNUmber) {
        return "ACLK:" + accountNUmber;
    }
}
