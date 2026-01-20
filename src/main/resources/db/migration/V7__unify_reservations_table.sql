-- Add new columns to reservations table
ALTER TABLE reservations ADD COLUMN type VARCHAR(20) NOT NULL DEFAULT 'MATCH';
ALTER TABLE reservations ADD COLUMN description VARCHAR(500);
ALTER TABLE reservations ADD COLUMN coach_id BIGINT;

-- Create reservation_players junction table
CREATE TABLE reservation_players (
    reservation_id BIGINT NOT NULL,
    person_id BIGINT NOT NULL,
    PRIMARY KEY (reservation_id, person_id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
    FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE
);

-- Migrate data from matches table to reservations
UPDATE reservations 
SET description = (
    SELECT player_name 
    FROM matches 
    WHERE matches.id = reservations.id
)
WHERE id IN (SELECT id FROM matches);

-- Add foreign key constraint for coach
ALTER TABLE reservations ADD CONSTRAINT fk_reservation_coach 
    FOREIGN KEY (coach_id) REFERENCES persons(id);

-- Drop matches table (data already migrated)
DROP TABLE matches;