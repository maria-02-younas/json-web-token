package com.task.Taskjwt.dao;

import com.task.Taskjwt.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}
