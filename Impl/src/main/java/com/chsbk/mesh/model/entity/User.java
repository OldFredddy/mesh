package com.chsbk.mesh.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(length = 500, nullable = false) private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<EmailData> emails = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PhoneData> phones = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = false)
    private Account account;

    public void addEmail(EmailData e){
        emails.add(e);
        e.setUser(this);
    }
    public void addPhone(PhoneData p){
        phones.add(p);
        p.setUser(this);
    }

}
