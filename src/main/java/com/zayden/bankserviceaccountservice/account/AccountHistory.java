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
@Table(name = "kakaobank_account_historys")
public class AccountHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private boolean isCharge;

    @Column
    private String accountNumber;

    @Column
    private BigInteger cost;

    @Column
    private String content;

    @Column
    private BigInteger amount;

    @Column(nullable = false)
    private LocalDateTime historyCreateTimeAt;

    @ManyToOne
    Account account;
}
