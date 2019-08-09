package br.com.danielsan.parseacldecodebug

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parse.Parse
import com.parse.ParseObject
import com.parse.ParseUser

/**
 * Created by daniel on 8/9/19
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Parse.initialize(Parse.Configuration.Builder(this)
            .server("https://parse.server/")
            .applicationId("foo")
            .clientKey("bar")
            .build())

        prepare()
    }

    private fun prepare() {
        val user = ParseUser.getCurrentUser()
        if (user != null) {
            showBug(user)
//            workaround(user)
            return
        }

        ParseUser.logInInBackground("foo", "bar") { u, e ->
            if (e == null && u != null) {
                Toast.makeText(this, "Run the app again!", Toast.LENGTH_LONG).show()
                finish()
            } else if (e != null) {
                throw e
            } else {
                throw IllegalStateException("User foo is note created!")
            }
        }
    }

    private fun workaround(user: ParseUser) {
        user.fetchInBackground<ParseUser> { u, e ->
            showBug(user)
        }
    }

    private fun showBug(user: ParseUser) {
        user.put("foo", "bar")

        val foo = ParseObject("Foo")
        foo.put("u", user)
        foo.put("a", "b")

        foo.saveInBackground {
            if (it == null) {
                Log.d("MainActivity:prepare", "No problem!")
            } else {
                Log.e("MainActivity:prepare", "Houston we have a problem!", it)
                throw it
            }
        }
    }

}
