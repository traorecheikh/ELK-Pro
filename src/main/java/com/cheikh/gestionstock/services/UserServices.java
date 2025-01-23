package com.cheikh.gestionstock.services;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cheikh.gestionstock.models.User;
import static com.cheikh.gestionstock.services.EntityManagerUtils.getEM;

import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.Setter;

public class UserServices {
    private static final Logger log = LoggerFactory.getLogger(UserServices.class);

    @Setter
    @Getter
    private static User userSession;
    private static EntityManager em;
    private static final Path sessions = Paths.get("sessions");
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public int Register(User user) {
        em = getEM();
        Object o = em.createQuery("select u from User u where u.email = :email")
                .setParameter("email", user.getEmail())
                .getSingleResultOrNull();

        if (o != null) {
            log.warn("User {} {} tried to register with an already existing email: {}", user.getPrenom(), user.getNom(), user.getEmail());
            return 2;
        }

        try {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);

            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            log.info("User successfully registered with email: {}", user.getEmail());
            return 0;
        } catch (Exception e) {
            em.getTransaction().rollback();
            log.error("Error occurred while registering user with email: {}", user.getEmail(), e);
            return 1;
        }
    }

    public boolean Login(String email, String password) {
        em = getEM();

        Object o = em.createQuery("select u from User u where u.email = :email")
                .setParameter("email", email)
                .getSingleResultOrNull();

        if (o == null) {
            log.warn("User not found with email: {}", email);
            return false;
        }

        User user = (User) o;

        if (passwordEncoder.matches(password, user.getPassword())) {
            String sessionKey = UUID.randomUUID().toString();

            user.setSessionKey(sessionKey);
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();

            try {
                if (!Files.exists(sessions)) {
                    Files.createDirectories(sessions);
                }

                Path sessionFile = sessions.resolve(sessionKey);
                Files.write(sessionFile, sessionKey.getBytes());

                setUserSession(user);
                log.info("User with email {} logged in successfully. Session key generated.", user.getEmail());
                return true;
            } catch (IOException e) {
                log.error("Error while creating session file for user with email: {}", user.getEmail(), e);
                return false;
            }
        }

        log.warn("Incorrect password for user with email: {}", user.getEmail());
        return false;
    }

    public void Logout() {
        setUserSession(null);

        try {
            Files.walk(sessions)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                            log.info("Session file deleted: {}", file);
                        } catch (IOException e) {
                            log.warn("Failed to delete session file: {} due to {}", file, e.getMessage());
                        }
                    });
            log.info("User logged out successfully.");
        } catch (IOException e) {
            log.error("Error while walking through session files: {}", e.getMessage());
        }
    }

    public static User checkSession() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sessions)) {
            for (Path entry : stream) {
                String sessionKey = new String(Files.readAllBytes(entry));
                em = getEM();
                Object o = em.createQuery("select u from User u where u.sessionKey = :sessionKey")
                        .setParameter("sessionKey", sessionKey)
                        .getSingleResultOrNull();

                if (o != null) {
                    User user = (User) o;
                    if (isSessionValid(user.getSessionKey())) {
                        setUserSession(user);
                        log.info("Valid session found for user with email: {}", user.getEmail());
                        return user;
                    }
                }
            }
        } catch (IOException e) {
            log.warn("No valid session found.");
            return null;
        }
        return null;
    }

    private static boolean isSessionValid(String sessionKey) {
        Path sessionFile = sessions.resolve(sessionKey);
        return Files.exists(sessionFile);
    }

    public boolean addAdmin(User admin, User newAdmin) {
        if (!admin.isAdmin()) {
            log.warn("User with email {} is not an administrator. Action forbidden.", userSession.getEmail());
            return false;
        }

        try {
            newAdmin.setAdmin(true);
            em.getTransaction().begin();
            em.merge(newAdmin);
            em.getTransaction().commit();
            log.info("User with email {} has been added as an administrator by {}.", newAdmin.getEmail(), admin.getEmail());
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            log.error("Error occurred while adding user {} as administrator: {}", newAdmin.getEmail(), e.getMessage());
            return false;
        }
    }

    public boolean removeAdmin(User admin, User targetUser) {
        if (!admin.isAdmin()) {
            log.warn("User with email {} is not an administrator. Action forbidden.", userSession.getEmail());
            return false;
        }

        try {
            targetUser.setAdmin(false);
            em.getTransaction().begin();
            em.merge(targetUser);
            em.getTransaction().commit();
            log.info("User with email {} has been removed from administrators by {}.", targetUser.getEmail(), admin.getEmail());
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            log.error("Error occurred while removing user {} from administrators: {}", targetUser.getEmail(), e.getMessage());
            return false;
        }
    }

    public List<User> GetUsers() {
        em = getEM();
        try {
            return em.createQuery("select u from User u").getResultList();
        } catch (Exception e) {
            log.error("Error occurred while retrieving users: {}", e.getMessage());
            return null;
        }
    }

    public void deleteUser(User user) {
        em = getEM();
        try {
            em.getTransaction().begin();
            em.remove(user);
            em.getTransaction().commit();
            log.info("User with email {} has been deleted.", user.getEmail());
        } catch (Exception e) {
            em.getTransaction().rollback();
            log.error("Error occurred while deleting user with email {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
