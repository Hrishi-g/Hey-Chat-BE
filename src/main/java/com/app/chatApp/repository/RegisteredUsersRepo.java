package com.app.chatApp.repository;

import org.springframework.stereotype.Repository;

import com.app.chatApp.vo.RegisteredUsers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface RegisteredUsersRepo extends JpaRepository<RegisteredUsers, Long> {

    @Query("SELECT u.mblNo FROM RegisteredUsers u where u.mblNo=:mblNo")
    Optional<String> findByMblNo(String mblNo);

    @Query("SELECT u FROM RegisteredUsers u WHERE u.mblNo = :mblNo")
    Optional<RegisteredUsers> findUserByMblNo(String mblNo);

    List<RegisteredUsers> findByMblNoIn(Collection<String> mblNos);

}
