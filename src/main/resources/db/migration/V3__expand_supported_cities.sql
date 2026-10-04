-- Expand the location master data without changing existing city or locality IDs.
ALTER TABLE listings ADD COLUMN IF NOT EXISTS address_pincode VARCHAR(20);
INSERT INTO cities (id, name, state, country, is_active) VALUES
('city_hyderabad', 'Hyderabad', 'Telangana', 'India', TRUE),
('city_pune', 'Pune', 'Maharashtra', 'India', TRUE),
('city_chennai', 'Chennai', 'Tamil Nadu', 'India', TRUE),
('city_kolkata', 'Kolkata', 'West Bengal', 'India', TRUE),
('city_ahmedabad', 'Ahmedabad', 'Gujarat', 'India', TRUE),
('city_jaipur', 'Jaipur', 'Rajasthan', 'India', TRUE),
('city_indore', 'Indore', 'Madhya Pradesh', 'India', TRUE),
('city_bhopal', 'Bhopal', 'Madhya Pradesh', 'India', TRUE),
('city_gurugram', 'Gurugram', 'Haryana', 'India', TRUE),
('city_noida', 'Noida', 'Uttar Pradesh', 'India', TRUE),
('city_chandigarh', 'Chandigarh', 'Chandigarh', 'India', TRUE)
ON CONFLICT (id) DO NOTHING;

-- Coordinates remain NULL until verified locality coordinates are supplied.
INSERT INTO localities (id, city_id, name, pincode, latitude, longitude) VALUES
('loc_mumbai_andheri', 'city_mumbai', 'Andheri', '', NULL, NULL),
('loc_mumbai_bandra', 'city_mumbai', 'Bandra', '', NULL, NULL),
('loc_delhi_connaught_place', 'city_delhi', 'Connaught Place', '', NULL, NULL),
('loc_delhi_dwarka', 'city_delhi', 'Dwarka', '', NULL, NULL),
('loc_hyderabad_gachibowli', 'city_hyderabad', 'Gachibowli', '', NULL, NULL),
('loc_hyderabad_hitec_city', 'city_hyderabad', 'HITEC City', '', NULL, NULL),
('loc_pune_baner', 'city_pune', 'Baner', '', NULL, NULL),
('loc_pune_hinjawadi', 'city_pune', 'Hinjawadi', '', NULL, NULL),
('loc_chennai_t_nagar', 'city_chennai', 'T. Nagar', '', NULL, NULL),
('loc_chennai_omr', 'city_chennai', 'OMR', '', NULL, NULL),
('loc_kolkata_salt_lake', 'city_kolkata', 'Salt Lake', '', NULL, NULL),
('loc_kolkata_park_street', 'city_kolkata', 'Park Street', '', NULL, NULL),
('loc_ahmedabad_prahlad_nagar', 'city_ahmedabad', 'Prahlad Nagar', '', NULL, NULL),
('loc_jaipur_malviya_nagar', 'city_jaipur', 'Malviya Nagar', '', NULL, NULL),
('loc_indore_vijay_nagar', 'city_indore', 'Vijay Nagar', '', NULL, NULL),
('loc_bhopal_arera_colony', 'city_bhopal', 'Arera Colony', '', NULL, NULL),
('loc_gurugram_cyber_city', 'city_gurugram', 'Cyber City', '', NULL, NULL),
('loc_noida_sector_62', 'city_noida', 'Sector 62', '', NULL, NULL),
('loc_chandigarh_sector_17', 'city_chandigarh', 'Sector 17', '', NULL, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sub_categories (id, category_id, code, display_label)
VALUES ('sub_warehouse', 'cat_commercial', 'WAREHOUSE', 'Warehouse')
ON CONFLICT (id) DO NOTHING;

INSERT INTO amenities (id, name, category, icon) VALUES
('am_ui_security', '24/7 Gated Security', 'GENERAL', 'Shield'),
('am_ui_backup', 'Power Backup', 'GENERAL', 'Zap'),
('am_ui_parking', 'Covered Car Parking', 'GENERAL', 'Car'),
('am_ui_elevator', 'Elevator', 'GENERAL', 'ArrowUp'),
('am_ui_gym', 'Gymnasium', 'GENERAL', 'Dumbbell'),
('am_ui_pool', 'Swimming Pool', 'GENERAL', 'Waves'),
('am_ui_ac', 'Air Conditioning', 'GENERAL', 'Wind'),
('am_ui_balcony', 'Balcony', 'GENERAL', 'Building'),
('am_ui_pets', 'Pet Friendly', 'GENERAL', 'PawPrint'),
('am_ui_kitchen', 'Modular Kitchen', 'GENERAL', 'Utensils'),
('am_ui_gas', 'Gas Pipeline', 'GENERAL', 'Flame')
ON CONFLICT (id) DO NOTHING;

-- New listings start unrated. Existing records are preserved.
ALTER TABLE listings ALTER COLUMN rating SET DEFAULT 0;
