import {
  BehaviorSubject,
  catchError,
  debounceTime,
  delay,
  EMPTY,
  map,
  of,
  retry,
  Subject,
  Subscription,
  switchMap,
  tap,
} from 'rxjs';
import { fromFetch } from 'rxjs/fetch';

export type StarWarsPerson = {
  name: string;
};

type PeopleResponse = {
  results: StarWarsPerson[];
};

export type PeopleData =
  | { status: 'initial' }
  | { status: 'loading' }
  | { status: 'fetching'; data: StarWarsPerson[] }
  | { status: 'loaded'; data: StarWarsPerson[] }
  | { status: 'error' };

class SwapiStore {
  private getPeople$ = new Subject<void>();
  private getPeopleSubscription$: Subscription | null = null;
  private peopleData$ = new BehaviorSubject<PeopleData>({ status: 'initial' });

  private getPeoplePipe$ = this.getPeople$.pipe(
    debounceTime(300),
    tap(() => {
      const peopleData = this.peopleData$.getValue();
      if (peopleData.status === 'initial' || peopleData.status === 'error') {
        this.peopleData$.next({ status: 'loading' });
      } else if (peopleData.status === 'loaded') {
        this.peopleData$.next({ status: 'fetching', data: peopleData.data });
      }
    }),
    switchMap(() => {
      const maxPage = 5;
      const minPage = 1;
      return fromFetch(
        `https://swapi.dev/api/people?page=${Math.floor(
          Math.random() * (maxPage - minPage) + minPage
        )}`
      ).pipe(
        switchMap((response) => {
          return response.json() as Promise<PeopleResponse>;
        }),
        map((data) => data.results),
        retry({ delay: 1000, count: 3 }),
        catchError(() => {
          this.peopleData$.next({ status: 'error' });
          return EMPTY;
        }),
      );
    }),
  );

  constructor() {
    this.getPeopleSubscription$ = this.getPeoplePipe$.subscribe((data) => {
      this.peopleData$.next({ status: 'loaded', data });
    });
  }

  dispose(): void {
    this.getPeopleSubscription$?.unsubscribe();
  }

  actions = {
    getPeople: () => {
      this.getPeople$.next();
    },
  };

  values = {
    peopleData$: this.peopleData$,
    displayLoader$: this.peopleData$.pipe(
      switchMap((data) => {
        if (data.status === 'loading') {
          return of(true).pipe(delay(500))
        } else {
          return of(false);
        }
      }),
    ),
  };
}

const store = new SwapiStore();

export default store;
