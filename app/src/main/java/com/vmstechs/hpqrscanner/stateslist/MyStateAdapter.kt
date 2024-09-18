package com.vmstechs.hpqrscanner.stateslist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.vmstechs.hpqrscanner.databinding.SpinnerItemBinding

class MyStateAdapter(private val myContext: Context, private val stateList: List<States>) :
    ArrayAdapter<States>(myContext, 0, stateList)  {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: SpinnerItemBinding
        val view: View

        if (convertView == null) {
            binding = SpinnerItemBinding.inflate(LayoutInflater.from(myContext), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as SpinnerItemBinding
        }

        val city = getItem(position)
        binding.txtCityName.text = city?.state_name


        return view
    }

}