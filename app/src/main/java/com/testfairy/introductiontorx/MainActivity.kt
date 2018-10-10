package com.testfairy.introductiontorx

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.Observable

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arrayExample()
        observableExample()
    }

    fun arrayExample() {
        val myArray = listOf(1, 2, 3)
        val transformation = myArray.map { e ->
            Log.d("Rx","Map: " + e)
            e * 2
        }
        val filtered = transformation.filter { e ->
            Log.d("Rx","Filter: " + e)
            e > 2
        }

        filtered.forEach { e ->
            Log.d("Rx","Result: " + e)
        }
        Log.d("Rx", "Done!")
    }

    fun observableExample() {
        val myObservable = Observable.fromArray(1, 2, 3)
        val transformation = myObservable.map { e ->
            Log.d("Rx","Map: " + e)
            e * 2
        }
        val filtered = transformation.filter { e ->
            Log.d("Rx","Filter: " + e)
            e > 2
        }

        filtered.subscribe(
            { e ->
                // onNext
                Log.d("Rx","Result: " + e)
            },
            { ex ->
                // onError
                Log.e("Rx", "Error", ex)
            },
            {
                // onComplete
                Log.d("Rx","Done!")
            },
            { disposable ->
                //onSubscribe
                Log.d("Rx","Subscribed!")
            }
        )
    }
}
