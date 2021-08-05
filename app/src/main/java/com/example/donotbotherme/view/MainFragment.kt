package com.example.donotbotherme.view

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.example.donotbotherme.R
import com.example.donotbotherme.databinding.FragmentMainBinding
import com.example.donotbotherme.model.DisturbCondition
import java.util.jar.Manifest

const val REQUEST_CODE = 42

class MainFragment : Fragment() {
    //разобраться с Parcelable и Intent, данные сохраняются, но не вытаскиваются


    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

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

        val conditionsList = ArrayList<DisturbCondition>()
        val intent = Intent(requireActivity(), MainFragment::class.java)
        val conditionsToLoad = intent.getParcelableArrayListExtra<Parcelable>("conditions")

        val retrievedData = intent.getParcelableArrayListExtra<Parcelable>("conditions")

        println("$retrievedData BEBA")


        if (!conditionsToLoad.isNullOrEmpty()) {
            println(conditionsToLoad.isNullOrEmpty().toString() + " BEB")
            for (i in 0..conditionsToLoad.size) {
                val element = conditionsToLoad[i] as DisturbCondition
                conditionsList.add(element)
                binding.createdConditionsLayout.addView(AppCompatTextView(requireContext()).apply {
                    val textToView = conditionsList[i].contactName + conditionsList[i].contactNumber
                    text = textToView
                })
            }
        } else {
            Toast.makeText(requireContext(), "List is null", Toast.LENGTH_SHORT).show()
        }

        binding.createBtn.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, ConditionFragment.newInstance())
                .commit()
        }

    }
    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}