import { readable } from 'svelte/store';
import _swapiStore from 'swapi-store';

export const swapiData = readable(
  _swapiStore.values.peopleData$.getValue(),
  (set) => {
    const subscription = _swapiStore.values.peopleData$.subscribe(set);
    return () => {
      subscription.unsubscribe();
    }
  }
);

export const swapiActions = _swapiStore.actions;

export const showLoading = readable(
  false,
  (set) => {
    const subscription = _swapiStore.values.displayLoader$.subscribe(set);
    return () => {
      subscription.unsubscribe();
    }
  }
);
