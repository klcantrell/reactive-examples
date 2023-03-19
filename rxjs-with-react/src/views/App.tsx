import Header from '../components/Header';
import useObservableState from '../hooks/useObservableState';
import swapiStore from 'swapi-store';

import './App.css';

function App() {
  const swapiData = useObservableState(
    swapiStore.values.peopleData$,
    swapiStore.values.peopleData$.getValue()
  );
  const showLoading = useObservableState(
    swapiStore.values.displayLoader$,
    false
  );

  let content: React.ReactNode = undefined;
  if (showLoading) {
    content = <p>Loading...</p>;
  } else if (swapiData.status === 'fetching' || swapiData.status === 'loaded') {
    content = (
      <ul style={{ padding: 0 }}>
        {swapiData.data.map((person) => (
          <li style={{ listStyle: 'none' }} key={person.name}>
            {person.name}
          </li>
        ))}
      </ul>
    );
  } else if (swapiData.status === 'error') {
    content = <p>Yikes, we ran into some trouble. Try again, please</p>;
  }

  return (
    <>
      <main>
        <h1>Star Wars People</h1>
        <button onClick={() => swapiStore.actions.getPeople()}>
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
