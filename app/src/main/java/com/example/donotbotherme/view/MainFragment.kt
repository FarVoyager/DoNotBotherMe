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

    //вылетает при попытке изменить состояние режима "Не беспокоить"

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    lateinit var conditionsList: ArrayList<DisturbCondition>

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
            conditionsList = tinyDB.getListObject(CONDITION_LIST, DisturbCondition::class.java) as ArrayList<DisturbCondition>
        } else {
            if (tinyDB.getListObject(CONDITION_LIST, DisturbCondition::class.java) != null) { // если список условий уже не пуст
                conditionsList = tinyDB.getListObject(CONDITION_LIST, DisturbCondition::class.java) as ArrayList<DisturbCondition>
            } else { //если список условий пуст
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
            val retrievedCondition = conditionBundle.getParcelable<DisturbCondition>(NEW_CONDITION)
            if (retrievedCondition != null) {
                conditionsList.add(retrievedCondition)
                    objectList.add(retrievedCondition)
                tinyDB.putListObject(CONDITION_LIST, objectList)
            }
        }
            if (!conditionsList.isNullOrEmpty()) {

                for (i in 0 until conditionsList.size) {
                    binding.createdConditionsLayout.addView(AppCompatTextView(requireContext()).apply {
                        val textToView = conditionsList[i].contactName + " " + conditionsList[i].contactNumber
                        text = textToView
                    })
                }
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
}