package model;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.io.Serializable;
import java.util.Objects;

/**
 * {@link User} is an abstraction, containing common state and behaviours shared by different kinds of users:
 * {@link Consumer}s and {@link Staff}s.
 */

//Added Serializable for SaveAppStateCommand
public abstract class User implements Serializable {
    private String email;
    private String passwordHash;

    /**
     * Create a new User and save the user email and payment account email, but do not save the password in plaintext!
     * This would be a security disaster, because a hacker could easily get it out of the application.
     * Instead, hash the user password using BCrypt - in other words, transform the password into another string,
     * using a library that always turns the same password into the same string (called a hash), but there is no way easy
     * way to reverse the hash back to the password. This allows to check whether someone entered the correct password
     * without ever storing the password itself, only a hash that cannot be easily reversed.
     *
     * @param email    new user email address (will be used to log in with)
     * @param password new user password (will be hashed and the hash will be used to verify future logins)
     * @see <a href="https://github.com/patrickfav/bcrypt">BCrypt library on GitHub that integrates with Gradle</a>
     */
    protected User(String email, String password) {
        this.email = email;
        this.passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    /**
     * Check whether the stored password hash matches the hash of the specified password.
     * You can use {@code return BCrypt.verifyer().verify(password.toCharArray(), passwordHash).verified;}
     *
     * @param password password to hash and check
     * @return True if the passwords match and false if they do not
     */
    public boolean checkPasswordMatch(String password) {
        return BCrypt.verifyer().verify(password.toCharArray(), passwordHash).verified;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if(obj == null || getClass() != obj.getClass()){
//            return false;
//        }
//        User other = (User) obj;
//        return Objects.equals(this.email, other.email) && Objects.equals(this.passwordHash, other.passwordHash);
//    }

    /**
     * Update the stored password hash to a new one, corresponding to the specified password.
     * You can use {@code passwordHash = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());}
     *
     * @param newPassword password to update the stored hash with
     */
    public void updatePassword(String newPassword) {
        this.passwordHash = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='***'" +
                '}';
    }
}
