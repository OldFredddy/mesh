package com.chsbk.mesh.repository;

import com.chsbk.mesh.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            select user from User user
            where (:name is null or lower(user.name) like lower(concat(:name, '%')))
              and (:email is null or exists (
                  select emailData.id from EmailData emailData
                  where emailData.user = user and emailData.email = :email))
              and (:phone is null or exists (
                  select phoneData.id from PhoneData phoneData
                  where phoneData.user = user and phoneData.phone = :phone))
              and (:dateOfBirth is null or user.dateOfBirth > :dateOfBirth)
            """)
    Page<User> search(@Param("name") String name,
                      @Param("email") String email,
                      @Param("phone") String phone,
                      @Param("dateOfBirth") LocalDate dateOfBirth,
                      Pageable pageable);

    Optional<User> findByEmails_Email(String email);

    Optional<User> findByPhones_Phone(String phone);
}
