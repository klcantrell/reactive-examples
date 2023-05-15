use crate::components::app::App;

mod components {
    pub mod app;
    pub mod header;
}

mod models {
    pub mod people;
}

mod library {
    pub mod js;
}

fn main() {
    dioxus_web::launch(App);
}
