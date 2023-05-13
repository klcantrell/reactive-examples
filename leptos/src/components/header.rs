use leptos::*;
use wasm_bindgen::prelude::Closure;

use crate::models::people::PeopleData;
use crate::library::js::{setup_people_subscription};

#[component]
pub fn Header(cx: Scope) -> impl IntoView {
    let (people_data, set_people_data) = create_signal(cx, PeopleData::Initial);

    setup_people_subscription(Closure::new(move |data| {
        let people_data: Result<PeopleData, serde_wasm_bindgen::Error> =
            serde_wasm_bindgen::from_value(data);

        match people_data {
            Ok(actual_people_data) => set_people_data(actual_people_data),
            Err(_) => set_people_data(PeopleData::Error),
        }
    }));

    let status_string =  move || match people_data() {
        PeopleData::Initial => "Initial",
        PeopleData::Loading => "Loading",
        PeopleData::Loaded { data: _ } => "Loaded",
        PeopleData::Fetching { data: _ } => "Fetching",
        PeopleData::Error => "Error",
    };
    let status_text = move || format!("Status: {}", status_string());

    view! { cx,
        <header>{status_text}</header>
    }
}
