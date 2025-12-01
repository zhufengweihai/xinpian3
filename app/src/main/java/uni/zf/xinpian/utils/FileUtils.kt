package uni.zf.xinpian.utils

import java.io.File
import java.util.zip.ZipFile

fun File.unzipTo(destDir: File) {
    ZipFile(this).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            val destFile = destDir.resolve(entry.name)
            if (entry.isDirectory) {
                destFile.mkdirs()
            } else {
                destFile.parentFile?.mkdirs()
                zip.getInputStream(entry).use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }
}