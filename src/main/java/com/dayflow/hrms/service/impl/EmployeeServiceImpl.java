package com.dayflow.hrms.service.impl;

import com.dayflow.hrms.dto.employee.EmployeeRequest;
import com.dayflow.hrms.dto.employee.EmployeeResponse;
import com.dayflow.hrms.dto.employee.EmployeeUpdateRequest;
import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.entity.User;
import com.dayflow.hrms.exception.BadRequestException;
import com.dayflow.hrms.exception.ResourceNotFoundException;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.repository.UserRepository;
import com.dayflow.hrms.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Override
    public EmployeeResponse getMyProfile(String email) {
        Employee employee = getEmployeeByEmail(email);
        return toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateMyProfile(String email, EmployeeUpdateRequest request) {
        Employee employee = getEmployeeByEmail(email);
        if (request.getPhone() != null) employee.setPhone(request.getPhone());
        if (request.getAddress() != null) employee.setAddress(request.getAddress());
        if (request.getProfilePictureUrl() != null) employee.setProfilePictureUrl(request.getProfilePictureUrl());
        if (request.getEmergencyContactName() != null) employee.setEmergencyContactName(request.getEmergencyContactName());
        if (request.getEmergencyContactPhone() != null) employee.setEmergencyContactPhone(request.getEmergencyContactPhone());
        return toResponse(employeeRepository.save(employee));
    }

    @Override
    public Page<EmployeeResponse> getAllEmployees(String department, String employeeId, Pageable pageable) {
        return employeeRepository.findAllActive(department, employeeId, pageable).map(e -> toResponse(e));
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(String employeeId, EmployeeRequest request) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for employeeId: " + employeeId));

        if (employeeRepository.findByUser(user).isPresent()) {
            throw new BadRequestException("Employee profile already exists for this user");
        }

        Employee employee = Employee.builder()
                .user(user)
                .employeeId(user.getEmployeeId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .dateOfJoining(request.getDateOfJoining())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .employmentType(request.getEmploymentType())
                .profilePictureUrl(request.getProfilePictureUrl())
                .bankAccountNumber(request.getBankAccountNumber())
                .bankName(request.getBankName())
                .ifscCode(request.getIfscCode())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .build();

        log.info("Creating employee profile for: {}", employeeId);
        return toResponse(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = findById(id);
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setAddress(request.getAddress());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setDateOfJoining(request.getDateOfJoining());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setEmploymentType(request.getEmploymentType());
        employee.setProfilePictureUrl(request.getProfilePictureUrl());
        employee.setBankAccountNumber(request.getBankAccountNumber());
        employee.setBankName(request.getBankName());
        employee.setIfscCode(request.getIfscCode());
        employee.setEmergencyContactName(request.getEmergencyContactName());
        employee.setEmergencyContactPhone(request.getEmergencyContactPhone());
        log.info("Updated employee: {}", id);
        return toResponse(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = findById(id);
        employee.setActive(false);
        employeeRepository.save(employee);
        log.info("Deactivated employee: {}", id);
    }

    private Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    private Employee getEmployeeByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return employeeRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for: " + email));
    }

    public static EmployeeResponse toResponse(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .employeeId(e.getEmployeeId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .email(e.getEmail())
                .phone(e.getPhone())
                .address(e.getAddress())
                .dateOfBirth(e.getDateOfBirth())
                .dateOfJoining(e.getDateOfJoining())
                .department(e.getDepartment())
                .designation(e.getDesignation())
                .employmentType(e.getEmploymentType())
                .profilePictureUrl(e.getProfilePictureUrl())
                .emergencyContactName(e.getEmergencyContactName())
                .emergencyContactPhone(e.getEmergencyContactPhone())
                .active(e.isActive())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
