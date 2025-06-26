-- 创建一个新用户 'movie_app_user'，并设置密码 'your_secure_password'
-- 'localhost' 表示这个用户只能从本地机器连接。如果您的应用和数据库不在同一台服务器，
CREATE USER 'movie_app_user' @'localhost' IDENTIFIED BY '123456';
-- 将 movie_db 数据库的 SELECT, INSERT, UPDATE, DELETE 权限授予 movie_app_user
GRANT
SELECT,
INSERT
,
UPDATE,
DELETE ON movie_db.* TO 'movie_app_user' @'localhost';

DROP USER 'movie_app_user' @'localhost';

-- 触发器1: 在插入新评论后更新平均分
DELIMITER $$

CREATE TRIGGER after_review_insert
AFTER INSERT ON reviews
FOR EACH ROW
BEGIN
    UPDATE movies
    SET average_rating = (SELECT ROUND(AVG(score), 1) FROM reviews WHERE movie_id = NEW.movie_id)
    WHERE id = NEW.movie_id;
END$$

DELIMITER;

-- 触发器2: 在更新评论后更新平均分
DELIMITER $$

CREATE TRIGGER 
after_review_update
AFTER UPDATE ON reviews
FOR EACH ROW
BEGIN
    UPDATE movies
    SET average_rating = (SELECT ROUND(AVG(score), 1) FROM reviews WHERE movie_id = NEW.movie_id)
    WHERE id = NEW.movie_id;
END$$

DELIMITER;

-- 触发器3: 在删除评论后更新平均分
DELIMITER $$

CREATE TRIGGER after_review_delete
AFTER DELETE ON reviews
FOR EACH ROW
BEGIN
    UPDATE movies
    SET average_rating = (SELECT ROUND(AVG(score), 1) FROM reviews WHERE movie_id = OLD.movie_id)
    WHERE id = OLD.movie_id;
END$$

DELIMITER;

-- 触发器验证 --
-- 测试对象: 以电影《信条》(movie_id = 3) 为例
SELECT id, title, average_rating FROM movies WHERE id = 3;
-- average_rating 应该是 10.0
-- 测试 1: after_review_insert 触发器
-- 步骤：插入一条新评论、然后查看分数
INSERT INTO
    reviews (
        movie_id,
        user_id,
        score,
        comment_text,
        likes,
        created_at
    )
VALUES (
        3,
        211,
        6,
        '这是一条用于测试INSERT触发器的评论',
        0,
        NOW()
    );
-- 测试 2: after_review_update 触发器
-- 刚才插入的6分评论，现在将其修改为2分。
-- 步骤：更新评论分数，然后查看分数
UPDATE reviews
SET
    score = 2
WHERE
    movie_id = 3
    AND user_id = 211;
-- 测试 3: after_review_delete 触发器
-- 步骤：删除评论，然后查看分数
DELETE FROM reviews WHERE movie_id = 3 AND user_id = 211;

-- 存储过程: 根据电影标题获取所有评论详情
DELIMITER $$

CREATE PROCEDURE get_reviews_by_movie_title(IN movieTitle VARCHAR(255))
BEGIN
    SELECT 
        r.id, r.comment_text, r.score, r.likes, r.created_at,
        r.user_id, u.username, u.profile_image_url,
        r.movie_id
    FROM reviews r
    JOIN users u ON r.user_id = u.id
    JOIN movies m ON r.movie_id = m.id
    WHERE m.title = movieTitle
    ORDER BY r.likes DESC;
END$$

DROP PROCEDURE get_reviews_by_movie_title;
-- 存储过程验证
CALL get_reviews_by_movie_title ('信条');

DELIMITER;

-- 视图: 创建一个包含评论、用户和电影标题的详细视图
CREATE OR REPLACE VIEW v_review_details AS
SELECT
    r.id AS review_id,
    r.comment_text,
    r.score,
    r.likes,
    r.created_at,
    u.id AS user_id,
    u.username,
    m.id AS movie_id,
    m.title AS movie_title
FROM
    reviews r
    JOIN users u ON r.user_id = u.id
    JOIN movies m ON r.movie_id = m.id;
-- 视图验证
SELECT * FROM v_review_details;

ALTER TABLE users ADD COLUMN phone VARCHAR(2