package com.library.borrowing.repository;

import com.library.borrowing.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Member entities.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    
    /**
     * Find a member by email.
     */
    Optional<Member> findByEmail(String email);
}

