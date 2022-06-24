package com.example.typing.view.util

import android.content.res.AssetManager
import android.util.JsonReader
import java.io.InputStreamReader

class ResourceLoader {
    companion object {
        private var typingTexts: ArrayList<String> = ArrayList();

        fun loadResources(assetManager: AssetManager) {
            if(typingTexts.isEmpty()) {
                val reader =
                    JsonReader(InputStreamReader(assetManager.open("data/typingResources.json")))
                reader.beginArray()
                typingTexts.clear()
                while (reader.hasNext()) {
                    typingTexts.add(reader.nextString())
                }
                reader.endArray()
            }
        }

        fun getTypingTexts(): ArrayList<String> {
            return typingTexts
        }
    }
}