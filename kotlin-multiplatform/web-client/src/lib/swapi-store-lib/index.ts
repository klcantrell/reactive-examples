/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable import/prefer-default-export */

import { com as SwapiStoreLib } from 'swapi-store';

export import StarWarsPerson = SwapiStoreLib.kal.swapistore.StarWarsPerson;
export import SwapiStore = SwapiStoreLib.kal.swapistore.SwapiStore;
export import PeopleList = SwapiStoreLib.kal.swapistore.PeopleList;

export namespace PeopleData {
  export import Initial = SwapiStoreLib.kal.swapistore.PeopleData.Initial;
  export import Loading = SwapiStoreLib.kal.swapistore.PeopleData.Loading
  export import Loaded = SwapiStoreLib.kal.swapistore.PeopleData.Loaded;
  export import Fetching = SwapiStoreLib.kal.swapistore.PeopleData.Fetching;
  export import Error = SwapiStoreLib.kal.swapistore.PeopleData.Error;
}
