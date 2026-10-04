-- ==========================================================
-- RentEase: V1 Initial Relational Schema
-- Normalized PostgreSQL DDL for Multi-Category Rental Engine
-- ==========================================================

-- 1. Locations
CREATE TABLE cities (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL DEFAULT 'India',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE localities (
    id VARCHAR(64) PRIMARY KEY,
    city_id VARCHAR(64) NOT NULL REFERENCES cities(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    pincode VARCHAR(20) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_localities_city_id ON localities(city_id);

-- 2. Categories and Sub-Categories
CREATE TABLE categories (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    display_label VARCHAR(100) NOT NULL,
    description TEXT,
    icon_name VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sub_categories (
    id VARCHAR(64) PRIMARY KEY,
    category_id VARCHAR(64) NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL UNIQUE,
    display_label VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE amenities (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    icon VARCHAR(50)
);

-- 3. Users, Roles and RBAC
CREATE TABLE roles (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    id VARCHAR(64) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(30) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    avatar_url TEXT,
    account_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    kyc_verified BOOLEAN NOT NULL DEFAULT FALSE,
    trust_score INT DEFAULT 95,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);

CREATE TABLE user_roles (
    user_id VARCHAR(64) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id VARCHAR(64) NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE refresh_tokens (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE provider_profiles (
    user_id VARCHAR(64) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    provider_type VARCHAR(30) NOT NULL, -- DIRECT_OWNER, BROKER, FLEET_OPERATOR, VENUE_MANAGER
    business_name VARCHAR(150),
    description TEXT,
    response_rate INT DEFAULT 98,
    response_time VARCHAR(50) DEFAULT 'Within 15 mins',
    rating DOUBLE PRECISION DEFAULT 5.0,
    total_rentals INT DEFAULT 0,
    verification_status VARCHAR(30) DEFAULT 'VERIFIED'
);

-- 4. Listings & Multi-Category Detail Tables
CREATE TABLE listings (
    id VARCHAR(64) PRIMARY KEY,
    provider_id VARCHAR(64) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id VARCHAR(64) NOT NULL REFERENCES categories(id),
    sub_category_id VARCHAR(64) NOT NULL REFERENCES sub_categories(id),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    pricing_unit VARCHAR(30) NOT NULL, -- /month, /day, /event, /sqft/mo
    security_deposit NUMERIC(12, 2) NOT NULL DEFAULT 0,
    service_fee NUMERIC(12, 2) NOT NULL DEFAULT 0,
    owner_type VARCHAR(30) NOT NULL, -- DIRECT_OWNER, BROKER
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- DRAFT, PENDING_VERIFICATION, ACTIVE, REJECTED, SUSPENDED
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_date TIMESTAMP WITH TIME ZONE,
    city_id VARCHAR(64) NOT NULL REFERENCES cities(id),
    locality_id VARCHAR(64) NOT NULL REFERENCES localities(id),
    masked_address VARCHAR(255) NOT NULL,
    full_address TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    rating DOUBLE PRECISION DEFAULT 5.0,
    review_count INT DEFAULT 0,
    featured BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_listings_city_cat ON listings(city_id, category_id, status);
CREATE INDEX idx_listings_price ON listings(price);
CREATE INDEX idx_listings_locality ON listings(locality_id);

CREATE TABLE property_details (
    listing_id VARCHAR(64) PRIMARY KEY REFERENCES listings(id) ON DELETE CASCADE,
    bedrooms INT,
    bathrooms INT,
    carpet_area_sq_ft INT,
    furnishing_type VARCHAR(30), -- FURNISHED, SEMI_FURNISHED, UNFURNISHED
    maintenance_monthly NUMERIC(10, 2) DEFAULT 0,
    gender_preference VARCHAR(30) DEFAULT 'ANY',
    food_included BOOLEAN DEFAULT FALSE,
    pet_friendly BOOLEAN DEFAULT TRUE,
    is_gated_society BOOLEAN DEFAULT TRUE,
    floor_number INT,
    total_floors INT,
    available_from DATE
);

CREATE TABLE vehicle_details (
    listing_id VARCHAR(64) PRIMARY KEY REFERENCES listings(id) ON DELETE CASCADE,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INT NOT NULL,
    transmission VARCHAR(30) NOT NULL, -- AUTOMATIC, MANUAL
    fuel_type VARCHAR(30) NOT NULL, -- PETROL, DIESEL, ELECTRIC, HYBRID
    seating_capacity INT NOT NULL,
    registration_number_masked VARCHAR(50),
    daily_km_limit INT DEFAULT 300,
    extra_km_charge NUMERIC(8, 2) DEFAULT 12.0
);

CREATE TABLE commercial_details (
    listing_id VARCHAR(64) PRIMARY KEY REFERENCES listings(id) ON DELETE CASCADE,
    carpet_area_sq_ft INT NOT NULL,
    suitable_for VARCHAR(100),
    furnishing_status VARCHAR(50),
    power_backup BOOLEAN DEFAULT TRUE,
    reserved_parking_spots INT DEFAULT 2,
    conference_rooms INT DEFAULT 1
);

CREATE TABLE venue_details (
    listing_id VARCHAR(64) PRIMARY KEY REFERENCES listings(id) ON DELETE CASCADE,
    guest_capacity INT NOT NULL,
    indoor_outdoor VARCHAR(50), -- INDOOR, OUTDOOR, BOTH
    parking_capacity INT,
    rooms_available INT DEFAULT 4,
    catering_policy VARCHAR(100),
    music_curfew VARCHAR(50) DEFAULT '11:00 PM',
    has_lawn BOOLEAN DEFAULT TRUE
);

CREATE TABLE listing_images (
    id VARCHAR(64) PRIMARY KEY,
    listing_id VARCHAR(64) NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    display_order INT DEFAULT 0,
    is_cover BOOLEAN DEFAULT FALSE
);

CREATE TABLE listing_amenities (
    listing_id VARCHAR(64) NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    amenity_id VARCHAR(64) NOT NULL REFERENCES amenities(id) ON DELETE CASCADE,
    PRIMARY KEY (listing_id, amenity_id)
);

-- 5. Bookings & Concurrency Slot Locking
CREATE TABLE bookings (
    id VARCHAR(64) PRIMARY KEY,
    booking_reference VARCHAR(50) NOT NULL UNIQUE,
    customer_id VARCHAR(64) NOT NULL REFERENCES users(id),
    listing_id VARCHAR(64) NOT NULL REFERENCES listings(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'CONFIRMED', -- REQUESTED, CONFIRMED, ACTIVE, COMPLETED, CANCELLED, DISPUTED
    base_amount NUMERIC(12, 2) NOT NULL,
    service_fee NUMERIC(12, 2) NOT NULL,
    security_deposit NUMERIC(12, 2) NOT NULL,
    tax_amount NUMERIC(12, 2) NOT NULL,
    discount_amount NUMERIC(12, 2) DEFAULT 0,
    total_amount NUMERIC(12, 2) NOT NULL,
    cancellation_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_bookings_listing_dates ON bookings(listing_id, start_date, end_date);
CREATE INDEX idx_bookings_customer ON bookings(customer_id);

-- 6. Payments & Escrow Ledger
CREATE TABLE payments (
    id VARCHAR(64) PRIMARY KEY,
    booking_id VARCHAR(64) NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    payment_method VARCHAR(50) NOT NULL, -- UPI, CREDIT_CARD, NET_BANKING, ESCROW_TRANSFER
    status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS', -- PENDING, SUCCESS, FAILED, REFUNDED
    amount NUMERIC(12, 2) NOT NULL,
    gateway_reference VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE refunds (
    id VARCHAR(64) PRIMARY KEY,
    payment_id VARCHAR(64) NOT NULL REFERENCES payments(id),
    booking_id VARCHAR(64) NOT NULL REFERENCES bookings(id),
    amount NUMERIC(12, 2) NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PROCESSED',
    processed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 7. Reviews
CREATE TABLE reviews (
    id VARCHAR(64) PRIMARY KEY,
    booking_id VARCHAR(64) NOT NULL UNIQUE REFERENCES bookings(id),
    listing_id VARCHAR(64) NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    reviewer_id VARCHAR(64) NOT NULL REFERENCES users(id),
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT NOT NULL,
    accuracy_score INT DEFAULT 5,
    cleanliness_score INT DEFAULT 5,
    communication_score INT DEFAULT 5,
    value_score INT DEFAULT 5,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8. Wishlist
CREATE TABLE wishlist_items (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    listing_id VARCHAR(64) NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_listing_wishlist UNIQUE (user_id, listing_id)
);

-- 9. Verification Documents & Compliance
CREATE TABLE verification_documents (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    listing_id VARCHAR(64) REFERENCES listings(id) ON DELETE SET NULL,
    document_type VARCHAR(50) NOT NULL, -- AADHAAR_CARD, TITLE_DEED, VEHICLE_REGISTRATION, PROPERTY_TAX, COMMERCIAL_LEASE
    document_url TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, VERIFIED, REJECTED
    reviewer_notes TEXT,
    reviewed_by VARCHAR(64) REFERENCES users(id),
    reviewed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 10. Disputes & Mediation
CREATE TABLE disputes (
    id VARCHAR(64) PRIMARY KEY,
    booking_id VARCHAR(64) NOT NULL REFERENCES bookings(id),
    listing_id VARCHAR(64) NOT NULL REFERENCES listings(id),
    claimant_id VARCHAR(64) NOT NULL REFERENCES users(id),
    respondent_id VARCHAR(64) NOT NULL REFERENCES users(id),
    dispute_type VARCHAR(50) NOT NULL, -- PROPERTY_DAMAGE, MISREPRESENTATION, SECURITY_DEPOSIT, CANCELLATION
    subject VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED', -- SUBMITTED, UNDER_REVIEW, RESOLVED, CLOSED
    resolution_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE dispute_messages (
    id VARCHAR(64) PRIMARY KEY,
    dispute_id VARCHAR(64) NOT NULL REFERENCES disputes(id) ON DELETE CASCADE,
    sender_id VARCHAR(64) NOT NULL REFERENCES users(id),
    message TEXT NOT NULL,
    attachment_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 11. Notifications
CREATE TABLE notifications (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL, -- BOOKING, PAYMENT, VERIFICATION, DISPUTE, SYSTEM
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    action_url VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 12. Security Audit Logs
CREATE TABLE audit_logs (
    id VARCHAR(64) PRIMARY KEY,
    actor_id VARCHAR(64),
    action VARCHAR(100) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id VARCHAR(64),
    ip_address VARCHAR(45),
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
