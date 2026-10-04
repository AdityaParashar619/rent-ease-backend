-- ==========================================================
-- RentEase: V2 Seed Data
-- Initial Master Data, Roles, Localities, Categories, and Demo Listings
-- ==========================================================

-- 1. Roles
INSERT INTO roles (id, name) VALUES
('role_customer', 'ROLE_CUSTOMER'),
('role_provider', 'ROLE_PROVIDER'),
('role_broker', 'ROLE_BROKER'),
('role_admin', 'ROLE_ADMIN'),
('role_super_admin', 'ROLE_SUPER_ADMIN');

-- 2. Cities & Localities
INSERT INTO cities (id, name, state, country, is_active) VALUES
('city_bengaluru', 'Bengaluru', 'Karnataka', 'India', TRUE),
('city_mumbai', 'Mumbai', 'Maharashtra', 'India', TRUE),
('city_delhi', 'Delhi NCR', 'Delhi', 'India', TRUE);

INSERT INTO localities (id, city_id, name, pincode, latitude, longitude) VALUES
('loc_indiranagar', 'city_bengaluru', 'Indiranagar', '560038', 12.9784, 77.6408),
('loc_koramangala', 'city_bengaluru', 'Koramangala', '560034', 12.9352, 77.6245),
('loc_whitefield', 'city_bengaluru', 'Whitefield', '560066', 12.9698, 77.7500),
('loc_hsr', 'city_bengaluru', 'HSR Layout', '560102', 12.9121, 77.6446),
('loc_mg_road', 'city_bengaluru', 'MG Road', '560001', 12.9756, 77.6066),
('loc_jp_nagar', 'city_bengaluru', 'JP Nagar', '560078', 12.9063, 77.5857);

-- 3. Categories & Subcategories
INSERT INTO categories (id, name, display_label, description, icon_name) VALUES
('cat_residential', 'RESIDENTIAL', 'Residential Rentals', 'Flats, Independent Villas, Studio PGs, and Co-Living Spaces', 'Home'),
('cat_vehicle', 'VEHICLE', 'Self-Drive Vehicles', 'Cars, SUVs, Electric Vehicles, and Premium Motorcycles', 'Car'),
('cat_commercial', 'COMMERCIAL', 'Commercial & Workspaces', 'Retail Shops, Private Offices, Warehouses, and Hot Desks', 'Briefcase'),
('cat_event', 'EVENT', 'Venues & Event Spaces', 'Marriage Gardens, Banquet Halls, Party Lawns, and Auditoriums', 'Sparkles');

INSERT INTO sub_categories (id, category_id, code, display_label) VALUES
('sub_flat', 'cat_residential', 'APARTMENT', 'Gated Society Flat'),
('sub_villa', 'cat_residential', 'VILLA', 'Independent Villa'),
('sub_pg', 'cat_residential', 'PG_COLIVING', 'Co-Living / PG Room'),
('sub_car', 'cat_vehicle', 'CAR', 'Self-Drive Car'),
('sub_bike', 'cat_vehicle', 'BIKE', 'Motorbike / Scooter'),
('sub_office', 'cat_commercial', 'OFFICE', 'Private Corporate Office'),
('sub_shop', 'cat_commercial', 'SHOP', 'Commercial Retail Shop'),
('sub_coworking', 'cat_commercial', 'COWORKING', 'Coworking Hot Desk'),
('sub_banquet', 'cat_event', 'BANQUET_HALL', 'Banquet Hall'),
('sub_lawn', 'cat_event', 'MARRIAGE_GARDEN', 'Open-Air Marriage Garden');

-- 4. Amenities
INSERT INTO amenities (id, name, category, icon) VALUES
('am_wifi', 'High-Speed Wi-Fi', 'GENERAL', 'Wifi'),
('am_power_backup', '100% Power Backup', 'GENERAL', 'Zap'),
('am_lift', 'High-Speed Elevator', 'RESIDENTIAL', 'ArrowUp'),
('am_security', '24/7 Gated Security & CCTV', 'GENERAL', 'Shield'),
('am_parking', 'Dedicated Reserved Parking', 'GENERAL', 'Car'),
('am_ac', 'Split Air Conditioning', 'GENERAL', 'Wind'),
('am_gym', 'Fitness Center / Gym', 'RESIDENTIAL', 'Dumbbell'),
('am_swimming_pool', 'Swimming Pool', 'RESIDENTIAL', 'Waves');
