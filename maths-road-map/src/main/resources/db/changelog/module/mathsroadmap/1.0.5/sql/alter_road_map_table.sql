ALTER TABLE maths_road_map_road_map DROP COLUMN creator_username;
ALTER TABLE maths_road_map_road_map ADD COLUMN creator_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE maths_road_map_road_map ADD CONSTRAINT fk_creator FOREIGN KEY (creator_id) REFERENCES security_users (id);
ALTER TABLE maths_road_map_road_map ALTER COLUMN creator_id DROP DEFAULT;