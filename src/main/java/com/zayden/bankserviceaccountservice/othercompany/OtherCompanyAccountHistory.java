package com.zayden.bankserviceaccountservice.othercompany;

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
@Table(name = "other_company_account_historys")
public class OtherCompanyAccountHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private boolean isCharge;

    @Column
    private BigInteger cost;

    @Column
    private String content;

    @Column
    private BigInteger amount;

    @Column(nullable = false)
    private LocalDateTime historyCreateTimeAt;

    @ManyToOne
    OtherCompanyAccount otherCompanyAccount;

}
