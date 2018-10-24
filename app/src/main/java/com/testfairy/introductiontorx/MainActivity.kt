package com.testfairy.introductiontorx

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arrayExample()
        observableExample()

        coldObservableCreate()
        hotObservableCreate()
    }

    fun arrayExample() {
        val myArray = listOf(1, 2, 3)
        val transformation = myArray.map { e ->
            Log.d("Rx", "Map: " + e)
            e * 2
        }
        val filtered = transformation.filter { e ->
            Log.d("Rx", "Filter: " + e)
            e > 2
        }

        filtered.forEach { e ->
            Log.d("Rx", "Result: " + e)
        }
        Log.d("Rx", "Done!")
    }

    fun observableExample() {
        val myObservable = Observable.fromArray(1, 2, 3)
        val transformation = myObservable.map { e ->
            Log.d("Rx", "Map: " + e)
            e * 2
        }
        val filtered = transformation.filter { e ->
            Log.d("Rx", "Filter: " + e)
            e > 2
        }

        filtered.subscribe(
                { e ->
                    // onNext
                    Log.d("Rx", "Result: " + e)
                },
                { ex ->
                    // onError
                    Log.e("Rx", "Error", ex)
                },
                {
                    // onComplete
                    Log.d("Rx", "Done!")
                },
                { disposable ->
                    //onSubscribe
                    Log.d("Rx", "Subscribed!")
                }
        )
    }

    fun coldObservableCreate() {
        val queue = Volley.newRequestQueue(this)

        val httpCallObservable: Observable<String> = Observable.create<String> { sub ->
            val req = StringRequest(Request.Method.GET, "https://httpbin.org/get",
                    Response.Listener<String> { response ->
                        sub.onNext(response)
                    },
                    Response.ErrorListener { error ->
                        sub.onError(error)
                    })

            queue.add(req)
        }

        httpCallObservable.subscribe(
                { data ->
                    Log.d("Rx", data)
                },
                { error ->
                    Log.e("Rx", error.message, error)
                },
                {},
                { _ -> }
        )
    }

    fun hotObservableCreate() {
        val clicks = textview.clicks()

        clicks.subscribe(
                {
                    Log.d("Rx", "click")
                },
                { error ->
                    Log.e("Rx", error.message, error)
                },
                {},
                { _ -> }
        )
    }
}
