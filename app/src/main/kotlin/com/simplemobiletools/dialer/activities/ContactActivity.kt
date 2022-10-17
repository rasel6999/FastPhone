package com.simplemobiletools.dialer.activities

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telecom.TelecomManager
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.macwap.rdxrasel.untils.FunctionManager.toast
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.NavigationIcon
import com.simplemobiletools.commons.helpers.REQUEST_CODE_SET_DEFAULT_DIALER
import com.simplemobiletools.dialer.R
import com.simplemobiletools.dialer.extensions.getHandleToUse
import kotlinx.android.synthetic.main.activity_contact.*
import kotlinx.android.synthetic.main.activity_dialpad.*

class ContactActivity : SimpleActivity() {

    var toolbar: Toolbar? = null
    var email: TextView? = null
    var phone: TextView? = null
    var website: TextView? = null
    var whatsappBtn: ImageView? = null
    var messengerBtn: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        init()
        setSupportActionBar(main_toolbar)
       supportActionBar?.setDisplayHomeAsUpEnabled(true)

        runOnUiThread {
          setup()
            main_toolbar?.setTitleTextColor(baseConfig.textColor)
            user_profile_name?.setTextColor(baseConfig.textColor)



            user_profile_name?.setTextColor(baseConfig.textColor)
            user_profile_short_bio?.setTextColor(baseConfig.textColor)

            contactEmail?.setTextColor(baseConfig.textColor)
            email?.setTextColor(baseConfig.accentColor)

            contactPhone?.setTextColor(baseConfig.textColor)
            phone?.setTextColor(baseConfig.accentColor)

            contactWerbsite?.setTextColor(baseConfig.textColor)
            website?.setTextColor(baseConfig.accentColor)

        }

    }



    fun setup() {

        whatsappBtn!!.setOnClickListener {


            var url = "https://api.whatsapp.com/send"
            url = url + "?phone=+"+getString(R.string.dev_number)+"&text="
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)



        }

        messengerBtn!!.setOnClickListener {
            val isWhatsappInstalled: Boolean = appInstalledOrNot("com.facebook.orca")
            if (isWhatsappInstalled) {
                var uri = Uri.parse("fb-messenger://user/")
                uri = ContentUris.withAppendedId(uri!!, getString(R.string.dev_fbID).toLong())
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            else {
                toast(getString(R.string.messenger_not_install), true)

                val uri = Uri.parse("market://details?id=com.facebook.orca")
                val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                startActivity(goToMarket)
            }
        }





        email!!.setOnClickListener {

            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:") // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.dev_email))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }

        }

        phone!!.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", getString(R.string.dev_number), null)))
        }

        website!!.setOnClickListener {
            toast(getString(R.string.please_wait), true)
            val uri = Uri.parse(getString(R.string.dev_link))
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            startActivity(goToMarket)
        }

    }

    fun init() {
     //   toolbar = findViewById(R.id.toolbar)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        website = findViewById(R.id.website)
        whatsappBtn = findViewById(R.id.whatsapp_btn)
        messengerBtn = findViewById(R.id.messenger_btn)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }


            else -> super.onOptionsItemSelected(item)
        }

    }
    private fun appInstalledOrNot(uri: String): Boolean {
        val pm: PackageManager = packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }
    override fun onResume() {
        super.onResume()
      //  setupToolbar(dialpad_toolbar, NavigationIcon.Arrow)
    }

}
