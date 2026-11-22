-- ============================================================
-- Contact Management System — Database Schema (DDL)
-- Group 17 | Object Oriented Programming in Java (CSE2003)
-- Database Architecture for MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS java_fullstack
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE java_fullstack;

-- 1. Main Contacts Table (Demonstrates Inheritance & Relational Mapping)
CREATE TABLE IF NOT EXISTS cms_contacts (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    contact_type ENUM('PERSONAL', 'BUSINESS', 'EMERGENCY') NOT NULL DEFAULT 'PERSONAL',
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) DEFAULT '',
    email VARCHAR(150) DEFAULT '',
    secondary_email VARCHAR(150) DEFAULT '',
    street VARCHAR(200) DEFAULT '',
    city VARCHAR(100) DEFAULT '',
    state VARCHAR(100) DEFAULT '',
    postal_code VARCHAR(20) DEFAULT '',
    country VARCHAR(100) DEFAULT 'India',
    contact_group VARCHAR(50) DEFAULT 'OTHER',
    is_favorite BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE, -- Soft-delete support
    avatar_color_hex VARCHAR(10) DEFAULT '#3B82F6',
    notes TEXT,
    
    -- Subtype Specific Attributes (Table-per-Hierarchy Strategy)
    -- For PERSONAL: specific_data1 = relationship, specific_data2 = birthday, specific_data3 = nickname
    -- For BUSINESS: specific_data1 = company, specific_data2 = job_title, specific_data3 = department
    -- For EMERGENCY: specific_data1 = priority, specific_data2 = blood_group, specific_data3 = alt_phone
    specific_data1 VARCHAR(255) DEFAULT '',
    specific_data2 VARCHAR(255) DEFAULT '',
    specific_data3 VARCHAR(255) DEFAULT '',
    
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_contact_name (first_name, last_name),
    INDEX idx_contact_type (contact_type),
    INDEX idx_contact_favorite (is_favorite),
    INDEX idx_contact_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Phone Numbers Table (1-to-Many Relationship, Cascade Delete)
CREATE TABLE IF NOT EXISTS cms_phone_numbers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    contact_id VARCHAR(50) NOT NULL,
    phone_type ENUM('MOBILE', 'HOME', 'WORK', 'OTHER') NOT NULL DEFAULT 'MOBILE',
    phone_number VARCHAR(30) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_phone_contact
        FOREIGN KEY (contact_id) REFERENCES cms_contacts(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_phone_number (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Contact Tags Table (Many-to-Many normalized tag index)
CREATE TABLE IF NOT EXISTS cms_contact_tags (
    contact_id VARCHAR(50) NOT NULL,
    tag VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (contact_id, tag),
    CONSTRAINT fk_tag_contact
        FOREIGN KEY (contact_id) REFERENCES cms_contacts(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_tag (tag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Call & Interaction History Table
CREATE TABLE IF NOT EXISTS cms_call_logs (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    contact_id VARCHAR(50) NOT NULL,
    call_type ENUM('INCOMING', 'OUTGOING', 'MISSED') NOT NULL DEFAULT 'OUTGOING',
    call_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    duration_sec INT NOT NULL DEFAULT 0,
    notes TEXT,
    
    CONSTRAINT fk_call_contact
        FOREIGN KEY (contact_id) REFERENCES cms_contacts(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_call_time (call_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Audit Trail & Activity Log Table
CREATE TABLE IF NOT EXISTS cms_audit_logs (
    id VARCHAR(50) NOT NULL PRIMARY KEY,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    action VARCHAR(50) NOT NULL,
    contact_id VARCHAR(50) DEFAULT 'N/A',
    contact_name VARCHAR(150) DEFAULT 'N/A',
    details TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
