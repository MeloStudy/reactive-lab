import sqlite3
import os
import logging

logger = logging.getLogger(__name__)

DB_PATH = 'data/inventory.db'

def get_db_connection():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn

def init_db():
    """Initializes the database, cleans existing data, and seeds fresh data."""
    if not os.path.exists('data'):
        os.makedirs('data')
    
    conn = get_db_connection()
    cursor = conn.cursor()
    
    logger.info("Cleaning up legacy inventory data...")
    cursor.execute('DROP TABLE IF EXISTS inventory')
    
    logger.info("Creating inventory table...")
    cursor.execute('''
        CREATE TABLE inventory (
            product_id TEXT PRIMARY KEY,
            stock_count INTEGER,
            base_price REAL
        )
    ''')
    
    # Fresh Seed Data
    products = [
        ('PROD-001', 100, 29.99),
        ('PROD-002', 50, 49.99),
        ('PROD-003', 0, 15.00),   # Out of stock
        ('PROD-004', 200, 120.00), # Slow product for testing
    ]
    
    logger.info(f"Seeding {len(products)} products into legacy database...")
    cursor.executemany('INSERT INTO inventory VALUES (?, ?, ?)', products)
    
    conn.commit()
    conn.close()
    logger.info("Database initialization completed successfully.")
