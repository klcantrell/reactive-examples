mod components;
mod library;
mod models;

use components::app::*;
use leptos::*;

fn main() {
    mount_to_body(|cx| view! { cx, <App /> })
}
