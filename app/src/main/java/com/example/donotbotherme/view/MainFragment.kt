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
import com.example.donotbotherme.databinding.FragmentMainBinding
import com.example.donotbotherme.model.DisturbCondition

const val REQUEST_CODE = 42
const val CONDITION_LIST = "CONDITION_LIST"


class MainFragment : Fragment() {
    //разобраться с Parcelable и Intent, данные сохраняются, но не вытаскиваются


    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var conditionsList: ArrayList<DisturbCondition>
    private val listBundle: Bundle = Bundle()

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

        if (this.arguments?.getParcelable<DisturbCondition>(NEW_CONDITION) != null) {
            println("Bundle NOT NULL BEB")
            conditionsList = listBundle.getParcelableArrayList<DisturbCondition>(CONDITION_LIST)!!
        } else {
            if (listBundle.getParcelableArrayList<DisturbCondition>(CONDITION_LIST) != null) {
                println("Bundle NOT NULL 2 BEB")
                conditionsList = listBundle.getParcelableArrayList<DisturbCondition>(CONDITION_LIST) as ArrayList<DisturbCondition>
            } else {
                println("Bundle NULL BEB")
                conditionsList = ArrayList()
            }

        }


        listBundle.putParcelableArrayList(CONDITION_LIST, conditionsList)
        println(listBundle.getParcelableArrayList<DisturbCondition>(CONDITION_LIST).toString() + " BEBUS")
//        arguments?.putParcelableArrayList(CONDITION_LIST, conditionsList)
        println("ZAPIHAL BEB")
//        println(arguments?.getParcelableArrayList<DisturbCondition>(CONDITION_LIST).toString() + " BEBUS")

        val conditionBundle = this.arguments
        if (conditionBundle != null) {
            println("Bundle is received BEB")
            val retrievedCondition = conditionBundle.getParcelable<DisturbCondition>(NEW_CONDITION)
            if (retrievedCondition != null) {
                conditionsList.add(retrievedCondition)
                println(conditionsList.size.toString() + " size BEB")
            }
            listBundle.putParcelableArrayList(CONDITION_LIST, conditionsList)
            println("ZAPIHAL 2 BEB")


            if (!conditionsList.isNullOrEmpty()) {
                for (i in 0 until conditionsList.size) {
                    binding.createdConditionsLayout.addView(AppCompatTextView(requireContext()).apply {
                        val textToView = conditionsList[i].contactName + " " + conditionsList[i].contactNumber
                        text = textToView
                    })
                }
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

    }

//    override fun onPause() {
//        super.onPause()
//
//        val pref = requireActivity().getSharedPreferences("PREF", 0)
//        val editor: SharedPreferences.Editor = pref.edit()
//        editor.putString("TASKS", )
//    }

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

    override fun onPause() {
        super.onPause()
        println("onPause BEB")
    }

    override fun onStop() {
        super.onStop()
        println("onStop BEB")
    }

    override fun onResume() {
        super.onResume()
        println("onResume BEB")
    }
}