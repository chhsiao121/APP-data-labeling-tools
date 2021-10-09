package models

import com.google.firebase.database.Exclude

data class PostFeature(
    var uid: String? = "",
    var left: String? = "",
    var top: String? = "",
    var right: String?= "",
    var bottom: String?= "",
    var label1: String?= "",
    var label2: String?= "",
    var label3: String?= ""

) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "left" to left,
            "top" to top,
            "right" to right,
            "bottom" to bottom,
            "label1" to label1,
            "label2" to label2,
            "label3" to label3,
            "uid" to uid
        )
    }
}