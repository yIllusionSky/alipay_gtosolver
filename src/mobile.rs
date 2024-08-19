// Copyright 2019-2023 Tauri Programme within The Commons Conservancy
// SPDX-License-Identifier: Apache-2.0
// SPDX-License-Identifier: MIT

use serde::de::DeserializeOwned;
use serde::{Deserialize, Serialize};
use tauri::{
    image::Image,
    plugin::{PluginApi, PluginHandle},
    AppHandle, Runtime,
};

use std::borrow::Cow;

#[cfg(target_os = "android")]
const PLUGIN_IDENTIFIER: &str = "app.tauri.toAlipayPlugin";

#[cfg(target_os = "ios")]
tauri::ios_plugin_binding!(init_plugin_clipboard);

// initializes the Kotlin or Swift plugin classes
pub fn init<R: Runtime, C: DeserializeOwned>(
    _app: &AppHandle<R>,
    api: PluginApi<R, C>,
) -> crate::Result<ToAlipayPlugin<R>> {
    let handle = api.register_android_plugin(PLUGIN_IDENTIFIER, "ToAlipayPlugin")?;
    Ok(ToAlipayPlugin(handle))
}

/// Access to the clipboard APIs.
pub struct ToAlipayPlugin<R: Runtime>(PluginHandle<R>);

impl<R: Runtime> ToAlipayPlugin<R> {
    pub fn write_text<'a, T: Into<Cow<'a, str>>>(&self, text: T) -> crate::Result<()> {
        let text = text.into().to_string();
        self.0
            .run_mobile_plugin("writeText", TextKind::PlainText { text, label: None })
            .map_err(Into::into)
    }
}

#[derive(Debug, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
enum TextKind {
    PlainText { label: Option<String>, text: String },
}