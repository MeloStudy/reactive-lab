const { BehaviorSubject, of } = require('rxjs');
const { map, catchError } = require('rxjs/operators');

describe('LAB-000: Reactive Mindset Validation', () => {

    test('Push Model: should automatically update derived values', (done) => {
        const a$ = new BehaviorSubject(10);
        const b$ = new BehaviorSubject(20);
        
        let lastSum = 0;
        
        // Define relationship
        const sum$ = a$.pipe(
            map(valA => valA + b$.value)
        );

        sum$.subscribe(sum => {
            lastSum = sum;
            if (lastSum === 50) {
                expect(lastSum).toBe(50);
                done();
            }
        });

        expect(lastSum).toBe(30);

        // Push new value
        a$.next(30);
    });

    test('Errors as Signals: should catch errors without crashing', (done) => {
        const errorStream$ = of(1).pipe(
            map(() => { throw new Error('Explosion!'); }),
            catchError(err => of('safe-landing'))
        );

        errorStream$.subscribe({
            next: (val) => {
                expect(val).toBe('safe-landing');
            },
            complete: () => done()
        });
    });

});
