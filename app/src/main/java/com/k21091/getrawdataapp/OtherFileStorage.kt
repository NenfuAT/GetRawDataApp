package com.k21091.getrawdataapp

import android.content.Context
import android.os.Environment
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter
class OtherFileStorage(context: Context, filename: String) {
    val fileAppend : Boolean = true //true=追記, false=上書き
    var fileName : String = filename
    val extension : String = ".csv"
    val FilePath = context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString().plus("/").plus(fileName).plus(extension) //内部ストレージのDocumentのURL

    fun writeText(text:String){
        val fil = FileWriter(FilePath,fileAppend)
        val pw = PrintWriter(BufferedWriter(fil))
        pw.println(text)
        pw.close()
    }
}