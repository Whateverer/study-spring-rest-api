package me.whatever.accounts;

import me.whatever.common.AppProperties;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {


    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AppProperties appProperties;

    @Test
    public void findByUserName() {
        // Given
        String password = "keesun";
        String username = "keesun@email.com";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(account);

        // When
        UserDetailsService userDetailsService = (UserDetailsService) accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(appProperties.getUserUsername());

        // Then
        assertThat(this.passwordEncoder.matches(appProperties.getUserPassword(), userDetails.getPassword())).isTrue();
    }

    @Test
    public void findByUsernameFail() {
        // Expected
        String username = "random@email.com";

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> accountService.loadUserByUsername(username));
        assertTrue(exception.getMessage().contains(username));
    }
}