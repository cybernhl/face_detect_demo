package com.guadou.lib_baselib.base.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
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
 * 默认为Loading布局的懒加载
 */
abstract class BaseVDBLazyLoadingFragment<VM : BaseViewModel, VDB : ViewDataBinding> : AbsFragment() {

    protected lateinit var mViewModel: VM
    protected lateinit var mBinding: VDB
    private var isViewCreated = false//布局是否被创建
    private var isLoadData = false//数据是否加载
    private var isFirstVisible = true//是否第一次可见
    protected lateinit var mGLoadingHolder: Gloading.Holder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true

        init(savedInstanceState)
        startObserve()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (isFragmentVisible(this) && this.isAdded) {

            if (parentFragment == null || isFragmentVisible(parentFragment)) {
                onLazyInitData()
                isLoadData = true
                if (isFirstVisible) isFirstVisible = false
            }
        }
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
    abstract fun onLazyInitData()

    //Loading Create Root View
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

    override fun onNetworkConnectionChanged(
        isConnected: Boolean,
        networkType: NetWorkUtil.NetworkType?
    ) {
    }

    // ============================  Lazy Load begin ↓  =============================

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isFragmentVisible(this) && !isLoadData && isViewCreated && this.isAdded) {
            onLazyInitData()
            isLoadData = true
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        //onHiddenChanged调用在Resumed之前，所以此时可能fragment被add, 但还没resumed
        if (!hidden && !this.isResumed)
            return
        //使用hide和show时，fragment的所有生命周期方法都不会调用，除了onHiddenChanged（）
        if (!hidden && isFirstVisible && this.isAdded) {
            onLazyInitData()
            isFirstVisible = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        isViewCreated = false
        isLoadData = false
        isFirstVisible = true
    }

    /**
     * 当前Fragment是否对用户是否可见
     * @param fragment 要判断的fragment
     * @return true表示对用户可见
     */
    private fun isFragmentVisible(fragment: Fragment?): Boolean {
        return !fragment?.isHidden!! && fragment.userVisibleHint
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