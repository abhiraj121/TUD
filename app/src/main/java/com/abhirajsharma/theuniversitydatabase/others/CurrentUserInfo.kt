package com.abhirajsharma.theuniversitydatabase.others

import java.io.File

class CurrentUserInfo {
    companion object {
        var email: String = ""
        var username: String = ""
        var password: String = ""
        var image: File? = null
        var imageUrl: String = ""
        var rollNum: String = ""
        var branch: String = ""
        var batch: String = ""
        var section: String = ""
        var accountType: String = ""
    }
}