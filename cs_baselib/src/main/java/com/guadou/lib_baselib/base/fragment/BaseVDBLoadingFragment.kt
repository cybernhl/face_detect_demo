package com.guadou.lib_baselib.base.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.guadou.lib_baselib.base.vm.BaseViewModel
import com.guadou.lib_baselib.bean.DataBindingConfig
import com.guadou.lib_baselib.bean.LoadAction
import com.guadou.lib_baselib.ext.getVMCls
import com.guadou.lib_baselib.utils.NetWorkUtil
import com.guadou.lib_baselib.view.LoadingDialogManager
import com.guadou.lib_baselib.view.gloading.Gloading

/**
 * 加入ViewModel与LoadState
 * 默认为Loading布局的加载方式
 */
abstract class BaseVDBLoadingFragment<VM : BaseViewModel, VDB : ViewDataBinding> : AbsFragment() {

    protected lateinit var mViewModel: VM
    protected lateinit var mBinding: VDB

    protected lateinit var mGLoadingHolder: Gloading.Holder


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(savedInstanceState)

        startObserve()
    }


    override fun transformRootView(view: View): View {

        mGLoadingHolder = generateGLoading(view)

        return mGLoadingHolder.wrapper
    }

    //如果要替换GLoading，重写次方法
    open protected fun generateGLoading(view: View): Gloading.Holder {
        return Gloading.getDefault().wrap(view).withRetry {
            onGoadingRetry()
        }
    }

    protected open fun onGoadingRetry() {
    }

    //使用这个方法简化ViewModewl的Hilt依赖注入获取
    protected inline fun <reified VM : BaseViewModel> getViewModel(): VM {
        val viewModel: VM by viewModels()
        return viewModel
    }

    //反射获取ViewModel实例
    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVMCls(this))
    }

    override fun setContentView(container: ViewGroup?): View {
        mViewModel = createViewModel()
        //观察网络数据状态
        mViewModel.getActionLiveData().observe(viewLifecycleOwner, stateObserver)

        val config = getDataBindingConfig()
        mBinding = DataBindingUtil.inflate(layoutInflater, config.getLayout(), container, false)
        mBinding.lifecycleOwner = viewLifecycleOwner

        if (config.getVmVariableId() != 0) {
            mBinding.setVariable(
                config.getVmVariableId(),
                config.getViewModel()
            )
        }

        val bindingParams = config.getBindingParams()
        bindingParams.forEach { key, value ->
            mBinding.setVariable(key, value)
        }

        return mBinding.root
    }

    abstract fun getDataBindingConfig(): DataBindingConfig
    abstract fun startObserve()
    abstract fun init(savedInstanceState: Bundle?)

    override fun onNetworkConnectionChanged(isConnected: Boolean, networkType: NetWorkUtil.NetworkType?) {
    }

    // ================== 网络状态的监听 ======================

    private var stateObserver: Observer<LoadAction> = Observer { loadAction ->
        if (loadAction != null) {

            when (loadAction.action) {
                LoadAction.STATE_NORMAL -> showStateNormal()
                LoadAction.STATE_ERROR -> showStateError(loadAction.message)
                LoadAction.STATE_SUCCESS -> showStateSuccess()
                LoadAction.STATE_LOADING -> showStateLoading()
                LoadAction.STATE_NO_DATA -> showStateNoData()
                LoadAction.STATE_PROGRESS -> showStateProgress()
                LoadAction.STATE_HIDE_PROGRESS -> hideStateProgress()
            }

        }
    }

    protected open fun showStateNormal() {}

    protected open fun showStateLoading() {
        mGLoadingHolder.showLoading()
    }

    protected open fun showStateSuccess() {
        mGLoadingHolder.showLoadSuccess()
    }

    protected open fun showStateError(message: String?) {
        mGLoadingHolder.showLoadFailed(message)
    }

    protected open fun showStateNoData() {
        mGLoadingHolder.showEmpty()
    }

    protected fun showStateProgress() {
        LoadingDialogManager.get().showLoading(mActivity)
    }

    protected fun hideStateProgress() {
        LoadingDialogManager.get().dismissLoading()
    }

}