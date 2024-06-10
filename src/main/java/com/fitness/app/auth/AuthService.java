public AuthResponse register(RegisterRequest request) {
        User user = new User();
        String passwordEncoded = passwordEncoder.encode(request.getPassword());
        try {
            // Determinar el rol del usuario
            String requestedRole = request.getRole() != null ? request.getRole() : "USER";
            Role userRole = roleRepository.findByRoleName(requestedRole);

            // Si el rol solicitado no existe, puedes crearlo
            if (userRole == null) {
                userRole = new Role();
                userRole.setRoleName(requestedRole);
                roleRepository.save(userRole);
            }

            // Asignar el rol al usuario
            user.setRole(UserRole.valueOf(requestedRole));

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
            String emailContent = "<div style='"
                    + "font-family: Arial, sans-serif;"
                    + "padding: 20px;"
                    + "background-color: #f9f9f9;"
                    + "color: #333;"
                    + "'>"
                    + "<h2 style='"
                    + "color: hsl(252, 75%, 65%);"
                    + "'>Confirmación de tu cuenta</h2>"
                    + "<p>¡Hola! Estamos emocionados de tenerte con nosotros. Por favor, confirma tu cuenta haciendo clic en el siguiente enlace:</p>"
                    + "<a href='" + URL + "/auth/verify?token=" + token + "' style='"
                    + "display: inline-block;"
                    + "color: #FFFFFF;"
                    + "background-color: hsl(252, 75%, 65%);"
                    + "padding: 12px 24px;"
                    + "text-decoration: none;"
                    + "border-radius: 4px;"
                    + "'>Confirma tu cuenta aquí</a>"
                    + "<p>¡Gracias por unirte a Fit-Track!</p>"
                    + "</div>";

            emailService.sendConfirmationEmail(user.getEmail(), "Confirmación de tu cuenta", emailContent);

            return AuthResponse.builder().token(jwtService.getToken(user)).build();
        } catch (Exception e) {
            userRepository.delete(user);
            throw new RuntimeException("Error al guardar el usuario en la base de datos: " + e.getMessage());
        }
    }