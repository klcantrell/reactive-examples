import useObservableState from '../hooks/useObservableValue';
import swapiStore from 'swapi-store';

export default function Header() {
  const swapiData = useObservableState(
    swapiStore.values.peopleData$,
    swapiStore.values.peopleData$.getValue(),
  );

  return <header>Status: {swapiData.status}</header>;
}
