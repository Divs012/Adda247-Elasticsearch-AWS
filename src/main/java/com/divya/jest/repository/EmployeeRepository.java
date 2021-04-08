package com.divya.jest.repository;

import com.divya.jest.model.Employee;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EmployeeRepository extends ElasticsearchRepository<Employee, String> {
    List<Employee> findByName(String Name);
}