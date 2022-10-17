@file:Suppress("DEPRECATION")
package com.macwap.rdxrasel.untils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.DownloadManager
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.text.format.DateUtils
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.macwap.rdxrasel.R
import com.macwap.rdxrasel.databases.MacwapDB
import com.macwap.rdxrasel.databases.MacwapDB.stringGet
import com.macwap.rdxrasel.shimmer.Shimmer
import com.macwap.rdxrasel.shimmer.ShimmerDrawable
import com.macwap.rdxrasel.untils.ConstructionID.USER_PATTERN
import org.jsoup.Jsoup
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

@Suppress("unused")
object FunctionManager {




    @JvmStatic
    fun loadCustomTabForSite(context: Context?, url: String, color: Int = Color.RED) {
        var mCustomTabsSession: CustomTabsSession? = null
        val mCustomTabsServiceConnection: CustomTabsServiceConnection?
        var mClient: CustomTabsClient?
        mCustomTabsServiceConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                componentName: ComponentName,
                customTabsClient: CustomTabsClient
            ) {
                mClient = customTabsClient
                mClient?.warmup(0L)
                mCustomTabsSession = mClient?.newSession(null)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                mClient = null
            }
        }
        CustomTabsClient.bindCustomTabsService(
            context!!,
            "com.android.chrome",
            mCustomTabsServiceConnection
        )

        val customTabsIntent = CustomTabsIntent.Builder(mCustomTabsSession)
            .setToolbarColor(color)
            .setShowTitle(true)
            .build()

        customTabsIntent.launchUrl(context, Uri.parse(url))


    }

    @JvmStatic @Suppress("LocalVariableName", "unused")
    fun getRelationTime(time: Long): String {
         val IN_MILLIS = DateUtils.DAY_IN_MILLIS * 30

        val now = Date().time
        val delta = now - time
        val resolution: Long = when {
            delta <= DateUtils.MINUTE_IN_MILLIS -> {
                DateUtils.SECOND_IN_MILLIS
            }
            delta <= DateUtils.HOUR_IN_MILLIS -> {
                DateUtils.MINUTE_IN_MILLIS
            }
            delta <= DateUtils.DAY_IN_MILLIS -> {
                DateUtils.HOUR_IN_MILLIS
            }
            delta <= DateUtils.WEEK_IN_MILLIS -> {
                DateUtils.DAY_IN_MILLIS
            }
            else -> return when {
                delta <= IN_MILLIS -> {
                    (delta / DateUtils.WEEK_IN_MILLIS).toInt().toString() + " weeks ago"
                }
                delta <= DateUtils.YEAR_IN_MILLIS -> {
                    (delta / IN_MILLIS).toInt().toString() + " months ago"
                }
                else -> {
                    (delta / DateUtils.YEAR_IN_MILLIS).toInt().toString() + " years ago"
                }
            }
        }
        return DateUtils.getRelativeTimeSpanString(time, now, resolution).toString()
    }

    @JvmStatic
    fun parseDate(time: Long): String {

      var date =  getRelationTime(time)
        date = date.replace("(.*.)second ago".toRegex(), "Now")
        date = date.replace("(.*.)seconds ago".toRegex(), "Now")
        date = date.replace(" minutes ago".toRegex(), "m")
        date = date.replace(" minute ago".toRegex(), "m")
        date = date.replace(" hours ago".toRegex(), "h")
        date = date.replace(" hour ago".toRegex(), "h")
        date = date.replace(" days ago".toRegex(), "d")
        date = date.replace(" day ago".toRegex(), "d")
        date = date.replace(" months ago".toRegex(), "M")
        date = date.replace(" month ago".toRegex(), "M")
        date = date.replace(" month(.*?)ago".toRegex(), "M")
        date = date.replace(" year(.*?)ago".toRegex(), "Y")

        date = date.replace(" week(.*?)ago".toRegex(), "w")
        date = date.replace("(.*?)yesterday(.*?)".toRegex(), "1d")
        date = date.replace("(.*.)In(.*.)".toRegex(), "Now")
        date = date.replace("(.*.)in(.*.)".toRegex(), "Now")
        date = date.replace("(.*?)Yesterday(.*?)".toRegex(), "1d")


        return  date

    }

    @JvmStatic
    fun loadJSONFromAsset(activity: Activity, filename: String): String? {
        return try {
            val `is` = activity.assets.open("layout/$filename")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val charset: Charset = Charsets.UTF_8
            String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }

    @JvmStatic
    fun getDateInMillis(srcDate: String): String {
        val isNumber = Pattern.matches("[0-9]+", srcDate)
        if (isNumber) {
            val time1 = srcDate.toInt()
            val date = Date(time1 * 1000L)
            val millis = date.time
            val timePassedString = DateUtils.getRelativeTimeSpanString(
                millis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS
            )



            return timePassedString.toString()
        }
        return ""
    }

    @JvmStatic
    fun getDateInMilliBN(srcDate: String, Type: String): String {
        var text: String
        text = if (Type == "1") {
            getDateInMillis(srcDate)
        } else {
            srcDate
        }

        //region translate bangle date and time
        if (Type == "2") {
            text = text.replace("January".toRegex(), "জানুয়ারী")
            text = text.replace("February".toRegex(), "ফেব্রুয়ারী")
            text = text.replace("March".toRegex(), "মার্চ")
            text = text.replace("April".toRegex(), "এপ্রিল")
            text = text.replace("May".toRegex(), "মে")
            text = text.replace("June".toRegex(), "জুন")
            text = text.replace("July".toRegex(), "জুলাই")
            text = text.replace("August".toRegex(), "আগস্ট")
            text = text.replace("September".toRegex(), "সেপ্টেম্বর")
            text = text.replace("October".toRegex(), "অক্টোবর")
            text = text.replace("November".toRegex(), "নভেম্বর")
            text = text.replace("December".toRegex(), "ডিসেম্বর")
            text = text.replace("AM".toRegex(), "সকাল")
            text = text.replace("PM".toRegex(), "বিকাল")
        }
        text = text.replace("yesterday".toRegex(), "গতকাল")
        text = text.replace("Yesterday".toRegex(), "গতকাল")
        text = text.replace("minute ago".toRegex(), "মিনিট পুর্বে ")
        text = text.replace("minutes ago".toRegex(), "মিনিট পুর্বে ")
        text = text.replace("hour ago".toRegex(), "ঘণ্টা পুর্বে ")
        text = text.replace("hours ago".toRegex(), "ঘণ্টা পুর্বে ")
        text = text.replace("day ago".toRegex(), "দিন পুর্বে ")
        text = text.replace("days ago".toRegex(), "দিন পুর্বে ")
        text = text.replace("month ago".toRegex(), "মাস পুর্বে ")
        text = text.replace("months ago".toRegex(), "মাস পুর্বে ")
        text = text.replace("year ago".toRegex(), "বছর পুর্বে ")
        text = text.replace("years ago".toRegex(), "বছর পুর্বে ")
        text = text.replace("hour".toRegex(), "ঘণ্টা পুর্বে ")
        text = text.replace("minutes".toRegex(), "মিনিট পুর্বে ")
        text = text.replace("day".toRegex(), "দিন পুর্বে ")
        text = text.replace("days".toRegex(), "দিন পুর্বে ")
        text = text.replace("month".toRegex(), "মাস পুর্বে ")
        text = text.replace("months".toRegex(), "মাস পুর্বে ")
        text = text.replace("year".toRegex(), "বছর পুর্বে ")
        text = text.replace("years".toRegex(), "বছর পুর্বে ")
        text = text.replace("In".toRegex(), "")
        text = text.replace("in".toRegex(), "")
        text = text.replace("s".toRegex(), "")
        text = text.replace("1".toRegex(), "১")
        text = text.replace("2".toRegex(), "২")
        text = text.replace("3".toRegex(), "৩")
        text = text.replace("4".toRegex(), "৪")
        text = text.replace("5".toRegex(), "৫")
        text = text.replace("6".toRegex(), "৬")
        text = text.replace("7".toRegex(), "৭")
        text = text.replace("8".toRegex(), "৮")
        text = text.replace("9".toRegex(), "৯")
        text = text.replace("0".toRegex(), "০")


        //endregion
        return text
    }

    @JvmStatic @SuppressLint("SimpleDateFormat")
    fun getDateAndTime(srcDate: String): String? {
        val isNumber = Pattern.matches("[0-9]+", srcDate)
        if (isNumber) {
            val t = srcDate.toInt()
            val date = Date(t * 1000L)
            val formatter = SimpleDateFormat("dd MMMM yyyy,  hh:mm a")
            return formatter.format(date)
        }
        return ""
    }

    @JvmStatic
    fun openLink(context: Context, url: String?) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    }

    @JvmStatic
    fun html2text(html: String?): String {
        return Jsoup.parse(html).text()
    }

    @JvmStatic
    fun getColor(context: Context, colorResId: Int): Int {

        //return ContextCompat.getColor(context, colorResId); // Doesn't seem to work for R.attr.colorPrimary
        val typedValue = TypedValue()
        val typedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(colorResId))
        val color = typedArray.getColor(0, 0)
        typedArray.recycle()
        return color
    }


    @JvmStatic
    fun Context.getColorRes(colorResId: Int): Int {
        return ContextCompat.getColor(this, colorResId)

    }


    @JvmStatic
    fun Context.getAttrColor(colorResId: Int): Int {

        val typedValue = TypedValue()
        theme.resolveAttribute(colorResId, typedValue, true)

        return ContextCompat.getColor(this, typedValue.resourceId)

    }

    @JvmStatic
    fun removeLastComma(text: String?): String {

        val reText =  text?.replace("^(,|\\s)*|(,|\\s)*$".toRegex(), "")
            ?.replace("(,\\s*)+".toRegex(), ",")

        if(reText !=null) return reText

        return  ""
    }

    @JvmStatic
    fun removeLastOR(text: String?): String {

        val reText = text?.replace("^(OR |\\s)*|(OR |\\s)*$".toRegex(), "")
            ?.replace("(OR \\s*)+".toRegex(), "OR ")

        if(reText !=null) return reText

        return  ""
    }

    @JvmStatic
    fun orToArray(txt: String?): Array<String?> {

        return txt!!.split(" OR ".toRegex()).toTypedArray()

    }

    @JvmStatic
    fun splitOR(txt: String?): String {

        val text = orToArray(txt)
        var mainText =""
        for (i in text.indices) {
            mainText += "\"${text[i]}\" OR "
        }

        return removeLastOR(mainText)

    }

    @JvmStatic
    fun shareText(context: Context, text: String?, title: String?) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text)
        context.startActivity(Intent.createChooser(sharingIntent, title))
    }






    @JvmStatic
    fun setHtml(text: String?): CharSequence {
        return if (text != null) {
            val snap: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(text)
            }
            snap
        } else {
            ""
        }
    }

    @JvmStatic
    fun Spanned.getHtml(): String {
    var text=    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.toHtml(this, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.toHtml(this)
    }

        text=  text?.replace(USER_PATTERN) {
            "[user=${it.groupValues[1]}]"+setHtml(it.groupValues[2])+"[/user]"}

        text= setHtml(text).toString()


        return (text)
    }

    @JvmStatic
    fun setHtml(text: CharSequence?): CharSequence {
        return if (text != null) {
            val snap: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(text.toString())
            }
            snap
        } else {
            ""
        }
    }
    @JvmStatic
    fun setHtmlImageGetter(text: String?, imageGetter: Html.ImageGetter): CharSequence {
        return if (text != null) {
            val snap: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT,imageGetter,null)
            } else {
                @Suppress("DEPRECATION") Html.fromHtml(text,imageGetter,null)
            }
            snap
        } else {
            ""
        }
    }

    @JvmStatic
    fun capitalizeWords(string: String?): String {
      return string?.split(",")?.joinToString(",") { it.capitalize(Locale.ROOT) }.toString()
    }

    @JvmStatic
    fun articleBBCode(html: String?): String? {
        var bbCode = html
         bbCode = bbCode?.replace("\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t".toRegex(), "")
        bbCode = bbCode?.replace("\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t".toRegex(), "")
        bbCode = bbCode?.replace("\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\r\\n\\t\\t\\t\\t\\t\\t".toRegex(), "")
        bbCode = bbCode?.replace("\\r\\n\\t\\t\\t\\t\\t".toRegex(), "")

        bbCode = bbCode?.replace("<img".toRegex(), "<img onclick=\"FullscreenIMG(this.src)\" ")

        return bbCode
    }

    @JvmStatic
    fun searchText(text: String?): String {
        var text1 = text
        text1 = text1?.replace(" {3}".toRegex(), " ")
        text1 = text1?.replace(" {2}".toRegex(), " ")
        text1 = text1?.replace(" ".toRegex(), " OR ")

        text1 =removeLastOR(text1)
         return text1
    }

    @JvmStatic
    fun removeSpecialCharacters(text: String): String {
        var text1 = text
        text1 = text1.replace("-".toRegex(), " ")

        text1 =removeLastOR(text1)
        return text1
    }

    @JvmStatic
    fun getDomain(url: String): String {
        var returnText=""
        when (url) {
            "", "about:blank" -> {
            }
            else -> {

                val startIndex: Int = url.indexOf("//") + 2


                returnText = url.indexOf('/', startIndex).let { url.substring(startIndex, it) }
            }
        }

        return returnText
    }

    @JvmStatic
    fun shimmerDrawable(baseColor:Int?,highLightColor:Int?,duration:Long): ShimmerDrawable {
        val shimmer = baseColor?.let {
            highLightColor?.let { it1 ->
                Shimmer.ColorHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
                    .setDuration(duration) // how long the shimmering animation takes to do one full sweep
                    .setBaseColor(it)
                    .setHighlightColor(it1)
                    // .setDropoff(50f)
                    .setBaseAlpha(1f) //the alpha of the underlying children
                    .setHighlightAlpha(1f) // the shimmer alpha amount
                    .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                    .setAutoStart(true)
                    .build()
            }
        }

        return ShimmerDrawable().apply { setShimmer(shimmer) }

    }

    @JvmStatic
    fun setupTheme(context: Context) {

        when (MacwapDB.getString(context,"default_theme")) {
            "day" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            }
            "night" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            }
            "default" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

            }
            else -> {
                when {
                    context.stringGet("defaultTheme")=="day" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                    }
                    context.stringGet("defaultTheme")=="night" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                    }
                    else ->{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

                    }
                }

            }
        }

    }

    @JvmStatic
    fun openExternalBrowser(activity: Activity?, url: String?) {
        val uri = Uri.parse(url)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        activity?.startActivity(goToMarket)

    }

    @JvmStatic
    fun String.removeAt():String {
      return  this.replace("@", "")

    }

    @JvmStatic
    fun RecyclerView?.getCurrentPosition() : Int {
        return (this?.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    @JvmStatic
    fun decodeCategory(category: String,index: Int): String {
        val items = removeLastComma(category.replace("@", "")).split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex()).toTypedArray()

        if(items.size>index)
            if(items[index] != "")

                return  "%@${items[index]}@%"

        return "@fakeId@"

    }

    @JvmStatic
    fun ImageView.loadColiImage(url: String?,context: Context, placeholder:Int,error:Int) {

        val imageLoader = ImageLoader.Builder(context)
            .componentRegistry { add(SvgDecoder(context)) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(500)
            .diskCachePolicy(CachePolicy.ENABLED)
            .placeholder(placeholder)
            .error(error)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }

    @JvmStatic
    fun hideKeyboard(activity: Activity?,view: View?){

        val manager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view?.windowToken, 0)
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    @JvmStatic
    fun Activity.showKeyboard(view: View?){
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view?.windowToken, 1)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
     }

    @JvmStatic @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun Activity.transparentStatusBar( ints: Int,backgroundInt:Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            val background = ContextCompat.getDrawable(this, backgroundInt)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this,android.R.color.transparent)
            if (ints == 1) {
                window.navigationBarColor = ContextCompat.getColor(this,android.R.color.transparent)
            }
            window.setBackgroundDrawable(background)
        }
    }

    @JvmStatic @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun Activity.statusBarColor(statusBarColor: Int, backgroundColor:Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = statusBarColor
            window.setBackgroundDrawable(ColorDrawable(backgroundColor))
        }
    }

    @JvmStatic
    fun Drawable.drawableTint(tintColor: Int): Drawable {
        val wrappedDrawable =  DrawableCompat.wrap(this)
        DrawableCompat.setTint(wrappedDrawable, tintColor)
        return this
    }


    @JvmStatic
    fun textToArray(txt: String?): Array<String?> {

        return txt!!.split(",".toRegex()).toTypedArray()

    }

    @JvmStatic
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
            .toInt()
    }

    @JvmStatic
    fun String.htmlEncode() :String{
        var text:String = this
      //  text =  TextUtils.htmlEncode(text)
        text =  text.replace( "+", "&plus;")

        return text
    }

    @JvmStatic
    fun String.toTypedArrayInt(): IntArray {
        val text = removeLastComma(this)
        val tokens = text.split(",".toRegex()).toTypedArray()
        val numbers = IntArray(tokens.size)
        for (i in tokens.indices) {
            numbers[i] = tokens[i].toInt()
        }
        return numbers
    }

    @JvmStatic
    fun shortIntArray(intArray: IntArray, lengths: Int) {
        var temp: Int
        for (counter in 0 until lengths - 1) {
            for (index in 0 until lengths - 1 - counter) {
                if (intArray[index] < intArray[index + 1]) {
                    temp = intArray[index]
                    intArray[index] = intArray[index + 1]
                    intArray[index + 1] = temp
                }
            }
        }
    }

    @JvmStatic
    fun Context.downloadRequest(url: String?, suggestedFilename: String?) {

        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, suggestedFilename)
        val dm: DownloadManager = getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
        Toast.makeText(applicationContext, "Downloading File", Toast.LENGTH_LONG).show()

    }

    @JvmStatic
    fun deleteFiles(file: File,day: Int){
        val diff: Long = Date().time - file.lastModified()

        if (diff > day * 24 * 60 * 60 * 1000) {
            file.delete()
        }
    }
    @JvmStatic
    fun getAllFilesInDir(dir: File?): ArrayList<File>? {
        if (dir == null) return null
        val files = ArrayList<File>()
        val dirList = Stack<File>()
        dirList.clear()
        dirList.push(dir)
        while (!dirList.isEmpty()) {
            val dirCurrent = dirList.pop()
            val fileList = dirCurrent.listFiles()
            for (aFileList in fileList) {
                if (aFileList.isDirectory) dirList.push(aFileList) else  if(aFileList.extension !="nomedia")
                    files.add(aFileList)
            }
        }
         return files
    }

    @JvmStatic
    fun View.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
        if ( layoutParams is ViewGroup.MarginLayoutParams) {
            val p: ViewGroup.MarginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            requestLayout()
        }
    }

    @JvmStatic
    fun String.shareUrlRewrite():String {
        var url = this
        url =  url. replace(" ", "_")
            .replace("\n\n\n".toRegex(), "_")
            .replace("\n\n".toRegex(), "_")
            .replace("\n".toRegex(), "_")
            .replace("/n".toRegex(), "_")
            .replace(" {3}".toRegex(), " ")
            .replace(" {2}".toRegex(), " ")
            .replace(" {2}".toRegex(), " ")
            .replace(" ".toRegex(), "_")
            .replace("'".toRegex(), "")
            .replace("\"".toRegex(), "")
            .replace(" ", "_")
        url = url.replace(" ", "_")
        url = url.replace("/", "_")
        url = url.replace("\\", "_")
        return url //urlEncoder(this)
    }

    @JvmStatic
    fun String.isNumber(): Boolean {
        return if (isNullOrEmpty()) false else all { Character.isDigit(it) }
    }

    fun Uri.fileUriSystem(): String? {
        val reString = this
        return if (Build.VERSION.SDK_INT >= 29) {

            reString .toString()

        }else{
            reString.path

        }

    }

    @JvmStatic
    fun onlineSystem(time: String): String {
        return when {
            isActiveOn5minute(time) -> {
                "online"
            }
            isActive1hour(time) -> {
                "1hour"
            }
            else -> {
                "offline"
            }
        }
    }

    @JvmStatic
    fun isActiveOn5minute(srcDate: String): Boolean {
        val isNumber = Pattern.matches("[0-9]+", srcDate)
        if (isNumber) {
            val time2 = srcDate.toInt()
            val date = Date(time2 * 1000L)
            val inMillis = date.time
            DateUtils.getRelativeTimeSpanString(
                inMillis,
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS
            )
            val osTime = (10 * 60 * 1000).toLong()
            val differenceInMillis = System.currentTimeMillis() - inMillis
            return osTime >= differenceInMillis
        }
        return false
    }
    @JvmStatic
    fun isActive1hour(srcDate: String): Boolean {
        val isNumber = Pattern.matches("[0-9]+", srcDate)
        if (isNumber) {
            val time2 = srcDate.toInt()
            val date = Date(time2 * 1000L)
            val inMillis = date.time
            DateUtils.getRelativeTimeSpanString(
                inMillis,
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS
            )
            val osTime = (60 * 60 * 1000).toLong()
            val differenceInMillis = System.currentTimeMillis() - inMillis
            return osTime >= differenceInMillis
        }
        return false
    }
    @JvmStatic
    fun readableType(text: String?): String {
        return if (text == "url") {
            "link"
        } else {
            "Posts"
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    @JvmStatic
    fun setBorder(context: Context, tab: LinearLayout, width: Int, colorResId: Int, removePadding: Boolean = false) {
        val border = GradientDrawable()
        border.setStroke(width, getColor(context, colorResId))
        val layers = arrayOf<Drawable>(border)
        val layerDrawable = LayerDrawable(layers)
        layerDrawable.setLayerInset(0, 1, -width, -width, -width)
        tab.background = layerDrawable
        if (removePadding)
            tab.setPadding(0, 0, 0, 0)
        else
            tab.setPadding(10, 5, 0, 0)

    }

    @JvmStatic
    fun copyText(context: Context, text: String?): Boolean{
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.text = text
        return true
    }

    @JvmStatic
    fun Context.toast(message: CharSequence,short: Boolean) {
        if(short)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    }

    @JvmStatic
    fun isJson(text: String?): Boolean {
        val m = Pattern.compile("\\{*.}").matcher(text!!)
        while (m.find()) {
            return true
        }
        return false
    }

    @JvmStatic
    fun setMargin(v: View, l: Int, t: Int, r: Int, b: Int) {
        if (v.layoutParams is ViewGroup.MarginLayoutParams) {
            val p: ViewGroup.MarginLayoutParams = v.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(l, t, r, b)
            v.requestLayout()
        }
    }


    fun urlEncoder(link: String?): String? {
        var s: String? = null
        try {
            s = URLEncoder.encode(link, "utf-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return s
    }

    fun bbCode(bbCode: String): String {
        var bbCode1 = bbCode
        bbCode1 = bbCode1.replace("\\n".toRegex(), "<br/>")
        bbCode1 = bbCode1.replace("\\n".toRegex(), "<br/>")
        bbCode1 = bbCode1.replace("&lt;em&gt;".toRegex(), "")
        bbCode1 = bbCode1.replace("&lt;/em&gt;".toRegex(), "")
        bbCode1 = bbCode1.replace("&lt;p".toRegex(), "&lt;div")
        bbCode1 = bbCode1.replace("&lt;/p&gt;".toRegex(), "&lt;/div&gt;")
        bbCode1 = bbCode1.replace("&lt;blockquote&gt;".toRegex(), "")
        bbCode1 = bbCode1.replace("&lt;/blockquote&gt;".toRegex(), "")
         bbCode1 = bbCode1.replace("https://".toRegex(), "http://")
        bbCode1 = bbCode1.replace("&lt;a".toRegex(), "&lt;p")
        bbCode1 = bbCode1.replace("&lt;/a&gt;".toRegex(), "&lt;/p&gt;")
         bbCode1 = bbCode1.replace("<img".toRegex(), "<img onclick=\"FullscreenIMG(this.src)\" ")

         return bbCode1
    }

    fun bbcode(html: String, activity: Activity?): String {
        var html1 = html
        var userName: String = MacwapDB.getString(activity, "user_name")
        userName = if (userName == "null" || userName == "" || userName == "1") {
            "Guest"
        } else {
            userName
        }
        html1 = html1.replace(":user:".toRegex(), userName)
        html1 = html1.replace(
            ":on-promotion:".toRegex(),
            MacwapDB.getString(activity, "on-promotion")
        )
        html1 = html1.replace(
            ":off-promotion:".toRegex(),
            MacwapDB.getString(activity, "off-promotion")
        )
        html1 = html1.replace(
            ":on-notification:".toRegex(),
            MacwapDB.getString(activity, "on-notification")
        )
        html1 = html1.replace(
            ":off-notification:".toRegex(),
            MacwapDB.getString(activity, "off-notification")
        )
        html1 =
            html1.replace(":linkColor:".toRegex(), MacwapDB.getString(activity, "link-color"))
        html1 =
            html1.replace(":textColor:".toRegex(), MacwapDB.getString(activity, "text-color"))
        html1 = html1.replace(
            ":classic-style:".toRegex(),
            MacwapDB.getString(activity, "classic-style")
        )
        html1 = html1.replace(
            ":light-style:".toRegex(),
            MacwapDB.getString(activity, "light-style")
        )
        html1 =
            html1.replace(":dark-style:".toRegex(), MacwapDB.getString(activity, "dark-style"))
        html1 = html1.replace(
            ":macwap-style:".toRegex(),
            MacwapDB.getString(activity, "macwap-style")
        )
        html1 = html1.replace("<a".toRegex(), "<a onclick=\"CallDuty(this.href,'xWebView')\" ")
        html1 = html1.replace(
            ":color-primary:".toRegex(),
            MacwapDB.getString(activity, "color-primary") + ""
        )
        html1 =
            "<script type=\"text/javascript\">   function CallDuty(url,type) {   Call.CallForDuty(url,type); }</script>$html1"
        html1 =
            "<script type=\"text/javascript\">   function CallDutyMore(url,type,id) {   Call.CallForDutyNew(url,type,id); }</script>$html1"
        html1 =
            "<script type=\"text/javascript\">function HideMePlease(e){[].forEach.call(document.querySelectorAll(\".\"+e),function(e){e.style.visibility=\"hidden\"})}</script>$html1"
        return html1
    }

    fun playSound(context: Context, _id: Int) {
        try {
            val notification: Uri = Uri.parse("android.resource://" + context.packageName + "/" + _id) //Here is FILE_NAME is the name of file that you want to play
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(activity: Activity): String {
        val deviceId: String = try {
            Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            "Not Found"
        }
        return deviceId
    }


    fun alertBox(message: String?,activity: Activity) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setMessage(Html.fromHtml(message))
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(activity.resources.getString(R.string.ok)) { _, _ -> }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }



    fun isNetworkAvailable(activity: Activity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    class SingleTapConfirm : GestureDetector.SimpleOnGestureListener() {


        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            return true
        }
    }

}