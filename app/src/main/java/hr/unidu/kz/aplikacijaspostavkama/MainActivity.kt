package hr.unidu.kz.aplikacijaspostavkama

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import hr.unidu.kz.aplikacijaspostavkama.databinding.ActivityMainBinding
import hr.unidu.kz.aplikacijaspostavkama.databinding.ActivityDrugaBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        primijeniPostavke()
    }
    // Po povratku iz ekrana postavki ažuriraju se postavke
    override fun onResume() {
        super.onResume()
        primijeniPostavke()
    }


    // Po pokretanju programa učitavaju se postavke ako postoje,
    // inače se primjenjuju defaultne vrijednosti
    private fun primijeniPostavke() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val slikaSpavac = prefs.getBoolean("slika_spavac", true)
        val naslov = prefs.getString("naslov_aktivnosti", "Aplikacija s postavkama")
        val boja = prefs.getString("lista_boja", "#ffffff")

        binding.slika.setImageResource(if (slikaSpavac) R.drawable.smiley else R.drawable.smiley2)
        title = naslov

        val pozadina: LinearLayout = findViewById(R.id.pozadina)
        try {
            // Color.parseColor očekuje format "#RRGGBB" ili "#AARRGGBB"
            val bojaInt = Color.parseColor(boja)
            binding.pozadina.setBackgroundColor(bojaInt)
        } catch (e: IllegalArgumentException) {
            // Ako je format boje neispravan, postavi fallback boju (npr. bijelu)
            // i logiraj grešku kako bi znao što se dogodilo
            binding.pozadina.setBackgroundColor(Color.WHITE)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // U Kotlinu koristimo 'when' umjesto 'switch'
        return when (item.itemId) {
            R.id.edit -> {
                novaAktivnost()
                true
            }

            R.id.properties -> {
                prikaziPostavke()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun prikaziPostavke() {
        val intent = Intent(this@MainActivity, PrefsActivity::class.java)
        startActivity(intent)

    }

    private fun novaAktivnost() {
        val intent = Intent(this@MainActivity, DrugaActivity::class.java)
        val sp = getPreferences(Context.MODE_PRIVATE)
        intent.putExtra("tekst", sp.getString("tekst", "Tekst nije spremljen u spremište!"))
        startActivity(intent)
    }

    fun spremiSpremisteAktivnosti(v: View) {
        val sp = getPreferences(Context.MODE_PRIVATE)
        sp.edit {
            putString("tekst", binding.tekst.getText().toString())
        }
    }

    fun spremiImenovanoSpremiste(v: View) {
        val sp = getSharedPreferences("moje_spremiste", Context.MODE_PRIVATE)
        sp.edit {
            putString("tekst", binding.tekst.getText().toString())
        }
    }

}


class PrefsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prefs)
    }
}

// Klasa za ažuriranje postavki - spremanje se odrađuje automatski
// po promjeni određene postavke
class PrefsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_stavke, rootKey)
    }
}

class DrugaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrugaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrugaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val t1 = intent.getStringExtra("tekst") ?: "Tekst nije spremljen u lokalno spremište!"
        // 1. Pristup imenovanom spremištu postavki aplikacije
        val sp = getSharedPreferences("moje_spremiste", Context.MODE_PRIVATE)
        // 2. Dohvat podatka iz spremišta
        val t2 = sp.getString("tekst", "Tekst nije spremljen u imenovano spremište!")

        binding.tekst1.text = t1
        binding.tekst2.text = t2
    }
}