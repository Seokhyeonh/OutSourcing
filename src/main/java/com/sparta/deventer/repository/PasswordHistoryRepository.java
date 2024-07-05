package com.sparta.deventer.repository;

import com.sparta.deventer.entity.PasswordHistory;
import com.sparta.deventer.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {

    List<PasswordHistory> findByUserOrderByCreatedAtAsc(User user);
}