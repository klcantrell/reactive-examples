#![allow(non_snake_case)]
use dioxus::prelude::*;
use wasm_bindgen::prelude::Closure;

use crate::{
    components::header::Header,
    library::js::{get_people, log, setup_people_subscription},
    models::people::PeopleData,
};

pub fn App(cx: Scope) -> Element {
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

    let content: Option<LazyNodes> = match people_data.get() {
        PeopleData::Initial => None,
        PeopleData::Loading => Some(rsx! {
            p {
                class: "pt-3",
                "Loading..."
            }
        }),
        PeopleData::Loaded { data: people_list } | PeopleData::Fetching { data: people_list } => {
            Some(rsx! {
                ul {
                    class: "pt-3",
                    people_list.iter().map(|person| {
                        rsx! {
                            li {
                                key: "{person.name}",
                                "{person.name}"
                            }
                        }
                    })
                }
            })
        }
        PeopleData::Error => Some(rsx! {
            p { class: "pt-3",
                "Yikes, we ran into some trouble. Try again, please"
            }
        }),
    };

    cx.render(rsx! {
        main { class: "text-center mx-auto text-gray-700 p-4",
            h1 { class: "text-6xl text-sky-700 font-thin uppercase my-16",
                "Star Wars People"
            }

            button { class: "button w-auto m-auto",
                onclick: move |_| get_people(),
                "Fetch them"
            }

            content

            div { class: "absolute top-2 right-2",
                Header(cx)
            }
        }
    })
}
