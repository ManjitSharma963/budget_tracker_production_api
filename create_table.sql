-- =========================================
-- Database: expenses_tracker
-- =========================================

SET FOREIGN_KEY_CHECKS = 0;

-- ======================
-- USERS
-- ======================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6)
) ENGINE=InnoDB;

-- ======================
-- CREDITS
-- ======================
CREATE TABLE IF NOT EXISTS credits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(38,2) NOT NULL,
    credit_type VARCHAR(20) NOT NULL,
    creditor VARCHAR(100),
    description VARCHAR(500),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    user_id BIGINT NOT NULL,
    INDEX idx_credits_user (user_id),
    CONSTRAINT fk_credits_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ======================
-- EXPENSES
-- ======================
CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(38,2) NOT NULL,
    category VARCHAR(100),
    description VARCHAR(500),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    user_id BIGINT NOT NULL,
    INDEX idx_expenses_user (user_id),
    CONSTRAINT fk_expenses_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ======================
-- INCOME
-- ======================
CREATE TABLE IF NOT EXISTS income (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(38,2) NOT NULL,
    source VARCHAR(100),
    description VARCHAR(500),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    user_id BIGINT NOT NULL,
    INDEX idx_income_user (user_id),
    CONSTRAINT fk_income_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ======================
-- PARTIES
-- ======================
CREATE TABLE IF NOT EXISTS parties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(500),
    email VARCHAR(100),
    phone VARCHAR(50),
    gst_number VARCHAR(100),
    notes VARCHAR(1000),
    opening_balance DECIMAL(19,2),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    user_id BIGINT NOT NULL,
    INDEX idx_parties_user (user_id),
    CONSTRAINT fk_parties_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ======================
-- LEDGER ENTRIES
-- ======================
CREATE TABLE IF NOT EXISTS ledger_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(19,2) NOT NULL,
    transaction_date DATE NOT NULL,
    transaction_type ENUM('PURCHASE','PAYMENT','ADJUSTMENT') NOT NULL,
    reference_number VARCHAR(100),
    description VARCHAR(500),
    payment_mode VARCHAR(50),
    running_balance DECIMAL(19,2),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    party_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    INDEX idx_ledger_party (party_id),
    INDEX idx_ledger_user (user_id),
    CONSTRAINT fk_ledger_party
        FOREIGN KEY (party_id) REFERENCES parties(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_ledger_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ======================
-- NOTES
-- ======================
CREATE TABLE IF NOT EXISTS notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    note TEXT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    user_id BIGINT NOT NULL,
    INDEX idx_notes_user (user_id),
    CONSTRAINT fk_notes_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ======================
-- TASKS
-- ======================
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    subtitle VARCHAR(500),
    date DATE NOT NULL,
    start_time VARCHAR(5),
    end_time VARCHAR(5),
    status ENUM('pending','completed','running','rejected'),
    created_at DATETIME,
    updated_at DATETIME,
    user_id BIGINT NOT NULL,
    INDEX idx_tasks_user (user_id),
    INDEX idx_tasks_date (date),
    INDEX idx_tasks_status (status),
    CONSTRAINT fk_tasks_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ======================
-- BUDGETS
-- ======================
CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    budget_type VARCHAR(20) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    percentage DECIMAL(5,2),
    period VARCHAR(20) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    user_id BIGINT NOT NULL,
    INDEX idx_budgets_user (user_id),
    INDEX idx_budgets_category (category),
    INDEX idx_budgets_period (period),
    UNIQUE KEY uk_budgets_user_category_period (user_id, category, period),
    CONSTRAINT fk_budgets_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;
