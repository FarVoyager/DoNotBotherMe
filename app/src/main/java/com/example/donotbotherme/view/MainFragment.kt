package com.example.donotbotherme.view

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import com.example.donotbotherme.R
import com.example.donotbotherme.TinyDB
import com.example.donotbotherme.databinding.FragmentMainBinding
import com.example.donotbotherme.model.DisturbCondition

const val REQUEST_CODE = 42
const val CONDITION_LIST = "CONDITION_LIST"


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var conditionsList: ArrayList<DisturbCondition>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tinyDB = TinyDB(requireContext())

        //логика заполнения списка условий
        if (this.arguments?.getParcelable<DisturbCondition>(NEW_CONDITION) != null) { //если было создано новое условие
            println("Bundle NOT NULL BEB")
            conditionsList = tinyDB.getListObject(CONDITION_LIST, DisturbCondition::class.java) as ArrayList<DisturbCondition>
        } else {
            if (tinyDB.getListObject(CONDITION_LIST, DisturbCondition::class.java) != null) { // если список условий уже не пуст
                println("Bundle NOT NULL 2 BEB")
                conditionsList = tinyDB.getListObject(CONDITION_LIST, DisturbCondition::class.java) as ArrayList<DisturbCondition>
            } else { //если список условий пуст
                println("Bundle NULL BEB")
                conditionsList = ArrayList()
            }
        }

        val objectList: ArrayList<Any> = ArrayList()
        for (i in 0 until conditionsList.size) {
            objectList.add(conditionsList[i])
        }
        tinyDB.putListObject(CONDITION_LIST, objectList)

        val retrievedList = tinyDB.getListObject(CONDITION_LIST, DisturbCondition::class.java)
        println("$retrievedList BEBUS")


        val conditionBundle = this.arguments
        if (conditionBundle != null) {
            println("Bundle is received BEB")
            val retrievedCondition = conditionBundle.getParcelable<DisturbCondition>(NEW_CONDITION)
            if (retrievedCondition != null) {
                conditionsList.add(retrievedCondition)
                    objectList.add(retrievedCondition)
                tinyDB.putListObject(CONDITION_LIST, objectList)
                println(conditionsList.size.toString() + " size BEB")
            }
        }

            if (!conditionsList.isNullOrEmpty()) {
                println(conditionsList.size.toString() + " size BEB")

                for (i in 0 until conditionsList.size) {
                    binding.createdConditionsLayout.addView(AppCompatTextView(requireContext()).apply {
                        val textToView = conditionsList[i].contactName + " " + conditionsList[i].contactNumber
                        text = textToView
                    })
                }
        } else {
            println("Bundle is null BEB")
        }

        binding.createBtn.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, ConditionFragment.newInstance())
                .commit()
        }

        binding.clearAllBtn.setOnClickListener {
            conditionsList.clear()
            objectList.clear()
            tinyDB.putListObject(CONDITION_LIST, objectList)
            binding.createdConditionsLayout.removeAllViews()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        println("onSaveInstanceState BEB")

        outState.putParcelableArrayList(CONDITION_LIST, conditionsList)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
        @JvmStatic
        fun newInstance(bundle: Bundle) : MainFragment {
            val fragment = MainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

//    override fun onPause() {
//        super.onPause()
//        println("onPause BEB")
//    }
//
//    override fun onStop() {
//        super.onStop()
//        println("onStop BEB")
//    }
//
//    override fun onResume() {
//        super.onResume()
//        println("onResume BEB")
//    }
}