-- 使用您的数据库
USE movie_db;

-- 为了方便重复执行，先清空所有表中的数据
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `reviews`;

TRUNCATE TABLE `movie_actors`;

TRUNCATE TABLE `movie_directors`;

TRUNCATE TABLE `movies`;

TRUNCATE TABLE `actors`;

TRUNCATE TABLE `directors`;

TRUNCATE TABLE `users`;

SET FOREIGN_KEY_CHECKS = 1;

-- =================================================================
-- 1. 插入用户 (users)
-- =================================================================
INSERT INTO
    `users` (
        `id`,
        `username`,
        `password`,
        `email`,
        `role`,
        `bio`,
        `birth_date`,
        `created_at`
    )
VALUES (
        111,
        'zhangsan',
        'hashed_password_123',
        'zhangsan@example.com',
        'ROLE_USER',
        '科幻电影爱好者，喜欢诺兰和卡梅隆。',
        '1990-05-15',
        NOW()
    ),
    (
        211,
        'lisi',
        'hashed_password_456',
        'lisi@example.com',
        'ROLE_USER',
        '中国科幻的忠实拥趸。',
        '1995-08-20',
        NOW()
    ),
    (
        311,
        'wangwu',
        'hashed_password_789',
        'wangwu@example.com',
        'ROLE_ADMIN',
        '英式喜剧发烧友。',
        '1988-01-30',
        NOW()
    );

-- =================================================================
-- 2. 插入导演 (directors)
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
        'MALE',
        '中国',
        'https://i.ibb.co/hxPz98nh/image.jpg'
    ),
    (
        2,
        '克里斯托弗·诺兰',
        '1970-07-30',
        'MALE',
        '英国 / 美国',
        'https://i.ibb.co/0jpH16PJ/image.webp'
    ),
    (
        3,
        '詹姆斯·卡梅隆',
        '1954-08-16',
        'MALE',
        '加拿大',
        'https://i.ibb.co/YvFv7zf/image.jpg'
    ),
    (
        4,
        '彼得·惠特莫尔',
        '1929-12-25',
        'MALE',
        '英国',
        NULL
    );

-- =================================================================
-- 3. 插入演员 (actors)
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
        'MALE',
        '中国',
        'https://i.ibb.co/fVnVbHxd/image.webp'
    ),
    (
        2,
        '屈楚萧',
        '1994-12-28',
        'MALE',
        '中国',
        'https://i.ibb.co/8LJn6C9V/image.jpg'
    ),
    (
        3,
        '赵今麦',
        '2002-09-29',
        'FEMALE',
        '中国',
        'https://i.ibb.co/8LJn6C9V/image.jpg'
    ),
    (
        4,
        '刘德华',
        '1961-09-27',
        'MALE',
        '中国香港',
        'https://i.ibb.co/TxNtjC8q/image.jpg'
    ),
    (
        5,
        '李雪健',
        '1954-02-07',
        'MALE',
        '中国',
        'https://i.ibb.co/gKQFm5R/image.webp'
    ),
    (
        6,
        '约翰·大卫·华盛顿',
        '1984-07-28',
        'MALE',
        '美国',
        'https://i.ibb.co/3m64kcWZ/image.jpg'
    ),
    (
        7,
        '罗伯特·帕丁森',
        '1986-05-13',
        'MALE',
        '英国',
        'https://i.ibb.co/DHjjwm58/image.jpg'
    ),
    (
        8,
        '马修·麦康纳',
        '1969-11-04',
        'MALE',
        '美国',
        'https://i.ibb.co/7B8WMgW/image.jpg'
    ),
    (
        9,
        '安妮·海瑟薇',
        '1982-11-12',
        'FEMALE',
        '美国',
        'https://i.ibb.co/FqDV1JQ2/image.webp'
    ),
    (
        10,
        '莱昂纳多·迪卡普里奥',
        '1974-11-11',
        'MALE',
        '美国',
        'https://i.ibb.co/rR6JsmHM/image.jpg'
    ),
    (
        11,
        '凯特·温丝莱特',
        '1975-10-05',
        'FEMALE',
        '英国',
        'https://i.ibb.co/XxvZxPf2/image.jpg'
    ),
    (
        12,
        '保罗·艾丁顿',
        '1927-06-18',
        'MALE',
        '英国',
        'https://i.ibb.co/YsWLy91/image.webp'
    ),
    (
        13,
        '奈杰尔·霍桑',
        '1929-04-05',
        'MALE',
        '英国',
        'https://i.ibb.co/WW1sPhST/image.webp'
    );

-- =================================================================
-- 4. 插入电影 (movies)
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
        '太阳即将毁灭，人类决定开启流浪地球计划，带着地球离开太阳系，寻找新家园。天文学家、工程师、军人和无数普通人挺身而出，经历了“月球坠落危机”等一系列惊心动魄的灾难。',
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
VALUES (1, 1),
    (1, 2),
    (1, 3),
    (2, 1),
    (2, 4),
    (2, 5),
    (3, 6),
    (3, 7),
    (4, 8),
    (4, 9),
    (5, 10),
    (5, 11),
    (6, 12),
    (6, 13);

-- =================================================================
-- 7. 插入评论和评分 (reviews) - 合并自原有的 user_ratings 和 reviews
-- =================================================================
INSERT INTO
    `reviews` (
        `user_id`,
        `movie_id`,
        `score`,
        `comment_text`,
        `likes`,
        `created_at`,
        `updated_at`
    )
VALUES
    -- 张三的评价
    (
        111,
        3,
        10,
        '用户已评分，但未留下文字评论。',
        0,
        NOW(),
        NOW()
    ), -- 原评分
    (
        111,
        4,
        10,
        '诺兰yyds！时空和引力的概念太震撼了！',
        152,
        NOW(),
        NOW()
    ), -- 原评分+评论
    (
        111,
        5,
        8,
        '用户已评分，但未留下文字评论。',
        0,
        NOW(),
        NOW()
    ), -- 原评分
    -- 李四的评价
    (
        211,
        1,
        9,
        '用户已评分，但未留下文字评论。',
        0,
        NOW(),
        NOW()
    ), -- 原评分
    (
        211,
        2,
        10,
        '特效和格局都超越了第一部，看到了中国科幻的希望！刘德华的角色很有深度。',
        288,
        NOW(),
        NOW()
    ), -- 原评分+评论
    (
        211,
        4,
        9,
        '用户已评分，但未留下文字评论。',
        0,
        NOW(),
        NOW()
    ), -- 原评分
    -- 王五的评价
    (
        311,
        6,
        10,
        '永不过时的经典政治喜剧，汉弗莱爵士的语言艺术出神入化！',
        99,
        NOW(),
        NOW()
    ), -- 原评分+评论
    (
        311,
        1,
        8,
        '用户已评分，但未留下文字评论。',
        0,
        NOW(),
        NOW()
    );
-- 原评分

-- =================================================================
-- 8. 更新所有电影的平均分 (在后端逻辑中会自动完成，此处为手动更新以保证数据完整性)
-- =================================================================
UPDATE movies m
SET
    m.average_rating = (
        SELECT AVG(r.score)
        FROM reviews r
        WHERE
            r.movie_id = m.id
    );

UPDATE actors SET gender = 'MALE' WHERE gender = 'Male';

UPDATE actors SET gender = 'FEMALE' WHERE gender = 'Female';

UPDATE actors SET gender = 'OTHER' WHERE gender = 'Other';

-- 标准化 directors 表中的性别数据
UPDATE directors SET gender = 'MALE' WHERE gender = 'Male';

UPDATE directors SET gender = 'FEMALE' WHERE gender = 'Female';

UPDATE directors SET gender = 'OTHER' WHERE gender = 'Other';

CREATE TABLE `actors` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `biography` text COLLATE utf8mb4_unicode_ci,
    `birth_date` date DEFAULT NULL,
    `gender` enum('FEMALE', 'MALE', 'OTHER') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    `nationality` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `profile_image_url` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci

CREATE TABLE `directors` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `biography` text COLLATE utf8mb4_unicode_ci,
    `birth_date` date DEFAULT NULL,
    `gender` enum('FEMALE', 'MALE', 'OTHER') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    `nationality` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `profile_image_url` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
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

CREATE TABLE `movies` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `average_rating` double DEFAULT NULL,
    `country` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `duration` int DEFAULT NULL,
    `genre` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `language` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `poster_url` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `release_year` int DEFAULT NULL,
    `synopsis` longtext COLLATE utf8mb4_unicode_ci,
    `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci

CREATE TABLE `reviews` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `comment_text` text COLLATE utf8mb4_unicode_ci NOT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `likes` int NOT NULL,
    `score` int NOT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `movie_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKovijrmb7g6cvqvlgr25vb8r98` (`user_id`, `movie_id`),
    KEY `FK87tlqya0rq8ijfjscldpvvdyq` (`movie_id`),
    CONSTRAINT `FK87tlqya0rq8ijfjscldpvvdyq` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`),
    CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci

CREATE TABLE `users` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `bio` longtext COLLATE utf8mb4_unicode_ci,
    `birth_date` date DEFAULT NULL,
    `created_at` datetime(6) DEFAULT NULL,
    `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    `personal_website` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `profile_image_url` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `role` enum('ROLE_ADMIN', 'ROLE_USER') COLLATE utf8mb4_unicode_ci NOT NULL,
    `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
    UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE = InnoDB AUTO_INCREMENT = 3 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci