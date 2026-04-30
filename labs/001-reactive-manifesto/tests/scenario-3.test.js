const { interval } = require('rxjs');
const { take, concatMap, delay } = require('rxjs/operators');

describe('Scenario 3: Elasticity & Backpressure (Validation)', () => {
  test('Elasticity: Should process messages sequentially without overlapping', (done) => {
    const results = [];
    const startTime = Date.now();

    // Fast producer: 3 messages, one every 20ms
    const producer$ = interval(20).pipe(take(3));

    // Slow consumer: 50ms per message
    producer$.pipe(
      concatMap(val => {
        return new Promise(resolve => {
          setTimeout(() => {
            results.push({ val, time: Date.now() - startTime });
            resolve();
          }, 50);
        });
      })
    ).subscribe({
      complete: () => {
        // The total time should be at least ~150ms (3 * 50ms processing) 
        // regardless of the 20ms producer speed.
        const totalTime = Date.now() - startTime;
        expect(totalTime).toBeGreaterThanOrEqual(150);
        
        // Ensure values arrived in order
        expect(results.map(r => r.val)).toEqual([0, 1, 2]);
        done();
      }
    });
  });
});
