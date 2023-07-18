package com.example.Account.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration // 자동 빈으로 등록되는 클래스
@EnableJpaAuditing // DB에 annotation 붙은 변수들 자동으로 저장해줌.
public class JpaAuditingConfiguration {
}
