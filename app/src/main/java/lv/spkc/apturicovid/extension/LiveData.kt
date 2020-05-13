package lv.spkc.apturicovid.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData


fun <T, K, R> LiveData<T>.combineWith(liveData: LiveData<K>, block: (T?, K?) -> R): LiveData<R> {
    MediatorLiveData<R>().also {
        it.addSource(this) { _ ->
            it.value = block.invoke(this.value, liveData.value)
        }
        it.addSource(liveData) { _ ->
            it.value = block.invoke(this.value, liveData.value)
        }
        return it
    }
}

fun <T> MutableLiveData<T>.mutate(block: (T)->T) {
    this.value?.also {
        this.postValue(block.invoke(it))
    }
}

fun <T> MutableLiveData<List<T>>.mutateListItems(block: (T)->T) {
    this.value?.also {
        this.postValue(it.map(block))
    }
}