const DataFetcher = require('../src/data-fetcher');

describe('Scenario 2: Promises vs Observables (Refined)', () => {
  let fetcher;

  beforeEach(() => {
    fetcher = new DataFetcher();
  });

  test('Observable: Should emit progress and final data in sequence', (done) => {
    const events = [];
    fetcher.fetchWithObservable().subscribe({
      next: (val) => events.push(val),
      complete: () => {
        expect(events[0]).toEqual({ type: 'PROGRESS', value: 0 });
        expect(events[events.length - 1]).toEqual({ type: 'DATA', value: 'Final Result' });
        expect(events.length).toBeGreaterThan(2);
        done();
      }
    });
  });

  test('Promise: Should return only the final result', async () => {
    const result = await fetcher.fetchWithPromise();
    expect(result).toEqual({ data: 'Final Result' });
  });
});
