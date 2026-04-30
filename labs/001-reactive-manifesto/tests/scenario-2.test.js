const DataFetcher = require('../src/data-fetcher');

describe('Scenario 2: Promises vs Observables', () => {
  let fetcher;

  beforeEach(() => {
    fetcher = new DataFetcher();
  });

  test('Promise: Should return only one final value', async () => {
    const result = await fetcher.fetchWithPromise();
    expect(result).toEqual({ data: 'Final Result' });
  });

  test('Observable: Should emit multiple values (progress and final result)', (done) => {
    const events = [];
    fetcher.fetchWithObservable().subscribe({
      next: (event) => events.push(event),
      complete: () => {
        // Verify multiple emissions
        expect(events.length).toBeGreaterThan(1);
        
        // Verify progress sequence
        const progressValues = events.filter(e => e.type === 'PROGRESS').map(e => e.value);
        expect(progressValues).toEqual([0, 50, 100]);
        
        // Verify final data
        const finalData = events.find(e => e.type === 'DATA');
        expect(finalData.value).toBe('Final Result');
        
        done();
      }
    });
  });
});
