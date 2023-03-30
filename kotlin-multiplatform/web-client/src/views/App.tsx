import React, { useEffect, useState } from 'react';

import { getPeopleDataState, PeopleDataState, swapiStore } from 'store';
import Header from 'components/Header';

import './App.css';

function App() {
  const [peopleData, setPeopleData] = useState<PeopleDataState>({ type: 'initial' });
  const [showLoading, setShowLoading] = useState(false);

  useEffect(() => {
    const unsubscribeFromPeople = swapiStore.subscribePeople((data) => {
      const peopleDataState = getPeopleDataState(data);
      setPeopleData(peopleDataState);
    });

    return () => unsubscribeFromPeople();
  }, []);

  useEffect(() => {
    const unsubscribeFromShowLoading = swapiStore.subscribeShowLoader((loading) => {
      setShowLoading(loading);
    });

    return () => unsubscribeFromShowLoading();
  }, []);

  let content: React.ReactNode;
  if (showLoading) {
    content = <p>Loading...</p>;
  } else if (peopleData.type === 'fetching' || peopleData.type === 'loaded') {
    content = (
      <ul style={{ padding: 0 }}>
        {peopleData.data.map((person) => (
          <li style={{ listStyle: 'none' }} key={person.name}>
            {person.name}
          </li>
        ))}
      </ul>
    );
  } else if (peopleData.type === 'error') {
    content = <p>Yikes, we ran into some trouble. Try again, please</p>;
  }

  return (
    <>
      <main>
        <h1>Star Wars People</h1>
        <button type="button" onClick={() => swapiStore.getPeople()}>
          Fetch them
        </button>
        {content}
      </main>
      <div style={{ position: 'absolute', top: 16, right: 16 }}>
        <Header />
      </div>
    </>
  );
}

export default App;
