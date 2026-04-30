const { Observable } = require('rxjs');

class DataFetcher {
  /**
   * Traditional Promise approach: One value, one time.
   */
  fetchWithPromise() {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({ data: 'Final Result' });
      }, 300);
    });
  }

  /**
   * Reactive Observable approach: Multiple values over time (Progress + Result).
   */
  fetchWithObservable() {
    return new Observable((subscriber) => {
      // We use explicit timing to demonstrate the stream lifecycle
      subscriber.next({ type: 'PROGRESS', value: 0 });

      // In a real app, these would be network events or database stream chunks
      const t1 = setTimeout(() => {
        subscriber.next({ type: 'PROGRESS', value: 50 });
      }, 100);

      const t2 = setTimeout(() => {
        subscriber.next({ type: 'PROGRESS', value: 100 });
        subscriber.next({ type: 'DATA', value: 'Final Result' });
        subscriber.complete();
      }, 300);

      // Cleanup logic is crucial in Reactive systems to avoid memory leaks
      return () => {
        clearTimeout(t1);
        clearTimeout(t2);
      };
    });
  }
}

module.exports = DataFetcher;
