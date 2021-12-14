package ro.unibuc.springlab8example1.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ro.unibuc.springlab8example1.domain.User;
import ro.unibuc.springlab8example1.domain.UserDetails;
import ro.unibuc.springlab8example1.domain.UserType;
import ro.unibuc.springlab8example1.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User save(User user) {
        String saveUserSql = "INSERT INTO users (username, full_name, user_type, account_created) VALUES (?,?,?,?)";
        jdbcTemplate.update(saveUserSql, user.getUsername(), user.getFullName(), user.getUserType().name(), LocalDateTime.now());

        User savedUser = getUserWith(user.getUsername());
        UserDetails userDetails = user.getUserDetails();

        if (userDetails != null) {
            String saveUserDetailsSql = "INSERT INTO user_details (cnp, age, other_information) VALUES (?, ?, ?)";
            jdbcTemplate.update(saveUserDetailsSql, userDetails.getCnp(), userDetails.getAge(), userDetails.getOtherInformation());

            UserDetails savedUserDetails = getUserDetailsWith(userDetails.getCnp());
            savedUser.setUserDetails(savedUserDetails);

            String saveUsersUserDetails = "INSERT INTO users_user_details (users, user_details) VALUES (?, ?)";
            jdbcTemplate.update(saveUsersUserDetails, savedUser.getId(), savedUserDetails.getId());
        }

        return savedUser;
    }

    public User get(String username) {
        // TODO : homework: use JOIN to fetch all details about the user

        String selectSql = "SELECT * from users u JOIN users_user_details details ON u.id=details.users JOIN user_details u_details ON u_details.id = details.user_details WHERE u.username = ?";
        RowMapper<User> rowMapper = (resultSet, rowNo) -> {
            UserDetails userDetails = UserDetails.builder()
                    .id(resultSet.getLong("id"))
                    .cnp(resultSet.getString("cnp"))
                    .age(resultSet.getInt("age"))
                    .otherInformation(resultSet.getString("other_information"))
                    .build();
            return User.builder()
                    .id(resultSet.getLong("id"))
                    .username(resultSet.getString("username"))
                    .fullName(resultSet.getString("full_name"))
                    .userType(UserType.valueOf(resultSet.getString("user_type")))
                    .userDetails(userDetails)
                    .build();
        };

        List<User> users = jdbcTemplate.query(selectSql, rowMapper, username);

        if (!users.isEmpty()) {
            return users.get(0);
        }

        throw new UserNotFoundException("User not found");
    }

    private User getUserWith(String username) {
        String selectSql = "SELECT * from users WHERE users.username = ?";
        RowMapper<User> rowMapper = (resultSet, rowNo) -> User.builder()
                .id(resultSet.getLong("id"))
                .username(resultSet.getString("username"))
                .fullName(resultSet.getString("full_name"))
                .userType(UserType.valueOf(resultSet.getString("user_type")))
                .build();

        List<User> users = jdbcTemplate.query(selectSql, rowMapper, username);

        if (!users.isEmpty()) {
            return users.get(0);
        }

        throw new UserNotFoundException("User not found");
    }

    private UserDetails getUserDetailsWith(String cnp) {
        String selectSql = "SELECT * from user_details WHERE user_details.cnp = ?";
        RowMapper<UserDetails> rowMapper = (resultSet, rowNo) -> UserDetails.builder()
                .id(resultSet.getLong("id"))
                .cnp(resultSet.getString("cnp"))
                .age(resultSet.getInt("age"))
                .otherInformation(resultSet.getString("other_information"))
                .build();

        List<UserDetails> details = jdbcTemplate.query(selectSql, rowMapper, cnp);

        if (!details.isEmpty()) {
            return details.get(0);
        }

        throw new UserNotFoundException("User details not found");
    }

    public void deleteUserWithId(Long id) {
        String deleteFromUsersSql = "DELETE FROM users WHERE id = ?";
        String deleteFromUsersUserDetailsSql = "DELETE FROM users_user_details WHERE users = ?";
        String deleteFromUserDetailsSql =
                "DELETE d FROM user_details d LEFT JOIN users_user_details ud ON d.id = ud.user_details WHERE ud.id IS NULL";

        jdbcTemplate.update(deleteFromUsersUserDetailsSql, id);
        jdbcTemplate.update(deleteFromUsersSql, id);
        jdbcTemplate.update(deleteFromUserDetailsSql);
    }

    public List<User> getUsersByType(UserType userType) {
        String selectSql = "SELECT * from users u JOIN users_user_details uud ON u.id=uud.users JOIN user_details ud ON ud.id = uud.user_details WHERE u.user_type = ?";
        RowMapper<User> rowMapper = (resultSet, rowNo) -> {
            UserDetails userDetails = UserDetails.builder()
                    .id(resultSet.getLong("id"))
                    .cnp(resultSet.getString("cnp"))
                    .age(resultSet.getInt("age"))
                    .otherInformation(resultSet.getString("other_information"))
                    .build();
            return User.builder()
                    .id(resultSet.getLong("id"))
                    .username(resultSet.getString("username"))
                    .fullName(resultSet.getString("full_name"))
                    .userType(UserType.valueOf(resultSet.getString("user_type")))
                    .userDetails(userDetails)
                    .build();
        };

        List<User> users = jdbcTemplate.query(selectSql, rowMapper, userType.name());

        if (!users.isEmpty()) {
            return users;
        }

        throw new UserNotFoundException("User not found");
    }

    private User getUserById(Long id) {
        String selectSql = "SELECT * from users u JOIN users_user_details uud ON u.id=uud.users JOIN user_details ud ON ud.id = uud.user_details WHERE u.id = ?";
        RowMapper<User> rowMapper = (resultSet, rowNo) -> {
            UserDetails userDetails = UserDetails.builder()
                    .id(resultSet.getLong("id"))
                    .cnp(resultSet.getString("cnp"))
                    .age(resultSet.getInt("age"))
                    .otherInformation(resultSet.getString("other_information"))
                    .build();
            return User.builder()
                    .id(resultSet.getLong("id"))
                    .username(resultSet.getString("username"))
                    .fullName(resultSet.getString("full_name"))
                    .userType(UserType.valueOf(resultSet.getString("user_type")))
                    .userDetails(userDetails)
                    .build();
        };

        List<User> users = jdbcTemplate.query(selectSql, rowMapper, id);

        if (!users.isEmpty()) {
            return users.get(0);
        }

        throw new UserNotFoundException("User not found");
    }

    public User updateUserWithId(User user, Long id) {
        String updateUsersSql = "UPDATE users SET username = ?, full_name = ? WHERE id = ?";
        String updateUserDetailsSql =
                "UPDATE user_details d JOIN users_user_details ud ON d.id = ud.user_details JOIN users u ON ud.users = u.id " +
                        "SET d.cnp = ?, d.age = ?, d.other_information = ? WHERE u.id = ?";

        jdbcTemplate.update(updateUsersSql, user.getUsername(), user.getFullName(), id);
        jdbcTemplate.update(updateUserDetailsSql, user.getUserDetails().getCnp(), user.getUserDetails().getAge(), user.getUserDetails().getOtherInformation(), id);

        return getUserById(id);
    }
}
