package com.linxiao.quickdevframework.sample.frameworkapi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.linxiao.framework.architecture.BaseFragment
import com.linxiao.framework.common.GsonParser
import com.linxiao.quickdevframework.databinding.FragmentJsonTestBinding

/**
 * class summary
 * <p>
 *  usage and notices
 * </p>
 *
 * @author lx8421bcd
 * @since 2023-10-18
 */
class JsonTestFragment : BaseFragment() {

    class TestObject {
        var id = "000000"
        var name = "abc"
        var age = 0
        var gender = 1
        override fun toString(): String {
            return "TestObject(id='$id', name='$name', age=$age, gender=$gender)"
        }

    }


    private val viewBinding by lazy {
        return@lazy FragmentJsonTestBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

    }

    private fun initViews() {
        viewBinding.btnTestParse.setOnClickListener {
            testDeserialize()
        }
    }

    private fun testDeserialize() {
        val obj = GsonParser.parser.fromJson("{\"name\":null}", TestObject::class.java)
        Log.d(TAG, "testDeserialize: $obj")
    }
}