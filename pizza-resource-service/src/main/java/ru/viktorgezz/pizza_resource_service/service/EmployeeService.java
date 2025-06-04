package ru.viktorgezz.pizza_resource_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.viktorgezz.pizza_resource_service.dto.rq.RegisterEmployeeDto;
import ru.viktorgezz.pizza_resource_service.dto.rq.EmployeeUpdateDto;
import ru.viktorgezz.pizza_resource_service.model.Employee;
import ru.viktorgezz.pizza_resource_service.util.Role;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);
    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;

    private final RowMapper<Employee> employeeMapper = (rs, rowNum) -> {
        Employee employee = new Employee();
        employee.setIdUser(rs.getLong("id_user"));
        employee.setIdEmployee(rs.getLong("id"));
        employee.setUsername(rs.getString("username"));
        employee.setPassword(rs.getString("password"));
        employee.setEnabled(rs.getBoolean("enabled"));
        employee.setRole(Role.valueOf(rs.getString("role")));
        employee.setEmail(rs.getString("email"));
        employee.setJobDescription(rs.getString("job_description"));
        employee.setIdRestaurant(rs.getLong("id_restaurant"));
        return employee;
    };

    @Autowired
    public EmployeeService(JdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Employee> getAllEmployees() {
        final String sql = """
                SELECT e.*, u.* 
                FROM Employee e 
                JOIN _user u ON e.id_user = u.id
                """;
        List<Employee> employees = jdbc.query(sql, employeeMapper);
        log.info("Retrieved {} employees", employees.size());
        return employees;
    }

    @Transactional
    public void hireEmployee(RegisterEmployeeDto employeeDto, Role role) {
        final String userSql = """
                INSERT INTO _user (username, password, enabled, role, email) 
                VALUES (?, ?, true, ?::role, ?)
                RETURNING id
                """;
        final String employeeSql = """
                INSERT INTO Employee (job_description, id_user, id_restaurant) 
                VALUES (?, ?, ?)
                """;

        Long userId = jdbc.queryForObject(
                userSql,
                Long.class,
                employeeDto.username(),
                passwordEncoder.encode(employeeDto.password()),
                role.name(),
                employeeDto.email()
        );

        jdbc.update(
                employeeSql,
                employeeDto.jobDescription(),
                userId,
                1L
        );

        log.info("Hired new employee with role {} and username: {}", role, employeeDto.username());
    }

    public List<Employee> getEmployeesByRole(Role role) {
        final String sql = """
                SELECT e.*, u.* 
                FROM Employee e 
                JOIN _user u ON e.id_user = u.id 
                WHERE u.role = ?::role
                """;
        List<Employee> employees = jdbc.query(sql, employeeMapper, role.name());
        log.info("Retrieved {} employees with role {}", employees.size(), role);
        return employees;
    }

    public Optional<Employee> findEmployeeByUsername(String username) {
        final String sql = """
                SELECT e.*, u.* 
                FROM Employee e 
                JOIN _user u ON e.id_user = u.id 
                WHERE u.username = ?
                """;
        try {
            Employee employee = jdbc.queryForObject(sql, employeeMapper, username);
            log.info("Retrieved employee with username: {}", username);
            return Optional.ofNullable(employee);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Employee not found with username: {}", username);
            return Optional.empty();
        }
    }


    @Transactional
    public void updateEmployee(EmployeeUpdateDto updateDto) {
        final String userSql = """
                UPDATE _user 
                SET username = ?, password = ?, email = ?, role = ?::role
                WHERE id = (SELECT id_user FROM Employee WHERE id = ?)
                """;
        final String employeeSql = """
                UPDATE Employee 
                SET job_description = ?, id_restaurant = ?
                WHERE id = ?
                """;

        Long idEmployee = findEmployeeByUsername(updateDto.username()).orElseThrow().getIdEmployee();

        jdbc.update(
                userSql,
                updateDto.username(),
                passwordEncoder.encode(updateDto.password()),
                updateDto.email(),
                updateDto.role().name(),
                idEmployee
        );

        jdbc.update(
                employeeSql,
                updateDto.jobDescription(),
                updateDto.restaurantId(),
                idEmployee
        );

        log.info("Updated employee with ID: {}", idEmployee);
    }

    @Transactional
    public void fireEmployee(String username) {
        Long idEmployee = findEmployeeByUsername(username).orElseThrow().getIdEmployee();

        final String getUserIdSql = "SELECT id_user FROM Employee WHERE id = ?";
        final String deleteEmployeeSql = "DELETE FROM Employee WHERE id = ?";
        final String updateUserSql = "UPDATE _user SET enabled = false WHERE id = ?";

        Long userId = jdbc.queryForObject(getUserIdSql, Long.class, idEmployee);
        jdbc.update(deleteEmployeeSql, idEmployee);
        jdbc.update(updateUserSql, userId);
        log.info("Fired employee with ID: {}", idEmployee);
    }
}
