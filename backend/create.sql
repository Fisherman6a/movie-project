-- UPDATE actors SET gender = 'MALE' WHERE gender = 'Male';

-- UPDATE actors SET gender = 'FEMALE' WHERE gender = 'Female';

-- UPDATE actors SET gender = 'OTHER' WHERE gender = 'Other';

-- -- 标准化 directors 表中的性别数据
-- UPDATE directors SET gender = 'MALE' WHERE gender = 'Male';

-- UPDATE directors SET gender = 'FEMALE' WHERE gender = 'Female';

-- UPDATE directors SET gender = 'OTHER' WHERE gender = 'Other';

-- actors: 演员信息表
CREATE TABLE `actors` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `biography` text,
    `birth_date` date DEFAULT NULL,
    `gender` enum('FEMALE', 'MALE', 'OTHER') DEFAULT NULL,
    `name` varchar(100) NOT NULL,
    `nationality` varchar(100) DEFAULT NULL,
    `profile_image_url` varchar(2048) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- directors: 导演信息表
CREATE TABLE `directors` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `biography` text,
    `birth_date` date DEFAULT NULL,
    `gender` enum('FEMALE', 'MALE', 'OTHER') DEFAULT NULL,
    `name` varchar(100) NOT NULL,
    `nationality` varchar(100) DEFAULT NULL,
    `profile_image_url` varchar(2048) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- movies: 电影信息表
CREATE TABLE `movies` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `average_rating` double DEFAULT NULL,
    `country` varchar(100) DEFAULT NULL,
    `duration` int DEFAULT NULL,
    `genre` varchar(100) DEFAULT NULL,
    `language` varchar(50) DEFAULT NULL,
    `poster_url` varchar(2048) DEFAULT NULL,
    `release_year` int DEFAULT NULL,
    `synopsis` longtext,
    `title` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- users: 用户信息表
CREATE TABLE `users` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `bio` longtext,
    `birth_date` date DEFAULT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `email` varchar(255) NOT NULL,
    `phone` VARCHAR(20),
    `password` varchar(255) NOT NULL,
    `personal_website` varchar(2048) DEFAULT NULL,
    `profile_image_url` varchar(2048) DEFAULT NULL,
    `role` enum('ROLE_ADMIN', 'ROLE_USER') NOT NULL,
    `username` varchar(50) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users_email` (`email`),
    UNIQUE KEY `uk_users_username` (`username`),
    UNIQUE KEY `uq_users_phone` (`phone`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- reviews: 评论与评分表
CREATE TABLE `reviews` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `comment_text` text NOT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `likes` int NOT NULL,
    `score` int NOT NULL,
    `movie_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_movie` (`user_id`, `movie_id`),
    KEY `idx_reviews_movie_id` (`movie_id`),
    CONSTRAINT `fk_reviews_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`),
    CONSTRAINT `fk_reviews_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- movie_actors: 电影-演员关联表 (多对多)
CREATE TABLE `movie_actors` (
    `movie_id` bigint NOT NULL,
    `actor_id` bigint NOT NULL,
    PRIMARY KEY (`movie_id`, `actor_id`),
    KEY `idx_ma_actor_id` (`actor_id`),
    CONSTRAINT `fk_movie_actors_actor` FOREIGN KEY (`actor_id`) REFERENCES `actors` (`id`),
    CONSTRAINT `fk_movie_actors_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- movie_directors: 电影-导演关联表 (多对多)
CREATE TABLE `movie_directors` (
    `movie_id` bigint NOT NULL,
    `director_id` bigint NOT NULL,
    PRIMARY KEY (`movie_id`, `director_id`),
    KEY `idx_md_director_id` (`director_id`),
    CONSTRAINT `fk_movie_directors_director` FOREIGN KEY (`director_id`) REFERENCES `directors` (`id`),
    CONSTRAINT `fk_movie_directors_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

