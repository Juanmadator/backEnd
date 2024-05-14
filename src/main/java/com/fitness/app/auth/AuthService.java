package com.fitness.app.auth;


import com.fitness.app.exceptions.InvalidPasswordException;
import com.fitness.app.exceptions.InvalidUsernameException;
import com.fitness.app.exceptions.UserNotVerified;
import com.fitness.app.jwt.JwtService;
import com.fitness.app.persistence.entities.*;
import com.fitness.app.persistence.repositories.PasswordResetTokenRepository;
import com.fitness.app.persistence.repositories.RoleRepository;
import com.fitness.app.persistence.repositories.TokenRepository;
import com.fitness.app.persistence.repositories.UserRepository;
import com.fitness.app.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final String URL = "http://localhost:8080";

    public AuthResponse login(LoginRequest request) {
        UserDetails user = userRepository.findByUsername(request.getUsername());

        // Verificar si el usuario no existe
        if (user == null) {
            AuthResponse response = new AuthResponse();
            response.setError("Nombre de usuario incorrecto");
            return response;
        }

        try {
            // Verificar la contraseña solo si el usuario existe
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            // Ahora que el usuario está autenticado, verificamos la contraseña
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new InvalidPasswordException("Contraseña incorrecta");
            }
        } catch (AuthenticationException ex) {
            // Maneja la excepción de autenticación aquí (contraseña incorrecta)
            AuthResponse response = new AuthResponse();
            response.setError("Contraseña incorrecta");
            return response;
        }

        User usuario = (User) user;
        if (!usuario.getVerified()) {
            throw new UserNotVerified("El usuario no está verificado");
        }

        String token = jwtService.getToken(user);

        // Si el token es correcto, lo agregamos a response.error
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        return response;
    }


    public boolean findByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean findByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public AuthResponse register(RegisterRequest request) {
        User user = new User();
        String passwordEncoded = passwordEncoder.encode(request.getPassword());
        try {
            Role userRole = roleRepository.findByRoleName("USER");
            // Si el role USER no existe, puedes crearlo
            if (userRole == null) {
                userRole = new Role();
                userRole.setRoleName("USER");
                roleRepository.save(userRole);
            }

            user.setRole(UserRole.USER);

            Field[] fields = User.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = null;
                switch (field.getName()) {
                    case "username":
                        value = request.getUsername();
                        break;
                    case "name":
                        value = request.getName();
                        break;
                    case "lastname":
                        value = request.getLastname();
                        break;
                    case "email":
                        value = request.getEmail();
                        break;
                    case "password":
                        value = passwordEncoded;
                        break;
                    case "age":
                        value = request.getAge();
                        break;
                    case "gender":
                        value = request.getGender();
                        break;
                    case "country":
                        value = request.getCountry();
                        break;
                    case "coach":
                        value = request.getCoach();
                        break;
                    default:
                        // No hacer nada para otros campos de la clase User
                        break;
                }
                if (value != null) {
                    field.set(user, value);
                }
            }

            // Duración de validez del token en minutos
            int tokenValidityDurationInMinutes = 10;
            // Crear el token en la base de datos
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = getVerificationToken(token, user, tokenValidityDurationInMinutes);

            // Guardar el usuario y el token en la misma transacción
            userRepository.save(user);
            tokenRepository.save(verificationToken);
            // Enviar el correo electrónico de verificación
            emailService.sendConfirmationEmail(user.getEmail(), "Verificación de correo electrónico",
                    "Por favor, haga clic en el siguiente enlace para verificar su cuenta: " +
                            "<a href='" + URL + "/auth/verify?token=" + token + "'>Clic aquí para verificar tu cuenta</a>");

            return AuthResponse.builder().token(jwtService.getToken(user)).build();
        } catch (Exception e) {
            userRepository.delete(user);
            throw new RuntimeException("Error al guardar el usuario en la base de datos: " + e.getMessage());
        }
    }


    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("El correo electrónico proporcionado no está registrado.");
        }

        // Generar y guardar un token de recuperación de contraseña
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // Válido por 24 horas
        passwordResetTokenRepository.save(resetToken);

        // Enviar correo electrónico con el enlace de restablecimiento de contraseña
        emailService.sendPasswordResetEmail(user.getEmail(), "Restablecer contraseña",
                "Haga clic en el siguiente enlace para restablecer su contraseña: " +
                        "http://localhost:8080/reset-password?token=" + token);

        return ResponseEntity.ok("Se ha enviado un correo electrónico con las instrucciones para restablecer su contraseña.");
    }

    private static VerificationToken getVerificationToken(String token, User user, int tokenValidityDurationInMinutes) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        // Obtener la fecha actual
        Date currentDate = new Date();
        // Calcular la fecha de expiración sumando la duración de validez del token a la fecha actual
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MINUTE, tokenValidityDurationInMinutes);
        Date expiryDate = calendar.getTime();

        log.info("TIEMPO DEL TOKEN GENERADO: {}", expiryDate);
        verificationToken.setExpiryDate(expiryDate);// Establecer la fecha de expiración
        return verificationToken;
    }


}
