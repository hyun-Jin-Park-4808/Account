package com.example.Account.repository;

import com.example.Account.domain.Account;
import com.example.Account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // 디비에 접근하기 위한 설정 JpaRepository 상속 받기
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findFirstByOrderByIdDesc();

    Integer countByAccountUser(AccountUser accountUser);
    // Account 클래스 안에 AccountUser거 accountNumber에 대해
    // ManyToOne 관계로 가지고 있기 때문에 AccountUser 한 명이 n개의 계좌를 가질 수 있음.

    Optional<Account> findByAccountNumber(String AccountNumber);

    List<Account> findByAccountUser(AccountUser accountUser);
}
