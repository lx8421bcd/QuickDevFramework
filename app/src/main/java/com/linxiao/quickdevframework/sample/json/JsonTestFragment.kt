package com.linxiao.quickdevframework.sample.json

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.linxiao.framework.architecture.BaseFragment
import com.linxiao.framework.json.GsonParser
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
        val objKt = GsonParser.parser.fromJson("{\"name\":null, \"study\":null}", TestKtObject::class.java)
        Log.d(TAG, "testDeserialize: $objKt")
        val objJava = GsonParser.parser.fromJson("{\"name\":null, \"study\":\"university\"}", TestJavaObject::class.java)
        Log.d(TAG, "testDeserialize: $objJava")
    }
}