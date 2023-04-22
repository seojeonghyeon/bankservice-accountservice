package com.zayden.bankserviceaccountservice.account;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "kakaobank_accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = false)
    private String userId;

    @Column(nullable = false)
    private String financialCompany;

    @Column(nullable = false, length = 120, unique = true)
    private String accountNumber;

    @Column
    private BigInteger balance;

    @Column(nullable = false)
    private String accountStatus;

    @Column(nullable = false)
    private LocalDateTime registAt;

    @Column(nullable = false)
    private boolean isEnabled;

    @Column(nullable = false)
    private boolean isMainAccount;
}
