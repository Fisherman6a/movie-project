CREATE TABLE `users` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `role` enum('ROLE_ADMIN', 'ROLE_USER') COLLATE utf8mb4_unicode_ci NOT NULL,
    `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `profile_image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `bio` longtext COLLATE utf8mb4_unicode_ci,
    `birth_date` date DEFAULT NULL,
    `personal_website` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
    UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE = InnoDB AUTO_INCREMENT = 112 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci

CREATE TABLE `user_ratings` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `rated_at` datetime(6) DEFAULT NULL,
    `score` int NOT NULL,
    `movie_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKg7jqqbnyse6j0vpl64ieg2rw` (`user_id`, `movie_id`),
    KEY `FKf6yrhb6tris6es0rrobdgh3l0` (`movie_id`),
    CONSTRAINT `FK79iiaqgo1bq1whumiv3evhfos` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `FKf6yrhb6tris6es0rrobdgh3l0` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci

CREATE TABLE `actors` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `birth_date` date DEFAULT NULL,
    `gender` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `nationality` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `profile_image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci

CREATE TABLE `directors` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `birth_date` date DEFAULT NULL,
    `gender` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `nationality` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `profile_image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci

CREATE TABLE `movies` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `average_rating` double DEFAULT NULL,
    `country` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `duration` int DEFAULT NULL,
    `genre` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `language` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `poster_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `release_year` int DEFAULT NULL,
    `synopsis` longtext COLLATE utf8mb4_unicode_ci,
    `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci

CREATE TABLE `reviews` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `comment_text` tinytext COLLATE utf8mb4_unicode_ci NOT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `movie_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK87tlqya0rq8ijfjscldpvvdyq` (`movie_id`),
    KEY `FKcgy7qjc1r99dp117y9en6lxye` (`user_id`),
    CONSTRAINT `FK87tlqya0rq8ijfjscldpvvdyq` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`),
    CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci

CREATE TABLE `movie_actors` (
    `movie_id` bigint NOT NULL,
    `actor_id` bigint NOT NULL,
    PRIMARY KEY (`movie_id`, `actor_id`),
    KEY `FK7svt4y6p0f9gpjy10awtubp02` (`actor_id`),
    CONSTRAINT `FK7svt4y6p0f9gpjy10awtubp02` FOREIGN KEY (`actor_id`) REFERENCES `actors` (`id`),
    CONSTRAINT `FKs4rlt03tdf55rwso4uyrwm0oq` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci

CREATE TABLE `movie_directors` (
    `movie_id` bigint NOT NULL,
    `director_id` bigint NOT NULL,
    PRIMARY KEY (`movie_id`, `director_id`),
    KEY `FKabpc9kvk8dao6yb3xro5i916r` (`director_id`),
    CONSTRAINT `FK90u08nnfro53e8vy5bgkkf77o` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`),
    CONSTRAINT `FKabpc9kvk8dao6yb3xro5i916r` FOREIGN KEY (`director_id`) REFERENCES `directors` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci