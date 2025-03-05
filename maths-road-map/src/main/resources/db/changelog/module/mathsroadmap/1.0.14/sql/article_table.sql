CREATE INDEX IF NOT EXISTS idx_maths_road_map_article_created_at_desc ON maths_road_map_article (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_maths_road_map_article_creator_id ON maths_road_map_article (creator_id);

ALTER TABLE maths_road_map_article ADD COLUMN tag_ids BIGINT[];
UPDATE maths_road_map_article SET tag_ids = '{}';
ALTER TABLE maths_road_map_article ALTER COLUMN tag_ids SET NOT NULL;