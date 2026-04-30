const { of, throwError } = require('rxjs');
const { catchError, map } = require('rxjs/operators');

console.log('--- ERRORS AS SIGNALS ---');

const stream$ = of(1, 2, 'ERROR_TRIGGER', 4).pipe(
    map(val => {
        if (val === 'ERROR_TRIGGER') {
            throw new Error('Something went wrong in the pipeline!');
        }
        return val * 10;
    }),
    catchError(err => {
        console.log(`[GRACEFUL CATCH] caught: ${err.message}`);
        // Return a fallback value instead of crashing
        return of('FALLBACK_VALUE');
    })
);

stream$.subscribe({
    next: val => console.log(`Received: ${val}`),
    error: err => console.error(`Terminal Error: ${err}`),
    complete: () => console.log('Stream completed gracefully.')
});

console.log('\nNotice that the program did not crash. The error was just another signal we handled.');
