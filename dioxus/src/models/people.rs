use serde::{Serialize, Deserialize};

#[derive(Serialize, Deserialize, Clone)]
pub struct StarWarsPerson {
    pub name: String
}

#[derive(Serialize, Deserialize, Clone)]
#[serde(tag = "status")]
pub enum PeopleData {
    #[serde(rename = "initial")]
    Initial,
    #[serde(rename = "loading")]
    Loading,
    #[serde(rename = "fetching")]
    Fetching { data: Vec<StarWarsPerson> },
    #[serde(rename = "loaded")]
    Loaded { data: Vec<StarWarsPerson> },
    #[serde(rename = "error")]
    Error,
}
