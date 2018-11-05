package com.testfairy.introductiontorx

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        arrayExample()
//        observableExample()
//
//        coldObservableCreate()
//        hotObservableCreate()

        incorrectHttpCallChaining()
        correctHttpCallChaining()
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

    fun httpGetRx(url: String): Observable<String> {
        val queue = Volley.newRequestQueue(this)

        return Observable.create<String> { sub ->
            val req = StringRequest(Request.Method.GET, url,
                    Response.Listener<String> { response ->
                        sub.onNext(response)
                    },
                    Response.ErrorListener { error ->
                        sub.onError(error)
                    })

            queue.add(req)
        }
    }

    fun httpPostRx(url: String, body: MutableMap<String, String>): Observable<String> {
        val queue = Volley.newRequestQueue(this)

        return Observable.create<String> { sub ->

            val req = object : StringRequest(Request.Method.POST, url,
                    Response.Listener<String> { response ->
                        sub.onNext(response)
                    },
                    Response.ErrorListener { error ->
                        sub.onError(error)
                    }) {
                override fun getParams(): MutableMap<String, String> {
                    return body
                }
            }

            queue.add(req)
        }
    }

    fun correctHttpCallChaining() {
        val loginButton = Button(this)
        val usernameText = EditText(this)
        val passText = EditText(this)

        loginButton.clicks()
                .flatMap {
                    httpPostRx("https://httpbin.org/get?call=login", mutableMapOf(usernameText.text.toString() to "user", passText.text.toString() to "pass"))
                }
                .flatMap { _ ->
                    httpGetRx("https://httpbin.org/get?call=getProfile")
                            .map { profile ->
                                listOf(profile)
                            }
                }
                .flatMap { responses ->
                    httpGetRx("https://httpbin.org/get?call=getNotifications")
                            .map { notifications ->
                                responses + listOf(notifications)
                            }
                }
                .flatMap { responses ->
                    httpGetRx("https://httpbin.org/get?call=getSettings")
                            .map { settings ->
                                responses + listOf(settings)
                            }
                }
                .subscribe(
                        { responses ->
                            // show everything
                        },
                        { error ->
                            Log.e("Rx", error.message, error)
                        },
                        {},
                        { _ -> }
                )
    }

    fun incorrectHttpCallChaining() {
        httpPostRx("https://httpbin.org/get?call=login", mutableMapOf( "example" to "user", "123456" to "pass"))
                .subscribe(
                        { _ ->
                            httpGetRx("https://httpbin.org/get?call=getProfile")
                                    .subscribe(
                                            { profile ->
                                                // Show profile
                                            },
                                            { error ->
                                                Log.e("Rx", error.message, error)
                                            },
                                            {},
                                            { _ -> }
                                    )
                        },
                        { error ->
                            Log.e("Rx", error.message, error)
                        },
                        {},
                        { _ -> }
                )

    }
}



