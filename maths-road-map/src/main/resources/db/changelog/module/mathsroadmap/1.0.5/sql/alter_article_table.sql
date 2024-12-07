ALTER TABLE maths_road_map_article DROP COLUMN creator_username;
ALTER TABLE maths_road_map_article ADD COLUMN creator_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE maths_road_map_article ADD CONSTRAINT fk_creator FOREIGN KEY (creator_id) REFERENCES security_users (id);
ALTER TABLE maths_road_map_article ALTER COLUMN creator_id DROP DEFAULT;