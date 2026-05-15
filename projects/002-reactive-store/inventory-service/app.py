import logging
from flask import Flask
from src import init_db, inventory_bp

# Setup structured logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(name)s: %(message)s'
)
logger = logging.getLogger(__name__)

def create_app():
    app = Flask(__name__)
    
    # Initialize and Seed Database
    logger.info("Initializing Legacy System...")
    init_db()
    
    # Register Blueprints
    app.register_blueprint(inventory_bp)
    
    return app

app = create_app()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
