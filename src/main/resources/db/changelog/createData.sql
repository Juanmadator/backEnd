<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                   https://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.3.xsd">

    <changeSet id="19" author="Juanma">
        <!-- Generar datos para la tabla Users -->
        <sql>
            INSERT INTO Users (UserID, Nombre, Apellido, Email, Contraseña, Fecha_registro, Ultima_fecha_inicio_sesion, Rol)
            VALUES (1, 'Juan', 'Pérez', 'juan@example.com', 'contraseña123', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'usuario');

            INSERT INTO Users (UserID, Nombre, Apellido, Email, Contraseña, Fecha_registro, Ultima_fecha_inicio_sesion, Rol)
            VALUES (2, 'María', 'García', 'maria@example.com', 'contraseña456', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'administrador');

            INSERT INTO Users (UserID, Nombre, Apellido, Email, Contraseña, Fecha_registro, Ultima_fecha_inicio_sesion, Rol)
            VALUES (3, 'Pedro', 'López', 'pedro@example.com', 'contraseña789', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'anonimo');
        </sql>

        <!-- Generar datos para la tabla Posts -->
        <sql>
            INSERT INTO Posts (PostID, Contenido, Fecha_publicacion, UserID)
            VALUES (1, 'Contenido del primer post', CURRENT_TIMESTAMP, 1);

            INSERT INTO Posts (PostID, Contenido, Fecha_publicacion, UserID)
            VALUES (2, 'Contenido del segundo post', CURRENT_TIMESTAMP, 2);
        </sql>

        <!-- Generar datos para la tabla Followers -->
        <sql>
            INSERT INTO Followers (FollowerID, UserID, UserID_followed)
            VALUES (1, 1, 2);

            INSERT INTO Followers (FollowerID, UserID, UserID_followed)
            VALUES (2, 2, 1);
        </sql>

        <!-- Generar datos para la tabla Comments -->
        <sql>
            INSERT INTO Comments (CommentID, Contenido, Fecha_publicacion, UserID, PostID)
            VALUES (1, 'Primer comentario', CURRENT_TIMESTAMP, 1, 1);

            INSERT INTO Comments (CommentID, Contenido, Fecha_publicacion, UserID, PostID)
            VALUES (2, 'Segundo comentario', CURRENT_TIMESTAMP, 2, 2);
        </sql>

        <!-- Generar datos para la tabla PrivateMessages -->
        <sql>
            INSERT INTO PrivateMessages (MessageID, Contenido, Fecha_envio, UserID_sender, UserID_receiver)
            VALUES (1, 'Primer mensaje privado', CURRENT_TIMESTAMP, 1, 2);

            INSERT INTO PrivateMessages (MessageID, Contenido, Fecha_envio, UserID_sender, UserID_receiver)
            VALUES (2, 'Segundo mensaje privado', CURRENT_TIMESTAMP, 2, 1);
        </sql>

        <!-- Generar datos para la tabla Interests -->
        <sql>
            INSERT INTO Interests (InterestID, Descripcion)
            VALUES (1, 'Interés 1');

            INSERT INTO Interests (InterestID, Descripcion)
            VALUES (2, 'Interés 2');
        </sql>

        <!-- Generar datos para la tabla Users_Interests -->
        <sql>
            INSERT INTO Users_Interests (UserID, InterestID)
            VALUES (1, 1);

            INSERT INTO Users_Interests (UserID, InterestID)
            VALUES (2, 2);
        </sql>

        <!-- Generar datos para la tabla Foods -->
        <sql>
            INSERT INTO Foods (food_id, name, description, category_id, average_rating)
            VALUES (1, 'Comida 1', 'Descripción de comida 1', 1, 4.5);

            INSERT INTO Foods (food_id, name, description, category_id, average_rating)
            VALUES (2, 'Comida 2', 'Descripción de comida 2', 2, 3.8);
        </sql>

        <!-- Generar datos para la tabla FoodCategories -->
        <sql>
            INSERT INTO FoodCategories (category_id, name)
            VALUES (1, 'Categoría 1');

            INSERT INTO FoodCategories (category_id, name)
            VALUES (2, 'Categoría 2');
        </sql>

        <!-- Generar datos para la tabla Nutrients -->
        <sql>
            INSERT INTO Nutrients (nutrient_id, name, unit)
            VALUES (1, 'Nutriente 1', 'g');

            INSERT INTO Nutrients (nutrient_id, name, unit)
            VALUES (2, 'Nutriente 2', 'mg');
        </sql>

        <!-- Generar datos para la tabla NutrientDetails -->
        <sql>
            INSERT INTO NutrientDetails (detail_id, food_id, nutrient_id, value)
            VALUES (1, 1, 1, 10.5);

            INSERT INTO NutrientDetails (detail_id, food_id, nutrient_id, value)
            VALUES (2, 2, 2, 20.8);
        </sql>

        <!-- Generar datos para la tabla Ratings -->
        <sql>
            INSERT INTO Ratings (rating_id, food_id, user_id, rating)
            VALUES (1, 1, 1, 5);

            INSERT INTO Ratings (rating_id, food_id, user_id, rating)
            VALUES (2, 2, 2, 4);
        </sql>
    </changeSet>

</databaseChangeLog>


