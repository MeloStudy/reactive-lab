const { from } = require('rxjs');
const ReactiveService = require('../reactive-service');
const BrittleService = require('../brittle-service');

const logger = {
  info: (msg) => console.log(`\x1b[36m[INFO]\x1b[0m ${msg}`),
  success: (msg) => console.log(`\x1b[32m[SUCCESS]\x1b[0m ${msg}`),
  error: (msg) => console.log(`\x1b[31m[ERROR]\x1b[0m ${msg}`),
  signal: (msg) => console.log(`\x1b[35m[SIGNAL]\x1b[0m ${msg}`)
};

async function runManifestoScenario() {
  console.log('\n=== SCENARIO 1: THE REACTIVE MANIFESTO PILLARS ===\n');
  
  const reactiveService = new ReactiveService();
  const brittleService = new BrittleService();

  logger.info('Running LEGACY Brittle Service with "error" input...');
  try {
    brittleService.processData('error', (err, res) => {
      if (err) logger.error(`Legacy handled error: ${err.message}`);
      else logger.success(res);
    });
  } catch (e) {
    logger.error(`Legacy service CRASHED the execution stack: ${e.message}`);
  }

  // Wait a bit for legacy to finish (it won't crash the whole script because of catch, but demonstrates the point)
  await new Promise(r => setTimeout(r, 200));

  console.log('\n--------------------------------------------------\n');

  logger.info('Running REACTIVE Service with stream containing "error"...');
  const source$ = from(['valid-1', 'error', 'valid-2']);

  reactiveService.processStream(source$).subscribe({
    next: (val) => logger.signal(`onNext: ${val}`),
    error: (err) => logger.error(`onError: ${err.message}`),
    complete: () => logger.success('onComplete: Stream finished gracefully (Resilience Pillar)')
  });
}

runManifestoScenario();
