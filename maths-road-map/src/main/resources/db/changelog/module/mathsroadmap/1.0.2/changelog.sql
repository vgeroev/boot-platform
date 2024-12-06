--changeset vmalibu:1.0.2
UPDATE maths_road_map_article SET description = '' WHERE description IS NULL;
CREATE INDEX IF NOT EXISTS index_title_description_trgm ON maths_road_map_article using gin ((title || ' ' || description) gin_trgm_ops);