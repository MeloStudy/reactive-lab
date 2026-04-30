const { interval } = require('rxjs');
const { take, map, concatMap, delay } = require('rxjs/operators');

const logger = {
  producer: (msg) => console.log(`\x1b[35m[PRODUCER]\x1b[0m ${msg}`),
  consumer: (msg) => console.log(`\x1b[32m[CONSUMER]\x1b[0m ${msg}`),
  info: (msg) => console.log(`\x1b[36m[INFO]\x1b[0m ${msg}`)
};

async function runElasticityScenario() {
  console.log('\n=== SCENARIO 3: ELASTICITY & BACKPRESSURE ===\n');
  
  logger.info('Simulating a FAST Producer (1 message every 50ms)');
  logger.info('And a SLOW Consumer (processing takes 200ms per message)');
  console.log('Observe how the Consumer maintains its own pace without crashing.\n');

  const fastProducer$ = interval(50).pipe(
    take(5),
    map(i => `Message #${i + 1}`)
  );

  fastProducer$.pipe(
    // concatMap ensures we process one message at a time, effectively "buffering"
    // the fast incoming stream to match the consumer's speed.
    concatMap(msg => {
      logger.producer(`Sent: ${msg}`);
      // Simulate slow processing
      return new Promise(resolve => {
        setTimeout(() => {
          logger.consumer(`Processed: ${msg}`);
          resolve();
        }, 200);
      });
    })
  ).subscribe({
    complete: () => {
      console.log('\n');
      logger.info('Elasticity demonstrated: The system stayed responsive and did not overflow.');
      logger.info('This decoupling is a core benefit of the Message-Driven pillar.');
    }
  });
}

runElasticityScenario();
