import { For, Show } from "solid-js";
import { PeopleData, StarWarsPerson } from "swapi-store";
import Header from "~/components/Header";

import { showLoading, swapiActions, swapiData } from "~/stores/swapiStore";

function hasLoaded(
  data: PeopleData
): { status: "loaded" | "fetching"; data: StarWarsPerson[] } | false {
  return data.status === "loaded" || data.status === "fetching" ? data : false;
}

export default function Home() {
  return (
    <main class="text-center mx-auto text-gray-700 p-4">
      <h1 class="text-6xl text-sky-700 font-thin uppercase my-16">
        Star Wars People
      </h1>
      <button
        class="button w-auto m-auto"
        onClick={() => swapiActions.getPeople()}
      >
        Fetch them
      </button>
      <Show when={hasLoaded(swapiData())}>
        {(data) => (
          <ul class="pt-3">
            <For each={data().data}>{(person) => <li>{person.name}</li>}</For>
          </ul>
        )}
      </Show>
      <Show when={showLoading()}>
        <p class="pt-3">Loading...</p>
      </Show>
      <div class="absolute top-2 right-2">
        <Header />
      </div>
    </main>
  );
}
