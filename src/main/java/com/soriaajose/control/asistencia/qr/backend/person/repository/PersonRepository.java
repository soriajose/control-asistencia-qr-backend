package com.soriaajose.control.asistencia.qr.backend.person.repository;

import com.soriaajose.control.asistencia.qr.backend.person.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByEmail(String name);

    boolean existsByEmail(String email);

}
