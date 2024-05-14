package com.fitness.app.auth;


import com.fitness.app.persistence.entities.PasswordResetToken;
import com.fitness.app.persistence.entities.User;
import com.fitness.app.persistence.entities.VerificationToken;
import com.fitness.app.persistence.repositories.PasswordResetTokenRepository;
import com.fitness.app.persistence.repositories.TokenRepository;
import com.fitness.app.persistence.repositories.UserRepository;
import com.fitness.app.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final String URL = "http://localhost:4200";


    @Autowired
    private PlatformTransactionManager transactionManager;
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/user/{username}")
    public Long getUserIdByUsername(@PathVariable String username) {
        return userRepository.findUserIdByUsername(username);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        try {
            AuthResponse authResponse = authService.register(request);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            String errorMessage = "Error al registrar el usuario: " + e.getMessage(); // Mensaje de error personalizado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }



    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestParam("password") String newPassword) {
        // Validar el token de recuperación de contraseña
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("El token de restablecimiento de contraseña es inválido o ha caducado.");
        }
        // Obtener el usuario asociado al token
        User user = resetToken.getUser();
        // Codificar la nueva contraseña
        String newPasswordEncoded = passwordEncoder.encode(newPassword);
        // Actualizar la contraseña del usuario
        user.setPassword(newPasswordEncoded);
        userRepository.save(user);
        // Eliminar el token de recuperación de contraseña
        passwordResetTokenRepository.delete(resetToken);
        return ResponseEntity.ok("Su contraseña ha sido restablecida con éxito.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        ResponseEntity<String> response = authService.forgotPassword(email);
        return response;
    }


    @PostMapping("/checkName")
    public boolean existsByUsername(@RequestBody String username){

        log.info("RESPUESTA",authService.findByUsername(username));
        return authService.findByUsername(username);
    }

    @PostMapping("/checkEmail")
    public boolean existsByEmail(@RequestBody String email){
        return authService.findByEmail(email);
    }


    @GetMapping("/verify")
    @Transactional
    public RedirectView verifyAccount(@RequestParam("token") String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken != null) {
            // Obtener la fecha actual en milisegundos
            long currentTimeMillis = System.currentTimeMillis();

            // Obtener la fecha de expiración del token en milisegundos
            long expiryTimeMillis = verificationToken.getExpiryDate().getTime();

            // Verificar si el token ha expirado comparando las fechas en milisegundos
            if (currentTimeMillis < expiryTimeMillis) {
                User user = verificationToken.getUser();
                if (user != null) {
                    // Verificar la cuenta del usuario
                    user.setVerified(true);
                    userRepository.save(user);
                    return new RedirectView(URL+"/verification"); // Redirigir a la ruta de verificación
                }
            } else {
                // El token ha expirado, eliminar el usuario después de un retraso de 20 segundos
                Long userId = verificationToken.getUser().getId();
                // Crear un hilo para eliminar el usuario después de 20 segundos
                new Thread(() -> {
                    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            try {
                                Thread.sleep(5000); // Esperar 20 segundos
                                deleteUser(userId); // Eliminar el usuario
                            } catch (InterruptedException e) {
                                log.error("Error: ",e);
                            }
                        }
                    });
                }).start();
                // Redirigir a la ruta de no verificado
                return new RedirectView(URL+"/notVerified");
            }
        }
        // El token no es válido, redirigir a la ruta de no verificado
        return new RedirectView(URL+"/notVerified");
    }




    @Transactional
    public void deleteUser(Long userId) {
        tokenRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

}
