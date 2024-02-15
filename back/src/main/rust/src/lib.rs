use std::env;

use crunchyroll_rs::{Crunchyroll, Locale, Series};
use crunchyroll_rs::media::Media;
use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;

async fn init_crunchyroll(email: String, password: String) -> Result<Crunchyroll, String> {
    Crunchyroll::builder()
        .locale(Locale::fr_FR)
        .login_with_credentials(email, password)
        .await.map_err(|err| format!("Error initializing Crunchyroll: {:?}", err))
}

async fn fetch_series(crunchyroll: &Crunchyroll, id: String) -> Result<String, String> {
    let series_result = Series::from_id(crunchyroll, id).await;
    match series_result {
        Ok(series) => { Ok(serde_json::to_string(&series).unwrap().to_string()) }
        Err(err) => { Err(format!("Error fetching series: {:?}", err)) }
    }
}

#[no_mangle]
pub extern "system" fn Java_fr_wolfdev_wolfanime_CrunchyrollNative_getCrunchyrollDataBySeriesId<'local>(
    mut env: JNIEnv<'local>, _: JClass<'local>, input: JString<'local>) -> jstring {
    let input: String = env.get_string(&input).expect("Couldn't get java string!").into();

    let result = tokio::runtime::Builder::new_multi_thread().enable_all().build().unwrap().block_on(async {
        let email = env::var("CRUNCHYROLL_EMAIL").expect("'CRUNCHYROLL_EMAIL' environment variable not found");
        let password = env::var("CRUNCHYROLL_PASSWORD").expect("'CRUNCHYROLL_PASSWORD' environment variable not found");
        let crunchyroll = init_crunchyroll(email, password).await?;

        fetch_series(&crunchyroll, input).await
    });

    let output = match result {
        Ok(data) => env.new_string(data).expect("Couldn't create java string"),
        Err(err) => env.new_string(err).expect("Couldn't create java string")
    };

    output.into_raw()
}

/*#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let email = env::var("EMAIL").expect("'EMAIL' environment variable not found");
    let password = env::var("PASSWORD").expect("'PASSWORD' environment variable not found");
    let crunchyroll = Crunchyroll::builder()
        .locale(Locale::fr_FR)
        .login_with_credentials(email, password)
        .await?;

    let series = Series::from_id(&crunchyroll, "GYZJ43JMR").await?;

    let result_str = format!("SeriesId: {}, TitleId: {}", series.id, series.title);
    /*let url = crunchyroll_rs::parse_url("https://www.crunchyroll.com/fr/series/that-time-i-got-reincarnated-as-a-slime").expect("url is not valid");
    if let UrlType::Series(series_id) = url {
        if let MediaCollection::Series(series) = crunchyroll.media_collection_from_id(series_id).await? {
            println!("Url is series {}, id {}", series.title, series.id)
        }
    } else {
        panic!("Url is not a series")
    }*/

    Ok(())
}*/
