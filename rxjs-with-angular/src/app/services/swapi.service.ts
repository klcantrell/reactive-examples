import { Injectable } from '@angular/core';
import { map } from 'rxjs';

import swapiStore from 'swapi-store';

@Injectable({
  providedIn: 'root',
})
export class SwapiService {
  getSwapiPeople() {
    swapiStore.actions.getPeople();
  }

  get swapiStatus() {
    return swapiStore.values.peopleData$.pipe(map((data) => data.status));
  }

  get swapiData() {
    return swapiStore.values.peopleData$.pipe(
      map((data) => {
        if (data.status === 'fetching' || data.status === 'loaded') {
          return data.data;
        } else {
          return null;
        }
      })
    );
  }

  get showLoading() {
    return swapiStore.values.displayLoader$;
  }
}
