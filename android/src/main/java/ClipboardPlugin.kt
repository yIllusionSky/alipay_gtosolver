// Copyright 2019-2023 Tauri Programme within The Commons Conservancy
// SPDX-License-Identifier: Apache-2.0
// SPDX-License-Identifier: MIT

package app.tauri.toAlipayPlugin

import android.R.attr.value
import android.app.Activity
import android.content.Intent
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.util.Log
import app.tauri.annotation.Command
import app.tauri.annotation.InvokeArg
import app.tauri.annotation.TauriPlugin
import app.tauri.plugin.Invoke
import app.tauri.plugin.Plugin
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.io.IOException
import android.content.pm.PackageManager


@InvokeArg
@JsonDeserialize(using = WriteOptionsDeserializer::class)
sealed class WriteOptions {
  @JsonDeserialize
  class PlainText: WriteOptions() {
    lateinit var text: String
    var label: String? = null
  }
}

@JsonSerialize(using = ReadClipDataSerializer::class)
sealed class ReadClipData {
  class PlainText: ReadClipData() {
    lateinit var text: String
  }
}

internal class ReadClipDataSerializer @JvmOverloads constructor(t: Class<ReadClipData>? = null) :
  StdSerializer<ReadClipData>(t) {
  @Throws(IOException::class, JsonProcessingException::class)
  override fun serialize(
    value: ReadClipData, jgen: JsonGenerator, provider: SerializerProvider
  ) {
    jgen.writeStartObject()
    when (value) {
      is ReadClipData.PlainText -> {
        jgen.writeObjectFieldStart("plainText")

        jgen.writeStringField("text", value.text)

        jgen.writeEndObject()
      }
    }

    jgen.writeEndObject()
  }
}

internal class WriteOptionsDeserializer: JsonDeserializer<WriteOptions>() {
  override fun deserialize(
    jsonParser: JsonParser,
    deserializationContext: DeserializationContext
  ): WriteOptions {
    val node: JsonNode = jsonParser.codec.readTree(jsonParser)
    node.get("plainText")?.let {
      return jsonParser.codec.treeToValue(it, WriteOptions.PlainText::class.java)
    } ?: run {
      throw Error("unknown write options $node")
    }
  }
}

@TauriPlugin
class ToAlipayPlugin(private val activity: Activity) : Plugin(activity) {

  @Command
  @Suppress("MoveVariableDeclarationIntoWhen")
  fun writeText(invoke: Invoke) {
       val args = invoke.parseArgs(WriteOptions::class.java)
    if(args is WriteOptions.PlainText) {
   
    Log.d("writeText", "writeUrl: "+ args.text)
    val scheme = "alipays://platformapi/startapp?appId=20000067&url=" + Uri.encode(args.text)
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme))

    activity.startActivity(intent)



    } 

    invoke.resolve()
  }
}
