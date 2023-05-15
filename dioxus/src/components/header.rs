#![allow(non_snake_case)]
use dioxus::prelude::*;
use wasm_bindgen::prelude::Closure;

use crate::{
    library::js::{log, setup_people_subscription},
    models::people::PeopleData,
};

pub fn Header(cx: Scope) -> Element {
    let people_data = use_state(cx, || PeopleData::Initial);
    let subscription_activated = use_ref(cx, || false);

    use_effect(
        cx,
        (subscription_activated, people_data),
        |(subscription_activated, people_data)| async move {
            if !*(subscription_activated.read()) {
                subscription_activated.set(true);
                setup_people_subscription(Closure::new(move |data| {
                    let data: Result<PeopleData, serde_wasm_bindgen::Error> =
                        serde_wasm_bindgen::from_value(data);

                    match data {
                        Ok(actual_people_data) => people_data.set(actual_people_data),
                        Err(_) => log(serde_wasm_bindgen::to_value(&PeopleData::Error).unwrap()),
                    }
                }))
            }
        },
    );

    let status_string = match people_data.get() {
        PeopleData::Initial => "Initial",
        PeopleData::Loading => "Loading",
        PeopleData::Loaded { data: _ } => "Loaded",
        PeopleData::Fetching { data: _ } => "Fetching",
        PeopleData::Error => "Error",
    };
    let status_text = format!("Status: {}", status_string);

    cx.render(rsx! {
        header {
            "{status_text}"
        }
    })
}
