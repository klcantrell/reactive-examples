use leptos::*;
use wasm_bindgen::prelude::*;

use crate::components::header::*;
use crate::library::js::{get_people, setup_people_subscription};
use crate::models::people::{PeopleData};

#[component]
pub fn App(cx: Scope) -> impl IntoView {
    let (people_data, set_people_data) = create_signal(cx, PeopleData::Initial);

    setup_people_subscription(Closure::new(move |data| {
        let people_data: Result<PeopleData, serde_wasm_bindgen::Error> =
            serde_wasm_bindgen::from_value(data);

        match people_data {
            Ok(actual_people_data) => set_people_data(actual_people_data),
            Err(_) => set_people_data(PeopleData::Error),
        }
    }));

    let people_list = move || match people_data() {
        PeopleData::Fetching { data } | PeopleData::Loaded { data } => Some(data),
        _ => None,
    };

    view! { cx,
        <main class="text-center mx-auto text-gray-700 p-4">
            <h1 class="text-6xl text-sky-700 font-thin uppercase my-16">
                "Star Wars People"
            </h1>

            <button
                class="button w-auto m-auto"
                on:click=move |_| { get_people() }
            >
                "Fetch them"
            </button>

            <Show
                when=move || matches!(people_list(), Some(_))
                fallback=|_| ()
            >
                <ul class="pt-3">
                    <For
                        each=move || people_list().unwrap_or_default()
                        key=|person| person.name.to_owned()
                        view=move |cx, person| {
                            view! { cx,
                                <li>{person.name}</li>
                            }
                        }
                    />
                </ul>
            </Show>

            <Show
                when=move || matches!(people_data(), PeopleData::Loading)
                fallback=|_| ()
            >
                <p class="pt-3">"Loading..."</p>
            </Show>

            <div class="absolute top-2 right-2">
                <Header />
            </div>
        </main>
    }
}
