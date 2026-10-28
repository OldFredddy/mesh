package com.chsbk.mesh;

import com.chsbk.mesh.model.entity.Account;
import com.chsbk.mesh.model.entity.User;
import com.chsbk.mesh.repository.AccountRepository;
import com.chsbk.mesh.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TransferIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("mesh")
            .withUsername("mesh")
            .withPassword("mesh");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl() + "?currentSchema=mesh");
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.cache.type", () -> "simple");
        registry.add("mesh.cache.redis.enabled", () -> "false");
        registry.add("mesh.scheduler.enabled", () -> "false");
        registry.add("mesh.security.jwt.secret", () -> "mesh-integration-test-secret-with-32-plus-chars");
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Test
    void loginAndTransferByPhoneChangeBalances() throws Exception {
        String setCookieHeader = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login":"ivan-megaman@gmail.com","password":"mesh-demo-123"}
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getHeader("Set-Cookie");

        assertThat(setCookieHeader).isNotNull();
        String jwt = setCookieHeader.split(";", 2)[0].substring("AuthToken=".length());

        User sender = userRepository.findByEmails_Email("ivan-megaman@gmail.com").orElseThrow();
        User receiver = userRepository.findByPhones_Phone("711111111111").orElseThrow();
        BigDecimal senderBalanceBefore = accountRepository.findByUser_Id(sender.getId()).orElseThrow().getBalance();
        BigDecimal receiverBalanceBefore = accountRepository.findByUser_Id(receiver.getId()).orElseThrow().getBalance();

        mockMvc.perform(post("/transfers")
                        .cookie(new Cookie("AuthToken", jwt))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"to":"711111111111","amount":40.00}
                                """))
                .andExpect(status().isOk());

        Account senderAccount = accountRepository.findByUser_Id(sender.getId()).orElseThrow();
        Account receiverAccount = accountRepository.findByUser_Id(receiver.getId()).orElseThrow();
        assertThat(senderAccount.getBalance()).isEqualByComparingTo(senderBalanceBefore.subtract(new BigDecimal("40.00")));
        assertThat(receiverAccount.getBalance()).isEqualByComparingTo(receiverBalanceBefore.add(new BigDecimal("40.00")));
    }
}
