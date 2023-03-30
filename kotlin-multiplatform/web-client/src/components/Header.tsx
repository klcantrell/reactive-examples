import React, { useEffect, useState } from 'react';
import { getPeopleDataState, PeopleDataState, swapiStore } from 'store';

export default function Header() {
  const [peopleData, setPeopleData] = useState<PeopleDataState>({ type: 'initial' });

  useEffect(() => {
    const unsubscribeFromPeople = swapiStore.subscribePeople((data) => {
      const peopleDataState = getPeopleDataState(data);
      setPeopleData(peopleDataState);
    });

    return () => unsubscribeFromPeople();
  }, []);

  return (
    <header>
      Status:
      {' '}
      {peopleData.type}
    </header>
  );
}
