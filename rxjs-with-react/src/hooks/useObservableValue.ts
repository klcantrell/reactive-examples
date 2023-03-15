import { useEffect, useState } from 'react';

type Observable<T> = {
  subscribe: (observerFunction: (value: T) => void) => Subscription;
}

type Subscription = {
  unsubscribe: () => void;
}

export default function useObservableState<T = undefined>(subject: Observable<T | undefined>): T | undefined;
export default function useObservableState<T>(subject: Observable<T>, initialState: T): T;

export default function useObservableState<T>(subject: Observable<T>, initialState?: T): T | undefined {
  const [value, setValue] = useState(initialState);

  useEffect(() => {
    const subscription = subject.subscribe(setValue);
    return () => {
      return subscription.unsubscribe();
    };
  }, [subject]);

  return value;
}
