import { PeopleData, StarWarsPerson, SwapiStore } from 'swapi-store-lib';

export type PeopleDataState =
  | { type: 'initial' }
  | { type: 'loading' }
  | { type: 'loaded', data: StarWarsPerson[] }
  | { type: 'fetching', data: StarWarsPerson[] }
  | { type: 'error' }

type PeopleDataEvent =
  | PeopleData.Initial
  | PeopleData.Loading
  | PeopleData.Loaded
  | PeopleData.Fetching
  | PeopleData.Error;

export function getPeopleDataState(event: PeopleDataEvent): PeopleDataState {
  if (event instanceof PeopleData.Initial) {
    return { type: 'initial' };
  } if (event instanceof PeopleData.Loading) {
    return { type: 'loading' };
  } if (event instanceof PeopleData.Loaded) {
    return { type: 'loaded', data: event.data.toArray() };
  } if (event instanceof PeopleData.Fetching) {
    return { type: 'fetching', data: event.data.toArray() };
  }
  return { type: 'error' };
}

export const swapiStore = new SwapiStore();
