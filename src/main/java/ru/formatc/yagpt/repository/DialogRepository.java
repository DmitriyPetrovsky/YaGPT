package ru.formatc.yagpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.formatc.yagpt.model.Dialog;

import java.util.List;
import java.util.Optional;

@Repository
public interface DialogRepository extends JpaRepository<Dialog, Integer> {

    Optional<Dialog> findFirstByUserIdOrderByIdDesc(long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Dialog d SET d.isNewContext = true WHERE d.id = :id")
    void setNewContextFlag(@Param("id") int id);

    List<Dialog> findByUserIdAndContextIdOrderByIdAsc(long userId, long contextId);

}
