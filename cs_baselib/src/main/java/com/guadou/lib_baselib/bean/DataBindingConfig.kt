package com.guadou.lib_baselib.bean

import android.util.SparseArray
import com.guadou.lib_baselib.base.vm.BaseViewModel

//DataBinding的封装数据
class DataBindingConfig(private val layout: Int, private val vmVariableId: Int, private val viewModel: BaseViewModel?) {

    constructor(layout: Int) : this(layout, 0, null)

    private var bindingParams: SparseArray<Any> = SparseArray()

    fun getLayout(): Int = layout

    fun getVmVariableId(): Int = vmVariableId

    fun getViewModel(): BaseViewModel? = viewModel

    fun getBindingParams(): SparseArray<Any> = bindingParams

    fun addBindingParams(variableId: Int, objezt: Any): DataBindingConfig {
        if (bindingParams.get(variableId) == null) {
            bindingParams.put(variableId, objezt)
        }
        return this
    }

}