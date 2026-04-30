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
      // Immediate signal
      subscriber.next({ type: 'PROGRESS', value: 0 });

      setTimeout(() => {
        subscriber.next({ type: 'PROGRESS', value: 50 });
      }, 100);

      setTimeout(() => {
        subscriber.next({ type: 'PROGRESS', value: 100 });
        subscriber.next({ type: 'DATA', value: 'Final Result' });
        subscriber.complete();
      }, 300);
    });
  }
}

module.exports = DataFetcher;
