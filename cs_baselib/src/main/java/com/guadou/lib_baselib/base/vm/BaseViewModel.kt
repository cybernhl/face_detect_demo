package com.guadou.lib_baselib.base.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guadou.lib_baselib.bean.LoadAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel : ViewModel() {

    var mLoadActionLiveData: MutableLiveData<LoadAction> = MutableLiveData()

    fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch { block() }
    }

    suspend fun <T> launchOnIO(block: suspend CoroutineScope.() -> T) {
        withContext(Dispatchers.IO) {
            block
        }
    }

    /**
     * 设置并发射加载状态
     */
    fun setStateLiveData(loadState: LoadAction) {
        mLoadActionLiveData.postValue(loadState)
    }

    fun loadStartLoading() {
        mLoadActionLiveData.postValue(LoadAction(LoadAction.STATE_LOADING))
    }

    fun loadSuccess() {
        mLoadActionLiveData.postValue(LoadAction(LoadAction.STATE_SUCCESS))
    }

    fun loadError(message: String?) {
        mLoadActionLiveData.postValue(LoadAction(LoadAction.STATE_ERROR, message))
    }

    fun loadNoData() {
        mLoadActionLiveData.postValue(LoadAction(LoadAction.STATE_NO_DATA))
    }

    fun loadStartProgress() {
        mLoadActionLiveData.postValue(LoadAction(LoadAction.STATE_PROGRESS))
    }

    fun loadHideProgress() {
        mLoadActionLiveData.postValue(LoadAction(LoadAction.STATE_HIDE_PROGRESS))
    }

    fun getActionLiveData(): MutableLiveData<LoadAction> {
        return mLoadActionLiveData
    }

}
