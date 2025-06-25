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

CREATE TRIGGER after_review_update
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

-- 存储过程: 根据电影标题获取所有评论详情
DELIMITER $$

CREATE PROCEDURE get_reviews_by_movie_title(IN movieTitle VARCHAR(255))
BEGIN
    SELECT 
        r.id, r.comment_text, r.score, r.likes, r.created_at, r.updated_at,
        r.user_id, u.username, u.profile_image_url,
        r.movie_id
    FROM reviews r
    JOIN users u ON r.user_id = u.id
    JOIN movies m ON r.movie_id = m.id
    WHERE m.title = movieTitle
    ORDER BY r.likes DESC;
END$$

DELIMITER;

-- 视图: 创建一个包含评论、用户和电影标题的详细视图
CREATE OR REPLACE VIEW v_review_details AS
SELECT
    r.id AS review_id,
    r.comment_text,
    r.score,
    r.likes,
    r.created_at,
    r.updated_at,
    u.id AS user_id,
    u.username,
    u.profile_image_url,
    m.id AS movie_id,
    m.title AS movie_title
FROM
    reviews r
    JOIN users u ON r.user_id = u.id
    JOIN movies m ON r.movie_id = m.id;

-- =================================================================
-- 步骤 4: 初始化电影平均分
-- =================================================================
UPDATE movies m
SET
    m.average_rating = (
        SELECT ROUND(AVG(r.score), 1)
        FROM reviews r
        WHERE
            r.movie_id = m.id
    );