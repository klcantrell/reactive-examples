use wasm_bindgen::prelude::*;

#[wasm_bindgen(module = "/../swapi-store/dist/swapi-store.js")]
extern "C" {
    #[wasm_bindgen(js_namespace = ["default", "actions"], js_name = getPeople)]
    pub fn get_people();

    #[wasm_bindgen(js_namespace = ["default", "values", "peopleData$"], js_name = subscribe)]
    fn subscribe_to_people_data(subscriptionFunction: &Closure<dyn Fn(JsValue)>);
}

#[wasm_bindgen]
extern "C" {
    #[wasm_bindgen(js_namespace = console)]
    pub fn log(value: JsValue);

    #[wasm_bindgen(js_namespace = console, js_name = log)]
    pub fn log_str(value: &str);
}

pub fn setup_people_subscription(subscription_function: Closure<dyn Fn(JsValue)>) {
    subscribe_to_people_data(&subscription_function);
    subscription_function.into_js_value();
}
