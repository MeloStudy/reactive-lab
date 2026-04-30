const { Observable, of } = require('rxjs');
const { catchError, map, delay } = require('rxjs/operators');

/**
 * REACTIVE SERVICE: Demonstrates the Reactive Manifesto pillars.
 */
class ReactiveService {
  /**
   * Transforms a stream of data.
   * Demonstrates Resilience by catching errors and providing fallbacks.
   * Demonstrates Responsiveness by using non-blocking operators.
   */
  processStream(data$) {
    return data$.pipe(
      map(data => {
        if (data === 'error') {
          throw new Error('Simulated Resilience Failure');
        }
        return `Reactive Processed: ${data}`;
      }),
      // Pillar: Resilience
      // Instead of crashing, we catch the error and provide a fallback or clean signal
      catchError(err => {
        console.error(`[REACTIVE] Resilience Pillar Active: Handling error -> ${err.message}`);
        return of('Fallback Value');
      })
    );
  }
}

module.exports = ReactiveService;
