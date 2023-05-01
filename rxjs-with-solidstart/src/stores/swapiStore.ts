import { createSignal } from 'solid-js';
import swapiStore from 'swapi-store';

const [people, setPeople] = createSignal(swapiStore.values.peopleData$.getValue());

swapiStore.values.peopleData$.subscribe((value) => setPeople(value));

const [loading, setLoading] = createSignal(false);

swapiStore.values.displayLoader$.subscribe((value) => setLoading(value));

export const swapiData = people;

export const showLoading = loading;

export const swapiActions = swapiStore.actions;
