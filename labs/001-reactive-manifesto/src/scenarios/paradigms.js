const DataFetcher = require('../data-fetcher');

const logger = {
  promise: (msg) => console.log(`\x1b[33m[PROMISE]\x1b[0m ${msg}`),
  observable: (msg) => console.log(`\x1b[34m[OBSERVABLE]\x1b[0m ${msg}`),
  success: (msg) => console.log(`\x1b[32m[DONE]\x1b[0m ${msg}`)
};

async function runParadigmsScenario() {
  console.log('\n=== SCENARIO 2: PROMISES VS OBSERVABLES ===\n');
  
  const fetcher = new DataFetcher();

  console.log('--- Executing PROMISE (Single Value) ---');
  const promiseResult = await fetcher.fetchWithPromise();
  logger.promise(`Received: ${JSON.stringify(promiseResult)}`);
  logger.success('Promise resolution complete.\n');

  console.log('--- Executing OBSERVABLE (Stream of Values) ---');
  fetcher.fetchWithObservable().subscribe({
    next: (val) => logger.observable(`Received Signal: ${JSON.stringify(val)}`),
    complete: () => logger.success('Observable stream complete.')
  });
}

runParadigmsScenario();
