// Copyright 2019-2023 Tauri Programme within The Commons Conservancy
// SPDX-License-Identifier: Apache-2.0
// SPDX-License-Identifier: MIT

//! [![](https://github.com/tauri-apps/plugins-workspace/raw/v2/plugins/clipboard-manager/banner.png)](https://github.com/tauri-apps/plugins-workspace/tree/v2/plugins/clipboard-manager)
//!
//! Read and write to the system clipboard.

#![doc(
    html_logo_url = "https://github.com/tauri-apps/tauri/raw/dev/app-icon.png",
    html_favicon_url = "https://github.com/tauri-apps/tauri/raw/dev/app-icon.png"
)]

use tauri::{
    plugin::{Builder, TauriPlugin},
    Manager, Runtime,
};

mod mobile;
mod error;

pub use error::{Error, Result};


pub use mobile::ToAlipayPlugin;

/// Extensions to [`tauri::App`], [`tauri::AppHandle`], [`tauri::WebviewWindow`], [`tauri::Webview`] and [`tauri::Window`] to access the clipboard APIs.
pub trait ClipboardExt<R: Runtime> {
    fn clipboard(&self) -> &ToAlipayPlugin<R>;
}

impl<R: Runtime, T: Manager<R>> crate::ClipboardExt<R> for T {
    fn clipboard(&self) -> &ToAlipayPlugin<R> {
        self.state::<ToAlipayPlugin<R>>().inner()
    }
}

/// Initializes the plugin.
pub fn init<R: Runtime>() -> TauriPlugin<R> {
    Builder::new("ToAlipayPlugin")
        .setup(|app, api| {
            let clipboard = mobile::init(app, api)?;
            app.manage(clipboard);
            Ok(())
        })
        .build()
}
