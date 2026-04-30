/**
 * LEGACY SERVICE: Demonstrates a brittle, callback-based approach.
 * This service is NOT responsive under load and lacks resilience.
 */
class BrittleService {
  /**
   * Simulates processing data with a callback.
   * If an error occurs, it throws globally, which can crash a basic process.
   */
  processData(data, callback) {
    console.log(`[LEGACY] Received: ${data}`);
    
    // Simulate some work
    setTimeout(() => {
      if (data === 'error') {
        // A common issue in legacy code: throwing inside an async block
        // without proper catch can lead to unhandled exceptions.
        throw new Error('Critical System Failure');
      }
      
      const result = `Processed: ${data}`;
      callback(null, result);
    }, 100);
  }
}

module.exports = BrittleService;
