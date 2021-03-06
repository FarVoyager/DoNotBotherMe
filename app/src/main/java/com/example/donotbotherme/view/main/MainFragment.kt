package com.example.donotbotherme.view.main

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.donotbotherme.R
import com.example.donotbotherme.TinyDB
import com.example.donotbotherme.app.App
import com.example.donotbotherme.databinding.FragmentMainBinding
import com.example.donotbotherme.model.AppState
import com.example.donotbotherme.model.DisturbCondition
import com.example.donotbotherme.view.ConditionFragment
import com.example.donotbotherme.view.NEW_CONDITION
import com.example.donotbotherme.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

const val REQUEST_CODE = 42
const val CONDITION_LIST = "CONDITION_LIST"
const val IS_CONDITION_CREATED = "IS_CONDITION_CREATED"

//Rv на 1-ом экране сделан, теперь надо организовать сохранение в БД на 2-ом экране


class MainFragment : Fragment() {
//контекстное меню не вызывается при долгом нажатии
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var selectedItemView: AppCompatTextView
    private var viewId = 9000

    private lateinit var conditionsList: ArrayList<DisturbCondition>
    private var objectList: ArrayList<Any> = ArrayList()
    private lateinit var tinyDB: TinyDB


    private val model: MainViewModel by viewModel()
    private var adapter: MainRvAdapter? = null
    private val onListItemClickListener: MainRvAdapter.OnListItemClickListener =
        object : MainRvAdapter.OnListItemClickListener {
            override fun onItemClick(data: DisturbCondition) {
                Toast.makeText(requireContext(), data.contactNumber, Toast.LENGTH_SHORT).show()
            }

        }

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

        model.subscribe().observe(viewLifecycleOwner, { renderData(it) })

        tinyDB = TinyDB(requireContext())

        //логика заполнения списка условий
        val arguments = this.arguments
        if (arguments?.getParcelable<DisturbCondition>(NEW_CONDITION) != null &&
            arguments.getInt(IS_CONDITION_CREATED) == 1) { //если было создано новое условие
            conditionsList = tinyDB.getListObject(CONDITION_LIST, DisturbCondition::class.java) as ArrayList<DisturbCondition>
            arguments.putInt(IS_CONDITION_CREATED, 0)
        } else {
            if (tinyDB.getListObject(CONDITION_LIST, DisturbCondition::class.java) != null) { // если список условий уже не пуст
                conditionsList = tinyDB.getListObject(CONDITION_LIST, DisturbCondition::class.java) as ArrayList<DisturbCondition>
            } else { //если список условий пуст
                conditionsList = ArrayList()
            }
        }

        objectList.clear()
        //сохранение списка условий в память телефона
        for (i in 0 until conditionsList.size) {
            objectList.add(conditionsList[i])
        }
        tinyDB.putListObject(CONDITION_LIST, objectList)

        val conditionBundle = this.arguments
        if (conditionBundle != null) {
            val retrievedCondition = conditionBundle.getParcelable<DisturbCondition>(NEW_CONDITION)
            if (retrievedCondition != null) {
                println(" $retrievedCondition BEBS")
                conditionsList.add(retrievedCondition)
                objectList.clear()
                for (i in 0 until conditionsList.size) {
                    objectList.add(conditionsList[i])
                }
                tinyDB.putListObject(CONDITION_LIST, objectList)
                conditionBundle.remove(NEW_CONDITION)
            }
        }


        //отображение списка условий
            if (!conditionsList.isNullOrEmpty()) {
                for (i in 0 until conditionsList.size) {
                    binding.createdConditionsLayout.addView(AppCompatTextView(requireContext()).apply {
                        registerForContextMenu(this)
                        this.apply {
                            gravity = Gravity.CENTER_VERTICAL
                            height = 100
                            textSize = 16f
                            this.id = i
                        }
                        val textToView = conditionsList[i].contactName + " , " + conditionsList[i].contactNumber + " , " + conditionsList[i].timeStart + "-" + conditionsList[i].timeEnd
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
            AlertDialog.Builder(requireContext())
                .setTitle("Вы уверены?")
                .setMessage("Удалить все условия?")
                .setNegativeButton("Нет") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Да") { _, _, ->
                    conditionsList.clear()
                    objectList.clear()
                    tinyDB.putListObject(CONDITION_LIST, objectList)
                    binding.createdConditionsLayout.removeAllViews()
                }
                .create().show()

        }
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                val data = appState.data
                if (data.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "data is null or empty", Toast.LENGTH_SHORT).show()
                } else {
                    if (adapter == null) {
                        binding.rvMain.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvMain.adapter = MainRvAdapter(onListItemClickListener, data)
                    } else {
                        adapter?.setData(data)
                    }
                }
            }
            is AppState.Loading -> {
                Toast.makeText(requireContext(), "AppState.Loading", Toast.LENGTH_SHORT).show()
            }
            is AppState.Error -> {
                Toast.makeText(requireContext(), "AppState.Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(CONDITION_LIST, conditionsList)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val menuInflater: MenuInflater = requireActivity().menuInflater
        println("onCreateContextMenu BEB")
        menuInflater.inflate(R.menu.context_menu, menu)
        if (v::class.java == AppCompatTextView::class.java) {
            selectedItemView = v as AppCompatTextView
            viewId = v.id
            println("selectedItemView = v as AppCompatTextView BEB")

        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val layout = requireActivity().findViewById<LinearLayout>(R.id.createdConditionsLayout)
        when (item.itemId) {
            R.id.context_info -> {
            }
            R.id.context_delete -> {
                layout.removeView(activity?.findViewById(viewId))
                val numberToDelete = selectedItemView.text.split(" , ")[1]

                for (i in 0 until conditionsList.size) {
                    val listNumber = conditionsList[i].contactNumber
                    println("$numberToDelete $listNumber BEBSA")
                    if (conditionsList[i].contactNumber == numberToDelete) {
                        conditionsList.removeAt(i)
                        objectList.removeAt(i)
                        tinyDB.putListObject(CONDITION_LIST, objectList)
                        println("$listNumber DELETED BEBSA")
                        break
                    } else {
                        Toast.makeText(requireContext(), "Nothing to remove", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return super.onContextItemSelected(item)
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