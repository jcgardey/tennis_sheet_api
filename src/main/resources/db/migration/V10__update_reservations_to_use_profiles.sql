-- Update reservations table to reference coach_profiles instead of persons
ALTER TABLE reservations DROP CONSTRAINT IF EXISTS fk_reservation_coach;
ALTER TABLE reservations DROP COLUMN IF EXISTS coach_id;
ALTER TABLE reservations ADD COLUMN coach_profile_id BIGINT;
ALTER TABLE reservations ADD CONSTRAINT fk_reservation_coach_profile FOREIGN KEY (coach_profile_id) REFERENCES coach_profiles(id) ON DELETE SET NULL;

-- Update reservation_players table to reference player_profiles instead of persons
ALTER TABLE reservation_players DROP CONSTRAINT IF EXISTS reservation_players_person_id_fkey;
ALTER TABLE reservation_players DROP COLUMN IF EXISTS person_id;
ALTER TABLE reservation_players ADD COLUMN player_profile_id BIGINT NOT NULL;
ALTER TABLE reservation_players ADD CONSTRAINT fk_reservation_players_player_profile FOREIGN KEY (player_profile_id) REFERENCES player_profiles(id) ON DELETE CASCADE;
