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

CREATE TABLE `reviews` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `comment_text` tinytext COLLATE utf8mb4_unicode_ci NOT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `movie_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `likes` int NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK87tlqya0rq8ijfjscldpvvdyq` (`movie_id`),
    KEY `FKcgy7qjc1r99dp117y9en6lxye` (`user_id`),
    CONSTRAINT `FK87tlqya0rq8ijfjscldpvvdyq` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`),
    CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci

-- =================================================================
-- 1. 插入用户 (Users)
-- 密码应该是经过哈希处理的，这里为了演示使用明文。在生产环境中请务必使用强哈希算法（如 bcrypt）。
-- =================================================================

DELETE FROM users WHERE username = 'zhangsan';

DELETE FROM users WHERE username = 'lisi';

DELETE FROM users WHERE username = 'wangwu';

INSERT INTO
    `users` (
        `id`,
        `created_at`,
        `email`,
        `password`,
        `role`,
        `username`,
        `bio`,
        `birth_date`
    )
VALUES (
        1,
        NOW(),
        'zhangsan@example.com',
        'hashed_password_123',
        'ROLE_USER',
        'zhangsan',
        '科幻电影爱好者，喜欢诺兰和卡梅隆。',
        '1990-05-15'
    ),
    (
        2,
        NOW(),
        'lisi@example.com',
        'hashed_password_456',
        'ROLE_USER',
        'lisi',
        '中国科幻的忠实拥趸。',
        '1995-08-20'
    ),
    (
        3,
        NOW(),
        'wangwu@example.com',
        'hashed_password_789',
        'ROLE_ADMIN',
        'wangwu',
        '英式喜剧发烧友。',
        '1988-01-30'
    );

-- =================================================================
-- 2. 插入导演 (Directors)
-- =================================================================
INSERT INTO
    `directors` (
        `id`,
        `name`,
        `birth_date`,
        `gender`,
        `nationality`,
        `profile_image_url`
    )
VALUES (
        1,
        '郭帆',
        '1980-12-15',
        'Male',
        '中国',
        'https://i.ibb.co/hxPz98nh/image.jpg'
    ),
    (
        2,
        '克里斯托弗·诺兰',
        '1970-07-30',
        'Male',
        '英国 / 美国',
        'https://i.ibb.co/0jpH16PJ/image.webp'
    ),
    (
        3,
        '詹姆斯·卡梅隆',
        '1954-08-16',
        'Male',
        '加拿大',
        'https://i.ibb.co/YvFv7zf/image.jpg'
    ),
    (
        4,
        '彼得·惠特莫尔',
        '1929-12-25',
        'Male',
        '英国',
        NULL
    );
-- =================================================================
-- 3. 插入演员 (Actors)
-- =================================================================
INSERT INTO
    `actors` (
        `id`,
        `name`,
        `birth_date`,
        `gender`,
        `nationality`,
        `profile_image_url`
    )
VALUES (
        1,
        '吴京',
        '1974-04-03',
        'Male',
        '中国',
        'https://i.ibb.co/fVnVbHxd/image.webp'
    ),
    (
        2,
        '屈楚萧',
        '1994-12-28',
        'Male',
        '中国',
        'https://i.ibb.co/8LJn6C9V/image.jpg'
    ),
    (
        3,
        '赵今麦',
        '2002-09-29',
        'Female',
        '中国',
        'https://i.ibb.co/8LJn6C9V/image.jpg'
    ),
    (
        4,
        '刘德华',
        '1961-09-27',
        'Male',
        '中国香港',
        'https://i.ibb.co/TxNtjC8q/image.jpg'
    ),
    (
        5,
        '李雪健',
        '1954-02-07',
        'Male',
        '中国',
        'https://i.ibb.co/gKQFm5R/image.webp'
    ),
    (
        6,
        '约翰·大卫·华盛顿',
        '1984-07-28',
        'Male',
        '美国',
        'https://i.ibb.co/3m64kcWZ/image.jpg'
    ),
    (
        7,
        '罗伯特·帕丁森',
        '1986-05-13',
        'Male',
        '英国',
        'https://i.ibb.co/DHjjwm58/image.jpg'
    ),
    (
        8,
        '马修·麦康纳',
        '1969-11-04',
        'Male',
        '美国',
        'https://i.ibb.co/7B8WMgW/image.jpg'
    ),
    (
        9,
        '安妮·海瑟薇',
        '1982-11-12',
        'Female',
        '美国',
        'https://i.ibb.co/FqDV1JQ2/image.webp'
    ),
    (
        10,
        '莱昂纳多·迪卡普里奥',
        '1974-11-11',
        'Male',
        '美国',
        'https://i.ibb.co/rR6JsmHM/image.jpg'
    ),
    (
        11,
        '凯特·温丝莱特',
        '1975-10-05',
        'Female',
        '英国',
        'https://i.ibb.co/XxvZxPf2/image.jpg'
    ),
    (
        12,
        '保罗·艾丁顿',
        '1927-06-18',
        'Male',
        '英国',
        'https://i.ibb.co/YsWLy91/image.webp'
    ),
    (
        13,
        '奈杰尔·霍桑',
        '1929-04-05',
        'Male',
        '英国',
        'https://i.ibb.co/WW1sPhST/image.webp'
    );

-- =================================================================
-- 4. 插入电影 (Movies)
-- =================================================================
INSERT INTO
    `movies` (
        `id`,
        `title`,
        `release_year`,
        `genre`,
        `country`,
        `language`,
        `duration`,
        `synopsis`,
        `poster_url`
    )
VALUES (
        1,
        '流浪地球',
        2019,
        '科幻/灾难',
        '中国',
        '汉语普通话',
        125,
        '太阳即将毁灭，人类在地球表面建造出巨大的推进器，寻找新的家园。然而宇宙之路危机四伏，为了拯救地球，为了人类能在漫长的2500年后抵达新的家园，流浪地球时代的年轻人挺身而出，展开了争分夺秒的生死之战。',
        'https://i.ibb.co/W4HxYVWY/image.webp'
    ),
    (
        2,
        '流浪地球2',
        2023,
        '科幻/冒险/灾难',
        '中国',
        '汉语普通话',
        173,
        '太阳即将毁灭，人类决定开启流浪地球计划，带着地球离开太阳系，寻找新家园。天文学家、工程师、军人和无数普通人挺身而出，经历了“月球坠落危机”等一系列惊心动魄的灾难，为人类的延续展开了一场与时间的赛跑。',
        'https://i.ibb.co/7t5s38pY/2.webp'
    ),
    (
        3,
        '信条',
        2020,
        '科幻/动作/悬疑',
        '美国/英国',
        '英语',
        150,
        '一名无名特工接到一项超越真实时间的神秘任务，他仅有的武器是一个词——“信条”。为了全世界的存亡，他穿越于一个国际谍战的黄昏世界。',
        'https://i.ibb.co/FLJC7NdL/image.webp'
    ),
    (
        4,
        '星际穿越',
        2014,
        '科幻/剧情/冒险',
        '美国/英国',
        '英语',
        169,
        '在不远的未来，地球环境恶化，农作物几乎无法种植。一支探险队利用对虫洞的新发现，踏上了超越人类太空旅行极限的星际航行，去为人类寻找新的家园。',
        'https://i.ibb.co/Rk7dMCqS/image.webp'
    ),
    (
        5,
        '泰坦尼克号',
        1997,
        '爱情/剧情/灾难',
        '美国',
        '英语',
        194,
        '1912年，穷画家杰克和贵族女露丝在一艘号称“永不沉没”的豪华巨轮——泰坦尼克号上相遇。两人坠入爱河，但一场突如其来的灾难却要将他们的爱情带向毁灭的边缘。',
        'https://i.ibb.co/sJMvXYCF/image.webp'
    ),
    (
        6,
        '是，大臣 1984圣诞特辑',
        1980,
        '喜剧/剧情',
        '英国',
        '英语',
        210,
        '该剧设定在英国白厅的中心，讲述了内阁大臣吉姆·哈克与他的常任秘书汉弗莱·阿普比爵士以及首席私人秘书伯纳德·伍利之间，在制定和执行政府政策过程中发生的种种令人捧腹的权力斗争。',
        'https://i.ibb.co/TMdK5gsw/1984.webp'
    );

-- =================================================================
-- 5. 关联电影和导演 (movie_directors)
-- =================================================================
INSERT INTO
    `movie_directors` (`movie_id`, `director_id`)
VALUES (1, 1),
    (2, 1),
    (3, 2),
    (4, 2),
    (5, 3),
    (6, 4);

-- =================================================================
-- 6. 关联电影和演员 (movie_actors)
-- =================================================================
INSERT INTO
    `movie_actors` (`movie_id`, `actor_id`)
VALUES
    -- 流浪地球
    (1, 1),
    (1, 2),
    (1, 3),
    -- 流浪地球2
    (2, 1),
    (2, 4),
    (2, 5),
    -- 信条
    (3, 6),
    (3, 7),
    -- 星际穿越
    (4, 8),
    (4, 9),
    -- 泰坦尼克号
    (5, 10),
    (5, 11),
    -- 是，大臣
    (6, 12),
    (6, 13);

-- =================================================================
-- 7. 插入用户评分 (user_ratings)
-- =================================================================
INSERT INTO
    `user_ratings` (
        `user_id`,
        `movie_id`,
        `score`,
        `rated_at`
    )
VALUES
    -- 张三的评分
    (1, 3, 10, NOW()), -- 给 信条 打10分
    (1, 4, 10, NOW()), -- 给 星际穿越 打10分
    (1, 5, 8, NOW()), -- 给 泰坦尼克号 打8分
    -- 李四的评分
    (2, 1, 9, NOW()), -- 给 流浪地球 打9分
    (2, 2, 10, NOW()), -- 给 流浪地球2 打10分
    (2, 4, 9, NOW()), -- 给 星际穿越 打9分
    -- 王五的评分
    (3, 6, 10, NOW()), -- 给 是，大臣 打10分
    (3, 1, 8, NOW());
-- 给 流浪地球 打8分

-- =================================================================
-- 8. 插入用户评论 (reviews)
-- =================================================================
INSERT INTO
    `reviews` (
        `user_id`,
        `movie_id`,
        `comment_text`,
        `created_at`,
        `updated_at`,
        `likes`
    )
VALUES
    -- 张三的评论
    (
        1,
        4,
        '诺兰yyds！时空和引力的概念太震撼了！',
        NOW(),
        NOW(),
        152
    ),
    -- 李四的评论
    (
        2,
        2,
        '特效和格局都超越了第一部，看到了中国科幻的希望！刘德华的角色很有深度。',
        NOW(),
        NOW(),
        288
    ),
    -- 王五的评论
    (
        3,
        6,
        '永不过时的经典政治喜剧，汉弗莱爵士的语言艺术出神入化！',
        NOW(),
        NOW(),
        99
    );