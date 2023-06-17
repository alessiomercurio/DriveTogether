package com.agmobiletech.drivetogether

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.agmobiletech.drivetogether.homepage.HomepageActivity
import com.agmobiletech.drivetogether.registrazione.RegistrazioneCompletataActivity
import com.agmobiletech.drivetogether.registrazione.RegistrazioneSecondaActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationManager(private val context: Context, private val bottomNavigationView: BottomNavigationView) {

    init {
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            gestioneItem(menuItem)
        }
    }

    private fun gestioneItem(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.homepageMenuItem -> {
                startActivity(HomepageActivity::class.java)
                true
            }
            R.id.profiloMenuItem -> {
                startActivity(ProfiloUtenteActivity::class.java)
                true
            }
            R.id.inserimentoMenuItem -> {
                startActivity(RegistrazioneSecondaActivity::class.java)
                true
            }
            R.id.autoMenuItem -> {
                startActivity(RegistrazioneCompletataActivity::class.java)
                true
            }
            else -> false
        }
    }

    private fun startActivity(activityClass: Class<*>) {
        val intent = Intent(context, activityClass)
        context.startActivity(intent)
    }

}