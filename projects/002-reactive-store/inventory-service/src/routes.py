import time
import random
import logging
from flask import Blueprint, request, jsonify
from .database import get_db_connection

logger = logging.getLogger(__name__)
inventory_bp = Blueprint('inventory', __name__)

@inventory_bp.route('/inventory/<product_id>', methods=['GET'])
def get_inventory(product_id):
    correlation_id = request.headers.get('X-Correlation-ID', 'UNKNOWN')
    logger.info(f"[{correlation_id}] Legacy Inventory Request: {product_id}")

    # Simulation of Legacy System slowness for specific products
    if product_id == 'PROD-004':
        delay = random.uniform(1.0, 2.0)
        logger.warning(f"[{correlation_id}] Simulating legacy latency: {delay:.2f}s")
        time.sleep(delay)

    conn = get_db_connection()
    product = conn.execute('SELECT * FROM inventory WHERE product_id = ?', (product_id,)).fetchone()
    conn.close()

    if product is None:
        logger.error(f"[{correlation_id}] Product not found: {product_id}")
        return jsonify({"error": "Product not found"}), 404

    return jsonify({
        "productId": product['product_id'],
        "stock": product['stock_count'],
        "price": product['base_price'],
        "status": "AVAILABLE" if product['stock_count'] > 0 else "OUT_OF_STOCK"
    })
