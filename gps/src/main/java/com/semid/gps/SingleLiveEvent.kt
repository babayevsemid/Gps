package com.semid.gps

import androidx.annotation.MainThread
import androidx.lifecycle.*
import java.util.concurrent.atomic.AtomicBoolean


class SingleLiveEvent<T> : MutableLiveData<T>() {
    private lateinit var liveDataToObserve: LiveData<T>
    private val mPending = AtomicBoolean(false)

    init {
        val outputLiveData = MediatorLiveData<T>()
        outputLiveData.addSource(this) { currentValue: T ->
            outputLiveData.value = currentValue
            mPending.set(false)
        }
        liveDataToObserve = outputLiveData
    }

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        liveDataToObserve.observe(owner) { t: T ->
            if (mPending.get())
                observer.onChanged(t)
        }
    }

    @MainThread
    override fun setValue(value: T?) {
        mPending.set(true)
        super.setValue(value)
    }
}