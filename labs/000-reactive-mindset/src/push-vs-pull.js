const { of } = require('rxjs');

console.log('--- 1. THE PULL MODEL (Imperative) ---');
let a = 10;
let b = 20;
let sum = a + b;

console.log(`Initial Sum (a+b): ${sum}`);

// Change 'a'
a = 30;
console.log(`Changed 'a' to 30. Current 'a': ${a}`);
console.log(`Sum is still: ${sum} (Wait, shouldn't it be 50?)`);
console.log('In the Pull model, you must manually re-calculate or "pull" the new value.\n');


console.log('--- 2. THE PUSH MODEL (Reactive) ---');
const { BehaviorSubject } = require('rxjs');
const { combineLatest } = require('rxjs');
const { map } = require('rxjs/operators');

const a$ = new BehaviorSubject(10);
const b$ = new BehaviorSubject(20);

// Define a relationship (Declarative)
const sum$ = combineLatest([a$, b$]).pipe(
    map(([valA, valB]) => valA + valB)
);

// Subscribe (React to changes)
sum$.subscribe(currentSum => {
    console.log(`[REACTION] The new sum is: ${currentSum}`);
});

console.log("Changing 'a' to 30 via a$.next(30)...");
a$.next(30); 

console.log('\nIn the Push model, the system "pushes" the change through the pipeline automatically.');
console.log('This is the "Excel Mindset".');
