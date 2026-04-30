const { of } = require('rxjs');
const ReactiveService = require('../src/reactive-service');

describe('Scenario 1: Reactive Manifesto Pillars', () => {
  let service;

  beforeEach(() => {
    service = new ReactiveService();
  });

  test('Resilience: Should catch errors and provide fallback without crashing', (done) => {
    const source$ = of('valid-data', 'error', 'more-data');
    
    const results = [];
    service.processStream(source$).subscribe({
      next: (val) => results.push(val),
      complete: () => {
        // The stream should have the first valid data and then the fallback
        // Note: catchError in the service as implemented completes the stream after fallback
        expect(results).toContain('Reactive Processed: valid-data');
        expect(results).toContain('Fallback Value');
        done();
      }
    });
  });

  test('Responsiveness: Should return values via non-blocking stream', (done) => {
    const source$ = of('fast-data');
    const start = Date.now();

    service.processStream(source$).subscribe({
      next: (val) => {
        const duration = Date.now() - start;
        expect(val).toBe('Reactive Processed: fast-data');
        expect(duration).toBeLessThan(100); // Should be near-instant
      },
      complete: () => done()
    });
  });
});
